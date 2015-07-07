package gdt.tg.com.googledrivetest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.services.drive.model.File;

import java.util.List;

import gdt.tg.com.googledrivetest.base.DriveApiErrorHandler;
import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.StatusCodes;
import gdt.tg.com.googledrivetest.base.SuccessHandler;
import gdt.tg.com.googledrivetest.tasks.CreateDirectoryTask;
import gdt.tg.com.googledrivetest.tasks.DeleteDirectoryTask;
import gdt.tg.com.googledrivetest.tasks.ListFilesTask;

public class MainActivity extends Activity {

    private EditText mStatusText;
    private EditText mResultsText;
    private Button mCreateDirButton;
    private Button mDeleteDirButton;
    private Button mCreateFileButton;
    private Button mDeleteFileButton;
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
                        new CreateDirectoryTask(errorHandler, driveManager.getmService()).execute("Nazwa katalogu 123");
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
                        mStatusText.setText("Trying to create directory ...");
                        new DeleteDirectoryTask(errorHandler, driveManager.getmService(), new SuccessHandler<String>() {
                            @Override
                            public void handle(String s) {
                                mStatusText.setText(s);
                                mResultsText.setText("");
                            }
                        }).execute("Nazwa katalogu 123");
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
            }
        });
        mDeleteFileButton = (Button) findViewById(R.id.button_delete_file);
        mDeleteFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatusText.setText("Trying to delete file ...");
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

