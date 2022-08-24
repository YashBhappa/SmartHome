package com.finalyear.smarthome.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.adapters.RoomsAdapter;
import com.finalyear.smarthome.database.SqliteDatabase;
import com.finalyear.smarthome.models.Rooms;
import com.finalyear.smarthome.adapters.RoomAdapter;
import com.finalyear.smarthome.activities.RoomDetailsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RoomAdapter.OnRoomListener {
 //   private static final String TAG = "RoomListActivity";

    private final int REQUEST_SPEECH=1;
    private SqliteDatabase mDatabase;
    private ArrayList<Rooms> allRooms =new ArrayList<>();
    private RoomsAdapter mAdapter;
    ImageButton mAddNewRoom;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView RoomView = view.findViewById(R.id.recycler_view);

        //RoomView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //    @Override
        //    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //        BottomNavigationView bottom_navigation = view.findViewById(R.id.bottomNav_view);
        //        if (dy > 0 && bottom_navigation.isShown()) {
        //            bottom_navigation.setVisibility(View.GONE);
        //        } else if (dy < 0 ) {
        //            bottom_navigation.setVisibility(View.VISIBLE);
        //        }
        //    }
        //    @Override
        //    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

        //        super.onScrollStateChanged(recyclerView, newState);
        //    }
        //});

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        RoomView.setLayoutManager(gridLayoutManager);
        RoomView.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(getActivity());
        allRooms = mDatabase.listRooms();

        if(allRooms.size() > 0){
            RoomView.setVisibility(View.VISIBLE);
            mAdapter = new RoomsAdapter(getActivity(), allRooms);
            RoomView.setAdapter(mAdapter);

        }else {
            RoomView.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "There are no rooms in the database. Start adding now", Toast.LENGTH_LONG).show();
        }

        mAddNewRoom = view.findViewById(R.id.addNewRoom);
        mAddNewRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskDialog();
                // onAddNewRoomClicked();
            }
        });
        //prepareRoomData();
        return view;
    }

    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.add_room, null);

        final EditText nameField = subView.findViewById(R.id.enterRoomName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton(R.string.add_room_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity(), "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    Rooms newRoom = new Rooms(name);
                    mDatabase.addRooms(newRoom);

                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    //private void onAddNewRoomClicked() {
    //        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_room,null);
    //        final EditText nameField = view.findViewById(R.id.enterRoomName);
    //        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    //        alert.setView(view);
//
    //        alert.setPositiveButton(R.string.homeFragment_13, new DialogInterface.OnClickListener() {
    //            public void onClick(DialogInterface dialog, int which) {
    //                Room room = new Room(""+ getId() , nameField.getText().toString());
    //                roomList.add(room);
    //                mAdapter.notifyItemInserted(roomList.size() - 1);
    //            }
    //        });
    //        alert.setNegativeButton( R.string.string_dialog_cancel, null);
    //        AlertDialog alert1 = alert.create();
    //        alert1.show();
    //}


    //private void prepareRoomData() {
    //    Rooms room = new Rooms("1", getString(R.string.homeFragment_11));
    //    listRoom.add(room);
    //    room = new Rooms("2", getString(R.string.homeFragment_12));
    //    roomList.add(room);
    //    room = new Rooms("3", getString(R.string.homeFragment_10));
    //    roomList.add(room);
    //    mAdapter.notifyDataSetChanged();
    //}

    @Override
    public void onRoomClicked(int position) {
      //  Log.d(TAG, "onRoomClicked: clicked." + position);
      //  roomList.get(position);
        Intent intent = new Intent(getActivity(), RoomDetailsActivity.class);
        startActivity(intent);
    }
}