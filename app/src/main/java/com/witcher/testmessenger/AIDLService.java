package com.witcher.testmessenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

import java.util.ArrayList;

public class AIDLService extends Service {

    private ArrayList<ITaskCallBack> mITaskCallBacks = new ArrayList<>();

    public AIDLService() {
    }

    private final IHelloService.Stub mBinder = new IHelloService.Stub() {

        @Override
        public void hello() throws RemoteException {
            L.i("子进程 hello threadName:"+Thread.currentThread().getName());

        }

        @Override
        public void helloMyString(String string) throws RemoteException {
            L.i("子进程 "+string+" threadName:"+Thread.currentThread().getName());
        }

        @Override
        public int getPID() throws RemoteException {
            L.i("子进程 getPid threadName:"+Thread.currentThread().getName());
            return Process.myPid();
        }

        @Override
        public void startTask(Task tast) throws RemoteException {
            tast.id = 99;
            L.i("子进程 获取任务 :"+tast.toString()+",threadName:"+Thread.currentThread().getName());
            for(ITaskCallBack callBack:mITaskCallBacks){
                if(callBack!=null){
                    callBack.onAddTask();
                }
            }
        }

        @Override
        public Task getLastTask() throws RemoteException {
            try {
                Thread.sleep(10000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            L.i("子进程 getPid getLastTask:"+Thread.currentThread().getName());
            return new Task(99, 100L,"远程TASK");
        }

        @Override
        public void getLastTaskByCallBack() throws RemoteException {
            try {
                Thread.sleep(10000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            L.i("子进程 getPid getLastTask:"+Thread.currentThread().getName());
            Task task = new Task(99, 100L,"远程TASK");
            for(ITaskCallBack callBack:mITaskCallBacks){
                if(callBack!=null){
                    callBack.onGetTask(task);
                }
            }
        }

        @Override
        public void registerCallback(ITaskCallBack callback) throws RemoteException {
            mITaskCallBacks.add(callback);
        }

        @Override
        public void unregisterCallback(ITaskCallBack callback) throws RemoteException {
            mITaskCallBacks.remove(callback);
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
