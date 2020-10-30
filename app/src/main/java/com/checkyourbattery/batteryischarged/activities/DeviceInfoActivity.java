package com.checkyourbattery.batteryischarged.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.system.Os;
import android.widget.TextView;
import android.widget.Toast;

import com.checkyourbattery.batteryischarged.R;
import com.github.abara.library.batterystats.BatteryStats;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.Objects;

public class DeviceInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Device and battery");

        RuntimePermissions();
        BatteryInfo();

    }

    //zadeklarowanie uprawnień
    private void RuntimePermissions() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //    Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(DeviceInfoActivity.this, "Permissions denied", Toast.LENGTH_LONG).show();
            }
        };


        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                .check();
    }


    private void BatteryInfo(){

        BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BatteryStats batteryStats = new BatteryStats(intent);

                String deviceModel = Build.MANUFACTURER
                        + " " + Build.MODEL;
                int batteryLevel=batteryStats.getLevel();
                int batteryHealth=batteryStats.getHealth();
                int batteryVoltage= (int) batteryStats.getVoltage();
                int batteryScale= batteryStats.getScale();
                String batteryTechnology= batteryStats.getBatteryTechnology();
                int plugedState=batteryStats.getPluggedState();
                boolean isCharging=batteryStats.isCharging();

                double batteryTemperatureFahrenheit=batteryStats.getTemperature(true);
                double celsius =(( 5 *(batteryTemperatureFahrenheit - 32.0)) / 9.0);


                TextView textdeviceModel=findViewById(R.id.device_model);
                TextView textbatteryLevel=findViewById(R.id.battery_level);

                TextView textbatteryVoltage=findViewById(R.id.battery_voltage);
                TextView textbatteryTemperature=findViewById(R.id.battery_temperature);
                TextView textbatteryTechnology=findViewById(R.id.battery_technology);
                TextView textbatteryScale=findViewById(R.id.battery_scale);
                TextView textbatteryIsChargin=findViewById(R.id.battery_ischarging);

                textdeviceModel.setText(deviceModel);
                textbatteryLevel.setText(String.valueOf(batteryLevel +"%"));
                textbatteryVoltage.setText(String.valueOf(batteryVoltage));
                textbatteryTemperature.setText(String.valueOf(batteryTemperatureFahrenheit +"°F"));
                textbatteryTechnology.setText(batteryTechnology);
                textbatteryScale.setText(String.valueOf(batteryScale));
                if(isCharging){
                    textbatteryIsChargin.setText("Yes");
                }else{
                    textbatteryIsChargin.setText("No");
                }


            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
}
