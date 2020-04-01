package com.example.mybus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybus.R;
import com.example.mybus.adapters.BusAdapter;
import com.example.mybus.apis.BusApi;
import com.example.mybus.apis.DriverApi;
import com.example.mybus.apis.LoginApi;
import com.example.mybus.apis.ShiftApi;
import com.example.mybus.models.Bus;
import com.example.mybus.models.Driver;
import com.example.mybus.models.Shift;
import com.example.mybus.services.LocationTrackerService;
import com.example.mybus.utilities.RetrofitUtils;
import com.example.mybus.utilities.SharedPreferencesConstants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private TextView txtDriverName, txtDriverNric, txtDriverMobile;
    private TextView txtErrorBus, txtShiftCode, txtShiftDesc, txtShiftTime, txtRouteCode, txtRouteDesc, txtCurrentBus;
    private TextView lblSelectBus;
    private Spinner spinnerBus;
    private Button btnStartEndShift, btnRefresh, btnLogout;
    private ConstraintLayout shiftExistLayout, shiftNonExistentLayout;
    private LinearLayout selectBusLayout;
    private ArrayList<Bus> buses;
    private BusAdapter busAdapter;
    private SharedPreferences sharedPreferences;

    private Bus selectedBus;
    private Shift currentShift;
    private boolean isShiftStart, isOngoingShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.NAME, Context.MODE_PRIVATE);
        initialize();
        getDriver();
        getShift();
    }

    private void initialize() {
        txtDriverName = findViewById(R.id.txt_driver_name);
        txtDriverNric = findViewById(R.id.txt_driver_nric);
        txtDriverMobile = findViewById(R.id.txt_driver_mobile);

        txtShiftCode = findViewById(R.id.txt_shift_code);
        txtShiftDesc = findViewById(R.id.txt_shift_desc);
        txtShiftTime = findViewById(R.id.txt_shift_time);
        txtRouteCode = findViewById(R.id.txt_route_code);
        txtRouteDesc = findViewById(R.id.txt_route_desc);
        lblSelectBus = findViewById(R.id.lbl_select_bus);
        spinnerBus = findViewById(R.id.spinner_bus);
        txtErrorBus = findViewById(R.id.txt_error_bus);
        txtCurrentBus = findViewById(R.id.txt_current_bus);

        btnStartEndShift = findViewById(R.id.btn_start_end_shift);
        btnStartEndShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEndShift();
            }
        });

        btnRefresh = findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        shiftExistLayout = findViewById(R.id.card_shift_exist_layout);
        shiftNonExistentLayout = findViewById(R.id.card_shift_nonexistent_layout);
        selectBusLayout = findViewById(R.id.select_bus_layout);
    }

    private void getDriver() {
        int driverId =  getSharedPreferences(SharedPreferencesConstants.NAME, Context.MODE_PRIVATE).getInt(SharedPreferencesConstants.DRIVER_ID, -1);
        if(driverId != -1) {
            Retrofit retrofit = RetrofitUtils.getRetrofit();
            DriverApi api = retrofit.create(DriverApi.class);
            Call<Driver> call = api.getDriver(driverId);
            call.enqueue(new Callback<Driver>() {
                @Override
                public void onResponse(Call<Driver> call, Response<Driver> response) {
                    if(response.isSuccessful()) {
                        Driver driver = response.body();
                        if(driver != null) {
                            displayDriver(driver);
                            return;
                        }
                    }
                    logout();
                }

                @Override
                public void onFailure(Call<Driver> call, Throwable t) {
                    logout();
                }
            });
        }
    }

    private void getShift() {
        //Check any shift is ongoing
        int shiftId = sharedPreferences.getInt(SharedPreferencesConstants.SHIFT_ID, -1);
        int driverId = sharedPreferences.getInt(SharedPreferencesConstants.DRIVER_ID, -1);

        if(driverId != -1) {
            Retrofit retrofit = RetrofitUtils.getRetrofit();
            ShiftApi api = retrofit.create(ShiftApi.class);
            Call<Shift> call = api.getShift(driverId);
            call.enqueue(new Callback<Shift>() {
                @Override
                public void onResponse(Call<Shift> call, Response<Shift> response) {
                    if(response.isSuccessful()) {
                        Shift shift = response.body();
                        if (shift != null) {
                            //if ongoing shift exists and the id is equal to the fetched id
                            if(shiftId != -1 && shift.getId() == shiftId) {
                                isOngoingShift = true;
                                isShiftStart = true;
                                selectedBus = shift.getBus();
                            }
                            currentShift = shift;
                            displayShift();
                            return;
                        }
                    }
                    displayNoShift();
                }

                @Override
                public void onFailure(Call<Shift> call, Throwable t) {
                    displayNoShift();
                }
            });
        } else {
            //logout
        }

    }

    private void getBuses() {
        Retrofit retrofit = RetrofitUtils.getRetrofit();
        BusApi api = retrofit.create(BusApi.class);
        Call<ArrayList<Bus>> call = api.getBuses();
        call.enqueue(new Callback<ArrayList<Bus>>() {
            @Override
            public void onResponse(Call<ArrayList<Bus>> call, Response<ArrayList<Bus>> response) {
                buses = response.body();
                initList();
            }

            @Override
            public void onFailure(Call<ArrayList<Bus>> call, Throwable t) {
                initList();
            }
        });
    }

    private void initList() {
        if(buses.size() > 0) {
            busAdapter = new BusAdapter(this, buses);
            spinnerBus.setAdapter(busAdapter);
            spinnerBus.setPrompt("Select a Bus");
            spinnerBus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedBus = (Bus) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            txtErrorBus.setVisibility(View.VISIBLE);
            spinnerBus.setEnabled(false);
            spinnerBus.setClickable(false);
            btnStartEndShift.setEnabled(false
            );
            btnStartEndShift.setClickable(false);
        }

    }

    private void displayDriver(Driver driver) {
        txtDriverName.setText(driver.getName());
        txtDriverNric.setText(driver.getNric());
        txtDriverMobile.setText(driver.getMobile());
    }

    private void displayNoShift() {
        stopLocationService();
        sharedPreferences.edit().remove(SharedPreferencesConstants.SHIFT_ID).apply();
        shiftNonExistentLayout.setVisibility(View.VISIBLE);
    }

    private void displayShift() {
        txtShiftCode.setText(currentShift.getShiftCode());
        txtShiftDesc.setText(currentShift.getShiftDescription());
        txtShiftTime.setText(String.format("%s - %s", currentShift.getStartTime(), currentShift.getEndTime()));
        txtRouteCode.setText(currentShift.getRouteCode());
        txtRouteDesc.setText(currentShift.getRouteDescription());

        if(isOngoingShift) {
            updateStartShift();
        } else {
            selectBusLayout.setVisibility(View.VISIBLE);
            getBuses();
            btnLogout.setEnabled(true);
        }
        shiftExistLayout.setVisibility(View.VISIBLE);
    }

    private void startEndShift() {
        Retrofit retrofit = RetrofitUtils.getRetrofit();
        ShiftApi api = retrofit.create(ShiftApi.class);
        int shiftId = currentShift.getId();

        if(!isShiftStart) {
            Call<ResponseBody> call = api.startShift(shiftId, selectedBus.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        sharedPreferences.edit().putInt(SharedPreferencesConstants.SHIFT_ID, shiftId).apply();
                        updateStartShift();
                    } else {
                        Toast.makeText(MainActivity.this, "An unexpected error occurred. Please refresh and start the shift again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } else {
            Call<ResponseBody> call = api.endShift(shiftId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        sharedPreferences.edit().remove(SharedPreferencesConstants.SHIFT_ID).apply();
                        updateEndShift();
                    } else {
                        Toast.makeText(MainActivity.this, "An unexpected error occurred. Please refresh and end the shift again.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    private void updateStartShift() {
        isShiftStart = true;
        getServicePermissions();
        selectBusLayout.setVisibility(View.GONE);
        txtCurrentBus.setText(selectedBus.toString());
        txtCurrentBus.setVisibility(View.VISIBLE);
        btnStartEndShift.setText(R.string.end_shift);
        btnLogout.setEnabled(false);
    }

    private void updateEndShift() {
        isShiftStart = false;
        stopLocationService();
        btnStartEndShift.setText(R.string.completed_shift);
        btnStartEndShift.setEnabled(false);
        btnLogout.setEnabled(true);
    }

    private void refresh(){
        isOngoingShift = false;
        isShiftStart = false;
        selectedBus = null;
        currentShift = null;
        buses = null;
        busAdapter = null;
        spinnerBus.setAdapter(null);


        shiftNonExistentLayout.setVisibility(View.GONE);
        shiftExistLayout.setVisibility(View.GONE);

        selectBusLayout.setVisibility(View.GONE);
        txtErrorBus.setVisibility(View.GONE);
        txtCurrentBus.setVisibility(View.GONE);

        btnStartEndShift.setText(R.string.start_shift);

        txtErrorBus.setVisibility(View.GONE);
        spinnerBus.setEnabled(true);
        spinnerBus.setClickable(true);
        btnStartEndShift.setEnabled(true);
        btnStartEndShift.setClickable(true);

        getShift();
    }

    //Service related methods


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 110) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getServicePermissions();
            }
        }
    }

    private void getServicePermissions(){
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                boolean backgroundLocationPermissionApproved =
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (backgroundLocationPermissionApproved) {
                    startLocationService();
                } else {
                    // App can only access location in the foreground. Display a dialog
                    // warning the user that your app must have all-the-time access to
                    // location in order to function properly. Then, request background
                    // location.
                    ActivityCompat.requestPermissions(this, new String[] {
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            110);
                }
            } else {
                startLocationService();
            }

        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        },
                        110);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        110);
            }
        }
    }

    private void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationTrackerService.class);
        startService(intent);
    }

    private void stopLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationTrackerService.class);
        stopService(intent);
    }

    //Logout
    private void logout() {
        stopLocationService();
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
