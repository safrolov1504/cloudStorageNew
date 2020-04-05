package com.cloudStorage.service.connection.handlers;


import com.cloudStorage.common.data.CreatCommand;
import com.cloudStorage.service.state.State;
import com.cloudStorage.service.state.StateSecond;
import com.cloudStorage.service.workWithFile.DelFileFromServer;
import com.cloudStorage.service.workWithFile.EditFileOnServer;
import com.cloudStorage.service.workWithFile.GetFileFromClient;
import com.cloudStorage.service.workWithFile.SendFileToClient;
import com.cloudStorage.service.workingWithMessage.ListFilesServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


import java.io.UnsupportedEncodingException;


// Идет после FirstHandler в конвеере
public class FileHandler extends ChannelInboundHandlerAdapter {
    private State state = State.IDLE;
    private StateSecond stateSecond = StateSecond.IDLE;

    private int fileNameLength;
    private String fileName;
    private String userName;
    private boolean flag = true;
    private String oldName;

    private ListFilesServer listFilesServer;
    private DelFileFromServer delFileFromServer;
    private SendFileToClient sendFileToClient;
    private GetFileFromClient getFileFromClient;
    private EditFileOnServer editFileOnServer;

    public FileHandler(String userName) {
        this.userName = userName;
        listFilesServer = new ListFilesServer();
        delFileFromServer = new DelFileFromServer();
        sendFileToClient = new SendFileToClient();
        getFileFromClient = new GetFileFromClient();
        editFileOnServer = new EditFileOnServer();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        while (buf.readableBytes()>0) {
            //work with command
                if (state == State.IDLE) {
                    byte read = buf.readByte();
                    if (read == CreatCommand.getSendListFileFromServer()) {
                        listFilesServer.sedList(ctx, userName);
                    } else if (read == CreatCommand.getSendFile()) {
                        state = State.NAME_LENGTH;
                        stateSecond = StateSecond.GET_FILE_FROM_CLIENT;
                    } else if (read == CreatCommand.getGetFile()) {
                        state = State.NAME_LENGTH;
                        stateSecond = StateSecond.SEND_FILE_TO_CLIENT;
                    } else if (read == CreatCommand.getDelFileFromServer()) {
                        state = State.NAME_LENGTH;
                        stateSecond = StateSecond.DEL_FILE_ON_SERVER;
                    } else if (read == CreatCommand.getEditFile()){
                        flag = true;
                        state = State.NAME_LENGTH;
                        stateSecond = StateSecond.EDIT_FILE_ON_SERVER;
                    } else {
                        System.out.println("ERROR: Invalid first byte - " + read);
                    }
                }

                if (state == State.NAME_LENGTH) {
                    fileName = getName(buf);
                }

                //analyse the second command
                if (stateSecond == StateSecond.GET_FILE_FROM_CLIENT) {
                    state = getFileFromClient.getFileFromClient(buf, ctx, fileName, userName, state);
                } else if (stateSecond == StateSecond.SEND_FILE_TO_CLIENT) {
                    sendFileToClient.sendFileToClient(fileName, userName, ctx);
                    state = State.IDLE;
                    stateSecond = StateSecond.IDLE;
                } else if (stateSecond == StateSecond.DEL_FILE_ON_SERVER) {
                    delFileFromServer.delFileFromServer(fileName, userName, ctx);
                    state = State.IDLE;
                    stateSecond = StateSecond.IDLE;
                } else if(stateSecond == StateSecond.EDIT_FILE_ON_SERVER){
                    if(flag){
                        oldName = fileName;
                        flag = false;
                        state = State.NAME_LENGTH;
                    } else {
                        editFileOnServer.edit(oldName, fileName, userName,ctx);
                        state = State.IDLE;
                        stateSecond = StateSecond.IDLE;
                    }
                }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    private String getName(ByteBuf buf) throws UnsupportedEncodingException {
        String fileName = null;
        if(state == State.NAME_LENGTH){
            if(buf.readableBytes() >= 4){
                fileNameLength = buf.readInt();
                state = State.NAME;
            }
        }

        if(state == State.NAME){
            if(buf.readableBytes()>= fileNameLength){
                byte[] tmpBuf = new byte[fileNameLength];
                buf.readBytes(tmpBuf);
                fileName = new String(tmpBuf,"UTF-8");
                state = State.FILE_LENGTH;
            }
        }
        return fileName;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
