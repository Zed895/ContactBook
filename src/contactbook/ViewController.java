package contactbook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * This is the Controller
 * @author Zed
 */
public class ViewController implements Initializable {
    
    @FXML
    TableView table;
    @FXML
    TextField inputLastname;
    @FXML
    TextField inputFirstname;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    SplitPane mainSplit;
    @FXML
    AnchorPane anchor;
    @FXML
    TextField inputExportName;
    @FXML
    Button exportButton;
    
    DB db = new DB();
    
    private final String MENU_CONTACTS = "Contacts";
    private final String MENU_LIST = "List";
    private final String MENU_EXPORT = "Export";
    private final String MENU_EXIT = "Exit";
    
    
    @FXML
    private void addContact(ActionEvent event){
        String email = inputEmail.getText();
        
        //The validation
        if (email.contains("@") && email.contains(".") && email.length()>4){
            Person newPerson = new Person (inputLastname.getText(), inputFirstname.getText(), email);
            db.addContact(newPerson); //first it goes to the DB to receive an ID, which is needed to delete and update
            inputLastname.clear();
            inputFirstname.clear();
            inputEmail.clear();
            data.removeAll(data); //to prevent duplication on the table
            data.addAll(db.getAllContacts()); //fresh data from the DB with IDs. Since it is a listenable ObservableList it appears immediately
        } else {alert("Set a real e-mail address! \n \n", 320.0);}
    }
    
    @FXML
    private void exportList(ActionEvent event){
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+","");//regex removes whitespaces
        if (fileName != null && !fileName.equals("")){
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
            alert("Saved.\n \n", 410.0);
        } else {alert("Name the file! \n \n", 380.0);}
    }
    
    //ObservableList is used as it is listenable static element. This data will receive an arrayList with Persons from the DB
    private final ObservableList<Person> data = FXCollections.observableArrayList();
    
    /**
     * This method handles the entries and put them onto the table.
     */
    public void setTableData(){
        TableColumn lastNameCol = new TableColumn("Surname");
        lastNameCol.setMinWidth(130);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());//cell is textfield to be editable
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName")); //the value of the cell should be the String "lastName" of a POJO
        
        lastNameCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){ //it listens to the CellEditEvent on the TableColumn
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){ //it receives event "t"
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow()); //since all of them from the DB by now, its ID has value
                    actualPerson.setLastName(t.getNewValue()); //null ID would be enough for this
                    db.updateContact(actualPerson);
                }
            }
        );
        
        TableColumn firstNameCol = new TableColumn("Given name");
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        firstNameCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                    actualPerson.setFirstName(t.getNewValue());
                    db.updateContact(actualPerson);
                }
            }
        );
        
        TableColumn emailCol = new TableColumn("E-mail");
        emailCol.setMinWidth(250);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        emailCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                    actualPerson.setEmail(t.getNewValue());
                    db.updateContact(actualPerson);
                }
            }
        );
        
        TableColumn removeCol = new TableColumn("Delete");
        //removeCol.setMinWidth(100);
        
        //Listener, callback for delete button
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory =
                new Callback<TableColumn<Person, String>,TableCell<Person, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Person, String> param) {
                        final TableCell <Person, String> cell = new TableCell<Person, String>() {
                            final Button btn = new Button("Delete");
                            @Override
                            public void updateItem(String item, boolean empty){
                                super.updateItem(item, empty);
                                if (empty){
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction( (ActionEvent event) ->
                                    {
                                        Person person = getTableView().getItems().get(getIndex());
                                        data.remove(person);
                                        db.removeContact(person);                                        
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        
        removeCol.setCellFactory(cellFactory);

        //the table receives its columns
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol); //ID is not needed here, but needed by the POJO for the DB
        
        //the observable list receives the content of the arrayList from the DB
        data.addAll(db.getAllContacts());
        
        //the observable list goes onto the table
        table.setItems(data);
        
    }
    
    /**
     * This method creates the menu on the left pane.
     */
    private void setMenuData() {
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menu");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);
        
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        //nodeItemA.setExpanded(true);        
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);
        
        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png"))); //ImageView extends Node
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));                                                                                              
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST,contactsNode); //this other constructor of TreeItem receives Node too
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT,exportNode);
        
        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);
        
        menuPane.getChildren().addAll(treeView);
        
        //listener
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue){
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu; //the items of the treeView are just strings
                selectedMenu = selectedItem.getValue();//but if we did not cast it to TreeItem there would not be getValue() method
                //System.out.println("Valasztott menu: " + selectedMenu);
                
                if(null != selectedMenu){
                    switch(selectedMenu){
                        case MENU_CONTACTS:
                            try { //if it had not elements
                            selectedItem.setExpanded(true);
                            } catch(Exception ex){};
                            break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORT:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;
                        case MENU_EXIT:
                            System.exit(0);
                            break;
                    }
                }
            } 
        });
        
    }
    
    /**
    * Creates dynamically an alert popup.
    * Can be called if you want to show an alert to the user.
    * @param text this will be the shown message.
    * @param hova is the desired position of the popup on the pane.
    */
    public void alert(String text, double hova){
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label, alertButton);
        vbox.setAlignment(Pos.CENTER);
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 270.0);
        anchor.setLeftAnchor(vbox, hova); 
        
        alertButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vbox.setVisible(false);
            }
        });  
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
    }
}
