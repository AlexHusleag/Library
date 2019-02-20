package library.assistant.ui.listmember;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.ui.main.MainController;
import library.assistant.Icons.LibraryAssistantIcon;

public class MemberListController implements Initializable {

    private ObservableList<MemberListController.Member> list = FXCollections.observableArrayList();
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email")); // cele de mai jos
    }

    private void loadData() {
        list.clear();

        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String mobile = rs.getString("mobile");
                String id = rs.getString("id");            // din baza de date
                String email = rs.getString("email");
                // am luat datele din baza de date si le-am bagat in lista
                list.add(new Member(name, id, mobile, email));
            }
        } catch (SQLException ex) {
            // ex.printStackTrace();
            System.out.println("lol");
        }
        // asociam datele cu tableView
        tableView.getItems().setAll(list);
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No member selected");
            alert.setContentText("Please select a member to edit first");
            alert.showAndWait();
            return;
        }

        try {
            // Facem in asa fel incat sa avem acces la controlleru-ul BookAddController
            // Communication between Controllers
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();

            MemberAddController controller = (MemberAddController) loader.getController();
            controller.populateEditMemberUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantIcon.setStageIcon(stage);

            stage.setOnCloseRequest(e -> {
                handleRefresh(new ActionEvent());
            });
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public Member(String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

    }
}
