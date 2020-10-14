package ru.nikita.purnov.smo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.nikita.purnov.smo.R;
import ru.nikita.purnov.smo.source.Source;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceViewHolder> {

    private final List<Source> sources;

    public SourceAdapter() {
        this.sources = new ArrayList<>();
    }

    @NonNull
    @Override
    public SourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SourceViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.source_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SourceViewHolder holder, int position) {
        if (position == 0) {
            holder.sourceNumberTextView.setText(holder.itemView.getContext().getResources().getString(R.string.name));
            holder.countGeneratedRequestTextView.setText(holder.itemView.getContext().getResources().getString(R.string.count_generated_requests));
            holder.countRefusedRequestNumberTextView.setText(holder.itemView.getContext().getResources().getString(R.string.count_refused_requests));
        } else {
            final Source source = sources.get(position - 1);
            holder.sourceNumberTextView.setText(holder.itemView.getContext().getResources().getString(R.string.source_by_number, source.getSourceNumber()));
            holder.countGeneratedRequestTextView.setText(String.valueOf(source.getRequestNumber()));
            holder.countRefusedRequestNumberTextView.setText(String.valueOf(source.getCountRefusedRequests()));
        }
    }

    @Override
    public int getItemCount() {
        return sources.isEmpty() ? 0 : sources.size() + 1;
    }

    public void setNewData(List<Source> newData) {
        if (newData != null && !newData.isEmpty()) {
            sources.clear();
            sources.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public static class SourceViewHolder extends RecyclerView.ViewHolder {

        public TextView sourceNumberTextView;
        public TextView countGeneratedRequestTextView;
        public TextView countRefusedRequestNumberTextView;

        public SourceViewHolder(@NonNull View itemView) {
            super(itemView);
            sourceNumberTextView = itemView.findViewById(R.id.source_number);
            countGeneratedRequestTextView = itemView.findViewById(R.id.count_generated_request);
            countRefusedRequestNumberTextView = itemView.findViewById(R.id.count_refused_request);
        }
    }
}
