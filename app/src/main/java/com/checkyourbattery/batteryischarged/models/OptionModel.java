package com.checkyourbattery.batteryischarged.models;

import androidx.annotation.NonNull;

public class OptionModel {

    public final String description;


    public OptionModel(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "DonationOption{" +
                "description='" + description + '\'' +
                '}';
    }
}