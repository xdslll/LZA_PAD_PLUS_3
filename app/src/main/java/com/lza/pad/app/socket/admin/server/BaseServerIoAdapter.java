package com.lza.pad.app.socket.admin.server;

import com.google.gson.Gson;
import com.lza.pad.app.socket.model.MinaClient;
import com.lza.pad.helper.GsonHelper;

import org.apache.mina.core.session.IoSession;

import de.greenrobot.event.EventBus;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 3/2/15.
 */
public class BaseServerIoAdapter extends OnServerIoAdapter {

    @Override
    public void onMessageReceived(IoSession session, Object message) {
        super.onMessageReceived(session, message);
        Gson gson = GsonHelper.instance();
        try {
            MinaClient client = gson.fromJson(message.toString(), MinaClient.class);
            client.setSession(session);
            MinaServerHelper.instance().addClient(client);
            if (client.getAction().equals(MinaClient.ACTION_CONNECT)) {
                EventBus.getDefault().post(client);
            } else if (client.getAction().equals(MinaClient.ACTION_SHAKE)) {
                //MinaServerHelper.instance().sendOK(client.getSession(), MinaServer.ACTION_SHAKE);
                EventBus.getDefault().post(client);
            } else if (client.getAction().equals(MinaClient.ACTION_APPLY_FOR_DOWNLOAD_FILE)) {
                EventBus.getDefault().post(client);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
