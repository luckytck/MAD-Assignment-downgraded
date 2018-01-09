package my.edu.tarc.assignment.Model;

import java.util.Date;

/**
 * Created by Han on 1/8/2018.
 */

public class Voucher {
    private String voucherCode;
    private String voucherType;
    private double amount;
    private Date expiryDate;
    private String status;

    public Voucher() {
    }

    public Voucher(String voucherCode, String voucherType, double amount, Date expiryDate, String status) {
        this.voucherCode=voucherCode;
        this.voucherType=voucherType;
        this.amount=amount;
        this.expiryDate=expiryDate;
        this.status=status;
    }
    public void setVoucherCode(){
        this.voucherCode = voucherCode;
    }
    public String getVoucherCode(){
        return voucherCode;
    }
    public void setVoucherType(String voucherType){
        this.voucherType=voucherType;
    }
    public String getVoucherType(){
        return voucherType;
    }
    public void setAmount(double amount){
        this.amount=amount;
    }
    public double getAmount(){
        return amount;
    }
    public void setExpiryDate(Date expiryDate){
        this.expiryDate=expiryDate;
    }
    public Date getExpiryDate(){
        return expiryDate;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getStatus(){
        return status;
    }
    @Override
    public String toString() {
        return "Voucher{" +
                "voucherCode='" + voucherCode + '\'' +
                ", voucherType=" + voucherType +
                ", amount=" + amount +
                ", expiryDate=" + expiryDate +
                ", status=" + status +
                 '\'' +
                '}';
    }



}
