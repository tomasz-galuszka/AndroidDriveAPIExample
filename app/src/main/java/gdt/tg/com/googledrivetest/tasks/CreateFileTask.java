package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import java.io.IOException;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

/**
 * Created by tomasz on 08.07.15.
 */
public class CreateFileTask extends AsyncTask<Params<CreateFileTask.FileData>, Void, CreateFileTask.FileData> {

    public static class FileData {

        private final File driveFile;
        private final java.io.File file;

        public FileData(File driveFile, java.io.File file) {
            this.driveFile = driveFile;
            this.file = file;
        }

        public File getDriveFile() {
            return driveFile;
        }

        public java.io.File getFile() {
            return file;
        }
    }

    private final com.google.api.services.drive.Drive service;
    private ErrorHandler errorHandler;
    private SuccessHandler<CreateFileTask.FileData> successHandler;
    private Throwable error;

    public CreateFileTask(com.google.api.services.drive.Drive service) {
        this.service = service;
    }

    @Override
    protected CreateFileTask.FileData doInBackground(Params<CreateFileTask.FileData>... params) {
        Params<CreateFileTask.FileData> config = params[0];
        this.errorHandler = config.getErrorHandler();
        this.successHandler = config.getSuccessHandler();

        return createFile(config.getData());
    }

    @Override
    protected void onPostExecute(CreateFileTask.FileData file) {
        if (this.error == null) {
            successHandler.handle(file);
        } else {
            errorHandler.handle(this.error);
        }
    }

    private CreateFileTask.FileData createFile(CreateFileTask.FileData data) {
        try {
            FileContent mediaContent = new FileContent(data.getDriveFile().getMimeType(), data.getFile());
            service.files().insert(data.getDriveFile(), mediaContent).execute();
        } catch (IOException e) {
            this.error = e;
        }
        return data;
    }
}