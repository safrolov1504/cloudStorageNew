package com.cloudStorage.service.connection.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SendBack {
    public static void sendBack(ChannelHandlerContext ctx, byte [] arr){
        ByteBuf buf = ctx.alloc().buffer(arr.length);
        buf.writeBytes(arr);

        //System.out.println("here "+ Arrays.toString(arr));
        ctx.writeAndFlush(buf);
    }

    //почему то не работает??
    public static void sendBack(ChannelHandlerContext ctx, byte [] arr, int sizeToSend){
        ByteBuf buf = ctx.alloc().buffer(sizeToSend);
        buf.writeBytes(arr);
        ctx.writeAndFlush(buf);
    }

    public static void sendBack(ChannelHandlerContext ctx, int intOut){
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.putInt(intOut);
////        ctx.writeAndFlush(buf);
//        System.out.println(Arrays.toString(buf.array()));
//        System.out.println(Arrays.toString(intToByteArray(intOut)));
        sendBack(ctx,buf.array());
        //ctx.writeAndFlush(buf);
    }

    public static void sendBack(ChannelHandlerContext ctx, long longOut){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(longOut);
        sendBack(ctx,buffer.array());
    }

    public static void sendBack(ChannelHandlerContext ctx, byte arr){
        ByteBuf buf = ctx.alloc().buffer(1);
        buf.writeByte(arr);
        ctx.writeAndFlush(buf);
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
