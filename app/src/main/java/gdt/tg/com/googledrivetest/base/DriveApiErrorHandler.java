package gdt.tg.com.googledrivetest.base;

import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import gdt.tg.com.googledrivetest.DriveManager;


/**
 * Created by tomasz on 07.07.15.
 */
public abstract class DriveApiErrorHandler implements ErrorHandler {

    private final Activity activity;
    private final DriveManager driverManager;

    public DriveApiErrorHandler(Activity activity, DriveManager driverManager) {
        this.activity = activity;
        this.driverManager = driverManager;
    }

    @Override
    public void handle(Throwable e) {
        if (e instanceof GooglePlayServicesAvailabilityIOException) {
            driverManager.showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) e).getConnectionStatusCode());
        } else if (e instanceof UserRecoverableAuthIOException) {
            activity.startActivityForResult(((UserRecoverableAuthIOException) e).getIntent(), StatusCodes.REQUEST_AUTHORIZATION);
        } else {
            handleError(e);
            // TODO zalogowac
        }
    }

    public abstract void handleError(Throwable e);
}
