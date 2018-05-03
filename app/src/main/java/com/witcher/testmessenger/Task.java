package com.witcher.testmessenger;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yhc on 2018/4/27.
 */

public class Task implements Parcelable {

    public int id;
    public long time;
    public String name;

    public Task(int id, long time, String name) {
        this.id = id;
        this.time = time;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        id = dest.readInt();
        time = dest.readLong();
        name = dest.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.time);
        dest.writeString(this.name);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.time = in.readLong();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", time=" + time +
                ", name='" + name + '\'' +
                '}';
    }
}
