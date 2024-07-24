package org.example;

import org.apache.commons.lang3.time.StopWatch;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PollutionController pc = new PollutionController();
        List<Pollution> pollutionRecords = pc.readCSV("pollution.csv");
        StopWatch watch = new StopWatch();
        System.out.println("Pomiar czasu dla niezakodowanych danych");
        watch.start();
        pc.insertToDB(pollutionRecords);
        watch.stop();
        System.out.println("Uplynelo: " + watch.getTime() + "ms");
        watch.reset();
        System.out.println("Pomiar czasu wraz z kodowaniem danych");
        watch.start();
        List<Pollution> encodedRecords = pc.encode(pollutionRecords);
        pc.insertToEncodedDB(encodedRecords);
        watch.stop();
        System.out.println("Uplynelo: " + watch.getTime() + "ms");
        List<Pollution> decodedRecords = pc.decode(encodedRecords);
        System.out.println("Czy dane są takie same po odkodowaniu co pierwotne dane");
        System.out.println(compare(decodedRecords, "pollution.csv"));
    }

    public static boolean compare(List<Pollution> decodedRecords, String filename) {
        PollutionController pc = new PollutionController();
        List<Pollution> pollutionRecords = pc.readCSV(filename);
        for(int i = 0; i < pollutionRecords.size() -1; i++) {
            BigDecimal decimal = new BigDecimal(pollutionRecords.get(i).getLws()).setScale(2, BigDecimal.ROUND_HALF_UP);
            if(!pollutionRecords.get(i).getYear().equals(decodedRecords.get(i).getYear())) {
                System.out.println("Year at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getMonth().equals(decodedRecords.get(i).getMonth())) {
                System.out.println("Month at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getDay().equals(decodedRecords.get(i).getDay())) {
                System.out.println("Day at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getHour().equals(decodedRecords.get(i).getHour())) {
                System.out.println("Hour at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getPm25().equals(decodedRecords.get(i).getPm25())) {
                System.out.println("Pm25 at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getDewp().equals(decodedRecords.get(i).getDewp())) {
                System.out.println("Dewp at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getTemp().equals(decodedRecords.get(i).getTemp())) {
                System.out.println("Temp at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getPres().equals(decodedRecords.get(i).getPres())) {
                System.out.println("Pres at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getCbwd().equals(decodedRecords.get(i).getCbwd())) {
                System.out.println("Cbwd at " + i);
                return false;
            } else if(decimal.doubleValue() != decodedRecords.get(i).getLws()) {
                System.out.println("Lws at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getLs().equals(decodedRecords.get(i).getLs())) {
                System.out.println("Ls at " + i);
                return false;
            } else if(!pollutionRecords.get(i).getLr().equals(decodedRecords.get(i).getLr())) {
                System.out.println("Lr at " + i);
                return false;
            }
        }
        return true;
    }
}