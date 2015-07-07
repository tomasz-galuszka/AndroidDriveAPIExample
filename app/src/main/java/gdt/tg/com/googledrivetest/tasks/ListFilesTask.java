package gdt.tg.com.googledrivetest.tasks;


import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.List;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

public class ListFilesTask extends AsyncTask<Integer, Void, List<File>> {

    private final com.google.api.services.drive.Drive service;
    private final ErrorHandler errorHandler;
    private final SuccessHandler<List<File>> successHandler;
    private Throwable error;

    public ListFilesTask(Drive service, ErrorHandler errorHandler, SuccessHandler<List<File>> successHandler) {
        this.service = service;
        this.errorHandler = errorHandler;
        this.successHandler = successHandler;
    }

    @Override
    protected List<File> doInBackground(Integer... params) {
        return getDataFromApi(params[0]);
    }

    @Override
    protected void onPostExecute(List<File> files) {
        if (this.error == null) {
            this.successHandler.handle(files);
        } else {
            errorHandler.handle(this.error);
        }
    }

    private List<File> getDataFromApi(Integer limit) {
        try {
            return service.files().list().setMaxResults(limit).execute().getItems();
        } catch (IOException e) {
            this.error = e;
            return null;
        }
    }
}