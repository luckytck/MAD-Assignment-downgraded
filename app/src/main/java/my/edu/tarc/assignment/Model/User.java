package my.edu.tarc.assignment.Model;

/**
 * Created by ken_0 on 5/1/2018.
 */

public class User {
    private String username;
    private String password;
    private String name;
    private String phoneNo;
    private String email;
    private int pin;
    private double balance;

    public User() {
    }

    public User(String username, String password, String name, String phoneNo, String email, int pin, double balance) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", email='" + email + '\'' +
                ", pin=" + pin +
                ", balance=" + balance +
                '}';
    }
}
