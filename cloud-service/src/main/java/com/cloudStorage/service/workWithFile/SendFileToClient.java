package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.*;
import java.nio.ByteBuffer;

public class SendFileToClient {
    private static int SIZE_CASH = 1024;

    public void sendFileToClient(String fileName, String userName, ChannelHandlerContext ctx) {
        File file = new File("cloud-service/global-storage/" + userName + "/" + fileName);

        //if file is not exist, then send error command
        if (!file.exists()) {
            SendBack.sendBack(ctx, CreatCommand.getGetFileNOk());
        } else {
            try (InputStream fileInputStream = new FileInputStream(file.getPath())){
                //fileInputStream = new FileInputStream(file.getPath());
                //send command
                SendBack.sendBack(ctx, CreatCommand.getGetFileOk());

                //send file's name
                SendBack.sendBack(ctx, fileName.length());

                //send name
                SendBack.sendBack(ctx, fileName.getBytes());

                //send length of file
                long lengthFile = file.length();
                SendBack.sendBack(ctx, lengthFile);

                //send file
                boolean flag=true;
                int i;
                byte [] byteArray = new byte[SIZE_CASH];
                System.out.println("Server: file "+fileName+ " start to send. Length "+lengthFile);
                while (flag){
                    if(lengthFile<SIZE_CASH){
                        byteArray = new byte[(int) lengthFile];
                        flag = false;
                    }
                    i = fileInputStream.read(byteArray);
                    lengthFile-=i;
                    SendBack.sendBack(ctx,byteArray);
                }
                System.out.println("Server: file "+ fileName + " was send to "+ userName + " Length " + lengthFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
