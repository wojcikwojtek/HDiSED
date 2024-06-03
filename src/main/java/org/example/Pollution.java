package org.example;

public class Pollution {
    private int no;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int pm25;
    private int dewp;
    private int temp;
    private int pres;
    private String cbwd;
    private double lws;
    private int ls;
    private int lr;

    public Pollution(int no, int year, int month, int day, int hour, int pm25, int dewp, int temp, int pres, String cbwd, double lws, int ls, int lr) {
        this.no = no;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.pm25 = pm25;
        this.dewp = dewp;
        this.temp = temp;
        this.pres = pres;
        this.cbwd = cbwd;
        this.lws = lws;
        this.ls = ls;
        this.lr = lr;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDewp() {
        return dewp;
    }

    public void setDewp(int dewp) {
        this.dewp = dewp;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getPres() {
        return pres;
    }

    public void setPres(int pres) {
        this.pres = pres;
    }

    public double getLws() {
        return lws;
    }

    public void setLws(double lws) {
        this.lws = lws;
    }

    public String getCbwd() {
        return cbwd;
    }

    public void setCbwd(String cbwd) {
        this.cbwd = cbwd;
    }

    public int getLs() {
        return ls;
    }

    public void setLs(int ls) {
        this.ls = ls;
    }

    public int getLr() {
        return lr;
    }

    public void setLr(int lr) {
        this.lr = lr;
    }
}
