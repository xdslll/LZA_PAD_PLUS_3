package com.lza.pad.app.socket.admin.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/4.
 */
public class CharsetCodeFactory implements ProtocolCodecFactory {

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return new MyCharsetEncoder();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new MyCharsetDecoder();
    }
}
