package com.insightfullogic.honest_profiler.delivery.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class EventListener {

    public void handleOpen(ChannelHandlerContext ctx, FullHttpRequest request) {

    }

    public void handleText(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        ctx.channel().write(new TextWebSocketFrame(frame.text().toUpperCase()));
    }

    public void handleClose(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {

    }

}
