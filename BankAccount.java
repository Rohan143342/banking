import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private double balance;
    private List<String> history;

    public BankAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 0.0;
        this.history = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<String> getHistory() { return history; }
    public void addHistory(String entry) { history.add(0, entry); }
} 