package contactbook;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class represents a database based on JDBC.
 */
public class DB {
    //final String JDBC_DRIVER = "org.apache.derby.jdbc.Embedded"; //not needed, Derby is default
    final String URL = "jdbc:derby:contactDB;create=true";
    final String USERNAME = "";
    final String PASSWORD = "";
    
    Connection conn = null;
    Statement createStatement = null;
    DatabaseMetaData dbmd = null;
    
    public DB() {
        
        try {
            conn= DriverManager.getConnection(URL);
            System.out.println("The database connection exists. ");
        } catch (SQLException ex) {
            System.out.println("Problem with creating connection. " + ex);
        }
        
        if(conn != null){
            try {
                createStatement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("There is a problem with the createStatement " + ex);
            }
        }
        
        try {
            dbmd = conn.getMetaData();
        } catch (SQLException ex) {
            System.out.println("There is a problem with the DatabaseMetaData (database description) " + ex);
        }
        
        try {
            ResultSet rs1 = dbmd.getTables(null, "APP", "CONTACTS", null);
            if(!rs1.next()){
                createStatement.execute("create table contacts (id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),lastname varchar(20), firstname varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
            System.out.println("There is a problem with datatable creation " + ex);
        }
    }
    
    /**
     * Select everything from the contacts table of the database.
     * @return an arrayList containing all of the Person POJOs.
     */
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ArrayList<Person> users = null; //if you receive null, then there is az error!
        try {
            ResultSet rs = createStatement.executeQuery(sql); 
            users = new ArrayList<>(); //if you receive only this, then there is no error but an empty database
            while (rs.next()){ 
                //POJO returns to the Controller with ID by now
                Person actualPerson = new Person(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"), rs.getString("email"));
                users.add(actualPerson);
            }
        } catch (SQLException ex) {
            System.out.println("There is a problem with reading table contacts " + ex);
        }
        return users;
    }
    
    /**
     * Insert a person into the contacts table of the database via SQL. 
     * @param person received person will be inserted into the DB as a new record.
     */
    public void addContact(Person person){
        try {
            String sgl = "insert into contacts (lastname, firstname, email) values (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sgl); //because type checking is needed from here
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("There is a problem with inserting contacts" + ex);
        }
    }
    
    /**
     * Updates a record in the contacts table of the database via SQL.
     * @param person the record representing this person will be updated.
     */
    public void updateContact(Person person){
        try {
            String sgl = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sgl);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("There is a problem with updating contact" + ex);
        }
    }
    
    /** 
     * This method removes a contact from the contacts table of the database via SQL.
     * @param person The entry which should be deleted.
     */
    public void removeContact(Person person){
        try {
            String sgl = "delete from contacts where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sgl);
            preparedStatement.setInt(1, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("There is a problem with deleting contact " + ex);
        }
    }
}
