package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import com.cloudStorage.service.state.State;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;

public class GetFileFromClient {
    private static int SIZE_CASH = 1024;
    private long fileLength;
    private byte[] cash = new byte[SIZE_CASH];
    private int iCash;
    private int receivedFileLength;
    private File file;
    private int tmp = 0;
    private OutputStream fileOutputStream;
    //private FileOutputStream fileOutputStream;

    public State getFileFromClient(ByteBuf buf, ChannelHandlerContext ctx, String fileName, String userName, State state) throws IOException {
        if(state == State.FILE_LENGTH){
            if(buf.readableBytes()>= 8){
                fileLength = buf.readLong();
                //preparation to take file
                //iCash =0;
                //receivedFileLength = 0;

                //getting ready for taking the file
                //creat folders if it needs
                file = new File("cloud-service/global-storage/"+userName);
                if(!file.exists()){
                    file.mkdirs();
                }

                //creat file
                file = new File(file.getPath()+"/"+ fileName);
                fileOutputStream = new FileOutputStream(file);
                receivedFileLength = 0;
                iCash = 0;
                System.out.println("Server: Start to get file from " + userName+", name file "+ fileName +", size file "+ receivedFileLength);
                state = State.FILE;
                if(fileLength < SIZE_CASH){
                    cash = new byte[(int)(fileLength)];
                } else {
                    cash = new byte[SIZE_CASH];
                }
                //tmp = 0;
            }
        }

        if(state == State.FILE){
            //file = new File(file.getPath()+"/"+ fileName);
                while (buf.readableBytes() > 0){
                    cash[iCash] = buf.readByte();
                    iCash++;
                    receivedFileLength ++;
                    //System.out.println(iCash +" "+receivedFileLength);
                    if(iCash == cash.length){
                        iCash = 0;
                        //System.out.println(Arrays.toString(cash));
                        fileOutputStream.write(cash);
                        if((fileLength-receivedFileLength) < SIZE_CASH && fileLength != receivedFileLength){
                            cash = new byte[(int)(fileLength-receivedFileLength)];
                        }
                    }

                    if(fileLength == receivedFileLength){
                        //fileOutputStream.write(cash);
                        state = State.IDLE;
                        fileOutputStream.close();
                        System.out.println("File was gotten from " + userName+", name file "+ fileName +", size file "+ fileLength);
                        SendBack.sendBack(ctx, CreatCommand.getSendFileOk());
                        break;
                    }
                }

        }
        return state;
    }
}
