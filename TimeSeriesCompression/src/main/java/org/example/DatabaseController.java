package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
    static private Connection con;

    public DatabaseController() {
        try {
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hdised", "root", "Mapacen123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try (Statement stmt = con.createStatement()) {
            String table = "CREATE TABLE IF NOT EXISTS pollutionEncoded"
                    + "(no int PRIMARY KEY AUTO_INCREMENT, year int, month int, day int, hour int,"
                    + "pm25 int, dewp int, temp int, pres int, cbwd varchar(2), lws float, ls int, lr int)";
            stmt.execute(table);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertData(Pollution p) {
        try (Statement stmt = con.createStatement()) {
            String insertSql = "INSERT INTO pollutionEncoded(no, year, month, day, hour, pm25, dewp, temp, pres, cbwd, lws, ls, lr)"
                    + " VALUES (" + p.getNo() + "," + p.getYear() + "," + p.getMonth() + "," + p.getDay() + "," + p.getHour() + ","
                    + p.getPm25() + "," + p.getDewp() + "," + p.getTemp() + "," + p.getPres() + ", '" + p.getCbwd() + "' ,"
                    + p.getLws() + "," + p.getLs() + "," + p.getLr() + ")";
            stmt.execute(insertSql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
