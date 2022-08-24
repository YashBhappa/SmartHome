package com.finalyear.smarthome.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.activities.RoomDetailsActivity;
import com.finalyear.smarthome.models.Room;
import com.finalyear.smarthome.utils.SwitchButton;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class SingleRoomAdapter extends RecyclerView.Adapter<SingleRoomAdapter.MyViewHolder> {

    Context context;
    private final List<Room> DeviceList;
    private final OnDeviceListener mOnDeviceListener;
    private static String MY_PREFS = "switch_prefs";
    private static String SWITCH_STATUS = "switch_status";

    boolean switch_status;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SingleRoomAdapter(List<Room> DeviceList, Context context, OnDeviceListener onDeviceListener) {
        this.DeviceList = DeviceList;
        this.context = context;
        this.mOnDeviceListener = onDeviceListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_room_row, parent, false);

        return new MyViewHolder(itemView, mOnDeviceListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Room room = DeviceList.get(position);

        holder.title.setText(room.getName());

    }

    @Override
    public int getItemCount() {
        return DeviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public CardView cardView;
        OnDeviceListener onDeviceListener;
        SwitchButton switchButton;

        public MyViewHolder(View view, OnDeviceListener onDeviceListener) {
            super(view);
            title = view.findViewById(R.id.title);
            cardView = view.findViewById(R.id.card_view);
            switchButton = view.findViewById(R.id.deviceSwitch);
            this.onDeviceListener = onDeviceListener;
            sharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
            editor = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();

            switch_status = sharedPreferences.getBoolean(SWITCH_STATUS, false);
            switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        editor.putBoolean(SWITCH_STATUS , true);
                        editor.apply();
                        switchButton.setChecked(true);
                        Log.d(TAG, "Command:" + title.getText().toString() + ":ON");
                        Toast.makeText(context, (title.getText().toString()+":ON"),Toast.LENGTH_LONG).show();
                    }else{
                        editor.putBoolean(SWITCH_STATUS , false);
                        editor.apply();
                        switchButton.setChecked(false);
                        Log.d(TAG, "Command:" + title.getText().toString() + ":OFF");
                        Toast.makeText(context, (title.getText().toString()+":OFF"),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            onDeviceListener.onDeviceClicked(getAdapterPosition());
        }
    }
    public interface OnDeviceListener{
        void onDeviceClicked(int position);
    }
}