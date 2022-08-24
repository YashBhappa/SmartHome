package com.finalyear.smarthome.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.finalyear.smarthome.R;
import com.finalyear.smarthome.utils.SwitchButton;

public class RoomsViewHolder extends RecyclerView.ViewHolder {

    private final SwitchButton switchButton;
    public TextView name;
    public ImageView deleteContact;
    public ImageView editContact;
    public CardView cardView;
    public TextView deviceCounter;

    public RoomsViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.title3);
        deleteContact = (ImageView)itemView.findViewById(R.id.deleteContact);
        editContact = (ImageView)itemView.findViewById(R.id.editContact);
        cardView = (CardView)itemView.findViewById(R.id.card_view);
        switchButton = (SwitchButton)itemView.findViewById(R.id.roomButton);
        deviceCounter = (TextView)itemView.findViewById(R.id.device_counter3);
    }
}
