package com.finalyear.smarthome.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.models.Room;
import com.finalyear.smarthome.utils.SwitchButton;

import java.sql.SQLException;
import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {

    Context context;
    private OnRoomListener mOnRoomListener;
    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList, Context context,OnRoomListener onRoomListener) {/**/
        this.roomList = roomList;
        this.context = context;
        this.mOnRoomListener = onRoomListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_room_row, parent, false);
        return new MyViewHolder(itemView, mOnRoomListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.title.setText(room.getName());
    }
            /*@Override
            public void onClick(View view) {
                roomList.remove(position);
                notifyItemRemoved(position);
                //this line below gives you the animation and also updates the
                //list items after the deleted item
                notifyItemRangeChanged(position, getItemCount());
            }*/
    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public CardView cardView;
        public ImageButton editRoom;
        public ImageView deleteContact;
        OnRoomListener onRoomListener;

        public MyViewHolder(View view, OnRoomListener onRoomListener) {
            super(view);
            title = view.findViewById(R.id.title3);
            cardView = view.findViewById(R.id.card_view);

            this.onRoomListener = onRoomListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRoomListener.onRoomClicked(getAdapterPosition());
        }
    }
    public interface OnRoomListener{
        void onRoomClicked(int position);
    }
}