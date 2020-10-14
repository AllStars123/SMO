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

public class DeviceKoefAdapter extends RecyclerView.Adapter<DeviceKoefAdapter.DeviceKoefViewHolder> {

    private final List<Device> devices;

    private long timeWorkingSystem = 0;

    public DeviceKoefAdapter() {
        this.devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceKoefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceKoefViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.device_usable_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceKoefViewHolder holder, int position) {
        if (position == 0) {
            holder.nameTextView.setText(R.string.name);
            holder.deviceUsableKoefTextView.setText(R.string.koef_use);
        } else {
            final Device device = devices.get(position - 1);
            holder.nameTextView.setText(holder.itemView.getContext().getResources().getString(R.string.device_by_number, device.getDeviceNumber()));
            holder.deviceUsableKoefTextView.setText(holder.itemView.getContext().getResources().getString(R.string.float_3_digits_after_dot, ((float) device.getAllProcessingTime() / timeWorkingSystem)));
        }
    }

    @Override
    public int getItemCount() {
        return devices.isEmpty() ? 0 : devices.size() + 1;
    }

    public void setNewData(List<Device> newData, long timeWorkingSystem) {
        if (newData != null && !newData.isEmpty()) {
            devices.clear();
            devices.addAll(newData);
            this.timeWorkingSystem = timeWorkingSystem;
            notifyDataSetChanged();
        }
    }

    public static class DeviceKoefViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView deviceUsableKoefTextView;

        public DeviceKoefViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            deviceUsableKoefTextView = itemView.findViewById(R.id.device_usable_koef);
        }
    }
}
