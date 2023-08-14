package com.example.bank;

import javax.persistence.*;

@Entity
@Table(name = "BankAccount")
public class BankAccount  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Column(nullable = false)
    private String owner;

    private double UAN;

    private double USD;

    private Double EUR;

    public BankAccount(String owner,double UAN,double USD, double EUR){
        this.owner = owner;
        this.UAN = UAN;
        this.USD = USD;
        this.EUR = EUR;
    }

    public BankAccount() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getEUR() {
        return EUR;
    }

    public void setEUR(Double EUR) {
        this.EUR = EUR;
    }

    public double getUAN() {
        return UAN;
    }

    public void setUAN(double UAN) {
        this.UAN = UAN;
    }

    public double getUSD() {
        return USD;
    }

    public void setUSD(double USD) {
        this.USD = USD;
    }


    @Override
    public String toString() {
        return "BankAccount{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", UAH=" + UAN +
                ", USD=" + USD +
                ", EUR=" + EUR +
                '}';
    }
}
