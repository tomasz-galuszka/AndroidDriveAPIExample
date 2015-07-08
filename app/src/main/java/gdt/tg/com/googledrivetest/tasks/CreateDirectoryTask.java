package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.services.drive.model.File;

import java.io.IOException;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

/**
 * Created by tomasz on 07.07.15.
 */
public class CreateDirectoryTask extends AsyncTask<Params<File>, Void, File> {

    private final com.google.api.services.drive.Drive service;
    private ErrorHandler errorHandler;
    private SuccessHandler<File> successHandler;

    private Throwable error;

    public CreateDirectoryTask(com.google.api.services.drive.Drive service) {
        this.service = service;
    }

    @Override
    protected File doInBackground(Params<File>... params) {
        Params<File> config = params[0];
        this.errorHandler = config.getErrorHandler();
        this.successHandler = config.getSuccessHandler();

        return createDirectory(config.getData());
    }

    @Override
    protected void onPostExecute(File file) {
        if (this.error == null) {
            successHandler.handle(file);
        } else {
            errorHandler.handle(this.error);
        }
    }

    private File createDirectory(File f) {
        try {
            return service.files().insert(f).execute();
        } catch (IOException e) {
            this.error = e;
            return f;
        }
    }
}
