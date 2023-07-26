package com.mobimarte.metaquotesassignment.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobimarte.metaquotesassignment.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView textViewLines;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewLines = itemView.findViewById(R.id.id_text_line);


    }

    public void bind(String textLine){
        textViewLines.setText(textLine);
    }

    @NonNull
    static ViewHolder create(@NonNull ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_layout, parent, false);

        return new ViewHolder(view);
    }
}
