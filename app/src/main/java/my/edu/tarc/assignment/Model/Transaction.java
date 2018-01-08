package my.edu.tarc.assignment.Model;

import java.util.GregorianCalendar;

/**
 * Created by ken_0 on 7/1/2018.
 */

public class Transaction {
    private String imageMerchant;
    private String title;
    private double amount;
    private String username;

    public Transaction() {
    }

    public Transaction(String imageMerchant, String title, double amount, String username) {
        this.imageMerchant = imageMerchant;
        this.title = title;
        this.amount = amount;
        this.username = username;
    }

    public String getImageMerchant() {
        return imageMerchant;
    }

    public void setImageMerchant(String imageMerchant) {
        this.imageMerchant = imageMerchant;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "imageMerchant='" + imageMerchant + '\'' +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                '}';
    }
}
