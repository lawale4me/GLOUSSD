/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package esmedeamon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Ahmed
 */
public class DataAccess {
    
    public DataAccess(){

    }

    public   Connection connectDB() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public String LogServices(String sessionID,String serviceCode) {
        Connection connection = connectDB();
        String message = "";
        String logUser = "INSERT into Services(sessionID,serviceCode,CreatedDate)values (?,?,now())";

        try {
            PreparedStatement stat = connection.prepareStatement(logUser);
            System.out.println("Logging  service code");

            stat.setString(1, sessionID);
            stat.setString(2, serviceCode);

            stat.executeUpdate();

            System.out.println("Service Code Logged Successfully");
            message += "Service Code Logged Successfully";

        } catch (SQLException ex) {
            message = message + ex.toString();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
            }
        }

        return  message;
    }
    public String LogSession(String msisdn,String sessionID) {
        Connection connection = connectDB();
        String message = "";
        String logUser = "INSERT into USSDLOGGLO(msisdn,sessionid,createdDate)values (?,?,now())";

        try {
            PreparedStatement stat = connection.prepareStatement(logUser);
            System.out.println("Logging  service code");

            stat.setString(1, msisdn);
            stat.setString(2, sessionID);

            stat.executeUpdate();

            System.out.println("Service Code Logged Successfully");
            message += "Session Logged Successfully";

        } catch (SQLException ex) {
            message = message + ex.toString();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return  message;
    }


    public String fetchServices(String sessionID) {

        String service = null;

        System.out.println("fetching Service code from local database");
        String sql = "select * from Services where sessionID='" + sessionID + "' ";

        Connection connection = connectDB();
        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            ResultSet rs = stat.executeQuery();

            while (rs.next()) {
                service  = rs.getString("serviceCode");
            }
            connection.close();
        } catch (SQLException ex) {
           System.out.println("Select Error  : " +ex.getMessage());
        }

        return service;

    }

    public String fetchUrl(String serviceCode){
        String url = "";
        String network = "GLO";
        String sqlUrl = "select * from accesslist where network = '"+network+"' and servicecode = '"+serviceCode+"' " ;

        Connection connection = connectDB();
        PreparedStatement stat = null;
        try{
             stat = connection.prepareStatement(sqlUrl);
            ResultSet rs = stat.executeQuery();

            while(rs.next()){
                url = rs.getString("url");
            }
            connection.close();
            rs.close();
        }catch(SQLException ex){
            System.out.println("Fetching error : " +ex.getMessage());
            ex.printStackTrace();
        }finally{
            try {
                if (stat != null) {
                    stat.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }catch(SQLException ex){
                System.out.println("SQL Error : " +ex.getMessage());
            }
        }

        return url;
    }

    

}
