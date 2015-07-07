package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

/**
 * Created by tomasz on 07.07.15.
 */
public class DeleteDirectoryTask extends AsyncTask<String, Void, Void> {

    private final com.google.api.services.drive.Drive service;
    private final ErrorHandler errorHandler;
    private final SuccessHandler<String> successHandler;
    private Throwable error;

    public DeleteDirectoryTask(ErrorHandler errorHandler, com.google.api.services.drive.Drive service, SuccessHandler<String> successHandler) {
        this.errorHandler = errorHandler;
        this.service = service;
        this.successHandler = successHandler;
    }

    @Override
    protected Void doInBackground(String... params) {
        deleteDirectory(params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (this.error == null) {
            successHandler.handle("Directory deleted successfully.");
        } else {
            errorHandler.handle(this.error);
        }
    }

    private void deleteDirectory(String name) {
        try {
            List<File> result = new ArrayList<File>();
            Drive.Files.List request = service.files().list().setQ("title='" + name + "'");

            do {
                try {
                    FileList files = request.execute();

                    result.addAll(files.getItems());
                    request.setPageToken(files.getNextPageToken());
                } catch (IOException e) {
                    error = e;
                    request.setPageToken(null);
                }
            } while (request.getPageToken() != null && request.getPageToken().length() > 0);

            for (File f : result) {
                String id = f.getId();
                DateTime createdDate = f.getCreatedDate();
                service.files().delete(id).execute();

            }
        } catch (IOException e) {
            error = e;
        }
    }
}
