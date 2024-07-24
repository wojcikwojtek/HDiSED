package org.example;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PollutionController {
    private DatabaseController dbController;
    private Simple8b simple8b;
    private int leadingZeros = 0;
    private int bitsInTheSubsequence = 0;
    private Map<Integer, Integer> simple8bMap = Stream.of(new Integer[][] {
            { 1, 60 },
            { 2, 30 },
            { 3, 20 },
            { 4, 15 },
            { 5, 12 },
            { 6, 10 },
            { 7, 8 },
            { 8, 7 },
            { 10, 6 },
            { 12, 5 },
            { 15, 4 },
            { 20, 3 },
            { 30, 2 },
            { 60, 1 },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    private Map<String, String> directionsMap = Map.of(
            "1", "SE",
            "2", "cv",
            "3", "NE",
            "4", "NW"
    );

    public PollutionController() {
        dbController = new DatabaseController();
        simple8b = new Simple8b();
    }

    public List<Pollution> readCSV(String filename) {
        List<Pollution> pollutionRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if(tokens[0].equals("No")) { continue; }
                Long pm25;
                if(tokens[5].equals("NA")) {
                    pm25 = 100L;
                } else {
                    pm25 = Long.parseLong(tokens[5]);
                }
                pollutionRecords.add(new Pollution(
                        Integer.parseInt(tokens[0]),
                        Long.parseLong(tokens[1]),
                        Long.parseLong(tokens[2]),
                        Long.parseLong(tokens[3]),
                        Long.parseLong(tokens[4]),
                        pm25,
                        Long.parseLong(tokens[6]),
                        Long.parseLong(tokens[7]),
                        Long.parseLong(tokens[8]),
                        tokens[9],
                        Float.parseFloat(tokens[10]),
                        Long.parseLong(tokens[11]),
                        Long.parseLong(tokens[12])
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pollutionRecords;
    }

    public void insertToDB(List<Pollution> pollutionRecords) {
        dbController.createTable();
        for(Pollution p : pollutionRecords) {
            dbController.insertData(p);
        }
    }

    public void insertToEncodedDB(List<Pollution> pollutionRecords) {
        dbController.createEncodedTable();
        for(Pollution p : pollutionRecords) {
            dbController.insertEncodedData(p);
        }
    }

    private double xorCompression(double Lws1, double Lws2) {
        long xorResult = Double.doubleToLongBits(Lws1) ^ Double.doubleToLongBits(Lws2);
        if(xorResult == 0) {
            return 0;
        }
        char[] xorBits = Long.toBinaryString(xorResult).toCharArray();
        String resultBits = "";
        int xorLeadingZeros = 64 - xorBits.length;
        int xorBitsInTheSubsequence = 0;
        for(int i = xorBits.length  - 1; i >= 0; i--) {
            if(xorBits[i] == '1') {
                xorBitsInTheSubsequence = i;
                break;
            }
        }
        if(leadingZeros + bitsInTheSubsequence < xorLeadingZeros + xorBitsInTheSubsequence && bitsInTheSubsequence < xorBitsInTheSubsequence) {
            resultBits += "11" + String.format("%5s", Integer.toBinaryString(xorLeadingZeros)).replace(' ', '0')
                    + String.format("%6s", Integer.toBinaryString(xorBitsInTheSubsequence)).replace(' ', '0');
            for(int i = 0; i <= xorBitsInTheSubsequence; i++) {
                resultBits += xorBits[i];
            }
            this.leadingZeros = xorLeadingZeros;
            this.bitsInTheSubsequence = xorBitsInTheSubsequence;
        } else {
            resultBits += "10";
            for(int i = 0; i < xorLeadingZeros - leadingZeros ; i++) {
                resultBits += "0";
            }
            for(int i = 0; i <= bitsInTheSubsequence - (xorLeadingZeros - leadingZeros); i++) {
                resultBits += xorBits[i];
            }
        }
        return Double.longBitsToDouble(Long.parseLong(resultBits, 2));
    }

    private double xorDecompression(double Lws1, double Lws2) {
        if(Lws1 == 0) return Lws2;
        String lws1Bits = Long.toBinaryString(Double.doubleToLongBits(Lws1));
        String xorBits = "";
        int index = 2;
        if(lws1Bits.substring(0, 2).equals("11")) {
            leadingZeros = Integer.parseInt(lws1Bits.substring(2, 7), 2);
            bitsInTheSubsequence = Integer.parseInt(lws1Bits.substring(7, 13), 2);
            index = 13;
        }
        for(int i = 0; i < leadingZeros; i++) {
            xorBits += '0';
        }
        for(int i = 0; i <= bitsInTheSubsequence; i++) {
            xorBits += lws1Bits.charAt(index);
            index++;
        }
        for(int i = xorBits.length(); i < 64; i++) {
            xorBits += '0';
        }
        long resultBits = Long.parseLong(xorBits, 2) ^ Double.doubleToLongBits(Lws2);
        double notRoundedResult = Double.longBitsToDouble(resultBits);
        BigDecimal decimal = new BigDecimal(notRoundedResult).setScale(2, BigDecimal.ROUND_HALF_UP);
        return decimal.doubleValue();
    }

    private String dictionaryCompression(String cbwd) {
        int key = 1;
        for(int i = 0; i < 4; i++) {
            if(directionsMap.get(key + "").equals(cbwd)) break;
            key++;
        }
        return key + "";
    }

    private List<Pollution> deltaEncoding(List<Pollution> pollutionRecords) {
        List<Pollution> deltaRecords = new ArrayList<>();
        deltaRecords.add(new Pollution(
                pollutionRecords.get(0).getNo(),
                pollutionRecords.get(0).getYear(),
                pollutionRecords.get(0).getMonth(),
                pollutionRecords.get(0).getDay(),
                pollutionRecords.get(0).getHour(),
                pollutionRecords.get(0).getPm25(),
                pollutionRecords.get(0).getDewp(),
                pollutionRecords.get(0).getTemp(),
                pollutionRecords.get(0).getPres(),
                dictionaryCompression(pollutionRecords.get(0).getCbwd()),
                pollutionRecords.get(0).getLws(),
                pollutionRecords.get(0).getLs(),
                pollutionRecords.get(0).getLr()
        ));
        for (int i = 1; i < pollutionRecords.size(); i++) {
            deltaRecords.add(new Pollution(
                    pollutionRecords.get(i).getNo(),
                    pollutionRecords.get(i).getYear() - deltaRecords.get(0).getYear(),
                    pollutionRecords.get(i).getMonth() - deltaRecords.get(0).getMonth(),
                    pollutionRecords.get(i).getDay() - deltaRecords.get(0).getDay(),
                    pollutionRecords.get(i).getHour() - deltaRecords.get(0).getHour(),
                    pollutionRecords.get(i).getPm25() - deltaRecords.get(0).getPm25(),
                    pollutionRecords.get(i).getDewp() - deltaRecords.get(0).getDewp(),
                    pollutionRecords.get(i).getTemp() - deltaRecords.get(0).getTemp(),
                    pollutionRecords.get(i).getPres() - deltaRecords.get(0).getPres(),
                    dictionaryCompression(pollutionRecords.get(i).getCbwd()),
                    xorCompression(pollutionRecords.get(i).getLws(), pollutionRecords.get(i-1).getLws()),
                    pollutionRecords.get(i).getLs() - deltaRecords.get(0).getLs(),
                    pollutionRecords.get(i).getLr() - deltaRecords.get(0).getLr()
            ));
        }
        return deltaRecords;
    }

    private List<Pollution> deltaOfDeltaEncoding(List<Pollution> pollutionRecords) {
        if(pollutionRecords.size() < 3) return pollutionRecords;
        List<Pollution> deltaOfDeltaRecords = new ArrayList<>();
        deltaOfDeltaRecords.add(pollutionRecords.get(0));
        deltaOfDeltaRecords.add(pollutionRecords.get(1));
        for(int i = 2; i < pollutionRecords.size(); i++) {
            deltaOfDeltaRecords.add(new Pollution(
                    pollutionRecords.get(i).getNo(),
                    pollutionRecords.get(i).getYear() - deltaOfDeltaRecords.get(1).getYear(),
                    pollutionRecords.get(i).getMonth() - deltaOfDeltaRecords.get(1).getMonth(),
                    pollutionRecords.get(i).getDay() - deltaOfDeltaRecords.get(1).getDay(),
                    pollutionRecords.get(i).getHour() - deltaOfDeltaRecords.get(1).getHour(),
                    pollutionRecords.get(i).getPm25() - deltaOfDeltaRecords.get(1).getPm25(),
                    pollutionRecords.get(i).getDewp() - deltaOfDeltaRecords.get(1).getDewp(),
                    pollutionRecords.get(i).getTemp() - deltaOfDeltaRecords.get(1).getTemp(),
                    pollutionRecords.get(i).getPres() - deltaOfDeltaRecords.get(1).getPres(),
                    pollutionRecords.get(i).getCbwd(),
                    pollutionRecords.get(i).getLws(),
                    pollutionRecords.get(i).getLs() - deltaOfDeltaRecords.get(1).getLs(),
                    pollutionRecords.get(i).getLr() - deltaOfDeltaRecords.get(1).getLr()
            ));
        }
        return deltaOfDeltaRecords;
    }

    private int getSimple8bMapValue(int key) {
        switch (key) {
            case 9:
                return simple8bMap.get(10);
            case 11:
                return simple8bMap.get(12);
            case 13, 14:
                return simple8bMap.get(15);
            case 16, 17, 18, 19:
                return simple8bMap.get(20);
            case 21, 22, 23, 24, 25, 26, 27, 28, 29:
                return simple8bMap.get(30);
            case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                 41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
                 51, 52, 53, 54, 55, 56, 57, 58, 59:
                return simple8bMap.get(60);
            default:
                return simple8bMap.get(key);
        }
    }

    private void simple8BCompressYear(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getYearCounter() == 0) return;
        simple8b.compress(helper.getYearTab(), 0, helper.getYearCounter(), helper.getYearTabOutput(), 0);
        helper.setYearCounter(0);
        helper.setMaxYearBits(1);
        helper.setYearTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getYearTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setYear(helper.getYearTabOutput()[j]);
        }
        helper.setYearTabOutput(new long[60]);
    }

    private void simple8BCompressMonth(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getMonthCounter() == 0) return;
        simple8b.compress(helper.getMonthTab(), 0, helper.getMonthCounter(), helper.getMonthTabOutput(), 0);
        helper.setMonthCounter(0);
        helper.setMaxMonthBits(1);
        helper.setMonthTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getMonthTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setMonth(helper.getMonthTabOutput()[j]);
        }
        helper.setMonthTabOutput(new long[60]);
    }

    private void simple8BCompressDay(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getDayCounter() == 0) return;
        simple8b.compress(helper.getDayTab(), 0, helper.getDayCounter(), helper.getDayTabOutput(), 0);
        helper.setDayCounter(0);
        helper.setMaxDayBits(1);
        helper.setDayTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getDayTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setDay(helper.getDayTabOutput()[j]);
        }
        helper.setDayTabOutput(new long[60]);
    }

    private void simple8BCompressHour(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getHourCounter() == 0) return;
        simple8b.compress(helper.getHourTab(), 0, helper.getHourCounter(), helper.getHourTabOutput(), 0);
        helper.setHourCounter(0);
        helper.setMaxHourBits(1);
        helper.setHourTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getHourTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setHour(helper.getHourTabOutput()[j]);
        }
        helper.setHourTabOutput(new long[60]);
    }

    private void simple8BCompressPm25(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getPm25Counter() == 0) return;
        simple8b.compress(helper.getPm25Tab(), 0, helper.getPm25Counter(), helper.getPm25TabOutput(), 0);
        helper.setPm25Counter(0);
        helper.setMaxPm25Bits(1);
        helper.setPm25Tab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getPm25TabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setPm25(helper.getPm25TabOutput()[j]);
        }
        helper.setPm25TabOutput(new long[60]);
    }

    private void simple8BCompressDewp(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getDewpCounter() == 0) return;
        simple8b.compress(helper.getDewpTab(), 0, helper.getDewpCounter(), helper.getDewpTabOutput(), 0);
        helper.setDewpCounter(0);
        helper.setMaxDewpBits(1);
        helper.setDewpTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getDewpTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setDewp(helper.getDewpTabOutput()[j]);
        }
        helper.setDewpTabOutput(new long[60]);
    }

    private void simple8BCompressTemp(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getTempCounter() == 0) return;
        simple8b.compress(helper.getTempTab(), 0, helper.getTempCounter(), helper.getTempTabOutput(), 0);
        helper.setTempCounter(0);
        helper.setMaxTempBits(1);
        helper.setTempTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getTempTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setTemp(helper.getTempTabOutput()[j]);
        }
        helper.setTempTabOutput(new long[60]);
    }

    private void simple8BCompressPres(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getPresCounter() == 0) return;
        simple8b.compress(helper.getPresTab(), 0, helper.getPresCounter(), helper.getPresTabOutput(), 0);
        helper.setPresCounter(0);
        helper.setMaxPresBits(1);
        helper.setPresTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getPresTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setPres(helper.getPresTabOutput()[j]);
        }
        helper.setPresTabOutput(new long[60]);
    }

    private void simple8BCompressCbwd(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getCbwdCounter() == 0) return;
        simple8b.compress(helper.getCbwdTab(), 0, helper.getCbwdCounter(), helper.getCbwdTabOutput(), 0);
        helper.setCbwdCounter(0);
        helper.setMaxCbwdBits(1);
        helper.setCbwdTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getCbwdTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setCbwd(helper.getCbwdTabOutput()[j] + "");
        }
        helper.setCbwdTabOutput(new long[60]);
    }

    private void simple8BCompressLs(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getLsCounter() == 0) return;
        simple8b.compress(helper.getLsTab(), 0, helper.getLsCounter(), helper.getLsTabOutput(), 0);
        helper.setLsCounter(0);
        helper.setMaxLsBits(1);
        helper.setLsTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getLsTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setLs(helper.getLsTabOutput()[j]);
        }
        helper.setLsTabOutput(new long[60]);
    }

    private void simple8BCompressLr(List<Pollution> simple8Records, int i, Simple8bHelper helper) {
        if(helper.getLrCounter() == 0) return;
        simple8b.compress(helper.getLrTab(), 0, helper.getLrCounter(), helper.getLrTabOutput(), 0);
        helper.setLrCounter(0);
        helper.setMaxLrBits(1);
        helper.setLrTab(new long[60]);
        int nonzerovalues = 0;
        for(int j = 0; j < 60; j++) {
            if(helper.getLrTabOutput()[j] == 0) break;
            nonzerovalues++;
        }
        for(int j = 0; j < nonzerovalues; j++) {
            simple8Records.get(i-(nonzerovalues - j - 1)).setLr(helper.getLrTabOutput()[j]);
        }
        helper.setLrTabOutput(new long[60]);
    }

    private List<Pollution> simple8BEncoding(List<Pollution> pollutionRecords) {
        List<Pollution> simple8Records = new ArrayList<>();
        simple8Records.add(pollutionRecords.get(0));
        simple8Records.add(pollutionRecords.get(1));
        Simple8bHelper helper = new Simple8bHelper();
        int newMaxYearBits = 1, newMaxMonthBits = 1, newMaxDayBits = 1, newMaxHourBits = 1, newMaxPm25Bits = 1,
                newMaxDewpBits = 1, newMaxTempBits = 1, newMaxPresBits = 1, newMaxCbwdBits = 1, newMaxLsBits = 1,
                newMaxLrBits = 1;
        for (int i = 2; i <= pollutionRecords.size(); i++) {
            if(i == pollutionRecords.size()) {
                simple8BCompressYear(simple8Records, i-1, helper);
                simple8BCompressMonth(simple8Records, i-1, helper);
                simple8BCompressDay(simple8Records, i-1, helper);
                simple8BCompressHour(simple8Records, i-1, helper);
                simple8BCompressPm25(simple8Records, i-1, helper);
                if (helper.getDewpCounter() == 1) {
                    simple8Records.get(i - 1).setDewp(helper.getDewpTab()[0]);
                    helper.setDewpTab(new long[60]);
                    helper.setDewpCounter(0);
                    helper.setMaxDewpBits(1);
                } else {
                    simple8BCompressDewp(simple8Records, i - 1, helper);
                }
                simple8BCompressTemp(simple8Records, i-1, helper);
                simple8BCompressPres(simple8Records, i-1, helper);
                simple8BCompressCbwd(simple8Records, i-1, helper);
                simple8BCompressLs(simple8Records, i-1, helper);
                simple8BCompressLr(simple8Records, i-1, helper);
                break;
            }
            simple8Records.add(pollutionRecords.get(i));
            if(pollutionRecords.get(i).getYear() >= 0) {
                helper.getYearTab()[helper.getYearCounter()] = pollutionRecords.get(i).getYear();
                helper.setYearCounter(helper.getYearCounter() + 1);
                newMaxYearBits = simple8b.bits(pollutionRecords.get(i).getYear());
                simple8Records.get(i).setYear(null);
            } else {
                simple8BCompressYear(simple8Records, i-1, helper);
            }
            if(pollutionRecords.get(i).getMonth() >= 0) {
                helper.getMonthTab()[helper.getMonthCounter()] = pollutionRecords.get(i).getMonth();
                helper.setMonthCounter(helper.getMonthCounter() + 1);
                newMaxMonthBits = simple8b.bits(pollutionRecords.get(i).getMonth());
                simple8Records.get(i).setMonth(null);
            } else {
                simple8BCompressMonth(simple8Records, i-1, helper);
            }
            if(pollutionRecords.get(i).getDay() >= 0) {
                helper.getDayTab()[helper.getDayCounter()] = pollutionRecords.get(i).getDay();
                helper.setDayCounter(helper.getDayCounter() + 1);
                newMaxDayBits = simple8b.bits(pollutionRecords.get(i).getDay());
                simple8Records.get(i).setDay(null);
            } else {
                simple8BCompressDay(simple8Records, i-1, helper);
            }
            if(pollutionRecords.get(i).getHour() >= 0) {
                helper.getHourTab()[helper.getHourCounter()] = pollutionRecords.get(i).getHour();
                helper.setHourCounter(helper.getHourCounter() + 1);
                newMaxHourBits = simple8b.bits(pollutionRecords.get(i).getHour());
                simple8Records.get(i).setHour(null);
            } else {
                simple8BCompressHour(simple8Records, i-1, helper);
            }
            if(pollutionRecords.get(i).getPm25() >= 0) {
                helper.getPm25Tab()[helper.getPm25Counter()] = pollutionRecords.get(i).getPm25();
                helper.setPm25Counter(helper.getPm25Counter() + 1);
                newMaxPm25Bits = simple8b.bits(pollutionRecords.get(i).getPm25());
                simple8Records.get(i).setPm25(null);
            } else {
                if(helper.getPm25Counter() == 1) {
                    simple8Records.get(i-1).setPm25(helper.getPm25Tab()[0]);
                    helper.setPm25Tab(new long[60]);
                    helper.setPm25Counter(0);
                    helper.setMaxPm25Bits(1);
                } else {
                    simple8BCompressPm25(simple8Records, i - 1, helper);
                }
            }
            if(pollutionRecords.get(i).getDewp() >= 0) {
                helper.getDewpTab()[helper.getDewpCounter()] = pollutionRecords.get(i).getDewp();
                helper.setDewpCounter(helper.getDewpCounter() + 1);
                newMaxDewpBits = simple8b.bits(pollutionRecords.get(i).getDewp());
                simple8Records.get(i).setDewp(null);
            } else {
                if (helper.getDewpCounter() == 1) {
                    simple8Records.get(i - 1).setDewp(helper.getDewpTab()[0]);
                    helper.setDewpTab(new long[60]);
                    helper.setDewpCounter(0);
                    helper.setMaxDewpBits(1);
                } else {
                    simple8BCompressDewp(simple8Records, i - 1, helper);
                }
            }
            if(pollutionRecords.get(i).getTemp() >= 0) {
                helper.getTempTab()[helper.getTempCounter()] = pollutionRecords.get(i).getTemp();
                helper.setTempCounter(helper.getTempCounter() + 1);
                newMaxTempBits = simple8b.bits(pollutionRecords.get(i).getTemp());
                simple8Records.get(i).setTemp(null);
            } else {
                if (helper.getTempCounter() == 1) {
                    simple8Records.get(i - 1).setTemp(helper.getTempTab()[0]);
                    helper.setTempTab(new long[60]);
                    helper.setTempCounter(0);
                    helper.setMaxTempBits(1);
                } else {
                    simple8BCompressTemp(simple8Records, i - 1, helper);
                }
            }
            if(pollutionRecords.get(i).getPres() >= 0) {
                helper.getPresTab()[helper.getPresCounter()] = pollutionRecords.get(i).getPres();
                helper.setPresCounter(helper.getPresCounter() + 1);
                newMaxPresBits = simple8b.bits(pollutionRecords.get(i).getPres());
                simple8Records.get(i).setPres(null);
            } else {
                if (helper.getPresCounter() == 1) {
                    simple8Records.get(i - 1).setPres(helper.getPresTab()[0]);
                    helper.setPresTab(new long[60]);
                    helper.setPresCounter(0);
                    helper.setMaxPresBits(1);
                } else {
                    simple8BCompressPres(simple8Records, i - 1, helper);
                }
            }
            helper.getCbwdTab()[helper.getCbwdCounter()] = Long.parseLong(pollutionRecords.get(i).getCbwd());
            helper.setCbwdCounter(helper.getCbwdCounter() + 1);
            newMaxCbwdBits = simple8b.bits(Long.parseLong(pollutionRecords.get(i).getCbwd()));
            simple8Records.get(i).setCbwd(null);
            if(pollutionRecords.get(i).getLs() >= 0) {
                helper.getLsTab()[helper.getLsCounter()] = pollutionRecords.get(i).getLs();
                helper.setLsCounter(helper.getLsCounter() + 1);
                newMaxLsBits = simple8b.bits(pollutionRecords.get(i).getLs());
                simple8Records.get(i).setLs(null);
            } else {
                simple8BCompressLs(simple8Records, i-1, helper);
            }
            if(pollutionRecords.get(i).getLr() >= 0) {
                helper.getLrTab()[helper.getLrCounter()] = pollutionRecords.get(i).getLr();
                helper.setLrCounter(helper.getLrCounter() + 1);
                newMaxLrBits = simple8b.bits(pollutionRecords.get(i).getLr());
                simple8Records.get(i).setLr(null);
            } else {
                simple8BCompressLr(simple8Records, i-1, helper);
            }

            if(newMaxYearBits > helper.getMaxYearBits()) {
                if(getSimple8bMapValue(newMaxYearBits) < helper.getYearCounter()) {
                    simple8BCompressYear(simple8Records, i, helper);
                } else {
                    helper.setMaxYearBits(newMaxYearBits);
                }
            }
            else if(helper.getYearCounter() >= getSimple8bMapValue(helper.getMaxYearBits())) {
                simple8BCompressYear(simple8Records, i, helper);
            }
            if(newMaxMonthBits > helper.getMaxMonthBits()) {
                if(getSimple8bMapValue(newMaxMonthBits) < helper.getMonthCounter()) {
                    simple8BCompressMonth(simple8Records, i, helper);
                } else {
                    helper.setMaxMonthBits(newMaxMonthBits);
                }
            }
            else if(helper.getMonthCounter() >= getSimple8bMapValue(helper.getMaxMonthBits())) {
                simple8BCompressMonth(simple8Records, i, helper);
            }
            if(newMaxDayBits > helper.getMaxDayBits()) {
                if(getSimple8bMapValue(newMaxDayBits) < helper.getDayCounter()) {
                    simple8BCompressDay(simple8Records, i, helper);
                } else {
                    helper.setMaxDayBits(newMaxDayBits);
                }
            }
            else if(helper.getDayCounter() >= getSimple8bMapValue(helper.getMaxDayBits())) {
                simple8BCompressDay(simple8Records, i, helper);
            }
            if(newMaxHourBits > helper.getMaxHourBits()) {
                if(getSimple8bMapValue(newMaxHourBits) < helper.getHourCounter()) {
                    simple8BCompressHour(simple8Records, i, helper);
                } else {
                    helper.setMaxHourBits(newMaxHourBits);
                }
            }
            else if(helper.getHourCounter() >= getSimple8bMapValue(helper.getMaxHourBits())) {
                simple8BCompressHour(simple8Records, i, helper);
            }
            if(newMaxPm25Bits > helper.getMaxPm25Bits()) {
                if(getSimple8bMapValue(newMaxPm25Bits) < helper.getPm25Counter()) {
                    simple8BCompressPm25(simple8Records, i, helper);
                } else {
                    helper.setMaxPm25Bits(newMaxPm25Bits);
                }
            }
            else if(helper.getPm25Counter() >= getSimple8bMapValue(helper.getMaxPm25Bits())) {
                simple8BCompressPm25(simple8Records, i, helper);
            }
            if(newMaxDewpBits > helper.getMaxDewpBits()) {
                if(getSimple8bMapValue(newMaxDewpBits) < helper.getDewpCounter()) {
                    simple8BCompressDewp(simple8Records, i, helper);
                } else {
                    helper.setMaxDewpBits(newMaxDewpBits);
                }
            }
            else if(helper.getDewpCounter() >= getSimple8bMapValue(helper.getMaxDewpBits())) {
                simple8BCompressDewp(simple8Records, i, helper);
            }
            if(newMaxTempBits > helper.getMaxTempBits()) {
                if(getSimple8bMapValue(newMaxTempBits) < helper.getTempCounter()) {
                    simple8BCompressTemp(simple8Records, i, helper);
                } else {
                    helper.setMaxTempBits(newMaxTempBits);
                }
            }
            else if(helper.getTempCounter() >= getSimple8bMapValue(helper.getMaxTempBits())) {
                simple8BCompressTemp(simple8Records, i, helper);
            }
            if(newMaxPresBits > helper.getMaxPresBits()) {
                if(getSimple8bMapValue(newMaxPresBits) < helper.getPresCounter()) {
                    simple8BCompressPres(simple8Records, i, helper);
                } else {
                    helper.setMaxPresBits(newMaxPresBits);
                }
            }
            else if(helper.getPresCounter() >= getSimple8bMapValue(helper.getMaxPresBits())) {
                simple8BCompressPres(simple8Records, i, helper);
            }
            if(newMaxCbwdBits > helper.getMaxCbwdBits()) {
                if(getSimple8bMapValue(newMaxCbwdBits) < helper.getCbwdCounter()) {
                    simple8BCompressCbwd(simple8Records, i, helper);
                } else {
                    helper.setMaxCbwdBits(newMaxCbwdBits);
                }
            }
            else if(helper.getCbwdCounter() >= getSimple8bMapValue(helper.getMaxCbwdBits())) {
                simple8BCompressCbwd(simple8Records, i, helper);
            }
            if(newMaxLsBits > helper.getMaxLsBits()) {
                if(getSimple8bMapValue(newMaxLsBits) < helper.getLsCounter()) {
                    simple8BCompressLs(simple8Records, i, helper);
                } else {
                    helper.setMaxLsBits(newMaxLsBits);
                }
            }
            else if(helper.getLsCounter() >= getSimple8bMapValue(helper.getMaxLsBits())) {
                simple8BCompressLs(simple8Records, i, helper);
            }
            if(newMaxLrBits > helper.getMaxLrBits()) {
                if(getSimple8bMapValue(newMaxLrBits) < helper.getLrCounter()) {
                    simple8BCompressLr(simple8Records, i, helper);
                } else {
                    helper.setMaxLrBits(newMaxLrBits);
                }
            }
            else if(helper.getLrCounter() >= getSimple8bMapValue(helper.getMaxLrBits())) {
                simple8BCompressLr(simple8Records, i, helper);
            }
        }
        return simple8Records;
    }

    private void decodeYear(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getYear() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getYear() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getYear();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setYear(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeMonth(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getMonth() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getMonth() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getMonth();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setMonth(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeDay(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getDay() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getDay() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getDay();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setDay(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeHour(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getHour() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getHour() == null
                        || encodedRecords.get(k).getHour() == -1) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getHour();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setHour(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodePm25(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getPm25() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getPm25() == null
                        || (encodedRecords.get(k).getPm25() < 0 && encodedRecords.get(k).getPm25() > -2000)
                        || (encodedRecords.get(k).getPm25() > 0 && encodedRecords.get(k).getPm25() < 2000)) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getPm25();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setPm25(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeDewp(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getDewp() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getDewp() == null
                        || (encodedRecords.get(k).getDewp() < 0 && encodedRecords.get(k).getDewp() > -2000)
                        || (encodedRecords.get(k).getDewp() > 0 && encodedRecords.get(k).getDewp() < 2000)) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getDewp();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setDewp(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeTemp(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getTemp() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getTemp() == null
                        || (encodedRecords.get(k).getTemp() < 0 && encodedRecords.get(k).getTemp() > -2000)
                        || (encodedRecords.get(k).getTemp() > 0 && encodedRecords.get(k).getTemp() < 2000)) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getTemp();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setTemp(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodePres(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getPres() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getPres() == null
                        || (encodedRecords.get(k).getPres() < 0 && encodedRecords.get(k).getPres() > -2000)) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getPres();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setPres(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeCbwd(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getCbwd() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getCbwd() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = Long.parseLong(encodedRecords.get(j + k).getCbwd());
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setCbwd(decodedRecordsArray[index] + "");
                index++;
            }
            break;
        }
    }

    private void decodeLs(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getLs() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getLs() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getLs();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setLs(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private void decodeLr(List<Pollution> encodedRecords, int i) {
        long[] decodedRecordsArray = new long[60];
        for(int j = i; j <= i + 60; j++) {
            if(encodedRecords.get(j).getLr() == null) continue;
            int nonNullValues = 0;
            for(int k = j; k <= j + 60; k++) {
                if(k >= encodedRecords.size() || encodedRecords.get(k).getLr() == null) break;
                nonNullValues++;
            }
            long[] encodedValue = new long[nonNullValues];
            for(int k = 0; k < nonNullValues; k++) {
                encodedValue[k] = encodedRecords.get(j + k).getLr();
            }
            simple8b.decompress(encodedValue, 0, nonNullValues, decodedRecordsArray, 0);
            int index = 0;
            for(int k = i; k < j + nonNullValues; k++) {
                encodedRecords.get(k).setLr(decodedRecordsArray[index]);
                index++;
            }
            break;
        }
    }

    private List<Pollution> decodeSimple8B(List<Pollution> encodedRecords) {
        List<Pollution> decodedRecords = new ArrayList<>();
        decodedRecords.add(encodedRecords.get(0));
        decodedRecords.add(encodedRecords.get(1));
        for (int i = 2; i < encodedRecords.size(); i++) {
            if(encodedRecords.get(i).getYear() == null) {
                decodeYear(encodedRecords, i);
            }
            if(encodedRecords.get(i).getMonth() == null) {
                decodeMonth(encodedRecords, i);
            }
            if(encodedRecords.get(i).getDay() == null) {
                decodeDay(encodedRecords, i);
            }
            if(encodedRecords.get(i).getHour() == null) {
                decodeHour(encodedRecords, i);
            }
            if(encodedRecords.get(i).getPm25() == null) {
                decodePm25(encodedRecords, i);
            }
            if(encodedRecords.get(i).getDewp() == null) {
                decodeDewp(encodedRecords, i);
            }
            if(encodedRecords.get(i).getTemp() == null) {
                decodeTemp(encodedRecords, i);
            }
            if(encodedRecords.get(i).getPres() == null) {
                decodePres(encodedRecords, i);
            }
            if(encodedRecords.get(i).getCbwd() == null) {
                decodeCbwd(encodedRecords, i);
            }
            if(encodedRecords.get(i).getLs() == null) {
                decodeLs(encodedRecords, i);
            }
            if(encodedRecords.get(i).getLr() == null) {
                decodeLr(encodedRecords, i);
            }
            decodedRecords.add(encodedRecords.get(i));
        }
        return decodedRecords;
    }

    private List<Pollution> decodePollutions(List<Pollution> encodedRecords) {
        List<Pollution> decodedRecords = new ArrayList<>();
        encodedRecords.get(0).setCbwd(directionsMap.get(encodedRecords.get(0).getCbwd()));
        decodedRecords.add(encodedRecords.get(0));
        decodedRecords.add(new Pollution(
                encodedRecords.get(1).getNo(),
                encodedRecords.get(1).getYear() + encodedRecords.get(0).getYear(),
                encodedRecords.get(1).getMonth() + encodedRecords.get(0).getMonth(),
                encodedRecords.get(1).getDay() + encodedRecords.get(0).getDay(),
                encodedRecords.get(1).getHour() + encodedRecords.get(0).getHour(),
                encodedRecords.get(1).getPm25() + encodedRecords.get(0).getPm25(),
                encodedRecords.get(1).getDewp() + encodedRecords.get(0).getDewp(),
                encodedRecords.get(1).getTemp() + encodedRecords.get(0).getTemp(),
                encodedRecords.get(1).getPres() + encodedRecords.get(0).getPres(),
                directionsMap.get(encodedRecords.get(1).getCbwd()),
                xorDecompression(encodedRecords.get(1).getLws(), encodedRecords.get(0).getLws()),
                encodedRecords.get(1).getLs() + encodedRecords.get(0).getLs(),
                encodedRecords.get(1).getLr() + encodedRecords.get(0).getLr()
        ));
        BigDecimal decimal = new BigDecimal(decodedRecords.get(0).getLws()).setScale(2, BigDecimal.ROUND_HALF_UP);
        decodedRecords.get(0).setLws(decimal.doubleValue());
        for(int i = 2; i < encodedRecords.size(); i++) {
            decodedRecords.add(new Pollution(
                    encodedRecords.get(i).getNo(),
                    encodedRecords.get(i).getYear() + encodedRecords.get(1).getYear() + encodedRecords.get(0).getYear(),
                    encodedRecords.get(i).getMonth() + encodedRecords.get(1).getMonth() + encodedRecords.get(0).getMonth(),
                    encodedRecords.get(i).getDay() + encodedRecords.get(1).getDay() + encodedRecords.get(0).getDay(),
                    encodedRecords.get(i).getHour() + encodedRecords.get(1).getHour() + encodedRecords.get(0).getHour(),
                    encodedRecords.get(i).getPm25() + encodedRecords.get(1).getPm25() + encodedRecords.get(0).getPm25(),
                    encodedRecords.get(i).getDewp() + encodedRecords.get(1).getDewp() + encodedRecords.get(0).getDewp(),
                    encodedRecords.get(i).getTemp() + encodedRecords.get(1).getTemp() + encodedRecords.get(0).getTemp(),
                    encodedRecords.get(i).getPres() + encodedRecords.get(1).getPres() + encodedRecords.get(0).getPres(),
                    directionsMap.get(encodedRecords.get(i).getCbwd()),
                    xorDecompression(encodedRecords.get(i).getLws(), decodedRecords.get(i-1).getLws()),
                    encodedRecords.get(i).getLs() + encodedRecords.get(1).getLs() + encodedRecords.get(0).getLs(),
                    encodedRecords.get(i).getLr() + encodedRecords.get(1).getLr() + encodedRecords.get(0).getLr()
            ));
        }
        return decodedRecords;
    }

    public List<Pollution> encode(List<Pollution> pollutionRecords) {
        List<Pollution> encodedRecords = this.deltaEncoding(pollutionRecords);
        encodedRecords = this.deltaOfDeltaEncoding(encodedRecords);
        encodedRecords = this.simple8BEncoding(encodedRecords);
        return encodedRecords;
    }

    public List<Pollution> decode(List<Pollution> encodedRecords) {
        List<Pollution> decodedRecords = this.decodeSimple8B(encodedRecords);
        decodedRecords = this.decodePollutions(decodedRecords);
        return decodedRecords;
    }

    public void saveToCSV(String filename, List<Pollution> pollutionRecords) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for(Pollution p : pollutionRecords) {
                String year = "", month = "", day = "", hour = "", pm25 = "", dewp = "",
                temp = "", pres = "", cbwd = "", ls = "", lr = "";
                if(p.getYear() != null) {
                    year = p.getYear().toString();
                }
                if(p.getMonth() != null) {
                    month = p.getMonth().toString();
                }
                if(p.getDay() != null) {
                    day = p.getDay().toString();
                }
                if(p.getHour() != null) {
                    hour = p.getHour().toString();
                }
                if(p.getPm25() != null) {
                    pm25 = p.getPm25().toString();
                }
                if(p.getDewp() != null) {
                    dewp = p.getDewp().toString();
                }
                if(p.getTemp() != null) {
                    temp = p.getTemp().toString();
                }
                if(p.getPres() != null) {
                    pres = p.getPres().toString();
                }
                if(p.getCbwd() != null) {
                    cbwd = p.getCbwd();
                }
                if(p.getLs() != null) {
                    ls = p.getLs().toString();
                }
                if(p.getLr() != null) {
                    lr = p.getLr().toString();
                }
                bw.write(p.getNo() + "," + year + "," + month + "," + day
                + "," + hour + "," + pm25 + "," + dewp + "," + temp
                + "," + pres + "," + cbwd + "," + p.getLws() + "," + ls + "," + lr);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
