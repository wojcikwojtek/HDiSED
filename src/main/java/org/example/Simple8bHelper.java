package org.example;

public class Simple8bHelper {
    private long[] yearTab = new long[60];
    private int yearCounter = 0;
    private int maxYearBits = 1;
    private long[] yearTabOutput = new long[60];
    private long[] monthTab = new long[60];
    private int monthCounter = 0;
    private int maxMonthBits = 1;
    private long[] monthTabOutput = new long[60];
    private long[] dayTab = new long[60];
    private int dayCounter = 0;
    private int maxDayBits = 1;
    private long[] dayTabOutput = new long[60];
    private long[] hourTab = new long[60];
    private int hourCounter = 0;
    private int maxHourBits = 1;
    private long[] hourTabOutput = new long[60];
    private long[] pm25Tab = new long[60];
    private int pm25Counter = 0;
    private int maxPm25Bits = 1;
    private long[] pm25TabOutput = new long[60];
    private long[] dewpTab = new long[60];
    private int dewpCounter = 0;
    private int maxDewpBits = 1;
    private long[] dewpTabOutput = new long[60];
    private long[] tempTab = new long[60];
    private int tempCounter = 0;
    private int maxTempBits = 1;
    private long[] tempTabOutput = new long[60];
    private long[] presTab = new long[60];
    private int presCounter = 0;
    private int maxPresBits = 1;
    private long[] presTabOutput = new long[60];
    private long[] cbwdTab = new long[60];
    private int cbwdCounter = 0;
    private int maxCbwdBits = 1;
    private long[] cbwdTabOutput = new long[60];
    private long[] lsTab = new long[60];
    private int lsCounter = 0;
    private int maxLsBits = 1;
    private long[] lsTabOutput = new long[60];
    private long[] lrTab = new long[60];
    private int lrCounter = 0;
    private int maxLrBits = 1;
    private long[] lrTabOutput = new long[60];

    public long[] getYearTab() {
        return yearTab;
    }

    public void setYearTab(long[] yearTab) {
        this.yearTab = yearTab;
    }

    public int getYearCounter() {
        return yearCounter;
    }

    public void setYearCounter(int yearCounter) {
        this.yearCounter = yearCounter;
    }

    public int getMaxYearBits() {
        return maxYearBits;
    }

    public void setMaxYearBits(int maxYearBits) {
        this.maxYearBits = maxYearBits;
    }

    public long[] getYearTabOutput() {
        return yearTabOutput;
    }

    public void setYearTabOutput(long[] yearTabOutput) {
        this.yearTabOutput = yearTabOutput;
    }

    public long[] getMonthTab() {
        return monthTab;
    }

    public void setMonthTab(long[] monthTab) {
        this.monthTab = monthTab;
    }

    public int getMonthCounter() {
        return monthCounter;
    }

    public void setMonthCounter(int monthCounter) {
        this.monthCounter = monthCounter;
    }

    public int getMaxMonthBits() {
        return maxMonthBits;
    }

    public void setMaxMonthBits(int maxMonthBits) {
        this.maxMonthBits = maxMonthBits;
    }

    public long[] getMonthTabOutput() {
        return monthTabOutput;
    }

    public void setMonthTabOutput(long[] monthTabOutput) {
        this.monthTabOutput = monthTabOutput;
    }

    public long[] getDayTab() {
        return dayTab;
    }

    public void setDayTab(long[] dayTab) {
        this.dayTab = dayTab;
    }

    public int getDayCounter() {
        return dayCounter;
    }

    public void setDayCounter(int dayCounter) {
        this.dayCounter = dayCounter;
    }

    public int getMaxDayBits() {
        return maxDayBits;
    }

    public void setMaxDayBits(int maxDayBits) {
        this.maxDayBits = maxDayBits;
    }

    public long[] getDayTabOutput() {
        return dayTabOutput;
    }

    public void setDayTabOutput(long[] dayTabOutput) {
        this.dayTabOutput = dayTabOutput;
    }

    public long[] getHourTab() {
        return hourTab;
    }

    public void setHourTab(long[] hourTab) {
        this.hourTab = hourTab;
    }

    public int getHourCounter() {
        return hourCounter;
    }

    public void setHourCounter(int hourCounter) {
        this.hourCounter = hourCounter;
    }

    public int getMaxHourBits() {
        return maxHourBits;
    }

    public void setMaxHourBits(int maxHourBits) {
        this.maxHourBits = maxHourBits;
    }

    public long[] getHourTabOutput() {
        return hourTabOutput;
    }

    public void setHourTabOutput(long[] hourTabOutput) {
        this.hourTabOutput = hourTabOutput;
    }

    public long[] getPm25Tab() {
        return pm25Tab;
    }

    public void setPm25Tab(long[] pm25Tab) {
        this.pm25Tab = pm25Tab;
    }

    public int getPm25Counter() {
        return pm25Counter;
    }

    public void setPm25Counter(int pm25Counter) {
        this.pm25Counter = pm25Counter;
    }

    public int getMaxPm25Bits() {
        return maxPm25Bits;
    }

    public void setMaxPm25Bits(int maxPm25Bits) {
        this.maxPm25Bits = maxPm25Bits;
    }

    public long[] getPm25TabOutput() {
        return pm25TabOutput;
    }

    public void setPm25TabOutput(long[] pm25TabOutput) {
        this.pm25TabOutput = pm25TabOutput;
    }

    public long[] getDewpTab() {
        return dewpTab;
    }

    public void setDewpTab(long[] dewpTab) {
        this.dewpTab = dewpTab;
    }

    public int getDewpCounter() {
        return dewpCounter;
    }

    public void setDewpCounter(int dewpCounter) {
        this.dewpCounter = dewpCounter;
    }

    public int getMaxDewpBits() {
        return maxDewpBits;
    }

    public void setMaxDewpBits(int maxDewpBits) {
        this.maxDewpBits = maxDewpBits;
    }

    public long[] getDewpTabOutput() {
        return dewpTabOutput;
    }

    public void setDewpTabOutput(long[] dewpTabOutput) {
        this.dewpTabOutput = dewpTabOutput;
    }

    public long[] getTempTab() {
        return tempTab;
    }

    public void setTempTab(long[] tempTab) {
        this.tempTab = tempTab;
    }

    public int getTempCounter() {
        return tempCounter;
    }

    public void setTempCounter(int tempCounter) {
        this.tempCounter = tempCounter;
    }

    public int getMaxTempBits() {
        return maxTempBits;
    }

    public void setMaxTempBits(int maxTempBits) {
        this.maxTempBits = maxTempBits;
    }

    public long[] getTempTabOutput() {
        return tempTabOutput;
    }

    public void setTempTabOutput(long[] tempTabOutput) {
        this.tempTabOutput = tempTabOutput;
    }

    public long[] getPresTab() {
        return presTab;
    }

    public void setPresTab(long[] presTab) {
        this.presTab = presTab;
    }

    public int getPresCounter() {
        return presCounter;
    }

    public void setPresCounter(int presCounter) {
        this.presCounter = presCounter;
    }

    public int getMaxPresBits() {
        return maxPresBits;
    }

    public void setMaxPresBits(int maxPresBits) {
        this.maxPresBits = maxPresBits;
    }

    public long[] getPresTabOutput() {
        return presTabOutput;
    }

    public void setPresTabOutput(long[] presTabOutput) {
        this.presTabOutput = presTabOutput;
    }

    public long[] getCbwdTab() {
        return cbwdTab;
    }

    public void setCbwdTab(long[] cbwdTab) {
        this.cbwdTab = cbwdTab;
    }

    public int getCbwdCounter() {
        return cbwdCounter;
    }

    public void setCbwdCounter(int cbwdCounter) {
        this.cbwdCounter = cbwdCounter;
    }

    public int getMaxCbwdBits() {
        return maxCbwdBits;
    }

    public void setMaxCbwdBits(int maxCbwdBits) {
        this.maxCbwdBits = maxCbwdBits;
    }

    public long[] getCbwdTabOutput() {
        return cbwdTabOutput;
    }

    public void setCbwdTabOutput(long[] cbwdTabOutput) {
        this.cbwdTabOutput = cbwdTabOutput;
    }

    public long[] getLsTab() {
        return lsTab;
    }

    public void setLsTab(long[] lsTab) {
        this.lsTab = lsTab;
    }

    public long[] getLrTab() {
        return lrTab;
    }

    public void setLrTab(long[] lrTab) {
        this.lrTab = lrTab;
    }

    public int getLsCounter() {
        return lsCounter;
    }

    public void setLsCounter(int lsCounter) {
        this.lsCounter = lsCounter;
    }

    public int getMaxLsBits() {
        return maxLsBits;
    }

    public void setMaxLsBits(int maxLsBits) {
        this.maxLsBits = maxLsBits;
    }

    public long[] getLsTabOutput() {
        return lsTabOutput;
    }

    public void setLsTabOutput(long[] lsTabOutput) {
        this.lsTabOutput = lsTabOutput;
    }

    public int getLrCounter() {
        return lrCounter;
    }

    public void setLrCounter(int lrCounter) {
        this.lrCounter = lrCounter;
    }

    public int getMaxLrBits() {
        return maxLrBits;
    }

    public void setMaxLrBits(int maxLrBits) {
        this.maxLrBits = maxLrBits;
    }

    public long[] getLrTabOutput() {
        return lrTabOutput;
    }

    public void setLrTabOutput(long[] lrTabOutput) {
        this.lrTabOutput = lrTabOutput;
    }
}
