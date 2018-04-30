package com.ucf.jsage.bleteacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }
    public void cont(View view) {
        //Next activity

        Intent next = new Intent(LoginActivity.this, MainActivity.class);

        next.putExtra("EXTRA_USER", ((EditText)findViewById(R.id.login_email)).getText().toString());
        next.putExtra("EXTRA_PW", ((EditText)findViewById(R.id.login_pw)).getText().toString());
        next.putExtra("EXTRA_CLASS", ((EditText)findViewById(R.id.login_class)).getText().toString());
        startActivity(next);
    }
}
