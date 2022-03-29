package com.affable.smartbills.Model;

import android.content.Context;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class InvoiceData implements Serializable {


    private String invoiceId, paymentMethod, orderTime, issueTime, currency;
    private Map<String, String> customerInfo;
    private Map<String, String> userInfo;
    private List<String[]> stringItemList;
    private double subTotal, tax, discount, totalMoney, dueMoney;

    public InvoiceData() {

    }

    public InvoiceData(String invoiceId, String paymentMethod, String orderTime, String issueTime, String currency, Map<String, String> customerInfo, Map<String, String> userInfo, List<String[]> stringItemList, double subTotal, double tax, double discount, double totalMoney, double dueMoney) {
        this.invoiceId = invoiceId;
        this.paymentMethod = paymentMethod;
        this.orderTime = orderTime;
        this.issueTime = issueTime;
        this.currency = currency;
        this.customerInfo = customerInfo;
        this.userInfo = userInfo;
        this.stringItemList = stringItemList;
        this.subTotal = subTotal;
        this.tax = tax;
        this.discount = discount;
        this.totalMoney = totalMoney;
        this.dueMoney = dueMoney;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, String> getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(Map<String, String> customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Map<String, String> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, String> userInfo) {
        this.userInfo = userInfo;
    }

    public List<String[]> getStringItemList() {
        return stringItemList;
    }

    public void setStringItemList(List<String[]> stringItemList) {
        this.stringItemList = stringItemList;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getDueMoney() {
        return dueMoney;
    }

    public void setDueMoney(double dueMoney) {
        this.dueMoney = dueMoney;
    }
}
