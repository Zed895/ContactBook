package phonebook;

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

public class ViewController implements Initializable {
    
    @FXML
    TableView table; //ebbe megy majd az ObservableList? (nagybetu mert ugye osztaly, nem objektum)
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
    
    
    @FXML private void addContact(ActionEvent event){
        String email = inputEmail.getText();
        if (email.contains("@") && email.contains(".") && email.length()>4){
            Person newPerson = new Person (inputLastname.getText(), inputFirstname.getText(), email);
            //data.add(newPerson);
            db.addContact(newPerson);
            inputLastname.clear();
            inputFirstname.clear();
            inputEmail.clear();
            //table.refresh(); //nem jott be
            data.removeAll(data);//torolni az osszeset, hogy legyen az ujhoz is ID, de ne jelenjen meg 2-szer
            data.addAll(db.getAllContacts());//hogy frissitse a tablat, hogy meglegyen az ID is a frissen felvittek torlesehez es update-jehez!
            
        } else {alert("Set a real e-mail address! \n \n", 320.0);}
    };
    
    @FXML
    private void exportList(ActionEvent event){
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+","");
        if (fileName != null && !fileName.equals("")){
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
            alert("Saved.\n \n", 410.0);
        } else {alert("Name the file! \n \n", 380.0);}
    }
    
    private final ObservableList<Person> data = FXCollections.observableArrayList(); //ez egy statikus fuggveny, nem kell peldanyositani, csak meghivni mint a mathRandom()-ot.
                                                //itt szepen sorban kapott uj objektumokat, new Person-okat.
    
    public void setTableData(){
        TableColumn lastNameCol = new TableColumn("Surname"); //meg nem tud a tablarol, nincs meg rateve
        lastNameCol.setMinWidth(130);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());//textfield legyen a cella. Lehetne label(Akkor nem szerkesztheto) vagy checkbox is.
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName")); //a cella ertekenek a beallitasahoz kell egy parameter
        //ami egy new PropertyValueFactory, ami Pojo-t tud kivenni, vesszo utan meg megadja, hogy milyen erteket kell a pojobol kivegyen, Stringet,
        //amit "lastName" neven talal. Tehat beallitjuk a cella erteket egy uj objektumra, amibol a lastName-t kell megjeleniteni.
        
        lastNameCol.setOnEditCommit( //Ez a kommentator aki figyeli az oszlopot,
                                     //Commit azt jelenti, hogy nem csak valtoztatas volt, de az el is lett kuldve, tehat nem ESC-et nyomott.
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){ //az esemenykezelo amit param-kent megkapott a kommentator.
                                                                           //Ennek a param-ja az esemeny: CellEditEvent tortent a TableColumn-on
                                                                           //azt meg ugye tudjuk, hogy Person-t es String-et kell figyelni
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){ //handler, tehat mit csinaljon, ha az event bekovetkezett
                                                                                 //megkapja az esemenyt t -kent.
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//tudjuk, hogy az adott row egy Person, azza cast-oljuk
                    actualPerson.setLastName(t.getNewValue());//igy meg tudjuk hivni a setLastName() method-jat, amiben az event-tol megkapott param van.
                    
                    db.updateContact(actualPerson);
                }
            }
        );
        
        TableColumn firstNameCol = new TableColumn("Given name"); //meg nem tud a tablarol, nincs meg rateve
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());//textfield legyen a cella. Lehetne label(Akkor nem szerkesztheto) vagy checkbox is.
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        firstNameCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//tudjuk, hogy az adott row egy Person, azza cast-oljuk
                    actualPerson.setFirstName(t.getNewValue());//igy meg tudjuk hivni a setLastName() method-jat, amiben az event-tol megkapott param van.
                    
                    db.updateContact(actualPerson);
                }
            }
        );
        
        TableColumn emailCol = new TableColumn("E-mail"); //meg nem tud a tablarol, nincs meg rateve
        emailCol.setMinWidth(250);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());//textfield legyen a cella. Lehetne label(Akkor nem szerkesztheto) vagy checkbox is.
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        emailCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>(){
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//tudjuk, hogy az adott row egy Person, azza cast-oljuk
                    actualPerson.setEmail(t.getNewValue());//igy meg tudjuk hivni a setLastName() method-jat, amiben az event-tol megkapott param van.                 
                    db.updateContact(actualPerson);

                }
            }
        );
        
        TableColumn removeCol = new TableColumn("Delete");
        //removeCol.setMinWidth(100);
        
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
                                    } );
                                    setGraphic(btn);
                                    setText(null);
                                }
                          }
                        };
                        return cell;
                    }
                };
        
        removeCol.setCellFactory(cellFactory);

        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol); //ha itt nem is kell az ID, attol meg a POJO-nak kell a adatbazis miatt.
        
        //data.removeAll(data);//torolni az osszeset, hogy legyen az ujhoz is ID, de ne jelenjen meg 2-szer
        data.addAll(db.getAllContacts());
        
        table.setItems(data);
        
    }
    
    private void setMenuData() {
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menu");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);
        
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        //nodeItemA.setExpanded(true);
        
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);
        
        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png"))); //itt az ImageView kiterjeszti a Node -ot, tehat a
                                                                                                      //Node az "allat" az ImageView a "macska".
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));                                                                                              
        
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST,contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT,exportNode);
        
        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);
        
        menuPane.getChildren().addAll(treeView);
        
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue){//mint az event, csak itt nem egyben kapja meg az eventet
                        //mint fent hanem 3 fele bontva. Fent csak a "t" eventet kapta meg, itt ugyan az csak kulon van a 3 osszetevoje az event-nek.
                TreeItem<String> selectedItem = (TreeItem<String>) newValue; //castolas, a newValue az amire eppen most kattintott, az az uj selected.
                String selectedMenu;
                selectedMenu = selectedItem.getValue();
                //System.out.println("Valasztott menu: " + selectedMenu);
                
                if(null != selectedMenu){
                    switch(selectedMenu){
                        case MENU_CONTACTS:
                            try{ //azert kell a try, mert lehet, hogy nincs mit lenyitni, ha nincs a kontaktok alatt semmi. Most van.
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
    
    public void alert(String text, double hova){
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        
        //dinamikusan letrehozzuk
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label, alertButton);
        vbox.setAlignment(Pos.CENTER);
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 270.0); //pozicionalas, hogy ne a bal felso sarokban jelenjen meg.
        anchor.setLeftAnchor(vbox, hova); //pozicionalas, hogy ne a bal felso sarokban jelenjen meg.
        
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
    public void initialize(URL url, ResourceBundle rb) { //ez az ami egyszer fut le az elejen
        setTableData();
        setMenuData();
        
    }
}
