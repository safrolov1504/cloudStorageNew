package com.cloudStorage.service.workWithFile;

import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.connection.handlers.SendBack;
import com.cloudStorage.service.state.State;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;

public class GetFileFromClient {
    private static int SIZE_CASH = 1024;
    private long fileLength;
    private byte[] cash;// = new byte[SIZE_CASH];
    private int iCash;
    private int receivedFileLength;
    private File file;
    private int tmp = 0;
    private OutputStream fileOutputStream;
    private BufferedOutputStream bfo;

    public GetFileFromClient() {
    }

    public State getFileFromClient(ByteBuf buf, ChannelHandlerContext ctx, String fileName, String userName, State state) throws IOException {
        if(state == State.FILE_LENGTH){
            if(buf.readableBytes()>= 8){
                fileLength = buf.readLong();

                //creat file
                file = new File("cloud-service/global-storage/"+userName+"/"+ fileName);
                fileOutputStream = new FileOutputStream(file);
                bfo = new BufferedOutputStream(fileOutputStream,SIZE_CASH);
                receivedFileLength = 0;
                iCash = 0;
                System.out.println("Server: Start to get file from " + userName+", name file "+ fileName +", size file "+ receivedFileLength);
                state = State.FILE;
                if(fileLength < SIZE_CASH){
                    cash = new byte[(int)(fileLength)];
                } else {
                    cash = new byte[SIZE_CASH];
                }
            }
        }

        if(state == State.FILE){
                while (buf.readableBytes() > 0){
                    bfo.write(buf.readByte());
                    receivedFileLength ++;

                    if(fileLength == receivedFileLength){
                        state = State.IDLE;
                        bfo.close();
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
