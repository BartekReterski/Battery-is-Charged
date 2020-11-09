package com.checkyourbattery.batteryischarged.models;

import androidx.annotation.NonNull;

public class OptionModel {

    public final String description;
    public final String alarmDescription;


    public OptionModel(String description, String alarmDescription) {
        this.description = description;
        this.alarmDescription=alarmDescription;
    }

    @NonNull
    @Override
    public String toString() {
        return "DonationOption{" +
                "description='" + description + '\'' +
                ", alarmDescription='" + alarmDescription + '\'' +
                '}';
    }
}