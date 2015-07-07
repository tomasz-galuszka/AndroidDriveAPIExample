package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.services.drive.model.File;

import java.io.IOException;

import gdt.tg.com.googledrivetest.base.ErrorHandler;

/**
 * Created by tomasz on 07.07.15.
 */
public class CreateDirectoryTask extends AsyncTask<String, Void, File> {

    private final com.google.api.services.drive.Drive service;
    private final ErrorHandler errorHandler;
    private Throwable error;

    public CreateDirectoryTask(ErrorHandler errorHandler, com.google.api.services.drive.Drive service) {
        this.errorHandler = errorHandler;
        this.service = service;
    }

    @Override
    protected File doInBackground(String... params) {
        return createDirectory(params[0]);
    }

    @Override
    protected void onPostExecute(File file) {
        if (this.error == null) {
            return;
        }
        errorHandler.handle(this.error);
    }

    private File createDirectory(String name) {
        try {
            File body = new File();
            body.setTitle(name);
            body.setMimeType("application/vnd.google-apps.folder");

            return service.files().insert(body).execute();
        } catch (IOException e) {
            errorHandler.handle(e);
            return null;
        }
    }
}
