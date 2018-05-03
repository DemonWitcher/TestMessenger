package com.witcher.testmessenger;
import com.witcher.testmessenger.Task;
interface ITaskCallBack {

    oneway void onAddTask();
    oneway void onGetTask(in Task task);

}