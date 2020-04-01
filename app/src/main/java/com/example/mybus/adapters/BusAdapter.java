package com.example.mybus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mybus.R;
import com.example.mybus.models.Bus;

import java.util.ArrayList;

public class BusAdapter extends ArrayAdapter<Bus> {
    public BusAdapter(Context context, ArrayList<Bus> buses) {
        super(context, 0, buses);
        Log.e("tes", "working!");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_spinner_row, parent, false);
        }

        TextView busName = convertView.findViewById(R.id.bus_name);
        Bus currentItem = getItem(position);

        if (currentItem != null) {
            busName.setText(currentItem.toString());
        }


        return convertView;
    }
}
