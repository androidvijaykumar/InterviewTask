package codes.by.vijaykumar.interviewtask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText edtUserName = (EditText) findViewById(R.id.edtUserName);
        final EditText edtPassword = (EditText) findViewById(R.id.edtPassword);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);


        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", "admin");
        editor.putString("PASSWORD", "admin");
        editor.apply();


        final String stored_username = sharedPreferences.getString("USERNAME", null);
        final String stored_password = sharedPreferences.getString("PASSWORD", null);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Fill the fields", Toast.LENGTH_SHORT).show();
                } else if (!userName.equals(stored_username) || !password.equals(stored_password)) {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(LoginActivity.this, VideoActivity.class));
                }

            }
        });


    }
}
