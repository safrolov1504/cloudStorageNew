package com.cloudStorage.client.workingWithMessage;


import com.cloudStorage.client.communication.Network;
import com.cloudStorage.common.data.CreatCommand;

import java.io.*;
import java.sql.SQLOutput;


public class SendMessage {
    private Network network;
    private static int CASH_SIZE = 1024;

    private long lengthFile;
    private byte[] byteArray;
    private int i;

    public SendMessage(Network network) {
        this.network = network;
    }

    public void sendSighIn(String login, String pass) {
        network.send(CreatCommand.getCommandAuth());

        network.send(login.length());
        network.send(login.getBytes());
        network.send(pass.length());
        network.send(pass.getBytes());
    }

    public void sendFileToServer(File file) {
        try(InputStream fileInputStream = new FileInputStream(file.getPath())) {
            lengthFile = file.length();
            byteArray = new byte[CASH_SIZE];

            //send command
            network.send(CreatCommand.getSendFile());

            //send name and length of name
            network.send(file.getName().length());
            network.send(file.getName().getBytes());

            //send length of file
            network.send(lengthFile);

            //start working with file
            System.out.println("Client: send file to server: start "+file.getName()+ "length "+ lengthFile);
            while (lengthFile > 0) {
                i = fileInputStream.read(byteArray);
                if (lengthFile < CASH_SIZE) {
                    byteArray = new byte[(int) lengthFile];
                    network.send(byteArray,0, (int) lengthFile);
                } else {
                    network.send(byteArray);
                }
                lengthFile -= i;
            }
            System.out.println("Client: send file to server: finish "+file.getName()+ "length "+ lengthFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequestToGetListFileFromService() {
        System.out.println("Client: Send command to server to update list "+ CreatCommand.getSendListFileFromService());
        network.send(CreatCommand.getSendListFileFromService());
    }

    public void getFileFromService(String nameFile) {
        network.send(CreatCommand.getGetFile());
        network.send(nameFile.length());
        network.send(nameFile.getBytes());
    }

    public void sendDelFileFromServer(String nameFile) {
        network.send(CreatCommand.getDelFileFromServer());
        network.send(nameFile.length());
        network.send(nameFile.getBytes());
    }

    public void sendEditFile(String nameOld, String nameNew) {
        network.send(CreatCommand.getEditFile());
        network.send(nameOld.length());
        network.send(nameOld.getBytes());
        network.send(nameNew.length());
        network.send(nameNew.getBytes());
    }
}
