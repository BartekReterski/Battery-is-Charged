package com.checkyourbattery.batteryischarged;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.checkyourbattery.batteryischarged.adapter.ChooseOptionAdapter;
import com.checkyourbattery.batteryischarged.models.OptionModel;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.util.ArrayList;
import java.util.List;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BattteryWidgetLogic();
        NotificationLogic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemInfo:

                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.info)
                        .setTitle("Battery is charged")
                        .setMessage("Charge battery up to chosen percent. Set your charging value and preferable notification method. Get info when battery is charged")
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

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void BattteryWidgetLogic() {

        final TextView chargingText = findViewById(R.id.chargingText);
        final BoxedVertical batterySeek = findViewById(R.id.seekbar_battery);

        batterySeek.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, final int value) {
                System.out.println(value);
                if (value == 80) {
                    Toast.makeText(getApplicationContext(), "This best charging value", Toast.LENGTH_LONG).show();
                }
                if (value < 20) {
                    Toast.makeText(getApplicationContext(), "Notification are disabled for charging level under 20%", Toast.LENGTH_SHORT).show();
                    //disabled buttona i ikonka notyfikacji na pasku pawiadomien z przekresloną kreską zeby toast sie nie pokazywał caly czas
                }
                chargingText.setText("Charge battery up to " + String.valueOf(value) + "%");
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
                //Toast.makeText(MainActivity.this, "onStartTrackingTouch", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                //Toast.makeText(MainActivity.this, "onStopTrackingTouch", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void NotificationLogic() {

        Button buttonNotification = findViewById(R.id.buttonNotification);

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayAdapter<OptionModel> adapter = new ChooseOptionAdapter(MainActivity.this, loadChooseOptions());
                new LovelyChoiceDialog(MainActivity.this)
                        .setTopColorRes(R.color.chooseOption)
                        .setTitle("Choose notification option")
                        .setIcon(R.drawable.notification_on)
                        .setMessage("Choose your preferable notification option and get info when battery is charged")
                        .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<OptionModel>() {
                            @Override
                            public void onItemSelected(int position, OptionModel item) {
                               // Toast.makeText(MainActivity.this, item.amount),Toast.LENGTH_SHORT).show();
                             if(item.description.equals("System notification")){

                                 //wykonaj metode
                             }else{

                                 //wykonaj metode pod email
                             }

                            }
                        })
                        .show();

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

