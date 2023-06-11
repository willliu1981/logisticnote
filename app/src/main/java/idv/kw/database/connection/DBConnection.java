package idv.kw.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import idv.logistic.logisticnote.database.connection.DBFactory;

public class DBConnection {


    public static Connection getConnection() {


        try {
            Class.forName("org.sqldroid.SQLDroidDriver");
            String url = "jdbc:sqldroid:" + DBFactory.getDBAbsolutePath();


            return DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }


        return null;
    }


}
