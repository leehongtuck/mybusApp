package com.example.mybus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybus.R;
import com.example.mybus.apis.LoginApi;
import com.example.mybus.models.Driver;
import com.example.mybus.utilities.RetrofitUtils;
import com.example.mybus.utilities.SharedPreferencesConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    EditText txtUsername, txtPassword;
    TextView txtError;
    Button btnLogin;
    ProgressBar loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(txtUsername.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    private void initialize() {
        txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        txtError = findViewById(R.id.txt_error);
        btnLogin = findViewById(R.id.btn_login);
        loading = findViewById(R.id.loading);
    }

    private void login(String username, String password) {
        triggerProgressBar();
        Retrofit retrofit = RetrofitUtils.getRetrofit();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<Driver> call = api.login(username, password);
        call.enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
                if(response.isSuccessful()){
                    Driver driver = response.body();
                    if(driver != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getSharedPreferences(SharedPreferencesConstants.NAME, Context.MODE_PRIVATE).edit().putBoolean(SharedPreferencesConstants.LOGGED_IN, true).apply();
                        getSharedPreferences(SharedPreferencesConstants.NAME, Context.MODE_PRIVATE).edit().putInt(SharedPreferencesConstants.DRIVER_ID, driver.getId()).apply();
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                txtError.setText(R.string.login_failed);
                disableProgressBar();
            }

            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                txtError.setText(R.string.login_error);
                disableProgressBar();
            }
        });
    }

    private void triggerProgressBar() {
        txtUsername.setVisibility(View.GONE);
        txtPassword.setVisibility(View.GONE);
        txtError.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void disableProgressBar() {
        txtUsername.setVisibility(View.VISIBLE);
        txtPassword.setVisibility(View.VISIBLE);
        txtError.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }
}
