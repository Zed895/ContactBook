package phonebook;

import javafx.beans.property.SimpleStringProperty;

/**
 * This is the Person POJO.
 * Instances of this class represent the entries in the Contact Book and it is also used to create the records in the database.
 * @author Zed
 */
public class Person {
    
    //SimpleStringProperty is used instead of String because it is compatible with TableView, listenable element
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;
    private final SimpleStringProperty id;
    
    public Person() {
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.id = new SimpleStringProperty("");
    }

    public Person(String lName, String fName, String email) {
        this.lastName = new SimpleStringProperty(lName);
        this.firstName = new SimpleStringProperty(fName);
        this.email = new SimpleStringProperty(email);
        this.id = new SimpleStringProperty("");
    }
    
    public Person(Integer id, String lName, String fName, String email) {
        this.lastName = new SimpleStringProperty(lName);
        this.firstName = new SimpleStringProperty(fName);
        this.email = new SimpleStringProperty(email);
        this.id = new SimpleStringProperty(String.valueOf(id));
    }
    
    //non-primitive class variables, so function calls by the getters and setters are needed.
    public String getFirstName() {
        return firstName.get(); 
    }
    
    public void setFirstName(String fName){
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    
    public void setLastName(String lName){
        lastName.set(lName);
    }

    public String getEmail() {
        return email.get();
    }
    
    public void setEmail(String eMail){
        email.set(eMail);
    }

    public String getId() {
        return id.get();
    }
    
    public void setId(String fId) {
       id.set(fId);
    }
}
