package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DelFileFromServer {
    private File file;

    public void delFileFromServer(String fileName, String userName, ChannelHandlerContext ctx) throws FileNotFoundException {
        file = new File("cloud-service/global-storage/"+userName+"/"+fileName);

        if (!file.exists()) {
            SendBack.sendBack(ctx, CreatCommand.getDelFileFromServerNOk());
            return;
        }

        //FileInputStream fileInputStream = new FileInputStream(file.getPath());
        file.delete();

        SendBack.sendBack(ctx, CreatCommand.getDelFileFromServerOk());
        System.out.println("Server: File "+ fileName + " was deleted");
    }
}
