package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class EditFileOnServer {
    private File fileOld;
    private File fileNew;

    public void edit(String nameOld, String nameNew, String userName, ChannelHandlerContext ctx) {
        fileOld = new File("cloud-service/global-storage/"+userName+"/"+nameOld);
        fileNew = new File("cloud-service/global-storage/"+userName+"/"+nameNew);

        Boolean flag = fileOld.renameTo(fileNew);
        if(flag){
            SendBack.sendBack(ctx, CreatCommand.getEditFileOk());
            System.out.println("Server: file "+ nameOld+ " was renamed. New name "+ nameNew);
        } else {
            SendBack.sendBack(ctx,CreatCommand.getEditFileNOk());
        }
    }
}
