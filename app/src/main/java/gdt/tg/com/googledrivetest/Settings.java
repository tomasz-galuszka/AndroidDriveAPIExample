package gdt.tg.com.googledrivetest;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tomasz on 07.07.15.
 */
public final class Settings {

    private final SharedPreferences preferences;
    private final Context context;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private Settings(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(Class.class.getName(), Context.MODE_PRIVATE);
    }

    public String getAccountName() {
        return getPreferences().getString(PREF_ACCOUNT_NAME, null);
    }

    public void setAccountName(String name) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(PREF_ACCOUNT_NAME, name);
        editor.commit();
    }

    private SharedPreferences getPreferences() {
        return preferences;
    }

    public static class Builder {

        private final Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Settings build() {
            return new Settings(this.context);
        }
    }
}
