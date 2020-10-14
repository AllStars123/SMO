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
import ru.nikita.purnov.smo.device.Device;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final List<Device> devices;

    public DeviceAdapter() {
        this.devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        if (position == 0) {
            holder.deviceNumberTextView.setText(holder.itemView.getContext().getResources().getString(R.string.name));
            holder.deviceStatusTextView.setText(holder.itemView.getContext().getResources().getString(R.string.status));
            holder.countProcessedRequestsTextView.setText(holder.itemView.getContext().getResources().getString(R.string.count_processed_requests));
        } else {
            final Device device = devices.get(position - 1);
            holder.deviceNumberTextView.setText(holder.itemView.getContext().getResources().getString(R.string.device_by_number, device.getDeviceNumber()));
            holder.deviceStatusTextView.setText(device.isBusy() ? R.string.busy : R.string.waiting);
            holder.countProcessedRequestsTextView.setText(String.valueOf(device.getCountProcessedRequests()));
        }
    }

    @Override
    public int getItemCount() {
        return devices.isEmpty() ? 0 : devices.size() + 1;
    }

    public void setNewData(List<Device> newData) {
        if (newData != null && !newData.isEmpty()) {
            devices.clear();
            devices.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        public TextView deviceNumberTextView;
        public TextView deviceStatusTextView;
        public TextView countProcessedRequestsTextView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNumberTextView = itemView.findViewById(R.id.device_number);
            deviceStatusTextView = itemView.findViewById(R.id.device_status);
            countProcessedRequestsTextView = itemView.findViewById(R.id.count_processed_requests);
        }
    }
}
