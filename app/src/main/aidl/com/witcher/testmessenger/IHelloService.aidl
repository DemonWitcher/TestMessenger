// IHelloService.aidl
package com.witcher.testmessenger;

// Declare any non-default types here with import statements
import com.witcher.testmessenger.ITaskCallBack;
import com.witcher.testmessenger.Task;

interface IHelloService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void hello();
    oneway void helloMyString(String string);
    int getPID();
    void startTask(inout Task tast);
    Task getLastTask();
    oneway void getLastTaskByCallBack();
    oneway void registerCallback(in ITaskCallBack callback);
    oneway void unregisterCallback(in ITaskCallBack callback);
}
