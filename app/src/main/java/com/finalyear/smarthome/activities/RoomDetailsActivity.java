package com.finalyear.smarthome.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.models.Room;
import com.finalyear.smarthome.adapters.SingleRoomAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailsActivity extends AppCompatActivity implements SingleRoomAdapter.OnDeviceListener {
    private static final String TAG = "RoomDetailsActivity";
    private List<Room> DeviceList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SingleRoomAdapter mAdapter;
    Button mAddDevice;

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);


        // switchButton.setChecked(switch_status);


        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new SingleRoomAdapter(DeviceList, getApplicationContext(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareRoomData();
    }

    private void prepareRoomData() {
        Room room = new Room("1", getString(R.string.roomDetails_4));
        DeviceList.add(room);
        room = new Room("2", getString(R.string.roomDetails_5));
        DeviceList.add(room);
        room = new Room("1", getString(R.string.roomDetails_6));
        DeviceList.add(room);
        room = new Room("2", getString(R.string.roomDetails_7));
        DeviceList.add(room);
        /*  room = new Room("1", "Stand Fan");
        roomList.add(room);
        room = new Room("2", "Footer Light");
        roomList.add(room);*/
        room = new Room("1", getString(R.string.roomDetails_8));
        DeviceList.add(room);

        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onBackClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onDeviceClicked(int position) {


    }
}
