package phonebook;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DB {
    //final String JDBC_DRIVER = "org.apache.derby.jdbc.Embedded"; //nem hasznaltuk, mert alapbol Derby van a JDBC szamara.
    final String URL = "jdbc:derby:sampleDB;create=true";
    final String USERNAME = "";
    final String PASSWORD = "";
    
    //Letrehozzuk a kapcsolatot (hidat)
    Connection conn = null;
    
    Statement createStatement = null;
    DatabaseMetaData dbmd = null;
    
    public DB() {
        
        //Megprobaljuk eletre kelteni
        try {
            conn= DriverManager.getConnection(URL);
            System.out.println("The connection exists.");
        } catch (SQLException ex) {
            System.out.println("Problem with creating connection" + ex);
        }
        
        //Ha eletre kelt, csinalunk egy megpakolhato teherautot
        if(conn != null){
            try {
                createStatement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("There is a problem with the createStatement (car)" + ex);
            }
        }
        
        //Megnezzuk, hogy ures-e az adatbazis. Megnezzuk letezik-e az adott adattabla.
        try {
            dbmd = conn.getMetaData(); //Itt kerunk metaadatot a connenction-tol! Ez az egesz adatbazisra vonatkozik!
        } catch (SQLException ex) {
            System.out.println("There is a problem with the DatabaseMetaData (database description)" + ex);
        }
        
        try {
            ResultSet rs1 = dbmd.getTables(null, "APP", "CONTACTS", null); //ratesszuk egy whiteboard-ra a metaadatot az egeszadatbazisrol.
            if(!rs1.next()){ //csak vegig megy a whiteboard-on
                createStatement.execute("create table contacts (id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),lastname varchar(20), firstname varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
            System.out.println("There is a problem with datatable creation" + ex);
        }
    }
    
    
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ArrayList<Person> users = null; //ha hiba van akkor ezt a null-t kapja vissza a meghivo, tehat tudja hogy hiba
        try {
            ResultSet rs = createStatement.executeQuery(sql); 
            users = new ArrayList<>(); //itt nincs hiba csak ureset kap vissza a meghivo, de nem null-t igy tudja, hogy minden oke, csak ures.
            while (rs.next()){ 
                Person actualPerson = new Person(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"), rs.getString("email")); //igy a visszaerkezo adatokbol
                                                                                                //letrehozol egy uj Person-t a konstruktor segitsegevel.
                                                     //azert kell egy letrehozni, hogy legyen id-ja (megha a table-be nem is kell) mert update-kor kell az id.
                users.add(actualPerson); //hozzaadjuk a listahoz
            }
        } catch (SQLException ex) {
            System.out.println("There is a problem with reading users" + ex);
        }
        
        return users; //barki hasznalhatja a listat
    }
    
    public void addContact(Person person){
        try {
            String sgl = "insert into contacts (lastname, firstname, email) values (?,?,?)";//ha minden oszlophoz kapna adatot, akkor nem kellene explicit
                                                                                            //megmondani, hogy (lastname, firstname, email).
            PreparedStatement preparedStatement = conn.prepareStatement(sgl);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("There is a problem with adding contacts" + ex);
        }
    }
    
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
    
    public void removeContact(Person person){
        try {
            String sgl = "delete from contacts where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sgl);
            preparedStatement.setInt(1, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("There is a problem with deleting contact" + ex);
        }
    }
}
