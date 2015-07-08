package gdt.tg.com.googledrivetest.tasks;

import android.os.AsyncTask;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

public class UpdateFileTask extends AsyncTask<Params<UpdateFileTask.FileData>, Void, UpdateFileTask.FileData> {

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

    private final Drive service;
    private SuccessHandler<UpdateFileTask.FileData> successHandler;
    private ErrorHandler errorHandler;
    private Throwable error;

    public UpdateFileTask(Drive service) {
        this.service = service;
    }

    @Override
    protected UpdateFileTask.FileData doInBackground(Params<UpdateFileTask.FileData>... params) {
        Params<UpdateFileTask.FileData> config = params[0];
        this.errorHandler = config.getErrorHandler();
        this.successHandler = config.getSuccessHandler();

        return updateFile(config.getData());
    }

    @Override
    protected void onPostExecute(UpdateFileTask.FileData data) {
        if (this.error == null) {
            successHandler.handle(data);
        } else {
            errorHandler.handle(this.error);
        }
        data.getFile().delete();
    }

    private UpdateFileTask.FileData updateFile(UpdateFileTask.FileData data) {
        try {
            List<File> driveFiles = findFilesInDrive(data);
            FileContent mediaContent = new FileContent(data.getDriveFile().getMimeType(), data.getFile());
            service.files().update(driveFiles.get(0).getId(), data.getDriveFile(), mediaContent).execute();

        } catch (IOException e) {
            error = e;
        }
        return data;
    }

    private List<File> findFilesInDrive(FileData data) throws IOException {
        List<File> result = new ArrayList<>();
        Drive.Files.List request = service.files().list().setQ("title='" + data.getDriveFile().getTitle() + "'");
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

        if (result.isEmpty()) {
            throw new IOException("File does not exists");
        }
        return result;
    }
}
