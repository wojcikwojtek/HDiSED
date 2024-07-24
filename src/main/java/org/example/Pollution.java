package org.example;

public class Pollution {
    private int no;
    private Long year;
    private Long month;
    private Long day;
    private Long hour;
    private Long pm25;
    private Long dewp;
    private Long temp;
    private Long pres;
    private String cbwd;
    private double lws;
    private Long ls;
    private Long lr;

    public Pollution(int no, Long year, Long month, Long day, Long hour, Long pm25, Long dewp,
                     Long temp, Long pres, String cbwd, double lws, Long ls, Long lr) {
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

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Long getHour() {
        return hour;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    public Long getDewp() {
        return dewp;
    }

    public void setDewp(Long dewp) {
        this.dewp = dewp;
    }

    public Long getPm25() {
        return pm25;
    }

    public void setPm25(Long pm25) {
        this.pm25 = pm25;
    }

    public Long getTemp() {
        return temp;
    }

    public void setTemp(Long temp) {
        this.temp = temp;
    }

    public Long getPres() {
        return pres;
    }

    public void setPres(Long pres) {
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

    public Long getLs() {
        return ls;
    }

    public void setLs(Long ls) {
        this.ls = ls;
    }

    public Long getLr() {
        return lr;
    }

    public void setLr(Long lr) {
        this.lr = lr;
    }
}
