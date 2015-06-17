package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

import java.util.Iterator;


/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 1:59
 */
public class MainTask extends BaseTask {


    private final boolean mInvis;

    public MainTask( ResultReceiver mReceiver, Bundle args ) {
        super( mReceiver, args );
        mInvis = args.getBoolean("isInvisible", false);
    }


    @Override
    public void run() {
        JSONObject ret = VKApi.executeMain(mInvis);
        if ( ret == null ) {
            handleResponse( null );
            return;
        }
        try {
            JSONObject response = ret.getJSONObject( "response" );
            //todo use later when friends are supported
//            if ( response.optBoolean( "requests", true ) ) {
//                VK.db().profiles().storeRequests( response.getJSONArray( "requests" ) );
//            }
            if ( response.optBoolean( "friends", true ) ) {
                JSONArray arr = response.getJSONArray( "friends" );
                VK.db().profiles().storeFriends( arr );
            }
            if ( response.optBoolean( "dialogs", true ) ) {
                JSONArray dialogs = response.getJSONArray( "dialogs" );
                JSONArray profiles = response.getJSONArray( "profiles" );
                VK.db().profiles().store( profiles );
                VK.db().msg().storeMessages( dialogs );
                VK.model().storeConversationsCount( dialogs.getInt( 0 ) );
            }
            if ( response.optBoolean( "me", true ) ) {
                JSONArray profiles = response.getJSONArray( "me" );
                VK.db().profiles().store( profiles );
            }
        } catch ( JSONException ignored ) {
        }
        handleResponse( ret );
    }

    private JSONObject cloneJSON( JSONObject obj ) throws JSONException {
        JSONObject ret = new JSONObject();
        Iterator i = obj.keys();
        while ( i.hasNext() ) {
            Object key = i.next();
            ret.put( key.toString(), obj.get( key.toString() ) );

        }
        return ret;  //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    protected void handleResponse( JSONObject json ) {
        super.handleResponse( json );
        sendResult( VKService.Result.MainActionPerformed );
    }


}
