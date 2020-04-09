package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SendFileToClient {
    private static int SIZE_CASH = 1024*3;

    public void sendFileToClient(String fileName, String userName, ChannelHandlerContext ctx) {
        File file = new File("cloud-service/global-storage/" + userName + "/" + fileName);

        //if file is not exist, then send error command
        if (!file.exists()) {
            SendBack.sendBack(ctx, CreatCommand.getGetFileNOk());
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(file.getPath())){
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
                System.out.println("Server: file "+fileName+ " start to send. Length "+lengthFile);
                FileRegion fileRegion = new DefaultFileRegion(fileInputStream.getChannel(),0,lengthFile);
                ctx.writeAndFlush(fileRegion);
                System.out.println("Server: file "+ fileName + " was send to "+ userName + " Length " + lengthFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
