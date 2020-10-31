package com.checkyourbattery.batteryischarged.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.checkyourbattery.batteryischarged.R;
import com.github.abara.library.batterystats.BatteryStats;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.Arrays;
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

    public long getBatteryCapacity(Context ctx) {
        BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
        long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
        return value;

    }

    private void BatteryInfo(){

        try {

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    BatteryStats batteryStats = new BatteryStats(intent);

                    String deviceModel = Build.MANUFACTURER
                            + " " + Build.MODEL;
                    int batteryLevel = batteryStats.getLevel();
                    int batteryScale = batteryStats.getScale();
                    String batteryTechnology = batteryStats.getBatteryTechnology();
                    String batteryHealth = batteryStats.getHealthText();
                    int plugedState = batteryStats.getPluggedState();
                    boolean isCharging = batteryStats.isCharging();
                    double batteryTemperatureFahrenheit = batteryStats.getTemperature(true);
                    double celsius = ((5 * (batteryTemperatureFahrenheit - 32.0)) / 9.0);
                    long capacity = getBatteryCapacity(context);

                    //zadeklarowanie osobnego receiver do samego voltage
                    Intent intentVoltage = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    assert intentVoltage != null;
                    int batteryVoltage = intentVoltage.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

                    TextView textdeviceModel = findViewById(R.id.device_model);
                    TextView textbatteryLevel = findViewById(R.id.battery_level);
                    TextView textbatteryHealth = findViewById(R.id.battery_health);
                    TextView textbatteryVoltage = findViewById(R.id.battery_voltage);
                    TextView textbatteryTemperature = findViewById(R.id.battery_temperature);
                    TextView textbatteryTechnology = findViewById(R.id.battery_technology);
                    TextView textbatteryScale = findViewById(R.id.battery_scale);
                    TextView textbatteryIsChargin = findViewById(R.id.battery_ischarging);
                    TextView textbatteryCapacity=findViewById(R.id.battery_capacity);


                    textdeviceModel.setText(deviceModel);
                    textbatteryLevel.setText(String.valueOf(batteryLevel + " %"));
                    textbatteryHealth.setText(batteryHealth);
                    textbatteryVoltage.setText(String.valueOf(batteryVoltage + " mV"));
                    textbatteryTemperature.setText(String.valueOf(batteryTemperatureFahrenheit + " °F"));
                    textbatteryTechnology.setText(batteryTechnology);
                    textbatteryScale.setText(String.valueOf(batteryScale));
                    textbatteryCapacity.setText(String.valueOf(capacity+ " mAh"));


                    if (isCharging) {
                        textbatteryIsChargin.setText("Yes");

                    } else {
                        textbatteryIsChargin.setText("No");
                    }

                }
            };

            registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }catch (Exception ex){

            System.out.println(ex.getMessage()+ Arrays.toString(ex.getStackTrace()));
        }
    }
}
