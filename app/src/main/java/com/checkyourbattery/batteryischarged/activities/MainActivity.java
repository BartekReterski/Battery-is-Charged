package com.checkyourbattery.batteryischarged.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.checkyourbattery.batteryischarged.BuildConfig;
import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.adapter.ChooseOptionAdapter;
import com.checkyourbattery.batteryischarged.models.OptionModel;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.ArrayList;
import java.util.List;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

  private SharedPreferences sharedPreferences;
  private SharedPreferences sharedPreferencesCheckbox;
  private SharedPreferences.Editor editorCheck;
  int choosen_battery_value;
  boolean check_box_value;
  private Menu menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BattteryWidgetLogic();
        NotificationLogic();

        //przypisanie przejscia do aktywnosci dla tekstu
        TextView getDeviceInfoText= findViewById(R.id.getDeviceInfoText);
        getDeviceInfoText.getPaint().setUnderlineText(true);

        getDeviceInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,DeviceInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menuList = menu;

        //odebranie danych tymczasowych na temat checkboxa
        SharedPreferences sharedPreferences2 = getSharedPreferences("PREFS_2", MODE_PRIVATE);
        check_box_value=sharedPreferences2.getBoolean("check",false);

        Toast.makeText(this,String.valueOf(check_box_value),Toast.LENGTH_LONG).show();
        menu.findItem(R.id.itemSettings).setChecked(check_box_value);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.itemInfo:

                String versionName = BuildConfig.VERSION_NAME;
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.info)
                        .setTitle("Battery is charged")
                        .setMessage("Charge battery up to chosen percent. Set your charging value and preferable notification method. Get info when battery is charged"
                                + "\n"  + "\n" +"Version: "+versionName)
                        .show();

                return true;

            case R.id.notifiOff:

                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorWarning)
                        .setIcon(R.drawable.notification_off)
                        .setTitle("Notifications disabled")
                        .setMessage("Notifications are disabled for charging level under 20%")
                        .show();
                return true;

            case R.id.deviceInfo:

                Intent intent= new Intent(this, DeviceInfoActivity.class);
                startActivity(intent);
                return true;

            case R.id.itemSettings:
                //ustawienie checkboxa z notyfikacjami

                //wyslanie danych tymczasowych na temat checkboxa
                sharedPreferencesCheckbox = getSharedPreferences("PREFS_2", MODE_PRIVATE);
                editorCheck = sharedPreferencesCheckbox.edit();


                if (item.isChecked()) {

                    item.setChecked(false);
                    editorCheck.putBoolean("check", false);
                    editorCheck.apply();
                } else {
                    item.setChecked(true);
                    editorCheck.putBoolean("check", true);
                    editorCheck.apply();
                }

                return true;

            default:


                return super.onOptionsItemSelected(item);
        }

    }

    private void BattteryWidgetLogic() {

        final TextView chargingText = findViewById(R.id.chargingText);
        final BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

        //odebranie danych tymczasowych na temat wybranej wartosci baterii i przypisanie wartości do widgetu baterii
        SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
        choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

        if(choosen_battery_value==0){
            batterySeek.setValue(20);
        }else{
            batterySeek.setValue(choosen_battery_value);
        }


        batterySeek.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {

                //wyslanie danych tymczasowych na temat wybranej wartosci baterii
                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("battery_value", value);
                editor.apply();

                //odebranie danych tymczasowych na temat wybranej wartosci baterii
                SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
                choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

                chargingText.setText("Charge battery up to " + String.valueOf(choosen_battery_value) + "%");
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                if (choosen_battery_value >= 80) {

                    ViewTooltip
                            .on(MainActivity.this,batterySeek)
                            .autoHide(true, 3500)
                            .corner(30)
                            .position(ViewTooltip.Position.RIGHT)
                            .text("To increase battery lifespan charge it is recommended to charge it up to the 80%")
                            .show();
                }
            }
        });
    }

    public void NotificationLogic() {

        Button buttonNotification = findViewById(R.id.buttonNotification);

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //odebranie danych tymczasowych na temat wybranej wartosci baterii i przypisanie wartości do widgetu baterii
                SharedPreferences sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE);
                choosen_battery_value=sharedPreferences1.getInt("battery_value",0);

                //wyświetlenie informacji na temat tego, że nie można używać aplikacji gdy jest ustawione poniżej 20%
                if(choosen_battery_value<20){
                    menuList.performIdentifierAction(R.id.notifiOff,0);
                }else{

                    ArrayAdapter<OptionModel> adapter = new ChooseOptionAdapter(MainActivity.this, loadChooseOptions());
                    new LovelyChoiceDialog(MainActivity.this)
                            .setTopColorRes(R.color.chooseOption)
                            .setTitle("Choose notification option")
                            .setIcon(R.drawable.notification_on)
                            .setMessage("Choose your preferable notification option and get info when battery is charged, based on your previous chosen value")
                            .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<OptionModel>() {
                                @Override
                                public void onItemSelected(int position, OptionModel item) {
                                    // Toast.makeText(MainActivity.this, item.amount),Toast.LENGTH_SHORT).show();
                                    if(item.description.equals("System notification")){

                                        Toasty.success(MainActivity.this,"Notification will be shown when battery achieved "+choosen_battery_value+" %",Toast.LENGTH_LONG).show();
                                        //wykonaj metode
                                    }else{

                                        //wykonaj metode pod email

                                        Toasty.success(MainActivity.this,"E-mail will be sent when battery achieved "+choosen_battery_value+" %",Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .show();
                }

            }

        });
    }
    private List<OptionModel> loadChooseOptions() {
        List<OptionModel> result = new ArrayList<>();
        String[] raw = getResources().getStringArray(R.array.options);
        for (String op : raw) {
            String[] info = op.split("%");
            result.add(new OptionModel(info[0]));
        }
        return result;
    }
}

