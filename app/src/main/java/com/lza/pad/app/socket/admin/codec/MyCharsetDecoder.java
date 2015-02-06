package com.lza.pad.app.socket.admin.codec;

import com.lza.pad.support.debug.AppLogger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class MyCharsetDecoder implements ProtocolDecoder {

    private final static Charset CHARSET = Charset.forName("UTF-8");

    private IoBuffer buff = IoBuffer.allocate(100).setAutoExpand(true);

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        AppLogger.e("#############decode#############");

        while (in.hasRemaining()) {
            byte b = in.get();
            if (b == '\n') {
                buff.flip();
                byte[] bytes = new byte[buff.limit()];
                buff.get(bytes);
                String message = new String(bytes, CHARSET);

                buff = IoBuffer.allocate(100).setAutoExpand(true);

                out.write(message);
            } else {
                buff.put(b);
            }
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        AppLogger.e("#############finishDecode#############");
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        AppLogger.e("#############dispose#############");
        AppLogger.e(session.getCurrentWriteMessage().toString());
    }
}
