package library.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listmember.MemberListController;
import library.assistant.ui.listmember.MemberListController.Member;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;

    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    private Boolean isInEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) name.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();

        if (mName.isEmpty() || mID.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Can't process member");
            alert.setContentText("No empty field is allowed");
            alert.showAndWait();
            return;
        }

        if (isInEditMode) {
            handleUpdateMember();
            return;
        }

        String st = "INSERT INTO MEMBER VALUES ("
                + "'" + mID + "',"
                + "'" + mName + "',"
                + "'" + mMobile + "',"
                + "'" + mEmail + "'"
                + ")";

        System.out.println(st);
        if (handler.execAction(st)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved");
            alert.showAndWait();
            return;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error");
            alert.showAndWait();
            return;
        }
    }

    public void populateEditMemberUI(MemberListController.Member member) {
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());

        isInEditMode = Boolean.TRUE;
    }

    private void handleUpdateMember() {
        Member member = new MemberListController.Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if (DatabaseHandler.getInstance().updateMember(member)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success");
            alert.setContentText("Member Updated");
            alert.showAndWait();
            return;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed");
            alert.setContentText("Can't update member");
            alert.showAndWait();
            return;
        }
    }

}
