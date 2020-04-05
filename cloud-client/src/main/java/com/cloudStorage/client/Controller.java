package com.cloudStorage.client;

import com.cloudStorage.client.communication.MyClientServer;
import com.cloudStorage.client.controllers.CreatAlert;
import com.cloudStorage.client.controllers.WorkWithTables;
import com.cloudStorage.client.workingWithMessage.GetMessage;
import com.cloudStorage.client.workingWithMessage.SendMessage;
import com.cloudStorage.common.data.FileForTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    //Login window
    public TextField textField_login;
    public PasswordField testField_pass;


    //work window
    //client
    public ProgressBar pb_client;
    public TableView<FileForTable> table_client;
    public TableColumn<FileForTable, String> table_clientName;
    public TableColumn<FileForTable,String> table_clientSize;
    public TableColumn<FileForTable,String> table_clientDate;
//    public ObservableList<FileForTable> fileDataClient = FXCollections.observableArrayList();


    //server
    public TableView<FileForTable> table_service;
    public TableColumn<FileForTable,String> table_serverName;
    public TableColumn<FileForTable,String> table_serverSize;
    public TableColumn<FileForTable,String> table_serverDate;
    public GridPane editWindow;
    public HBox mainWindow;

    //public ProgressBar pb_server;
//    public ObservableList<FileForTable> fileDataService = FXCollections.observableArrayList();

    private MyClientServer messageService;
    private SendMessage sendMessage;
    private WorkWithTables workWithTables;
    private GetMessage getMessage;

    public void shutdown() {
        System.exit(0);
    }

    public WorkWithTables getWorkWithTables() {
        return workWithTables;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            this.messageService = App.getMessageService();
            this.sendMessage = new SendMessage(this.messageService.getNetwork());
            App.getMessageService().getNetwork().getGetMessage().setController(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(App.isFlag()){
            //creatTables();
            workWithTables = new WorkWithTables(this,sendMessage);
            workWithTables.updateTableClient();
            workWithTables.updateTableService();
        }
    }

    //button login
    @FXML
    public void login_buttonSignIn(ActionEvent actionEvent) {
        sendMessage.sendSighIn(textField_login.getText(), testField_pass.getText());
    }

    //button clients
    @FXML
    public void button_sendToService(ActionEvent actionEvent) {
        File file;

        FileForTable selectedFile = table_client.getSelectionModel().getSelectedItem();
        //System.out.println(selectedFile);
        if(selectedFile !=null){
            file = new File("cloud-client/storage/"+selectedFile.nameFileTable);
                sendMessage.sendFileToServer(file);
                //workWithTables.updateTableService();
        }
    }

    @FXML
    public void button_delete(ActionEvent actionEvent) {
        File file;
        FileForTable selectedFile = table_client.getSelectionModel().getSelectedItem();
        //System.out.println(selectedFile);
        if(selectedFile !=null) {
            file = new File("cloud-client/storage/" + selectedFile.nameFileTable);
            file.delete();
        }
        workWithTables.updateTableClient();
        CreatAlert.setAlert(Alert.AlertType.INFORMATION,"File on client", "File was deleted");
    }

    @FXML
    public void button_edit(ActionEvent actionEvent) {
        FileForTable selectedFile = table_client.getSelectionModel().getSelectedItem();
        if(selectedFile != null){
            Optional<String> result = editName(selectedFile.nameFileTable);
            result.ifPresent(name -> {
                System.out.println("Client: change name on client. Old name "+ selectedFile.nameFileTable +". New name: " + name);
                changeNameOnClient(name,selectedFile.nameFileTable);
            });
        }

    }

    private void changeNameOnClient(String nameNew, String nameOld) {
        File fileOld = new File("cloud-client/storage/"+nameOld);
        File fileNew = new File("cloud-client/storage/"+nameNew);
        fileOld.renameTo(fileNew);
        workWithTables.updateTableClient();
    }

    //button server
    @FXML
    public void button_sendToClient(ActionEvent actionEvent) {
        FileForTable selectedFile = table_service.getSelectionModel().getSelectedItem();
        System.out.println(selectedFile);
        if(selectedFile !=null) {
            sendMessage.getFileFromService(selectedFile.nameFileTable);
        }
    }



    public void button_delService(ActionEvent actionEvent) {
        FileForTable fileForTable = table_service.getSelectionModel().getSelectedItem();
        System.out.println("Server del file "+ fileForTable);
        if(fileForTable != null){
            sendMessage.sendDelFileFromServer(fileForTable.nameFileTable);
        }
    }

    public void button_EditService(ActionEvent actionEvent) {
        FileForTable selectedFile = table_service.getSelectionModel().getSelectedItem();
        Optional<String> result =editName(selectedFile.nameFileTable);
        result.ifPresent(name -> {
            System.out.println("Client: change name on server. Old name "+ selectedFile.nameFileTable + ". New name: " + name);
            sendMessage.sendEditFile(selectedFile.nameFileTable, name);
        });
    }

    private Optional<String> editName(String nameFileTable){
        TextInputDialog textInputDialog = new TextInputDialog(nameFileTable);
        textInputDialog.setTitle("Change name");
        textInputDialog.setHeaderText("Write a new name:");
        textInputDialog.setContentText("New name:");
        return textInputDialog.showAndWait();
    }


    @FXML
    public void button_exit(ActionEvent actionEvent) {
        System.exit(0);
    }

}
