package com.checkyourbattery.batteryischarged.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.checkyourbattery.batteryischarged.R;
import com.checkyourbattery.batteryischarged.models.OptionModel;

import java.util.List;

public class ChooseOptionAdapter extends ArrayAdapter<OptionModel> {

    public ChooseOptionAdapter(Context context, List<OptionModel> donationOptions) {
        super(context, 0, donationOptions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_option, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        OptionModel option = getItem(position);
        if (option != null) {
            vh.description.setText(option.description);

        }

        return convertView;
    }

    private static final class ViewHolder {
        TextView description;


        public ViewHolder(View v) {
            description = v.findViewById(R.id.description);

        }
    }
}