package gdt.tg.com.googledrivetest;

import android.os.AsyncTask;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import java.io.IOException;

/**
 * Created by tomasz on 07.07.15.
 */
public class CreateFileAsyncTask extends AsyncTask<Void, Void, Void> {

    private MainActivity mActivity;

    /**
     * Constructor.
     *
     * @param activity MainActivity that spawned this task.
     */
    CreateFileAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            createFile("GoogleDriveTest");
        } catch (IOException e) {
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
    private File createFile(String name) throws IOException {
        File body = new File();
        body.setTitle(name);
        body.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = mActivity.mService.files().insert(body).execute();
            return file;
        } catch (IOException e) {
            throw new IOException("An error occured: " + e);
        }
    }
}
