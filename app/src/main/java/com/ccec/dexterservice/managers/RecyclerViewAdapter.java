package com.ccec.dexterservice.managers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterservice.R;
import com.ccec.dexterservice.ServiceFragment;
import com.ccec.dexterservice.entities.RequestRow;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<RequestRow> requestRow;
    protected Context context;
    private ServiceFragment fragment;

    public RecyclerViewAdapter(Context context, List<RequestRow> requestRow, ServiceFragment fragment) {
        this.requestRow = requestRow;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_service_row, parent, false);
        viewHolder = new RecyclerViewHolders(layoutView, requestRow);

        fragment.stopLoading();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        Map<String, Object> requestMap = requestRow.get(position).getRequestMap();
        Map<String, Object> itemMap = requestRow.get(position).getItemMap();

        holder.requestID.setText((String) requestMap.get("key"));
        holder.areaModel.setText((String) itemMap.get("model"));
        holder.openTime.setText((String) requestMap.get("openTime"));
        holder.scheduledTime.setText((String) requestMap.get("scheduleTime"));

        holder.requestID.setTypeface(FontsManager.getBoldTypeface(context));
        holder.areaModel.setTypeface(FontsManager.getRegularTypeface(context));
        holder.openTime.setTypeface(FontsManager.getRegularTypeface(context));
        holder.scheduledTime.setTypeface(FontsManager.getRegularTypeface(context));

        if (((String) requestMap.get("status")).equals("Accepted")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.chat.setTypeface(FontsManager.getRegularTypeface(context));
        } else if (((String) requestMap.get("status")).equals("Completed")) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.chat.setVisibility(View.INVISIBLE);

        } else {
            holder.chat.setTypeface(FontsManager.getRegularTypeface(context));
            holder.accept.setTypeface(FontsManager.getRegularTypeface(context));
        }
    }

    @Override
    public int getItemCount() {
        return this.requestRow.size();
    }
}
