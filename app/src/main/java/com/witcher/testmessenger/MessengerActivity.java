package com.witcher.testmessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

public class MessengerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtBind;
    private Button mBtHello;
    private Button mBtMainHello;
    private Messenger mMessenger;

    private boolean mBound;

    public static final int HI = 666;


    private final Messenger mClientMessenger = new Messenger(new ClientHandler(this));

    private static class ClientHandler extends Handler {
        private WeakReference<MessengerActivity> activityWeakReference;
        private ClientHandler(MessengerActivity activity){
            activityWeakReference = new WeakReference<>(activity);
        }
        private MessengerActivity getActivity(){
            if(activityWeakReference == null){
                return null;
            }
            return activityWeakReference.get();
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessengerActivity activity = getActivity();
            if(activity == null){
                return;
            }
            switch (msg.what){
                case HI:{
                    activity.hi();
                }break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        initView();
    }

    private void initView() {
        mBtBind = findViewById(R.id.bind);
        mBtHello = findViewById(R.id.hello);
        mBtMainHello = findViewById(R.id.main_hello);

        mBtBind.setOnClickListener(this);
        mBtHello.setOnClickListener(this);
        mBtMainHello.setOnClickListener(this);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            mBound = false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind: {
                bindService(new Intent(this, MessengerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            }
            break;
            case R.id.hello: {
                hello();
            }
            break;
            case R.id.main_hello: {
                Log.i("witcher","main hello  threadName:"+Thread.currentThread().getName());
            }
            break;
        }
    }
    private void hello(){
        if(!mBound){
            return;
        }
        Message msg = Message.obtain(null,MessengerService.HELLO);
        msg.replyTo = mClientMessenger;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void hi(){
        L.i("客户端 收到了 来自服务端的 HI 消息 threanName:"+Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }
}
