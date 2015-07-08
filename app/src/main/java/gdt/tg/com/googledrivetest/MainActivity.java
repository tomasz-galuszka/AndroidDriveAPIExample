package gdt.tg.com.googledrivetest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.util.List;

import gdt.tg.com.googledrivetest.base.DriveApiErrorHandler;
import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.StatusCodes;
import gdt.tg.com.googledrivetest.base.SuccessHandler;
import gdt.tg.com.googledrivetest.tasks.CreateDirectoryTask;
import gdt.tg.com.googledrivetest.tasks.CreateFileTask;
import gdt.tg.com.googledrivetest.tasks.DeleteFileTask;
import gdt.tg.com.googledrivetest.tasks.ListFilesTask;
import gdt.tg.com.googledrivetest.tasks.Params;
import gdt.tg.com.googledrivetest.tasks.UpdateFileTask;

public class MainActivity extends Activity {

    private EditText mStatusText;
    private EditText mResultsText;
    private Button mCreateDirButton;
    private Button mDeleteDirButton;
    private Button mCreateFileButton;
    private Button mDeleteFileButton;
    private Button mUpdateFileButton;
    private Button mListButton;
    private DriveManager driveManager;
    private ErrorHandler errorHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driveManager = new DriveManager(this);

        mStatusText = (EditText) findViewById(R.id.statusText);

        errorHandler = new DriveApiErrorHandler(this, driveManager) {
            @Override
            public void handleError(Throwable e) {
                mStatusText.setText(e.getMessage());
            }
        };


        mResultsText = (EditText) findViewById(R.id.resultText);
        mListButton = (Button) findViewById(R.id.button_list);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new BaseTask(mStatusText, mResultsText, driveManager) {
                    @Override
                    public void doAction() {
                        mResultsText.setText("Retrieve file list");
                        new ListFilesTask(driveManager.getmService(), errorHandler, new SuccessHandler<List<File>>() {
                            @Override
                            public void handle(List<File> files) {
                                if (files.isEmpty()) {
                                    mStatusText.setText("No data.");
                                    mResultsText.setText("Data not found.");
                                } else {
                                    mStatusText.setText("Done.");
                                    mResultsText.setText(TextUtils.join("\n\n", files));
                                }
                            }
                        }).execute(10);
                    }
                });
            }
        });
        mCreateDirButton = (Button) findViewById(R.id.create_dir_button);
        mCreateDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new BaseTask(mStatusText, mResultsText, driveManager) {
                    @Override
                    public void doAction() {
                        mStatusText.setText("Trying to create directory ...");

                        File dir = new File();
                        dir.setTitle("SimpleDirectory");
                        dir.setMimeType("application/vnd.google-apps.folder");

                        Params<File> fileParams = new Params<File>(dir, errorHandler, new SuccessHandler<File>() {
                            @Override
                            public void handle(File directory) {
                                mStatusText.setText("Directory: " + directory.getTitle() + " created successfully.");
                            }
                        });

                        new CreateDirectoryTask(driveManager.getmService()).execute(fileParams);
                    }
                });
            }
        });
        mDeleteDirButton = (Button) findViewById(R.id.delete_dir_button);
        mDeleteDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new BaseTask(mStatusText, mResultsText, driveManager) {
                    @Override
                    public void doAction() {
                        mStatusText.setText("Trying to delete directory ...");

                        File dir = new File();
                        dir.setTitle("SimpleDirectory");
                        dir.setMimeType("application/vnd.google-apps.folder");

                        Params<File> fileParams = new Params<File>(dir, errorHandler, new SuccessHandler<File>() {
                            @Override
                            public void handle(File directory) {
                                mStatusText.setText("Directory: " + directory.getTitle() + " deleted successfully.");
                            }
                        });
                        new DeleteFileTask(driveManager.getmService()).execute(fileParams);
                    }
                });
            }
        });
        mCreateFileButton = (Button) findViewById(R.id.button_create_file);
        mCreateFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create simple file
                mStatusText.setText("Trying to create file ...");
                runOnUiThread(new BaseTask(mStatusText, mResultsText, driveManager) {
                    @Override
                    public void doAction() {
                        try {
                            mStatusText.setText("Trying to create file ...");

                            File driveFile = new File();
                            driveFile.setTitle("simple file.txt");
                            driveFile.setMimeType("text/plain");

                            final java.io.File tmpFile = new java.io.File(Environment.getExternalStorageDirectory() + java.io.File.separator + "simple file");
                            tmpFile.createNewFile();
                            if (tmpFile.exists()) {
                                FileOutputStream fo = new FileOutputStream(tmpFile);
                                fo.write("Simple content 123123123".getBytes());
                                fo.close();

                                CreateFileTask.FileData data = new CreateFileTask.FileData(driveFile, tmpFile);
                                Params<CreateFileTask.FileData> params = new Params<>(data, errorHandler, new SuccessHandler<CreateFileTask.FileData>() {
                                    @Override
                                    public void handle(CreateFileTask.FileData fileData) {
                                        mStatusText.setText("File: " + fileData.getDriveFile().getTitle() + " created successfully.");
                                    }
                                });
                                new CreateFileTask(driveManager.getmService()).execute(params);

                            } else {
                                throw new Exception("File couldn't be created on your phone.");
                            }
                        } catch (Exception e) {
                            errorHandler.handle(e);
                        }
                    }
                });
            }
        });
        mUpdateFileButton = (Button) findViewById(R.id.button_update_file);
        mUpdateFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mStatusText.setText("Trying to update file ...");

                    File driveFile = new File();
                    driveFile.setTitle("simple file.txt");
                    driveFile.setMimeType("text/plain");

                    final java.io.File tmpFile = new java.io.File(Environment.getExternalStorageDirectory() + java.io.File.separator + "simple file");
                    tmpFile.createNewFile();
                    if (tmpFile.exists()) {
                        FileOutputStream fo = new FileOutputStream(tmpFile);
                        fo.write("File po wykonaniu update :)".getBytes());
                        fo.close();

                        UpdateFileTask.FileData data = new UpdateFileTask.FileData(driveFile, tmpFile);
                        Params<UpdateFileTask.FileData> params = new Params<>(data, errorHandler, new SuccessHandler<UpdateFileTask.FileData>() {
                            @Override
                            public void handle(UpdateFileTask.FileData fileData) {
                                mStatusText.setText("File: " + fileData.getDriveFile().getTitle() + " updated successfully.");
                            }
                        });
                        new UpdateFileTask(driveManager.getmService()).execute(params);

                    } else {
                        throw new Exception("File couldn't be created on your phone.");
                    }
                } catch (Exception e) {
                    errorHandler.handle(e);
                }
            }
        });
        mDeleteFileButton = (Button) findViewById(R.id.button_delete_file);
        mDeleteFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatusText.setText("Trying to delete file ...");

                File driveFile = new File();
                driveFile.setTitle("simple file.txt");
                driveFile.setMimeType("text/plain");

                Params<File> fileParams = new Params<File>(driveFile, errorHandler, new SuccessHandler<File>() {
                    @Override
                    public void handle(File directory) {
                        mStatusText.setText("File: " + directory.getTitle() + " deleted successfully.");
                    }
                });
                new DeleteFileTask(driveManager.getmService()).execute(fileParams);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case StatusCodes.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    driveManager.isGooglePlayServicesAvailable();
                }
                break;

            case StatusCodes.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        driveManager.setAccountName(accountName);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText("Account unspecified.");
                }
                break;

            case StatusCodes.REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    driveManager.chooseAccount();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

