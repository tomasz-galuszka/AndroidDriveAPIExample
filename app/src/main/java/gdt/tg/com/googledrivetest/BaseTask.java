package gdt.tg.com.googledrivetest;

import android.widget.EditText;

import java.sql.DriverManager;

/**
 * Created by tomasz on 07.07.15.
 */
public abstract class BaseTask implements Runnable {

    private final EditText statusText;
    private final EditText listText;
    private final DriveManager driveManager;

    public BaseTask(EditText statusText, EditText listText, DriveManager driveManager) {
        this.statusText = statusText;
        this.listText = listText;
        this.driveManager = driveManager;
    }

    @Override
    public void run() {
        statusText.setText("Retrieving data …");
        listText.setText("");

        if (driveManager.getSelectedAccountName() == null) {
            driveManager.chooseAccount();
        } else {
            if (!driveManager.isDeviceOnline()) {
                statusText.setText("No network connection available.");
                return;
            }
            doAction();
        }
    }
    public abstract void doAction();
}
