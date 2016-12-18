package com.ccec.dexterservice.managers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.entities.RequestRow;
import com.pkmmte.view.CircularImageView;

import java.util.List;
import java.util.Map;

public class FilesRecyclerViewHolders extends RecyclerView.ViewHolder {
    public CircularImageView RVCircle;
    public TextView requestID, areaModel;
    private List<String> itemMap;
    public CardView card_view;

    public FilesRecyclerViewHolders(final View itemView, final List<String> requestRowObject) {
        super(itemView);
        this.itemMap = requestRowObject;

        RVCircle = (CircularImageView) itemView.findViewById(R.id.product_circle);
        card_view = (CardView) itemView.findViewById(R.id.card_view);

        requestID = (TextView) itemView.findViewById(R.id.tvItem);
        areaModel = (TextView) itemView.findViewById(R.id.tvAreaModel);
    }
}