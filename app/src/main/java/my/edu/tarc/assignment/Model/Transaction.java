package my.edu.tarc.assignment.Model;

import java.util.GregorianCalendar;

/**
 * Created by ken_0 on 7/1/2018.
 */

public class Transaction {
    private String imageMerchant;
    private String title;
    private double amount;
    private GregorianCalendar transactionDate;

    public Transaction() {
    }

    public Transaction(String imageMerchant, String title, double amount, GregorianCalendar transactionDate) {
        this.imageMerchant = imageMerchant;
        this.title = title;
        this.amount = amount;
        this.transactionDate = transactionDate;
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

    public GregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(GregorianCalendar transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "imageMerchant='" + imageMerchant + '\'' +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
