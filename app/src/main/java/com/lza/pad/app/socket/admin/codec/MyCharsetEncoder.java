package com.lza.pad.app.socket.admin.codec;

import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

import java.nio.charset.Charset;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MyCharsetEncoder implements ProtocolEncoder {

    private final static Charset CHARSET = Charset.forName("UTF-8");

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        AppLogger.e("#############encode#############");
        IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);
        buff.putString(message.toString(), CHARSET.newEncoder());
        buff.putString(LineDelimiter.DEFAULT.getValue(), CHARSET.newEncoder());
        buff.flip();

        out.write(buff);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        AppLogger.e("#############dispose#############");
    }
}
