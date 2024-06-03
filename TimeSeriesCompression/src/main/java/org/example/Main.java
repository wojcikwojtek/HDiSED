package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PollutionController pc = new PollutionController();
//        List<Pollution> pollutionRecords = pc.readCSV("pollution.csv");
//        List<PollutionEncoded> encodedPollutions = pc.deltaEncoding(pollutionRecords);
//        encodedPollutions = pc.deltaOfDeltaEncoding(encodedPollutions);
//        pc.insertToDB(encodedPollutions);
//        pc.saveToCSV("pollutionEncoded.csv", encodedPollutions);
        System.out.println(pc.xorCompression(0.007859230041503906, 0.0076792240142822266));
        System.out.println(pc.xorCompression(0.0076792240142822266, 0.008411884307861328));
        System.out.println(pc.xorCompression(0.008411884307861328, 0.00831747055053711));
    }
}