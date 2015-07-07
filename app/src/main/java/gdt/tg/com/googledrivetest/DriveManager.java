package gdt.tg.com.googledrivetest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.util.Arrays;

import gdt.tg.com.googledrivetest.base.Settings;
import gdt.tg.com.googledrivetest.base.StatusCodes;

/**
 * Created by tomasz on 07.07.15.
 */
public class DriveManager {

    private static final java.util.Set<String> SCOPES = DriveScopes.all();
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private com.google.api.services.drive.Drive mService;
    private GoogleAccountCredential credential;
    private final Activity activity;

    public DriveManager(Activity activity) {
        this.activity = activity;

        // Initialize credentials and service object.
        credential = GoogleAccountCredential.usingOAuth2(activity.getApplicationContext(), SCOPES)
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(getSettings().getAccountName());

        mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName("Drive API Android Quickstart")
                .build();

    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        activity,
                        StatusCodes.REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public void setAccountName(String name) {
        credential.setSelectedAccountName(name);
        getSettings().setAccountName(name);
    }

    public boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public void chooseAccount() {
        activity.startActivityForResult(credential.newChooseAccountIntent(), StatusCodes.REQUEST_ACCOUNT_PICKER);
    }

    public String getSelectedAccountName() {
        return credential.getSelectedAccountName();
    }

    private Settings getSettings() {
        return new Settings.Builder(activity).build();
    }

    public com.google.api.services.drive.Drive getmService() {
        return mService;
    }
}
