package ru.kurganec.vk.messenger.model.sync;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.Model;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.classes.VKProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author sam
 */
public class ContactsSyncAdapterService extends Service {
    public final static String SYNC_FINISHED = "ru.kurganec.vk.messenger.SYNC_FINISHED";
    public final static String FORCE_SYNC = "FORCE_SYNC";

    private static SyncAdapterImpl sSyncAdapter = null;

    public ContactsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this);
        return sSyncAdapter;
    }


    private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {//

        boolean doSync = false;
        if (extras != null && extras.containsKey(ContactsSyncAdapterService.FORCE_SYNC)) {
            doSync = true;
        } else {
            long lastSyncDate = VK.model().getLastSyncTime();
            long nextSyncTime = lastSyncDate + 1000 * 60 * 60 * 24 * 7;    //TODO once a week ? , test!
            long currentTime = System.currentTimeMillis();
            if (Model.NOT_SYNCED == lastSyncDate) {
                doSync = false;
            } else if (currentTime < nextSyncTime) {
                doSync = false;
            } else {
                doSync = true;
            }
        }
        if (!doSync){
            return;
        }


        Log.d("VKLOL", "performSync: " + account.toString());
        ContentResolver mContentResolver = context.getContentResolver();

        ArrayList<String> phones = new ArrayList<String>();
        HashMap<String /*phone */, String /*Phone*/> phonesMap = new HashMap<String, String>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = (pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        phone = phone.replaceAll(" ", "");
                        phone = phone.replaceAll("-", "");
                        if (phone.startsWith("8") && phone.length() == 11) {
                            phone = phone.replaceFirst("8", "+7");
                        }
                        phones.add(phone);
                        phonesMap.put(phone, name);
                    }
                    pCur.close();
                }
            }
        }
        String[] selectionARgs = {account.type};
        int deleted = mContentResolver.delete(RawContacts.CONTENT_URI, RawContacts.ACCOUNT_TYPE + " = ?", selectionARgs);
        JSONObject ret = VKApi.syncContactBook(phones);
        if (ret == null){
            //todo send result
            return;
        }
        JSONArray response;
        try {
            response = ret.getJSONArray("response");

            List<VKProfile> profileList = VKProfile.parseArray(response);
//            VK.db().storeProfiles(profileList);


            for (int i = 0; i < response.length(); ++i) {
                JSONObject contact = response.getJSONObject(i);
                String name = phonesMap.get(contact.getString("phone"));
                if (name == null) {
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(RawContacts.ACCOUNT_NAME, account.name);
                values.put(RawContacts.ACCOUNT_TYPE, account.type);
//                values.put(RawContacts.AGGREGATION_MODE, TYPE_AUTOMATIC);
                Uri newRawContact = mContentResolver.insert(RawContacts.CONTENT_URI, values);


                long newId = ContentUris.parseId(newRawContact);

                String[] Projection = {ContactsContract.Data.RAW_CONTACT_ID};
                String[] selection = {contact.getString("phone")};
                Cursor manualAggregation = mContentResolver.query(ContactsContract.Data.CONTENT_URI,
                        Projection,
                        ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?"
                        , selection, null
                );
                if (manualAggregation.moveToFirst()) {
                    ContentValues cv = new ContentValues();
                    cv.put(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER);
                     cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, manualAggregation.getLong(0));
                    cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, newId);
                    mContentResolver.update(ContactsContract.AggregationExceptions.CONTENT_URI, cv, null, null);

                }
                manualAggregation.close();

                values.clear();

//                values.put(ContactsContract.);

                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, newId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getString("phone"));
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                mContentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, newId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                Uri r1 = mContentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, newId);
                values.put(ContactsContract.Data.MIMETYPE, context.getString(R.string.contact_mime_type));
                values.put(ContactsContract.Data.DATA1, contact.getString("last_name") + " " + contact.getString("first_name"));
                values.put(ContactsContract.Data.DATA2, "Сообщения ВК");
                values.put(ContactsContract.Data.DATA3, "Написать сообщение");
                values.put(ContactsContract.Data.DATA11, contact.getLong("uid"));//hidden uid
                Uri r2 = mContentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
            }
        } catch (JSONException e) {
            return;
            //TODO insert here notification finished
        }
        VK.model().storeLastSyncTime(System.currentTimeMillis());
        Log.d("VKLOL-SYNC", "SYNCED " + response.length() + " contacts");
    }
}
