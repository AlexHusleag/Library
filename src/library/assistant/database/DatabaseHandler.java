package library.assistant.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class DatabaseHandler {
    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    public DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
    }
    
    public static DatabaseHandler getInstance(){
        if(handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }
    private void createConnection(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            //Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            e.printStackTrace();
        }
        
     }
    
    private void setupBookTable(){
        String TABLE_NAME = "BOOK";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "      id varchar(200) primary key,\n"
                        + "      title varchar(200),\n"
                        + "      author varchar(200),\n"
                        + "      publisher varchar(100),\n"
                        + "      isAvailable boolean default true"
                        + ")");
            }
        } catch (SQLException e) {
            //Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally{
          
        }
    }   
    
    
    public ResultSet execQuery(String query){
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
            
        } catch (SQLException ex) {
            //Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Exception at execQuery");
            return null;
        } finally{
            
        }
        return result;
        
    }
    
    
    public boolean execAction(String qu){
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
            //Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            return false;
        }
        
    }

    private void setupMemberTable() {
           String TABLE_NAME = "MEMBER";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if(tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "      id varchar(200) primary key,\n"
                        + "      name varchar(200),\n"
                        + "      mobile varchar(20),\n"
                        + "      email varchar(100)"
                        + ")");
            }
        } catch (SQLException e) {
            //Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(e.getMessage() + " --- setupDatabase");
            e.printStackTrace();
        } finally{
          
        }
    }
}
