package org.example;

import java.math.BigDecimal;
import java.sql.*;

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
            ResultSet resultSet = con.getMetaData().getTables(null, null, "pollution", null);
            if(resultSet.next()) {
                String dropTableSQL = "DROP TABLE pollution";
                stmt.executeUpdate(dropTableSQL);
            }
            String table = "CREATE TABLE IF NOT EXISTS pollution"
                    + "(no int PRIMARY KEY AUTO_INCREMENT, year int, month int, day int, hour int,"
                    + "pm25 int, dewp int, temp int, pres int, cbwd varchar(2), lws double, ls int, lr int)";
            stmt.execute(table);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertData(Pollution p) {
        try (Statement stmt = con.createStatement()) {
            BigDecimal decimal = new BigDecimal(p.getLws()).setScale(2, BigDecimal.ROUND_HALF_UP);
            String insertSql = "INSERT INTO pollution(no, year, month, day, hour, pm25, dewp, temp, pres, cbwd, lws, ls, lr)"
                    + " VALUES (" + p.getNo() + "," + p.getYear() + "," + p.getMonth() + "," + p.getDay() + "," + p.getHour() + ","
                    + p.getPm25() + "," + p.getDewp() + "," + p.getTemp() + "," + p.getPres() + ", '" + p.getCbwd() + "' ,"
                    + decimal.doubleValue() + "," + p.getLs() + "," + p.getLr() + ")";
            stmt.execute(insertSql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createEncodedTable() {
        try (Statement stmt = con.createStatement()) {
            ResultSet resultSet = con.getMetaData().getTables(null, null, "pollutionEncoded", null);
            if(resultSet.next()) {
                String dropTableSQL = "DROP TABLE pollutionEncoded";
                stmt.executeUpdate(dropTableSQL);
            }
            String table = "CREATE TABLE IF NOT EXISTS pollutionEncoded"
                    + "(no int PRIMARY KEY AUTO_INCREMENT, year bigint(64), month bigint(64), day bigint(64), hour bigint(64),"
                    + "pm25 bigint(64), dewp bigint(64), temp bigint(64), pres bigint(64), cbwd bigint(64), lws double, ls bigint(64), lr bigint(64))";
            stmt.execute(table);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertEncodedData(Pollution p) {
        try (Statement stmt = con.createStatement()) {
            Long cbwd = null;
            if(p.getCbwd() != null) cbwd = Long.parseLong(p.getCbwd());
            String insertSql = "INSERT INTO pollutionEncoded(no, year, month, day, hour, pm25, dewp, temp, pres, cbwd, lws, ls, lr)"
                    + " VALUES (" + p.getNo() + "," + p.getYear() + "," + p.getMonth() + "," + p.getDay() + "," + p.getHour() + ","
                    + p.getPm25() + "," + p.getDewp() + "," + p.getTemp() + "," + p.getPres() + ", " + cbwd + " ,"
                    + p.getLws() + "," + p.getLs() + "," + p.getLr() + ")";
            stmt.execute(insertSql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
