package library.assistant.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import library.assistant.ui.listbook.BookListController.Book;
import library.assistant.ui.listmember.MemberListController;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    public DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't load database", "Datavase Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

    }

    private void setupBookTable() {
        String TABLE_NAME = "BOOK";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
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
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {

        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);

        } catch (SQLException ex) {
            System.err.println("Exception at execQuery");
            return null;
        } finally {

        }
        return result;

    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
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
            if (tables.next()) {
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
            System.err.println(e.getMessage() + " --- setupDatabase");
            e.printStackTrace();
        } finally {

        }
    }

    private void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + "already exists. Ready for go");

            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "       bookID varchar(200) primary key,\n"
                        + "       memberID varchar(200),\n"
                        + "       issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + "       renew_count integer default 0,\n"
                        + "       FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "       FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        + " )");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean deleteBook(Book book) {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int deletedRows = stmt.executeUpdate();

            if (deletedRows == 1) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isBookAlreadyIssues(Book book) {
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();

            // If execution was successful, verificam daca exista vreo carte deja issues ( FOREIGN_KEY , ISSUE table ) 
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);

                return (count > 0);
            }
            int deletedRows = stmt.executeUpdate();
            System.out.println(deletedRows);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateBook(Book book) {
        try {
            String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();  // return 1 if it's successfull because we update one row
            return (res > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateMember(MemberListController.Member member) {
        try {
            String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMobile());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();  // return 1 if it's successfull because we update one row
            return (res > 0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
