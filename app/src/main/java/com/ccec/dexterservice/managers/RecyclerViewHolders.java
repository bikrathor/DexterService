package com.ccec.dexterservice.managers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.entities.RequestRow;

import java.util.List;

public class RecyclerViewHolders extends RecyclerView.ViewHolder {
    public TextView requestID, areaModel, openTime, scheduledTime;
    public Button chat, accept;
    private List<RequestRow> requestRowObject;

    public RecyclerViewHolders(final View itemView, final List<RequestRow> requestRowObject) {
        super(itemView);
        this.requestRowObject = requestRowObject;

        requestID = (TextView) itemView.findViewById(R.id.tvItem);
        areaModel = (TextView) itemView.findViewById(R.id.tvAreaModel);
        openTime = (TextView) itemView.findViewById(R.id.tvOT);
        scheduledTime = (TextView) itemView.findViewById(R.id.tvST);

        chat = (Button) itemView.findViewById(R.id.btnChat);
        accept = (Button) itemView.findViewById(R.id.btnAccept);


    }
}