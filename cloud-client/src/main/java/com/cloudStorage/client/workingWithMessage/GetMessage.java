package com.cloudStorage.client.workingWithMessage;

import com.cloudStorage.client.App;
import com.cloudStorage.client.Controller;
import com.cloudStorage.client.controllers.ChangeStage;
import com.cloudStorage.client.controllers.CreatAlert;
import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.common.data.FileForTable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;

public class GetMessage {

    private Controller controller;
    public ObservableList<FileForTable> fileDataService = FXCollections.observableArrayList();
    //private WorkFile workFile;

    private static int CASH_SIZE = 1024*3;
    private byte [] cash = new byte[CASH_SIZE];
    private long lengthFile;
    private File file;

    public GetMessage() {
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void getListFile(String str){
        fileDataService.clear();


        if(!str.equals("")){
            String [] subString = str.split(FileForTable.getEnd());
            for (String strFile:subString) {
                fileDataService.add(new FileForTable(strFile));
            }
        }
        System.out.println("Client: End to get list file from server");
        controller.getWorkWithTables().addInfoTableService(fileDataService);
    }

    //get file from server
    public void getFileFromService(String nameFile, DataInputStream inputStream) {
        //get length of file

        try {
            lengthFile = 0;
            cash = new byte[CASH_SIZE];

            //get length file
            lengthFile = inputStream.readLong();

            System.out.println("Client: Start to get file from service "+ nameFile +" size: "+lengthFile);

            file = new File("cloud-client/storage/"+nameFile);

            try (OutputStream fileOutputStream = new FileOutputStream(file)){
                while (lengthFile>0) {
                    if (lengthFile < CASH_SIZE) {
                        cash = new byte[(int) lengthFile];
                    }
                    inputStream.read(cash);
                    fileOutputStream.write(cash);
                    //System.out.println(Arrays.toString(cash));
                    lengthFile -= cash.length;
                }

                System.out.println("Client: End to get file from service "+ nameFile +" size: "+lengthFile);
                controller.getWorkWithTables().updateTableClient();
                Platform.runLater(() ->CreatAlert.setAlert(Alert.AlertType.INFORMATION,
                        "File", "File was got"));
            } catch (FileNotFoundException e) {
                System.out.println("Client ERROR: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Client ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Client ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //get information from server about authentication
    public void infoAuthIn(byte innerByte) {
        String userName = controller.textField_login.getText();
        if(innerByte == CreatCommand.getCommandAuthOk()){
            App.setFlag(true);
            System.out.println("Client: Checking " + userName + " is ok");
            ChangeStage.changeStageDo((Stage) controller.testField_pass.getScene().getWindow(),
                    "/com.cloud.client/workInterface.fxml","Working window "+ controller.textField_login.getText());
        } else if(innerByte == CreatCommand.getCommandAuthNok()){

            System.out.println("Client: Checking " + userName + " is not ok");
            CreatAlert.setAlert(Alert.AlertType.WARNING,"Authentication is failed","Wrong user or password");
        }
    }

    //get information about sending file to the server
    public void infoSendFileToServer(byte innerByte){
        if(innerByte == CreatCommand.getSendFileOk()){
            CreatAlert.setAlert(Alert.AlertType.INFORMATION, "File", "File was send");
            controller.getWorkWithTables().updateTableService();
        }   else {
            CreatAlert.setAlert(Alert.AlertType.WARNING, "File", "File was not send");
        }
    }

    //get information about deleting file from the server
    public void infoDelFileOnServer(byte innerByte){
        //workFile.delFileReady(innerByte);
        if(innerByte == CreatCommand.getDelFileFromServerOk()){
            CreatAlert.setAlert(Alert.AlertType.INFORMATION, "File", "File was deleted");
            controller.getWorkWithTables().updateTableService();
        }   else {
            CreatAlert.setAlert(Alert.AlertType.WARNING, "File", "File was not deleted");
        }
    }


    public void infoEditFileOnServer(byte innerByte) {
        if(innerByte == CreatCommand.getEditFileOk()){
            CreatAlert.setAlert(Alert.AlertType.INFORMATION, "File", "File was renamed");
            controller.getWorkWithTables().updateTableService();
        }   else {
            CreatAlert.setAlert(Alert.AlertType.WARNING, "File", "File was not renamed");
        }
    }
}
