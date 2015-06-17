package ru.kurganec.vk.messenger.model.sync;


import android.accounts.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import ru.kurganec.vk.messenger.newui.SignInActivity;


public class AccountAuthenticatorService extends Service {
	private static final String TAG = "VK-MESSAGES-ACCOUNT-SYNC";
	private static AccountAuthenticatorImpl sAccountAuthenticator = null;

	public AccountAuthenticatorService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
			ret = getAuthenticator().getIBinder();
		return ret;
	}

	private AccountAuthenticatorImpl getAuthenticator() {
		if (sAccountAuthenticator == null)
			sAccountAuthenticator = new AccountAuthenticatorImpl(this);
		return sAccountAuthenticator;
	}

	private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
		private Context mContext;

		public AccountAuthenticatorImpl(Context context) {
			super(context);
			mContext = context;
		}


		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
				throws NetworkErrorException {
			Bundle result = new Bundle();
			Intent i = new Intent(mContext, SignInActivity.class);
			i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			result.putParcelable(AccountManager.KEY_INTENT, i);
			return result;
		}


		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
			Log.i(TAG, "confirmCredentials");
			return null;
		}



		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
			Log.i(TAG, "editProperties");
			return null;
		}


		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
			Log.i(TAG, "getAuthToken");
			return null;
		}


		@Override
		public String getAuthTokenLabel(String authTokenType) {
			Log.i(TAG, "getAuthTokenLabel");
			return null;
		}


		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
			Log.i(TAG, "hasFeatures: " + features);
			return null;
		}


		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
		    Log.i(TAG, "updateCredentials");
			return null;
		}
	}
}
