import java.io.*;
import java.util.*;

public class BankingApp {
    private static final String DATA_FILE = "users.dat";
    private static HashMap<String, BankAccount> users = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static BankAccount currentUser = null;

    public static void main(String[] args) {
        loadUsers();
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showBankMenu();
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println("\n=== Online Banking CLI ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": register(); break;
            case "2": login(); break;
            case "3": System.exit(0); break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void showBankMenu() {
        System.out.println("\n=== Welcome, " + currentUser.getUsername() + " ===");
        System.out.println("Balance: $" + String.format("%.2f", currentUser.getBalance()));
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Send Money");
        System.out.println("4. Transaction History");
        System.out.println("5. Change Password");
        System.out.println("6. Delete Account");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": deposit(); break;
            case "2": withdraw(); break;
            case "3": sendMoney(); break;
            case "4": showHistory(); break;
            case "5": changePassword(); break;
            case "6": deleteAccount(); break;
            case "7": logout(); break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        users.put(username, new BankAccount(username, password));
        saveUsers();
        System.out.println("Registration successful!");
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        BankAccount acc = users.get(username);
        if (acc == null || !acc.getPassword().equals(password)) {
            System.out.println("Invalid credentials.");
            return;
        }
        currentUser = acc;
        System.out.println("Login successful!");
    }

    private static void deposit() {
        System.out.print("Enter deposit amount: ");
        double amt = readDouble();
        if (amt > 0) {
            currentUser.setBalance(currentUser.getBalance() + amt);
            currentUser.addHistory("Deposited $" + String.format("%.2f", amt) + " [" + new Date() + "]");
            saveUsers();
            System.out.println("Deposited successfully!");
        } else {
            System.out.println("Invalid amount.");
        }
    }

    private static void withdraw() {
        System.out.print("Enter withdraw amount: ");
        double amt = readDouble();
        if (amt > 0 && amt <= currentUser.getBalance()) {
            currentUser.setBalance(currentUser.getBalance() - amt);
            currentUser.addHistory("Withdrew $" + String.format("%.2f", amt) + " [" + new Date() + "]");
            saveUsers();
            System.out.println("Withdrawn successfully!");
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    private static void sendMoney() {
        System.out.print("Enter recipient username: ");
        String recipient = scanner.nextLine().trim();
        if (!users.containsKey(recipient)) {
            System.out.println("Recipient does not exist.");
            return;
        }
        if (recipient.equals(currentUser.getUsername())) {
            System.out.println("Cannot send money to yourself.");
            return;
        }
        System.out.print("Enter amount to send: ");
        double amt = readDouble();
        if (amt <= 0 || amt > currentUser.getBalance()) {
            System.out.println("Insufficient balance or invalid amount.");
            return;
        }
        BankAccount recAcc = users.get(recipient);
        currentUser.setBalance(currentUser.getBalance() - amt);
        recAcc.setBalance(recAcc.getBalance() + amt);
        currentUser.addHistory("Sent $" + String.format("%.2f", amt) + " to " + recipient + " [" + new Date() + "]");
        recAcc.addHistory("Received $" + String.format("%.2f", amt) + " from " + currentUser.getUsername() + " [" + new Date() + "]");
        saveUsers();
        System.out.println("Sent $" + String.format("%.2f", amt) + " to " + recipient + ".");
    }

    private static void showHistory() {
        System.out.println("\n--- Transaction History ---");
        List<String> hist = currentUser.getHistory();
        if (hist.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            hist.stream().limit(10).forEach(System.out::println);
        }
    }

    private static void changePassword() {
        System.out.print("Enter old password: ");
        String oldPass = scanner.nextLine();
        if (!currentUser.getPassword().equals(oldPass)) {
            System.out.println("Old password is incorrect.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = scanner.nextLine();
        currentUser.setPassword(newPass);
        saveUsers();
        System.out.println("Password changed!");
    }

    private static void deleteAccount() {
        System.out.print("Are you sure you want to delete your account? (y/n): ");
        String ans = scanner.nextLine();
        if (ans.equalsIgnoreCase("y")) {
            users.remove(currentUser.getUsername());
            saveUsers();
            currentUser = null;
            System.out.println("Account deleted.");
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    private static double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (HashMap<String, BankAccount>) ois.readObject();
        } catch (Exception e) {
            users = new HashMap<>();
        }
    }

    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            System.out.println("[Warning] Could not save users: " + e.getMessage());
        }
    }
} 