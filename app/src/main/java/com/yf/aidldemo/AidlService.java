package com.yf.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.yf.aidldemo.IMyAidlInterface;
import com.yf.aidldemo.bean.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName :  com.yf.aidldemo.AidlService
 * Author   :  zhizhongbiao
 * Date     :  2018/8/9
 * Describe :
 */

public class AidlService extends Service {


    private final List persons = new ArrayList<Person>();

    private final RemoteCallbackList<IOnNewPersonArrivedListener> remoteCallbacks = new RemoteCallbackList<IOnNewPersonArrivedListener>();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("AidlService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("AidlService onBind");
        return myBinder;
    }


    private final IMyAidlInterface.Stub myBinder = new IMyAidlInterface.Stub() {

        @Override
        public void addPersonIn(Person person) throws RemoteException {
            if (person == null) {
                LogUtils.e("addPersonIn   person==null");
            } else {
                onNewPersonArrived(person);
            }
        }

        @Override
        public void addPersonOut(Person person) throws RemoteException {
            if (person == null) {
                LogUtils.e("addPersonOut   person==null");
            } else {
                onNewPersonArrived(person);
            }
        }

        @Override
        public void addPersonInOut(Person person) throws RemoteException {
            if (person == null) {
                LogUtils.e("addPersonInOut   person==null");
            } else {
                onNewPersonArrived(person);
            }
        }

        @Override
        public List<Person> getPersons() throws RemoteException {
            return persons;
        }

        @Override
        public int getPersonCount() throws RemoteException {
            return persons.size();
        }

        @Override
        public void registerOnNewPersonArrivedListener(IOnNewPersonArrivedListener listener) throws RemoteException {
            remoteCallbacks.register(listener);
        }

        @Override
        public void unregisterOnNewPersonArrivedListener(IOnNewPersonArrivedListener listener) throws RemoteException {
            remoteCallbacks.unregister(listener);
        }

    };


    private void onNewPersonArrived(Person newPerson) throws RemoteException {
        persons.add(newPerson);
        LogUtils.e("onNewPersonArrived :  -----  " + newPerson.toString());
        int count = remoteCallbacks.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IOnNewPersonArrivedListener onNewPersonArrivedListener = remoteCallbacks.getBroadcastItem(i);
            if (onNewPersonArrivedListener != null) {
                onNewPersonArrivedListener.onNewPersonArrived(newPerson);
                LogUtils.e(" onNewPersonArrivedListener.onNewPersonArrived(newPerson) ");
            }
        }
        //最后必须调用该方法关闭；
        remoteCallbacks.finishBroadcast();
    }


}
