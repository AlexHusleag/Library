/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.assistant.ui.listbook;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.ui.main.MainController;
import library.assistant.Icons.LibraryAssistantIcon;

public class BookListController implements Initializable {

    private ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    public void loadData() {
        list.clear();
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                String author = rs.getString("author");
                String id = rs.getString("id");            // din baza de date
                String publisher = rs.getString("publisher");
                Boolean avail = rs.getBoolean("isAvailable");
                // am luat datele din baza de date si le-am bagat in lista
                list.add(new Book(author, id, titlex, publisher, avail));
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        // asociam datele cu tableView
        // tableView.getItems().setAll(list);
        tableView.setItems(list);
    }

    @FXML
    private void handleBookDeleteOption(ActionEvent event) {
        // Fetch the selected row

        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No book selected");
            alert.setContentText("Please select a book for deletion first");
            alert.showAndWait();
            return;
        }

        // Verify if the book we wish to delete is already issued
        Boolean isbookAlreadyIssued = DatabaseHandler.getInstance().isBookAlreadyIssues(selectedForDeletion);
        if (isbookAlreadyIssued) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setHeaderText(null);
            alert2.setContentText(selectedForDeletion.getTitle() + " is already issued");
            alert2.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting book");
        alert.setContentText("Are you sure you want to delete the book " + selectedForDeletion.getTitle() + "?");
        Optional<ButtonType> answer = alert.showAndWait();

        if (answer.get().equals(ButtonType.OK)) {
            // Delete the selected row

            Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);

            if (result) {
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setHeaderText(null);
                alert2.setContentText(selectedForDeletion.getTitle() + " was deleted successfully");
                alert2.showAndWait();
                list.remove(selectedForDeletion);
            } else {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setHeaderText(null);
                alert2.setContentText("Failed to delete " + selectedForDeletion.getTitle());
                alert2.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setHeaderText(null);
            alert1.setContentText("Deletion canceled.");
            alert1.showAndWait();
        }

    }

    @FXML
    private void handleBookEditOption(ActionEvent event) {
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No book selected");
            alert.setContentText("Please select a book to edit first");
            alert.showAndWait();
            return;
        }

        try {
            // Facem in asa fel incat sa avem acces la controlleru-ul BookAddController
            // Communication between Controllers
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"));
            Parent parent = loader.load();

            BookAddController controller = (BookAddController) loader.getController();
            controller.populateEditBookUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantIcon.setStageIcon(stage);
            
            stage.setOnCloseRequest(e->{
                handleRefresh();
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleRefresh(){
        loadData();
    }

    public static class Book {

        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;

        public Book(String title, String id, String author, String publisher, Boolean avail) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.availability = new SimpleBooleanProperty(avail);
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }
    }

}
