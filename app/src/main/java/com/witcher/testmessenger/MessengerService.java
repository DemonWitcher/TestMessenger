package com.witcher.testmessenger;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;

public class MessengerService extends Service {

    public static final int HELLO = 999;

    private final Messenger messenger = new Messenger(new WorkHandler(this));

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
    private void hello(Message msg){
        try {
            Thread.sleep(4999);
        } catch (InterruptedException e) {
        }
        Log.i("witcher","hello  threadName:"+Thread.currentThread().getName());
        try {
            msg.replyTo.send(Message.obtain(null,MessengerActivity.HI));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private static class WorkHandler extends Handler{
        private WeakReference<MessengerService> serviceWeakReference;
        private WorkHandler(MessengerService service){
            serviceWeakReference = new WeakReference<>(service);
        }
        private MessengerService getService(){
            if(serviceWeakReference == null){
                return null;
            }
            return serviceWeakReference.get();
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessengerService service = getService();
            if(service == null){
                return;
            }
            switch (msg.what){
                case HELLO:{
                    service.hello(msg);
                }break;
            }
        }
    }
}
