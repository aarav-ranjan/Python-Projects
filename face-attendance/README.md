# Face Recognition Attendance System

A Python application that uses a webcam to recognize faces and automatically logs attendance records into an Oracle database.

## Features

- Live webcam face detection and recognition
- Face encodings stored in Oracle DB (XEPDB1)
- Attendance logged by name, date, and time
- One record per person per day (no duplicates)
- CLI to register users, list users, and view attendance

## Requirements

- Python 3.10+
- Oracle Database XE (with XEPDB1)
- Webcam

## Installation

```bash
pip install -r requirements.txt
```

## Oracle Setup

Run the following in SQLPlus as AARAV1075:

```sql
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE attendance_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
    id          NUMBER        PRIMARY KEY,
    name        VARCHAR2(255) NOT NULL,
    photo       BLOB          NOT NULL,
    encoding    BLOB          NOT NULL
);

CREATE TABLE attendance (
    id          NUMBER        PRIMARY KEY,
    user_id     NUMBER        NOT NULL REFERENCES users(id),
    name        VARCHAR2(255) NOT NULL,
    att_date    DATE          NOT NULL,
    att_time    VARCHAR2(8)   NOT NULL,
    CONSTRAINT  one_per_day UNIQUE (user_id, att_date)
);
```

## Usage

**Run the attendance scanner:**
```bash
python attendance_system.py
```

**Register a new user:**
```bash
python attendance_system.py --add-user "Full Name" path/to/photo.jpg
```

**List all registered users:**
```bash
python attendance_system.py --list-users
```

**View attendance records:**
```bash
python attendance_system.py --view-attendance
```

## Notes

- Press `q` to quit the webcam window
- If `XEPDB1` doesn't connect, change the `dsn` in `DB_CONFIG` to `localhost/XE`
