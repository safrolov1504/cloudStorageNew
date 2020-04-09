package com.cloudStorage.client.communication;

import com.cloudStorage.client.Controller;
import com.cloudStorage.client.workingWithMessage.GetMessage;
import com.cloudStorage.common.data.CreatCommand;
import com.sun.security.jgss.GSSUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

public class Network {
    private final String serverAddress;
    private final int port;
    private final MyClientServer myClientServer;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Scanner scannerIn;
    private GetMessage getMessage;
    private static int SIZE_CASH = 1024;

    //for method get String
    private byte [] byteIn;
    private byte [] cash;
    private int getSize;
    private StringBuilder sb;
    private int length;

    public GetMessage getGetMessage() {
        return getMessage;
    }

    public Network(String serverAddress, int port, MyClientServer myClientServer) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.myClientServer = myClientServer;
        this.getMessage = new GetMessage();

        try {
            //it's first connection or not
            initNetworkState(serverAddress, port);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connection is failed");
            alert.setContentText("Нет подключения в серверу");
            alert.showAndWait();
        }
    }

    //creat connection
    private void initNetworkState(String serverAddress, int port) throws IOException {
        this.socket = new Socket(serverAddress,port);
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.scannerIn = new Scanner(socket.getInputStream());

        //подключили и ждем сообщений
        getMessage();
    }

    public void getMessage(){
        new Thread(() ->{
            while (true){
                //waiting for message
                try {
                    byte inByte = inputStream.readByte();
                    sortInnerMessage(inByte);
                } catch (IOException e){
                    System.exit(0);
                }
            }
        }).start();
    }

    public void sortInnerMessage(byte innerByte) throws IOException {
        if (innerByte == CreatCommand.getCommandAuthOk() || innerByte == CreatCommand.getCommandAuthNok()) {
            //info from server about auth
            System.out.println("Client: Get info from server. Result of authentication");
            Platform.runLater(() -> getMessage.infoAuthIn(innerByte));
        } else if (innerByte == CreatCommand.getSendFileOk() || innerByte == CreatCommand.getSendFileNOk()) {
            //info about sending file to the server
            System.out.println("Client: Get info from server. Result of sending file to the server");
            Platform.runLater(() -> getMessage.infoSendFileToServer(innerByte));
        } else if (innerByte == CreatCommand.getDelFileFromServerOk() || innerByte == CreatCommand.getDelFileFromServerNOk()) {
            //info about deleting file on the server
            System.out.println("Client: get info from server about deleting file on the server");
            Platform.runLater(() -> getMessage.infoDelFileOnServer(innerByte));
        } else if (innerByte == CreatCommand.getSendListFileFromService()) {
            //get list of file from server
            System.out.println("Client: get info from server. List of files on the server is getting");
            String listFile = getStringFromServer();
            getMessage.getListFile(listFile);
        } else if (innerByte == CreatCommand.getGetFileOk() || innerByte == CreatCommand.getGetFileNOk()) {
            //get file from server
            System.out.println("Client: get info from server. File from the server is getting");
            String nameFile = getStringFromServer();
            getMessage.getFileFromService(nameFile, inputStream);
        } else if(innerByte == CreatCommand.getEditFileNOk() || innerByte == CreatCommand.getEditFileOk()){
            System.out.println("Client: get info from server. File from the server is edited");
            Platform.runLater(() -> getMessage.infoEditFileOnServer(innerByte));
        } else {
            System.out.println("Unexpected value: " + innerByte);
        }
    }



    public String getStringFromServer() throws IOException {

        //waiting for size of string (name or list from service)
        length = inputStream.readInt();

        if(length < SIZE_CASH){
            //if length < size of cash
            byteIn = new byte[length];
            inputStream.read(byteIn);
            return new String(byteIn,"UTF-8");
        }
        else {
            sb.delete(0,sb.length());
            getSize = 0;
            cash = new byte[SIZE_CASH];
            while (getSize < length) {
                if (length - getSize < SIZE_CASH) {
                    cash = new byte[length - getSize];
                }
                inputStream.read(cash);
                sb.append(new String(cash,"UTF-8"));
                getSize += SIZE_CASH;
            }
        }
        return sb.toString();
    }

    public void send(int intIn) {
        try {
            outputStream.writeInt(intIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(long longIn){
        try {
            outputStream.writeLong(longIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(byte byteIn){
        try {
            outputStream.writeByte(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(byte[] outByte) {
        try {
            outputStream.write(outByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] outByte,int off,int length) {
        try {
            outputStream.write(outByte,off,length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
