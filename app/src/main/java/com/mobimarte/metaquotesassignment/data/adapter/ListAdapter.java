package com.mobimarte.metaquotesassignment.data.adapter;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.mobimarte.metaquotesassignment.R;
import com.mobimarte.metaquotesassignment.data.datamodel.DataModel;

import java.util.List;

public class ListAdapter extends androidx.recyclerview.widget.ListAdapter<DataModel, ViewHolder> {

    private final List<DataModel> list;

    private final Context context;

    public ListAdapter(@NonNull DiffUtil.ItemCallback<DataModel> diffCallback,
                       List<DataModel> list, Context context) {
        super(diffCallback);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(list.get(position).getTextLine());


        holder.textViewLines.setOnLongClickListener(v -> {

            v.startActionMode(new ActionModeCallback(holder.textViewLines));

            return false;
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class ProductDiff extends DiffUtil.ItemCallback<DataModel> {


        @Override
        public boolean areItemsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return false;
        }
    }


    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.text_copied_label), text);
        if (clipboard == null || clip == null) return;
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
    }


    private class ActionModeCallback implements ActionMode.Callback {
        private final TextView textView;

        ActionModeCallback(TextView textView) {
            this.textView = textView;
        }

        @Override
        public boolean onCreateActionMode(@NonNull ActionMode mode, Menu menu) {
            // Inflate the menu resource file
            mode.getMenuInflater().inflate(R.menu.menu_text_selection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, @NonNull MenuItem item) {
            if (item.getItemId() == R.id.action_copy) {
                // Copy the selected text to the clipboard
                String selectedText = textView.getText().toString();
                copyToClipboard(selectedText);

                // Close the action mode
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }

}
