import cv2
import face_recognition
import oracledb
import numpy as np
import argparse
from datetime import datetime, date

DB_CONFIG = {
    "user":     "AARAV1075",
    "password": "24051075",
    "dsn":      "localhost/XEPDB1",
}

def get_connection() -> oracledb.Connection:
    try:
        conn = oracledb.connect(**DB_CONFIG)
        return conn
    except oracledb.Error as e:
        print(f"[ERROR] Could not connect to Oracle DB: {e}")
        raise SystemExit(1)

def init_db() -> None:
    conn = get_connection()
    cursor = conn.cursor()

    def create_if_missing(obj_type: str, name: str, ddl: str) -> None:
        cursor.execute(
            "SELECT COUNT(*) FROM user_objects WHERE object_type = :t AND object_name = :n",
            {"t": obj_type, "n": name.upper()}
        )
        if cursor.fetchone()[0] == 0:
            cursor.execute(ddl)
            print(f"[DB] Created {obj_type.lower()} '{name}'.")

    try:
        create_if_missing("SEQUENCE", "users_seq",
            "CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1")
        create_if_missing("SEQUENCE", "attendance_seq",
            "CREATE SEQUENCE attendance_seq START WITH 1 INCREMENT BY 1")
        create_if_missing("TABLE", "users", """
            CREATE TABLE users (
                id          NUMBER        PRIMARY KEY,
                name        VARCHAR2(255) NOT NULL,
                photo       BLOB          NOT NULL,
                encoding    BLOB          NOT NULL
            )
        """)
        create_if_missing("TABLE", "attendance", """
            CREATE TABLE attendance (
                id          NUMBER        PRIMARY KEY,
                user_id     NUMBER        NOT NULL REFERENCES users(id),
                name        VARCHAR2(255) NOT NULL,
                att_date    DATE          NOT NULL,
                att_time    VARCHAR2(8)   NOT NULL,
                CONSTRAINT  one_per_day UNIQUE (user_id, att_date)
            )
        """)
        conn.commit()
    finally:
        cursor.close()
        conn.close()

def add_user(name: str, photo_path: str) -> None:
    try:
        open(photo_path, "rb").close()
    except FileNotFoundError:
        print(f"[ERROR] Photo not found: {photo_path}")
        raise SystemExit(1)

    image = face_recognition.load_image_file(photo_path)
    encodings = face_recognition.face_encodings(image)

    if not encodings:
        print("[ERROR] No face detected in the photo. Use a clear, frontal face image.")
        raise SystemExit(1)

    encoding_bytes = encodings[0].tobytes()

    with open(photo_path, "rb") as f:
        photo_bytes = f.read()

    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO users (id, name, photo, encoding) VALUES (users_seq.NEXTVAL, :1, :2, :3)",
            (name, photo_bytes, encoding_bytes)
        )
        conn.commit()
        print(f"[OK] User '{name}' registered successfully.")
    finally:
        cursor.close()
        conn.close()

def load_known_faces() -> tuple[list[np.ndarray], list[str], list[int]]:
    encodings, names, ids = [], [], []
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT id, name, encoding FROM users")
        for (uid, uname, enc_lob) in cursor.fetchall():
            enc_bytes = enc_lob.read() if hasattr(enc_lob, "read") else bytes(enc_lob)
            enc = np.frombuffer(enc_bytes, dtype=np.float64)
            encodings.append(enc)
            names.append(uname)
            ids.append(uid)
    finally:
        cursor.close()
        conn.close()
    return encodings, names, ids

def list_users() -> None:
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT id, name FROM users ORDER BY id")
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()

    if not rows:
        print("No users registered yet.")
        return
    print(f"\n{'ID':<6} {'Name'}")
    print("-" * 30)
    for uid, uname in rows:
        print(f"{uid:<6} {uname}")
    print()

def view_attendance() -> None:
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "SELECT name, att_date, att_time FROM attendance ORDER BY att_date DESC, att_time DESC"
        )
        rows = cursor.fetchall()
    finally:
        cursor.close()
        conn.close()

    if not rows:
        print("No attendance records yet.")
        return
    print(f"\n{'Name':<20} {'Date':<12} {'Time'}")
    print("-" * 44)
    for uname, rec_date, rec_time in rows:
        print(f"{uname:<20} {str(rec_date)[:10]:<12} {rec_time}")
    print()

def record_attendance(user_id: int, name: str) -> bool:
    now = datetime.now().strftime("%H:%M:%S")
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            """INSERT INTO attendance (id, user_id, name, att_date, att_time)
               VALUES (attendance_seq.NEXTVAL, :1, :2, TRUNC(SYSDATE), :3)""",
            (user_id, name, now)
        )
        conn.commit()
        return True
    except oracledb.IntegrityError:
        conn.rollback()
        return False
    finally:
        cursor.close()
        conn.close()

TOLERANCE    = 0.50
FRAME_SCALE  = 0.5
COOLDOWN_SEC = 5
BOX_COLOR_OK  = (0, 200,   0)
BOX_COLOR_UNK = (0,   0, 200)
TEXT_COLOR    = (255, 255, 255)

def run_recognition() -> None:
    known_encodings, known_names, known_ids = load_known_faces()

    if not known_encodings:
        print("[WARN] No registered users found. Add users first with --add-user.")
        print("       The webcam will still open but no matches will be made.")

    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        print("[ERROR] Cannot open webcam.")
        raise SystemExit(1)

    print("\n[INFO] Webcam started. Press 'q' to quit.\n")

    last_seen: dict[int, datetime] = {}

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        small     = cv2.resize(frame, (0, 0), fx=FRAME_SCALE, fy=FRAME_SCALE)
        rgb_small = cv2.cvtColor(small, cv2.COLOR_BGR2RGB)

        locations = face_recognition.face_locations(rgb_small)
        encodings = face_recognition.face_encodings(rgb_small, locations)

        for enc, loc in zip(encodings, locations):
            top, right, bottom, left = [int(v / FRAME_SCALE) for v in loc]

            name    = "Unknown"
            user_id = None
            color   = BOX_COLOR_UNK

            if known_encodings:
                distances = face_recognition.face_distance(known_encodings, enc)
                best_idx  = int(np.argmin(distances))
                if distances[best_idx] <= TOLERANCE:
                    name    = known_names[best_idx]
                    user_id = known_ids[best_idx]
                    color   = BOX_COLOR_OK

            cv2.rectangle(frame, (left, top), (right, bottom), color, 2)

            label_y = bottom + 20
            cv2.rectangle(frame, (left, bottom), (right, label_y + 4), color, cv2.FILLED)
            cv2.putText(frame, name, (left + 4, label_y),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.55, TEXT_COLOR, 1)

            if user_id is not None:
                now  = datetime.now()
                last = last_seen.get(user_id)
                if last is None or (now - last).total_seconds() > COOLDOWN_SEC:
                    last_seen[user_id] = now
                    newly_marked = record_attendance(user_id, name)
                    if newly_marked:
                        print(f"[ATTENDANCE] ✓  {name}  —  {date.today().isoformat()}  {now.strftime('%H:%M:%S')}")
                    else:
                        print(f"[INFO] {name} already marked present today.")

        cv2.imshow("Face Recognition Attendance", frame)
        if cv2.waitKey(1) & 0xFF == ord("q"):
            break

    cap.release()
    cv2.destroyAllWindows()

def main() -> None:
    init_db()

    parser = argparse.ArgumentParser(description="Face Recognition Attendance System")
    parser.add_argument("--add-user", nargs=2, metavar=("NAME", "PHOTO"),
                        help="Register a new user: --add-user 'Full Name' photo.jpg")
    parser.add_argument("--list-users",      action="store_true",
                        help="List all registered users")
    parser.add_argument("--view-attendance", action="store_true",
                        help="Print all attendance records")
    args = parser.parse_args()

    if args.add_user:
        add_user(args.add_user[0], args.add_user[1])
    elif args.list_users:
        list_users()
    elif args.view_attendance:
        view_attendance()
    else:
        run_recognition()

if __name__ == "__main__":
    main()
