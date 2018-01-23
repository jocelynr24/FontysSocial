package routin.fontyssocial.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import routin.fontyssocial.R;
import routin.fontyssocial.main.MainActivity;
import routin.fontyssocial.model.User;

/**
 * Created by conte on 30/11/2017.
 */

public class SignUpActivity extends AppCompatActivity {
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final String TAG = "EmailPassword";
    private static Context context;
    private AutoCompleteTextView mail_account;
    private EditText password, pseudo, passwordCheck;
    private Button register;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SignUpActivity.context = getApplicationContext();
        mail_account = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCheck = findViewById(R.id.passwordCheck);
        pseudo = findViewById(R.id.pseudo);
        register = findViewById(R.id.done);
        register.setEnabled(false);
        auth = FirebaseAuth.getInstance();
        addAdapterToViews();

        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            updateUI(user);
        }

        mail_account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mail_account.getText().toString().isEmpty() || pseudo.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordCheck.getText().toString().isEmpty() || !password.getText().toString().equals(passwordCheck.getText().toString())){
                    register.setEnabled(false);
                }else{
                    register.setEnabled(true);
                }
            }
        });

        pseudo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mail_account.getText().toString().isEmpty() || pseudo.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordCheck.getText().toString().isEmpty() || !password.getText().toString().equals(passwordCheck.getText().toString())){
                    register.setEnabled(false);
                }else{
                    register.setEnabled(true);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mail_account.getText().toString().isEmpty() || pseudo.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordCheck.getText().toString().isEmpty() || !password.getText().toString().equals(passwordCheck.getText().toString())){
                    register.setEnabled(false);
                }else{
                    register.setEnabled(true);
                }
            }
        });

        passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mail_account.getText().toString().isEmpty() || pseudo.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordCheck.getText().toString().isEmpty() || !password.getText().toString().equals(passwordCheck.getText().toString())){
                    register.setEnabled(false);
                }else{
                    register.setEnabled(true);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if sign up failed, it will displayed an message
                auth.createUserWithEmailAndPassword(mail_account.getText().toString(),password.getText().toString()).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(SignUpActivity.this, getString(R.string.signup_failed),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        String pseudo = this.pseudo.getText().toString();
        String mail = this.mail_account.getText().toString();
        User u = new User(pseudo, mail);
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
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
