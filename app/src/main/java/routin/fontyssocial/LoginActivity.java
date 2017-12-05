package routin.fontyssocial;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.util.Patterns;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by conte on 30/11/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    AutoCompleteTextView mail_account;
    private static Context context;
   // @SuppressLint("Registered")

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            LoginActivity.context = getApplicationContext();
            mail_account = findViewById(R.id.email);
            addAdapterToViews();
        }


    private Account[] getAccounts(){
        return AccountManager.get(context).getAccounts();
    }
    private void addAdapterToViews() {

        Account[] accounts = getAccounts();

        Set<String> emailSet = new HashSet<>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }
        mail_account.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(emailSet)));

    }
}
