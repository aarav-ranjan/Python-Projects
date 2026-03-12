import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BankManagement extends JFrame {

    static final Color C_BG       = new Color(15, 23, 42);
    static final Color C_SIDEBAR  = new Color(10, 16, 30);
    static final Color C_CARD     = new Color(22, 33, 58);
    static final Color C_CARD2    = new Color(30, 44, 74);
    static final Color C_ACCENT   = new Color(56, 189, 248);
    static final Color C_GREEN    = new Color(52, 211, 153);
    static final Color C_RED      = new Color(248, 113, 113);
    static final Color C_YELLOW   = new Color(251, 191, 36);
    static final Color C_PURPLE   = new Color(167, 139, 250);
    static final Color C_TEXT     = new Color(226, 232, 240);
    static final Color C_MUTED    = new Color(100, 116, 139);
    static final Color C_BORDER   = new Color(51, 65, 85);
    static final Color C_SEL      = new Color(56, 189, 248, 40);

    static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD, 26);
    static final Font F_HEAD   = new Font("Segoe UI", Font.BOLD, 14);
    static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_MONO   = new Font("Consolas",  Font.PLAIN, 13);
    static final Font F_NAV    = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font F_BIG    = new Font("Segoe UI", Font.BOLD, 28);

    static int nextCustomerId = 1001, nextAccountId = 2001, nextTxnId = 3001, nextLoanId = 4001;

    static class Customer {
        int id; String name, email, phone, address; String createdAt;
        Customer(String n, String e, String p, String a) {
            id = nextCustomerId++; name = n; email = e; phone = p; address = a;
            createdAt = now();
        }
    }

    static class Account {
        int id, customerId; String type, status; double balance; String createdAt;
        Account(int cid, String t, double bal) {
            id = nextAccountId++; customerId = cid; type = t; balance = bal; status = "Active";
            createdAt = now();
        }
    }

    static class Transaction {
        int id, accountId; String type, description; double amount, balanceAfter; String date;
        Transaction(int aid, String t, double amt, String desc, double balAfter) {
            id = nextTxnId++; accountId = aid; type = t; amount = amt;
            description = desc; balanceAfter = balAfter; date = now();
        }
    }

    static class Loan {
        int id, customerId; double amount, interest; int duration; String status, appliedAt;
        Loan(int cid, double amt, double rate, int dur) {
            id = nextLoanId++; customerId = cid; amount = amt; interest = rate;
            duration = dur; status = "Pending"; appliedAt = now();
        }
    }

    static class Employee {
        int id; String name, role, email, phone; double salary; String joinedAt;
        static int nextId = 5001;
        Employee(String n, String r, String e, String p, double s) {
            id = nextId++; name = n; role = r; email = e; phone = p; salary = s; joinedAt = now();
        }
    }

    static List<Customer>    customers    = new ArrayList<>();
    static List<Account>     accounts     = new ArrayList<>();
    static List<Transaction> transactions = new ArrayList<>();
    static List<Loan>        loans        = new ArrayList<>();
    static List<Employee>    employees    = new ArrayList<>();

    static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    static String today() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    JPanel contentArea;
    CardLayout cardLayout;
    JButton activeNavBtn = null;
    Map<String, JPanel> panels = new LinkedHashMap<>();

    public BankManagement() {
        setTitle("BankOS  •  Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1380, 820);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);

        seedData();

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    void seedData() {
        Customer c1 = new Customer("Arjun Sharma",   "arjun@email.com",  "9876543210", "Mumbai");
        Customer c2 = new Customer("Priya Patel",    "priya@email.com",  "9123456789", "Delhi");
        Customer c3 = new Customer("Ravi Kumar",     "ravi@email.com",   "9988776655", "Bangalore");
        customers.addAll(List.of(c1, c2, c3));

        Account a1 = new Account(c1.id, "Savings",  85000);
        Account a2 = new Account(c2.id, "Current",  250000);
        Account a3 = new Account(c3.id, "Savings",  42000);
        accounts.addAll(List.of(a1, a2, a3));

        transactions.add(new Transaction(a1.id, "Deposit",    85000, "Initial deposit",  85000));
        transactions.add(new Transaction(a2.id, "Deposit",   250000, "Initial deposit", 250000));
        transactions.add(new Transaction(a3.id, "Deposit",    42000, "Initial deposit",  42000));
        transactions.add(new Transaction(a1.id, "Withdrawal", 5000,  "ATM withdrawal",   80000));
        a1.balance = 80000;

        loans.add(new Loan(c1.id, 500000, 8.5, 24));
        loans.add(new Loan(c2.id, 1200000, 7.2, 36));

        employees.add(new Employee("Sunita Rao",    "Manager",          "sunita@bank.com",  "9001122334", 75000));
        employees.add(new Employee("Manoj Singh",   "Teller",           "manoj@bank.com",   "9005544332", 32000));
        employees.add(new Employee("Deepa Nair",    "Loan Officer",     "deepa@bank.com",   "9007766554", 48000));
    }

    JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(C_SIDEBAR);
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(220, 0));
        sb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, C_BORDER));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        brand.setBackground(new Color(8, 12, 24));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel logo = new JLabel("◈  BankOS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(C_ACCENT);
        brand.add(logo);
        sb.add(brand);
        sb.add(Box.createVerticalStrut(10));

        String[][] nav = {
            {"⊞", "Dashboard"},
            {"◉", "Customers"},
            {"▣", "Accounts"},
            {"⇄", "Transactions"},
            {"◈", "Loans"},
            {"◑", "Employees"},
            {"⊟", "Reports"}
        };
        for (String[] n : nav) sb.add(navBtn(n[0], n[1]));

        sb.add(Box.createVerticalGlue());
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 12));
        foot.setBackground(new Color(8, 12, 24));
        foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        JLabel v = new JLabel("v1.0  •  Pure Java Edition");
        v.setFont(F_SMALL); v.setForeground(C_MUTED);
        foot.add(v);
        sb.add(foot);
        return sb;
    }

    JButton navBtn(String icon, String label) {
        JButton b = new JButton(icon + "   " + label);
        b.setFont(F_NAV);
        b.setForeground(C_MUTED);
        b.setBackground(C_SIDEBAR);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (b != activeNavBtn) { b.setBackground(C_CARD); b.setForeground(C_TEXT); }
            }
            public void mouseExited(MouseEvent e) {
                if (b != activeNavBtn) { b.setBackground(C_SIDEBAR); b.setForeground(C_MUTED); }
            }
        });
        b.addActionListener(e -> {
            if (activeNavBtn != null) {
                activeNavBtn.setBackground(C_SIDEBAR);
                activeNavBtn.setForeground(C_MUTED);
            }
            activeNavBtn = b;
            b.setBackground(C_CARD2);
            b.setForeground(C_ACCENT);
            cardLayout.show(contentArea, label);
            refreshPanel(label);
        });
        return b;
    }

    JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(C_BG);

        String[] names = {"Dashboard","Customers","Accounts","Transactions","Loans","Employees","Reports"};
        for (String n : names) {
            JPanel p = buildPanel(n);
            panels.put(n, p);
            contentArea.add(p, n);
        }
        cardLayout.show(contentArea, "Dashboard");
        return contentArea;
    }

    JPanel buildPanel(String name) {
        return switch (name) {
            case "Dashboard"    -> dashboardPanel();
            case "Customers"    -> customersPanel();
            case "Accounts"     -> accountsPanel();
            case "Transactions" -> transactionsPanel();
            case "Loans"        -> loansPanel();
            case "Employees"    -> employeesPanel();
            case "Reports"      -> reportsPanel();
            default             -> new JPanel();
        };
    }

    void refreshPanel(String name) {
        JPanel old = panels.get(name);
        JPanel fresh = buildPanel(name);
        panels.put(name, fresh);
        contentArea.remove(old);
        contentArea.add(fresh, name);
        cardLayout.show(contentArea, name);
        contentArea.revalidate();
        contentArea.repaint();
    }

    JPanel dashboardPanel() {
        JPanel root = page();
        root.add(pageHeader("Dashboard", "Welcome back — here's your bank at a glance."));
        root.add(vgap(16));

        JPanel stats = new JPanel(new GridLayout(1, 5, 14, 0));
        stats.setBackground(C_BG);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        double totalFunds = accounts.stream().filter(a -> a.status.equals("Active")).mapToDouble(a -> a.balance).sum();
        long pendingLoans = loans.stream().filter(l -> l.status.equals("Pending")).count();
        stats.add(statCard("Customers",    String.valueOf(customers.size()),    C_ACCENT));
        stats.add(statCard("Accounts",     String.valueOf(accounts.size()),     C_PURPLE));
        stats.add(statCard("Total Funds",  "₹" + fmt(totalFunds),              C_GREEN));
        stats.add(statCard("Pending Loans",String.valueOf(pendingLoans),        C_YELLOW));
        stats.add(statCard("Employees",    String.valueOf(employees.size()),    new Color(251, 113, 133)));
        root.add(stats);
        root.add(vgap(16));

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(C_BG);

        String[] tc = {"Txn#", "Acc#", "Type", "Amount", "Date"};
        Object[][] td = transactions.stream().sorted((a,b)->b.id-a.id).limit(8)
            .map(t -> new Object[]{t.id, t.accountId, t.type, "₹"+fmt(t.amount), t.date})
            .toArray(Object[][]::new);
        row.add(tableCard("Recent Transactions", tc, td));

        String[] ac = {"Acc#", "Customer", "Type", "Balance", "Status"};
        Object[][] ad = accounts.stream().sorted((a,b)->b.id-a.id).limit(8)
            .map(a -> {
                String cname = customers.stream().filter(c->c.id==a.customerId).map(c->c.name).findFirst().orElse("?");
                return new Object[]{a.id, cname, a.type, "₹"+fmt(a.balance), a.status};
            }).toArray(Object[][]::new);
        row.add(tableCard("Recent Accounts", ac, ad));
        root.add(row);
        return root;
    }

    JPanel customersPanel() {
        JPanel root = page();
        root.add(pageHeader("Customers", "Manage customer profiles and registrations."));
        root.add(vgap(16));

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(C_BG);

        JPanel form = card("Add New Customer");
        JTextField nameF    = field("e.g. Rahul Gupta");
        JTextField emailF   = field("e.g. rahul@email.com");
        JTextField phoneF   = field("e.g. 9876543210");
        JTextField addrF    = field("e.g. Chennai, Tamil Nadu");
        form.add(formRow("Full Name *",  nameF));
        form.add(formRow("Email *",      emailF));
        form.add(formRow("Phone *",      phoneF));
        form.add(formRow("Address",      addrF));
        form.add(vgap(10));
        JButton addBtn = btn("Add Customer", C_ACCENT, C_BG);
        addBtn.addActionListener(e -> {
            if (nameF.getText().isBlank() || emailF.getText().isBlank() || phoneF.getText().isBlank()) {
                err("Name, email and phone are required."); return;
            }
            customers.add(new Customer(nameF.getText().trim(), emailF.getText().trim(),
                                       phoneF.getText().trim(), addrF.getText().trim()));
            nameF.setText(""); emailF.setText(""); phoneF.setText(""); addrF.setText("");
            ok("Customer added successfully."); refreshPanel("Customers");
        });
        form.add(btnRow(addBtn));
        row.add(form);

        String[] cols = {"ID","Name","Email","Phone","Address","Joined"};
        DefaultTableModel model = tableModel(cols);
        customers.forEach(c -> model.addRow(new Object[]{c.id, c.name, c.email, c.phone, c.address, c.createdAt.substring(0,10)}));
        JTable table = styledTable(model);
        JPanel tc = tableCard2("All Customers", table);
        JButton del = btn("Delete Selected", C_RED, Color.WHITE);
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { err("Select a customer first."); return; }
            int id = (int) model.getValueAt(r, 0);
            if (confirm("Delete customer #" + id + "?")) {
                customers.removeIf(c -> c.id == id);
                refreshPanel("Customers");
            }
        });
        tc.add(btnRow(del));
        row.add(tc);
        root.add(row);
        return root;
    }

    JPanel accountsPanel() {
        JPanel root = page();
        root.add(pageHeader("Accounts", "Open accounts and manage their status."));
        root.add(vgap(16));

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(C_BG);

        JPanel form = card("Open New Account");
        JTextField cidF  = field("Customer ID (e.g. 1001)");
        JComboBox<String> typeC = combo("Savings", "Current", "Fixed Deposit", "Recurring Deposit");
        JTextField balF  = field("Initial deposit (₹)");
        form.add(formRow("Customer ID *", cidF));
        form.add(formRow("Account Type *", typeC));
        form.add(formRow("Initial Balance", balF));
        form.add(vgap(8));
        JButton openBtn = btn("Open Account", C_ACCENT, C_BG);
        openBtn.addActionListener(e -> {
            try {
                int cid = Integer.parseInt(cidF.getText().trim());
                boolean exists = customers.stream().anyMatch(c -> c.id == cid);
                if (!exists) { err("Customer ID " + cid + " not found."); return; }
                double bal = balF.getText().isBlank() ? 0 : Double.parseDouble(balF.getText().trim());
                Account acc = new Account(cid, (String) typeC.getSelectedItem(), bal);
                accounts.add(acc);
                if (bal > 0) transactions.add(new Transaction(acc.id, "Deposit", bal, "Initial deposit", bal));
                cidF.setText(""); balF.setText("");
                ok("Account #" + acc.id + " opened."); refreshPanel("Accounts");
            } catch (NumberFormatException ex) { err("Invalid number."); }
        });
        form.add(btnRow(openBtn));

        form.add(vgap(14));
        JLabel sep = new JLabel("─── Update Status ───────────────────");
        sep.setFont(F_SMALL); sep.setForeground(C_MUTED);
        form.add(sep);
        form.add(vgap(8));
        JTextField accIdF2 = field("Account ID");
        JComboBox<String> statC = combo("Active", "Frozen", "Closed");
        form.add(formRow("Account ID *", accIdF2));
        form.add(formRow("New Status *", statC));
        JButton updBtn = btn("Update Status", C_YELLOW, C_BG);
        updBtn.addActionListener(e -> {
            try {
                int aid = Integer.parseInt(accIdF2.getText().trim());
                Account a = accounts.stream().filter(ac -> ac.id == aid).findFirst().orElse(null);
                if (a == null) { err("Account not found."); return; }
                a.status = (String) statC.getSelectedItem();
                accIdF2.setText("");
                ok("Status updated to " + a.status); refreshPanel("Accounts");
            } catch (NumberFormatException ex) { err("Invalid ID."); }
        });
        form.add(btnRow(updBtn));
        row.add(form);

        String[] cols = {"Acc#","Customer","Type","Balance","Status","Opened"};
        DefaultTableModel model = tableModel(cols);
        accounts.forEach(a -> {
            String cn = customers.stream().filter(c->c.id==a.customerId).map(c->c.name).findFirst().orElse("?");
            model.addRow(new Object[]{a.id, cn, a.type, "₹"+fmt(a.balance), a.status, a.createdAt.substring(0,10)});
        });
        row.add(tableCard("All Accounts", cols, model));
        root.add(row);
        return root;
    }

    JPanel transactionsPanel() {
        JPanel root = page();
        root.add(pageHeader("Transactions", "Deposit, withdraw and transfer funds."));
        root.add(vgap(16));

        JPanel top = new JPanel(new GridLayout(1, 3, 14, 0));
        top.setBackground(C_BG);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel depCard = card("Deposit");
        JTextField dAccF = field("Account ID"); JTextField dAmtF = field("Amount (₹)");
        JButton depBtn = btn("Deposit", C_GREEN, C_BG);
        depBtn.addActionListener(e -> {
            try {
                Account a = findAccount(Integer.parseInt(dAccF.getText().trim()));
                if (a == null) { err("Account not found."); return; }
                if (!a.status.equals("Active")) { err("Account is " + a.status + "."); return; }
                double amt = Double.parseDouble(dAmtF.getText().trim());
                if (amt <= 0) { err("Amount must be positive."); return; }
                a.balance += amt;
                transactions.add(new Transaction(a.id, "Deposit", amt, "Cash deposit", a.balance));
                dAccF.setText(""); dAmtF.setText("");
                ok("₹" + fmt(amt) + " deposited successfully."); refreshPanel("Transactions");
            } catch (NumberFormatException ex) { err("Invalid input."); }
        });
        depCard.add(formRow("Account ID", dAccF)); depCard.add(formRow("Amount", dAmtF));
        depCard.add(vgap(8)); depCard.add(btnRow(depBtn));
        top.add(depCard);

        JPanel wdCard = card("Withdraw");
        JTextField wAccF = field("Account ID"); JTextField wAmtF = field("Amount (₹)");
        JButton wdBtn = btn("Withdraw", C_RED, Color.WHITE);
        wdBtn.addActionListener(e -> {
            try {
                Account a = findAccount(Integer.parseInt(wAccF.getText().trim()));
                if (a == null) { err("Account not found."); return; }
                if (!a.status.equals("Active")) { err("Account is " + a.status + "."); return; }
                double amt = Double.parseDouble(wAmtF.getText().trim());
                if (amt <= 0) { err("Amount must be positive."); return; }
                if (a.balance < amt) { err("Insufficient funds. Balance: ₹" + fmt(a.balance)); return; }
                a.balance -= amt;
                transactions.add(new Transaction(a.id, "Withdrawal", amt, "Cash withdrawal", a.balance));
                wAccF.setText(""); wAmtF.setText("");
                ok("₹" + fmt(amt) + " withdrawn successfully."); refreshPanel("Transactions");
            } catch (NumberFormatException ex) { err("Invalid input."); }
        });
        wdCard.add(formRow("Account ID", wAccF)); wdCard.add(formRow("Amount", wAmtF));
        wdCard.add(vgap(8)); wdCard.add(btnRow(wdBtn));
        top.add(wdCard);

        JPanel trCard = card("Fund Transfer");
        JTextField fFromF = field("From Account ID"); JTextField fToF = field("To Account ID");
        JTextField fAmtF  = field("Amount (₹)");
        JButton trBtn = btn("Transfer", C_ACCENT, C_BG);
        trBtn.addActionListener(e -> {
            try {
                Account from = findAccount(Integer.parseInt(fFromF.getText().trim()));
                Account to   = findAccount(Integer.parseInt(fToF.getText().trim()));
                if (from == null || to == null) { err("One or both accounts not found."); return; }
                if (!from.status.equals("Active")) { err("Source account is " + from.status + "."); return; }
                if (!to.status.equals("Active"))   { err("Dest account is " + to.status + "."); return; }
                double amt = Double.parseDouble(fAmtF.getText().trim());
                if (amt <= 0) { err("Amount must be positive."); return; }
                if (from.balance < amt) { err("Insufficient funds."); return; }
                from.balance -= amt; to.balance += amt;
                transactions.add(new Transaction(from.id, "Transfer Out", amt, "To Acc#"+to.id, from.balance));
                transactions.add(new Transaction(to.id,   "Transfer In",  amt, "From Acc#"+from.id, to.balance));
                fFromF.setText(""); fToF.setText(""); fAmtF.setText("");
                ok("Transfer successful."); refreshPanel("Transactions");
            } catch (NumberFormatException ex) { err("Invalid input."); }
        });
        trCard.add(formRow("From Account", fFromF)); trCard.add(formRow("To Account", fToF));
        trCard.add(formRow("Amount", fAmtF)); trCard.add(vgap(8)); trCard.add(btnRow(trBtn));
        top.add(trCard);

        root.add(top);
        root.add(vgap(14));

        String[] cols = {"Txn#","Acc#","Type","Amount","Balance After","Description","Date"};
        Object[][] data = transactions.stream().sorted((a,b)->b.id-a.id)
            .map(t -> new Object[]{t.id, t.accountId, t.type,
                "₹"+fmt(t.amount), "₹"+fmt(t.balanceAfter), t.description, t.date})
            .toArray(Object[][]::new);
        root.add(tableCard("Transaction History", cols, data));
        return root;
    }

    JPanel loansPanel() {
        JPanel root = page();
        root.add(pageHeader("Loans", "Apply for loans, calculate EMI and manage approvals."));
        root.add(vgap(16));

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(C_BG);

        JPanel form = card("Apply for Loan");
        JTextField lcidF  = field("Customer ID");
        JTextField lamtF  = field("Loan amount (₹)");
        JTextField lratF  = field("Annual interest rate (%)");
        JTextField ldurF  = field("Duration (months)");
        form.add(formRow("Customer ID *",  lcidF));
        form.add(formRow("Loan Amount *",  lamtF));
        form.add(formRow("Interest Rate *",lratF));
        form.add(formRow("Duration (mo)*", ldurF));
        form.add(vgap(8));
        JButton applyBtn = btn("Apply Loan", C_ACCENT, C_BG);
        applyBtn.addActionListener(e -> {
            try {
                int cid = Integer.parseInt(lcidF.getText().trim());
                if (customers.stream().noneMatch(c->c.id==cid)) { err("Customer not found."); return; }
                double amt  = Double.parseDouble(lamtF.getText().trim());
                double rate = Double.parseDouble(lratF.getText().trim());
                int dur     = Integer.parseInt(ldurF.getText().trim());
                Loan l = new Loan(cid, amt, rate, dur);
                loans.add(l);
                lcidF.setText(""); lamtF.setText(""); lratF.setText(""); ldurF.setText("");
                ok("Loan #" + l.id + " submitted. Status: Pending"); refreshPanel("Loans");
            } catch (NumberFormatException ex) { err("Invalid input."); }
        });
        form.add(btnRow(applyBtn));

        form.add(vgap(14));
        JLabel sep = new JLabel("─── EMI Calculator ──────────────────");
        sep.setFont(F_SMALL); sep.setForeground(C_MUTED);
        form.add(sep); form.add(vgap(8));
        JTextField epF = field("Principal (₹)"), erF = field("Rate (%)"), enF = field("Months");
        JLabel emiLbl = new JLabel("EMI:  —");
        emiLbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); emiLbl.setForeground(C_GREEN);
        JButton emiBtn = btn("Calculate", C_PURPLE, Color.WHITE);
        emiBtn.addActionListener(e -> {
            try {
                double p = Double.parseDouble(epF.getText().trim());
                double r = Double.parseDouble(erF.getText().trim()) / 12 / 100;
                int n    = Integer.parseInt(enF.getText().trim());
                double emi = r == 0 ? p/n : (p*r*Math.pow(1+r,n))/(Math.pow(1+r,n)-1);
                emiLbl.setText("EMI:  ₹" + fmt(emi) + " / month");
            } catch (NumberFormatException ex) { emiLbl.setText("Invalid input"); }
        });
        form.add(formRow("Principal", epF)); form.add(formRow("Rate %", erF));
        form.add(formRow("Months", enF));
        JPanel er = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        er.setBackground(C_CARD); er.add(emiBtn); er.add(Box.createHorizontalStrut(12)); er.add(emiLbl);
        form.add(er);
        row.add(form);

        String[] cols = {"Loan#","Customer","Amount","Rate","Duration","Status","Applied"};
        DefaultTableModel model = tableModel(cols);
        loans.stream().sorted((a,b)->b.id-a.id).forEach(l -> {
            String cn = customers.stream().filter(c->c.id==l.customerId).map(c->c.name).findFirst().orElse("?");
            model.addRow(new Object[]{l.id, cn, "₹"+fmt(l.amount), l.interest+"%", l.duration+" mo", l.status, l.appliedAt.substring(0,10)});
        });
        JTable table = styledTable(model);
        JPanel lc = tableCard2("All Loans", table);

        JPanel updateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        updateRow.setBackground(C_CARD);
        JTextField lidF = field("Loan ID"); lidF.setPreferredSize(new Dimension(100, 34));
        JComboBox<String> lstatC = combo("Approved","Rejected","Closed");
        JButton lupdBtn = btn("Update Status", C_YELLOW, C_BG);
        lupdBtn.addActionListener(e -> {
            try {
                int lid = Integer.parseInt(lidF.getText().trim());
                Loan l = loans.stream().filter(x->x.id==lid).findFirst().orElse(null);
                if (l == null) { err("Loan not found."); return; }
                l.status = (String) lstatC.getSelectedItem();
                lidF.setText("");
                ok("Loan #" + lid + " status → " + l.status); refreshPanel("Loans");
            } catch (NumberFormatException ex) { err("Invalid ID."); }
        });
        updateRow.add(new JLabel("Loan ID:") {{ setForeground(C_MUTED); setFont(F_BODY); }});
        updateRow.add(lidF); updateRow.add(lstatC); updateRow.add(lupdBtn);
        lc.add(updateRow);
        row.add(lc);
        root.add(row);
        return root;
    }

    JPanel employeesPanel() {
        JPanel root = page();
        root.add(pageHeader("Employees", "Manage bank staff and their roles."));
        root.add(vgap(16));

        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(C_BG);

        JPanel form = card("Add New Employee");
        JTextField enameF  = field("Full name");
        JTextField eemailF = field("Email");
        JTextField ephoneF = field("Phone");
        JTextField esalF   = field("Monthly salary (₹)");
        JComboBox<String> eroleC = combo("Manager","Teller","Loan Officer","IT Staff","Security","HR","Accountant","Customer Service");
        form.add(formRow("Full Name *", enameF));
        form.add(formRow("Role *",      eroleC));
        form.add(formRow("Email *",     eemailF));
        form.add(formRow("Phone",       ephoneF));
        form.add(formRow("Salary *",    esalF));
        form.add(vgap(10));
        JButton addEmp = btn("Add Employee", C_ACCENT, C_BG);
        addEmp.addActionListener(e -> {
            if (enameF.getText().isBlank() || eemailF.getText().isBlank() || esalF.getText().isBlank()) {
                err("Name, email and salary required."); return;
            }
            try {
                double sal = Double.parseDouble(esalF.getText().trim());
                employees.add(new Employee(enameF.getText().trim(), (String)eroleC.getSelectedItem(),
                    eemailF.getText().trim(), ephoneF.getText().trim(), sal));
                enameF.setText(""); eemailF.setText(""); ephoneF.setText(""); esalF.setText("");
                ok("Employee added."); refreshPanel("Employees");
            } catch (NumberFormatException ex) { err("Invalid salary."); }
        });
        form.add(btnRow(addEmp));
        row.add(form);

        String[] cols = {"ID","Name","Role","Email","Phone","Salary","Joined"};
        DefaultTableModel model = tableModel(cols);
        employees.forEach(e -> model.addRow(new Object[]{e.id, e.name, e.role, e.email, e.phone, "₹"+fmt(e.salary), e.joinedAt.substring(0,10)}));
        JTable table = styledTable(model);
        JPanel ec = tableCard2("All Employees", table);
        JButton delEmp = btn("Remove Selected", C_RED, Color.WHITE);
        delEmp.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { err("Select an employee."); return; }
            int id = (int) model.getValueAt(r, 0);
            if (confirm("Remove employee #" + id + "?")) {
                employees.removeIf(x -> x.id == id);
                refreshPanel("Employees");
            }
        });
        ec.add(btnRow(delEmp));
        row.add(ec);
        root.add(row);
        return root;
    }

    JPanel reportsPanel() {
        JPanel root = page();
        root.add(pageHeader("Reports", "Analytics and financial overview."));
        root.add(vgap(16));

        double totalBal    = accounts.stream().filter(a->a.status.equals("Active")).mapToDouble(a->a.balance).sum();
        double totalDeposit= transactions.stream().filter(t->t.type.equals("Deposit")).mapToDouble(t->t.amount).sum();
        double totalWithdr = transactions.stream().filter(t->t.type.equals("Withdrawal")).mapToDouble(t->t.amount).sum();
        double totalLoaned = loans.stream().filter(l->l.status.equals("Approved")).mapToDouble(l->l.amount).sum();
        double totalPayroll= employees.stream().mapToDouble(e->e.salary).sum();

        JPanel statsRow = new JPanel(new GridLayout(1, 5, 14, 0));
        statsRow.setBackground(C_BG);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.add(statCard("Active Funds",   "₹"+fmt(totalBal),     C_GREEN));
        statsRow.add(statCard("Total Deposits", "₹"+fmt(totalDeposit), C_ACCENT));
        statsRow.add(statCard("Total Withdrawn","₹"+fmt(totalWithdr),  C_RED));
        statsRow.add(statCard("Loans Approved", "₹"+fmt(totalLoaned),  C_YELLOW));
        statsRow.add(statCard("Monthly Payroll","₹"+fmt(totalPayroll), C_PURPLE));
        root.add(statsRow);
        root.add(vgap(16));

        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 14, 0));
        tablesRow.setBackground(C_BG);

        Map<String,Long> accByType = new LinkedHashMap<>();
        Map<String,Double> balByType = new LinkedHashMap<>();
        accounts.forEach(a -> {
            accByType.merge(a.type, 1L, Long::sum);
            balByType.merge(a.type, a.balance, Double::sum);
        });
        String[] ac = {"Account Type","Count","Total Balance"};
        Object[][] ad = accByType.entrySet().stream()
            .map(en -> new Object[]{en.getKey(), en.getValue(), "₹"+fmt(balByType.getOrDefault(en.getKey(),0.0))})
            .toArray(Object[][]::new);
        tablesRow.add(tableCard("Accounts by Type", ac, ad));

        Map<String,Long> txnByType = new LinkedHashMap<>();
        Map<String,Double> txnAmt  = new LinkedHashMap<>();
        transactions.forEach(t -> {
            txnByType.merge(t.type, 1L, Long::sum);
            txnAmt.merge(t.type, t.amount, Double::sum);
        });
        String[] tc = {"Txn Type","Count","Total"};
        Object[][] td = txnByType.entrySet().stream()
            .map(en -> new Object[]{en.getKey(), en.getValue(), "₹"+fmt(txnAmt.getOrDefault(en.getKey(),0.0))})
            .toArray(Object[][]::new);
        tablesRow.add(tableCard("Transactions by Type", tc, td));
        root.add(tablesRow);
        root.add(vgap(14));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 14, 0));
        row2.setBackground(C_BG);
        Map<String,Long> loanByStat = new LinkedHashMap<>();
        loans.forEach(l -> loanByStat.merge(l.status, 1L, Long::sum));
        String[] lc = {"Status","Count"};
        Object[][] ld = loanByStat.entrySet().stream()
            .map(en -> new Object[]{en.getKey(), en.getValue()}).toArray(Object[][]::new);
        row2.add(tableCard("Loans by Status", lc, ld));

        Map<String,Long> empByRole = new LinkedHashMap<>();
        employees.forEach(e -> empByRole.merge(e.role, 1L, Long::sum));
        String[] ec = {"Role","Count"};
        Object[][] ed = empByRole.entrySet().stream()
            .map(en -> new Object[]{en.getKey(), en.getValue()}).toArray(Object[][]::new);
        row2.add(tableCard("Staff by Role", ec, ed));
        root.add(row2);
        return root;
    }

    Account findAccount(int id) {
        return accounts.stream().filter(a -> a.id == id).findFirst().orElse(null);
    }

    String fmt(double v) {
        if (v == (long) v) return String.format("%,d", (long) v);
        return String.format("%,.2f", v);
    }

    JPanel page() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));
        return p;
    }

    JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(C_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel t = new JLabel(title);
        t.setFont(F_TITLE); t.setForeground(C_TEXT);
        JLabel s = new JLabel(subtitle);
        s.setFont(F_BODY); s.setForeground(C_MUTED);
        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    JPanel statCard(String label, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, color),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel v = new JLabel(value); v.setFont(F_BIG); v.setForeground(color);
        JLabel l = new JLabel(label); l.setFont(F_SMALL); l.setForeground(C_MUTED);
        p.add(v, BorderLayout.CENTER);
        p.add(l, BorderLayout.SOUTH);
        return p;
    }

    JPanel card(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER, 1),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        if (title != null && !title.isBlank()) {
            JLabel t = new JLabel(title); t.setFont(F_HEAD); t.setForeground(C_ACCENT);
            t.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
            t.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(t);
        }
        return p;
    }

    JPanel tableCard(String title, String[] cols, Object[][] data) {
        DefaultTableModel m = tableModel(cols);
        for (Object[] row : data) m.addRow(row);
        return tableCard2(title, styledTable(m));
    }

    JPanel tableCard(String title, String[] cols, DefaultTableModel model) {
        return tableCard2(title, styledTable(model));
    }

    JPanel tableCard2(String title, JTable table) {
        JPanel p = card(title);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane sp = new JScrollPane(table);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setBorder(BorderFactory.createLineBorder(C_BORDER));
        sp.getViewport().setBackground(C_CARD);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.add(sp);
        return p;
    }

    DefaultTableModel tableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(F_BODY);
        t.setForeground(C_TEXT);
        t.setBackground(C_CARD);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        t.setSelectionBackground(C_SEL);
        t.setSelectionForeground(C_TEXT);
        t.setFillsViewportHeight(true);
        JTableHeader th = t.getTableHeader();
        th.setFont(F_HEAD);
        th.setBackground(C_CARD2);
        th.setForeground(C_ACCENT);
        th.setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_BORDER));
        th.setPreferredSize(new Dimension(th.getWidth(), 36));
        th.setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setFont(F_BODY);
                setForeground(sel ? C_TEXT : C_TEXT);
                setBackground(sel ? C_SEL : row%2==0 ? C_CARD : C_CARD2);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return t;
    }

    JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(F_BODY);
        f.setForeground(C_TEXT);
        f.setBackground(C_BG);
        f.setCaretColor(C_ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(210, 34));
        f.setToolTipText(placeholder);
        return f;
    }

    JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(F_BODY);
        cb.setBackground(C_BG);
        cb.setForeground(C_TEXT);
        cb.setPreferredSize(new Dimension(210, 34));
        return cb;
    }

    JPanel formRow(String label, JComponent comp) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        r.setBackground(C_CARD);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel l = new JLabel(label); l.setFont(F_BODY); l.setForeground(C_MUTED);
        l.setPreferredSize(new Dimension(130, 30));
        r.add(l); r.add(comp);
        return r;
    }

    JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(F_HEAD);
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 36));
        b.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });
        return b;
    }

    JPanel btnRow(JButton... btns) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        r.setBackground(C_CARD);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        for (JButton b : btns) r.add(b);
        return r;
    }

    Component vgap(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    void err(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    void ok(String msg)  { JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new BankManagement().setVisible(true));
    }
}
