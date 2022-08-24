
package com.finalyear.smarthome.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.activities.RoomDetailsActivity;
import com.finalyear.smarthome.database.SqliteDatabase;
import com.finalyear.smarthome.models.RoomsViewHolder;
import com.finalyear.smarthome.models.Rooms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsViewHolder>{

    private final Context context;
    private final ArrayList<Rooms> listRooms;

    private final SqliteDatabase mDatabase;

    public RoomsAdapter(Context context, ArrayList<Rooms> listRooms) {
        this.context = context;
        this.listRooms = listRooms;
        mDatabase = new SqliteDatabase(context);
    }

    @NotNull
    @Override
    public RoomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list, parent, false);
        return new RoomsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomsViewHolder holder, int position) {
        final Rooms rooms = listRooms.get(position);

        holder.name.setText(rooms.getName());
        //holder.deviceCounter.setText(getItemCount() + "Devices");

        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(rooms);
            }
        });

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database

                mDatabase.deleteRoom(rooms.getId());

                //refresh the activity page.
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.d(TAG, "onRoomClicked: clicked." + position);
                //  roomList.get(position);
                Intent intent = new Intent(context, RoomDetailsActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRooms.size();
    }


    private void editTaskDialog(final Rooms rooms) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.edit_room, null);

        final EditText nameField = subView.findViewById(R.id.enterRoomName);

        if (rooms != null) {
            nameField.setText(rooms.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                } else {
                    mDatabase.updateRoom(new Rooms(rooms.getId(), name));
                    //refresh the activity
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
}

