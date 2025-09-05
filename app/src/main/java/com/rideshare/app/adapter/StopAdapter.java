package com.rideshare.app.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.rideshare.app.R;
import com.rideshare.app.fragement.HomeFragment;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private List<String> stopList;
    private OnRemoveClickListener onRemoveClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
        void onAddStopClick(int position);
    }

    public StopAdapter(List<String> stopList, OnRemoveClickListener onRemoveClickListener) {
        this.stopList = stopList;
        this.onRemoveClickListener = onRemoveClickListener;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_stop_layout, parent, false);
        return new StopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        holder.etStop.setText(stopList.get(position));

        holder.etStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRemoveClickListener.onAddStopClick(position);
                holder.btnRemove.setVisibility(View.VISIBLE);
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRemoveClickListener.onRemoveClick(position);
                holder.btnRemove.setVisibility(View.GONE);
                stopList.clear();
                stopList.add("");
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stopList.size();
    }

    public static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView etStop;
        ImageButton btnRemove;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            etStop = itemView.findViewById(R.id.et_stop);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}

