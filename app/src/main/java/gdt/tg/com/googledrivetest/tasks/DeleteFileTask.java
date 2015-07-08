package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

public class DeleteFileTask extends AsyncTask<Params<File>, Void, File> {

    private final com.google.api.services.drive.Drive service;
    private SuccessHandler<File> successHandler;
    private ErrorHandler errorHandler;
    private Throwable error;

    public DeleteFileTask(com.google.api.services.drive.Drive service) {
        this.service = service;
    }

    @Override
    protected File doInBackground(Params<File>... params) {
        Params<File> config = params[0];
        this.errorHandler = config.getErrorHandler();
        this.successHandler = config.getSuccessHandler();

        return deleteDirectory(config.getData());
    }

    @Override
    protected void onPostExecute(File dir) {
        if (this.error == null) {
            successHandler.handle(dir);
        } else {
            errorHandler.handle(this.error);
        }
    }

    private File deleteDirectory(File directory) {
        try {
            List<File> result = new ArrayList<>();
            Drive.Files.List request = service.files().list().setQ("title='" + directory.getTitle() + "'");
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
                service.files().delete(f.getId()).execute();
            }
        } catch (IOException e) {
            error = e;
        }
        return directory;
    }
}
