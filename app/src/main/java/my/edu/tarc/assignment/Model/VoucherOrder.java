package my.edu.tarc.assignment.Model;


import java.util.Date;

/**
 * Created by Han on 1/9/2018.
 */

public class VoucherOrder {
    private int id;
    private String voucherCode;
    private Date orderDate;
    private String username;

    public VoucherOrder(){}

    public VoucherOrder(int id,String voucherCode,Date orderDate,String username){
        this.id=id;
        this.voucherCode=voucherCode;
        this.orderDate=orderDate;
        this.username=username;
    }
    public VoucherOrder(int id,String voucherCode,String username){
        this.id=id;
        this.voucherCode=voucherCode;
         this.username=username;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
        }
        public void setVoucherCode(String voucherCode){
            this.voucherCode=voucherCode;
        }
        public String getVoucherCode(){
            return voucherCode;
        }
        public void setOrderDate(Date orderDate){
            this.orderDate=orderDate;
        }
    public Date getOrderDate(){
        return orderDate;
    }
    public void setUsername(){
        this.username=username;
    }
    public String getUsername(){
        return username;
    }

    @Override
    public String toString() {
        return "VoucherOrder{" +
                "id='" + id + '\'' +
                ", voucherCode=" + voucherCode +
                ", orderDate=" + orderDate +
                ", username=" + username +
                '\'' +
                '}';
    }
}
