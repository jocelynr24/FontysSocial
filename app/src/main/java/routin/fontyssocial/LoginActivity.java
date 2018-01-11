package routin.fontyssocial;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import model.User;

/**
 * Created by conte on 30/11/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    AutoCompleteTextView mail_account;
    EditText password;
    private static Context context;
    private static final String TAG = "EmailPassword";
    // @SuppressLint("Registered")
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginActivity.context = getApplicationContext();
        mail_account = findViewById(R.id.email);
        password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        addAdapterToViews();

        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            updateUI(user);
        }

        Button signIn = findViewById(R.id.sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(mail_account.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        Button signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        User u = new User(user.getEmail());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
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
