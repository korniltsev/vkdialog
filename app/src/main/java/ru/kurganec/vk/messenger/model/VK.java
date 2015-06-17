package ru.kurganec.vk.messenger.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.crittercism.app.Crittercism;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import ru.kurganec.vk.messenger.model.actions.Actions;
import ru.kurganec.vk.messenger.model.db.VKDatabase;
import ru.kurganec.vk.messenger.newui.MainActivity;

/**
 * User: anatoly
 * Date: 10.06.12
 * Time: 23:03
 */
public class VK extends Application {

    private static VK inst;
    private Model mModel;
    private Actions mActions;
    private VKDatabase mDB;
    private ConnectivityManager mConMgr;
    private Notifications mNotifications;


    private Bus bus = new Bus(ThreadEnforcer.MAIN);
    private static final String TAG = "VK-CHAT-APP";


    public static Notifications notifications() {
        return inst.mNotifications;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inst = this;
        mModel = new Model(this);
        mActions = new Actions(this);
        mDB = new VKDatabase(this);
        mConMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNotifications = new Notifications(this);
        Crittercism.initialize(this, "504b6a0f067e7c1598000003");



    }

    public static Bus bus() {
        return inst().bus;
    }



    public static VKDatabase db() {
        return inst.mDB;
    }


    public boolean isNetworkAvailable() {

        NetworkInfo activeNetwork = mConMgr.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }
        if (activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (activeNetwork.getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        Log.d("VKLOL-NETWORK_STATE", "WIFI " +
                mConMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState());

        //we don't know the state, so may be let's try
        return true;
    }

    public boolean isWifiAvailable() {
        return mConMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }


    /**
     * @return
     */
    public static Model model() {
        return inst.mModel;
    }

    public static Actions actions() {
        return inst.mActions;
    }

    public static VK inst() {
        return inst;
    }


    public static void restartApp() {
        Intent i = new Intent(inst, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        inst.startActivity(i);

    }


}
