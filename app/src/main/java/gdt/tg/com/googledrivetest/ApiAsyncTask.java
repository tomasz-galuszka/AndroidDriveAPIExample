package gdt.tg.com.googledrivetest;


import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;

    /**
     * Constructor.
     *
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Drive API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(userRecoverableException.getIntent(), StatusCodes.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     *
     * @return List of Strings describing files, or an empty list if no files
     * found.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        List<String> fileInfo = new ArrayList<String>();
        FileList result = mActivity.mService.files().list().setMaxResults(2).execute();

        List<File> files = result.getItems();
        if (files != null) {
            for (File file : files) {
                fileInfo.add(String.format("%s (%s)\n", file.getTitle(), file.getId()));
            }
        }
        return fileInfo;
    }
}
