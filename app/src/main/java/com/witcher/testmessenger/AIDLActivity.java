package com.witcher.testmessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AIDLActivity extends AppCompatActivity {

    /**
     * messenger OK
     * messenger双向通信 OK
     * aidl OK
     * aidl in out inout oneway OK
     * aidl 回调  OK
     * aidl 有返回值的不阻塞 用回调就行 回调在子线程里 OK
     *
     * 主进程的主线程 调用子进程的子线程 阻塞UI吗？ 阻塞
     * 怎么才能不阻塞  用Oneway
     * 但是oneway不能带返回值 怎么带返回值 用回调
     */

    @BindView(R.id.bind)
    Button bind;
    @BindView(R.id.hello)
    Button hello;
    @BindView(R.id.hello_my_string)
    Button helloMyString;
    @BindView(R.id.get_pid)
    Button getPid;
    @BindView(R.id.main_hello)
    Button mainHello;
    @BindView(R.id.send_task)
    Button sendTask;
    @BindView(R.id.get_task)
    Button getTask;
    @BindView(R.id.get_task_by_call_back)
    Button getTaskByCallBack;
    private boolean mBound;
    private IHelloService mHelloService;
    private Task task = new Task(1,1000L,"主进程给的新任务");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        ButterKnife.bind(this);
    }

    private ITaskCallBack.Stub mITaskCallBack = new ITaskCallBack.Stub() {
        @Override
        public void onAddTask() throws RemoteException {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            L.i("onAddTask threadName:"+Thread.currentThread().getName());
        }

        @Override
        public void onGetTask(Task task) throws RemoteException {
            L.i("onGetTask "+task.toString()+" threadName:"+Thread.currentThread().getName());
        }

    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mHelloService = IHelloService.Stub.asInterface(service);
            mBound = true;
            try {
                mHelloService.registerCallback(mITaskCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mHelloService = null;
            mBound = false;
        }
    };

    @OnClick({R.id.bind, R.id.hello, R.id.hello_my_string, R.id.get_pid, R.id.main_hello,R.id.send_task, R.id.get_task,R.id.get_task_by_call_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bind: {
                bindService(new Intent(this, AIDLService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            }
            break;
            case R.id.hello: {
                hello();
            }
            break;
            case R.id.hello_my_string: {
                helloMyString();
            }
            break;
            case R.id.get_pid: {
                getPid();
            }
            break;
            case R.id.main_hello: {
                L.i("主进程 hello  threadName:" + Thread.currentThread().getName()+",  task id:"+task.id);
            }
            break;
            case R.id.send_task:{
                sendTask();
            }
                break;
            case R.id.get_task:{
                getTask();
            }
                break;
            case R.id.get_task_by_call_back:{
                getTaskByCallBack();
            }
            break;
        }
    }

    private void getTaskByCallBack() {
        try {
            mHelloService.getLastTaskByCallBack();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void getTask() {
        try {
            Task task = mHelloService.getLastTask();
            L.i("拿到子进程任务:"+task.toString()+",threadName:"+Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTask() {
        try {
            mHelloService.startTask(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getPid() {
        try {
            L.i("主进程 pid:" + mHelloService.getPID()+",threadName:"+Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void helloMyString() {
        if (!mBound) {
            return;
        }
        try {
            mHelloService.helloMyString("aaa");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void hello() {
        if (!mBound) {
            return;
        }
        try {
            mHelloService.hello();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            if(mHelloService!=null){
                try {
                    mHelloService.unregisterCallback(mITaskCallBack);
                } catch (RemoteException e) {
                }
            }
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

}
