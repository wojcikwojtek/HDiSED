package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PollutionController {
    private DatabaseController dbController;
    private int leadingZeros = 0;
    private int bitsInTheSubsequence = 0;

    public PollutionController() {
        dbController = new DatabaseController();
    }

    public List<Pollution> readCSV(String filename) {
        List<Pollution> pollutionRecords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if(tokens[0].equals("No")) { continue; }
                int pm25;
                if(tokens[5].equals("NA")) {
                    pm25 = 100;
                } else {
                    pm25 = Integer.parseInt(tokens[5]);
                }
                pollutionRecords.add(new Pollution(
                        Integer.parseInt(tokens[0]),
                        Integer.parseInt(tokens[1]),
                        Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        pm25,
                        Integer.parseInt(tokens[6]),
                        Integer.parseInt(tokens[7]),
                        Integer.parseInt(tokens[8]),
                        tokens[9],
                        Float.parseFloat(tokens[10]),
                        Integer.parseInt(tokens[11]),
                        Integer.parseInt(tokens[12])
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

    public long xorCompression(double Lws1, double Lws2) {
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
        if(leadingZeros + bitsInTheSubsequence < xorLeadingZeros + xorBitsInTheSubsequence) {
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
        return Long.parseLong(resultBits, 2);
    }

    public List<PollutionEncoded> deltaEncoding(List<Pollution> pollutionRecords) {
        List<PollutionEncoded> deltaRecords = new ArrayList<>();
        deltaRecords.add(new PollutionEncoded(
                pollutionRecords.get(0).getNo(),
                pollutionRecords.get(0).getYear(),
                pollutionRecords.get(0).getMonth(),
                pollutionRecords.get(0).getDay(),
                pollutionRecords.get(0).getHour(),
                pollutionRecords.get(0).getPm25(),
                pollutionRecords.get(0).getDewp(),
                pollutionRecords.get(0).getTemp(),
                pollutionRecords.get(0).getPres(),
                pollutionRecords.get(0).getCbwd(),
                Double.doubleToLongBits(pollutionRecords.get(0).getLws()),
                pollutionRecords.get(0).getLs(),
                pollutionRecords.get(0).getLr()
        ));
        for (int i = 1; i < pollutionRecords.size(); i++) {
            deltaRecords.add(new PollutionEncoded(
                    pollutionRecords.get(i).getNo(),
                    pollutionRecords.get(i).getYear() - deltaRecords.get(0).getYear(),
                    pollutionRecords.get(i).getMonth() - deltaRecords.get(0).getMonth(),
                    pollutionRecords.get(i).getDay() - deltaRecords.get(0).getDay(),
                    pollutionRecords.get(i).getHour() - deltaRecords.get(0).getHour(),
                    pollutionRecords.get(i).getPm25() - deltaRecords.get(0).getPm25(),
                    pollutionRecords.get(i).getDewp() - deltaRecords.get(0).getDewp(),
                    pollutionRecords.get(i).getTemp() - deltaRecords.get(0).getTemp(),
                    pollutionRecords.get(i).getPres() - deltaRecords.get(0).getPres(),
                    pollutionRecords.get(i).getCbwd(),
                    xorCompression(pollutionRecords.get(i).getLws(), pollutionRecords.get(i-1).getLws()),
                    pollutionRecords.get(i).getLs() - deltaRecords.get(0).getLs(),
                    pollutionRecords.get(i).getLr() - deltaRecords.get(0).getLr()
            ));
        }
        return deltaRecords;
    }

    public List<PollutionEncoded> deltaOfDeltaEncoding(List<PollutionEncoded> pollutionRecords) {
        if(pollutionRecords.size() < 3) return pollutionRecords;
        List<PollutionEncoded> deltaOfDeltaRecords = new ArrayList<>();
        deltaOfDeltaRecords.add(pollutionRecords.get(0));
        deltaOfDeltaRecords.add(pollutionRecords.get(1));
        for(int i = 2; i < pollutionRecords.size(); i++) {
            deltaOfDeltaRecords.add(new PollutionEncoded(
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

    public void saveToCSV(String filename, List<PollutionEncoded> pollutionRecords) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for(PollutionEncoded p : pollutionRecords) {
                bw.write(p.getNo() + "," + p.getYear() + "," + p.getMonth() + "," + p.getDay()
                + "," + p.getHour() + "," + p.getPm25() + "," + p.getDewp() + "," + p.getTemp()
                + "," + p.getPres() + "," + p.getCbwd() + "," + p.getLws() + "," + p.getLs() + "," + p.getLr());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
