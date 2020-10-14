package ru.nikita.purnov.smo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ru.nikita.purnov.smo.R;
import ru.nikita.purnov.smo.adapter.DeviceAdapter;
import ru.nikita.purnov.smo.adapter.DeviceKoefAdapter;
import ru.nikita.purnov.smo.adapter.SourceAdapter;
import ru.nikita.purnov.smo.controller.Controller;
import ru.nikita.purnov.smo.device.Device;

public class SmoActivity extends AppCompatActivity {

    public static final String COUNT_SOURCES_EXTRA = "count_sources";
    public static final String COUNT_DEVICES_EXTRA = "count_devices";
    public static final String COUNT_REQUESTS_EXTRA = "count_requests";
    public static final String BUFFER_CAPACITY_EXTRA = "buffer_capacity";
    public static final String LAMBDA_EXTRA = "lambda";
    public static final String ALPHA_EXTRA = "alpha_sources";
    public static final String BETA_EXTRA = "beta_sources";

    private DeviceKoefAdapter deviceKoefAdapter;

    private RecyclerView sourceRecyclerView;
    private RecyclerView deviceRecyclerView;
    private RecyclerView deviceUsableRecyclerView;

    private TextView countSourcesTextView;
    private TextView countDevicesTextView;
    private TextView bufferCapacityTextView;
    private TextView lambdaTextView;
    private TextView alphaTextView;
    private TextView betaTextView;
    private TextView probabilityRefusalTextView;
    private TextView loadFactorTextView;
    private TextView timeInSystemTextView;
    private TextView startTextView;

    private View dividerSecond;
    private View dividerThird;

    Controller controller;
    int countSources;
    int countDevices;
    int countRequests;
    int bufferCapacity;
    float lambda;
    float alpha;
    float beta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smo);

        sourceRecyclerView = findViewById(R.id.source_recycler);
        deviceRecyclerView = findViewById(R.id.device_recycler);
        deviceUsableRecyclerView = findViewById(R.id.device_usable_recycler);

        countSourcesTextView = findViewById(R.id.count_sources);
        countDevicesTextView = findViewById(R.id.count_devices);
        bufferCapacityTextView = findViewById(R.id.buffer_capacity);
        lambdaTextView = findViewById(R.id.lambda);
        alphaTextView = findViewById(R.id.alpha);
        betaTextView = findViewById(R.id.beta);
        probabilityRefusalTextView = findViewById(R.id.probability_refusal);
        loadFactorTextView = findViewById(R.id.load_factor);
        timeInSystemTextView = findViewById(R.id.time_in_system);
        startTextView = findViewById(R.id.start_tv);
        startTextView.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            if (controller != null) {
                controller.clear();
            }
            start();
        });

        dividerSecond = findViewById(R.id.divider_second);
        dividerThird = findViewById(R.id.divider_third);

        SourceAdapter sourceAdapter = new SourceAdapter();
        sourceRecyclerView.setAdapter(sourceAdapter);
        sourceRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        DeviceAdapter deviceAdapter = new DeviceAdapter();
        deviceRecyclerView.setAdapter(deviceAdapter);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        deviceKoefAdapter = new DeviceKoefAdapter();
        deviceUsableRecyclerView.setAdapter(deviceKoefAdapter);
        deviceUsableRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        final Intent intent = getIntent();
        countSources = intent.getIntExtra(COUNT_SOURCES_EXTRA, 13);
        countDevices = intent.getIntExtra(COUNT_DEVICES_EXTRA, 3);
        countRequests = intent.getIntExtra(COUNT_REQUESTS_EXTRA, 5000);
        bufferCapacity = intent.getIntExtra(BUFFER_CAPACITY_EXTRA, 15);
        lambda = intent.getFloatExtra(LAMBDA_EXTRA, 2.7f);
        alpha = intent.getFloatExtra(ALPHA_EXTRA, 0.05f);
        beta = intent.getFloatExtra(BETA_EXTRA, 0.15f);
        controller = new Controller(sourceAdapter, deviceAdapter, this);
    }

    private void start() {
        controller.startSystem(countSources, countDevices, countRequests, bufferCapacity, lambda, alpha, beta);
    }

    public void showResult(int countSources, int countDevices, int bufferCapacity,
                           float lambda, float alpha, float beta, double probabilityRefusal,
                           double loadFactor, double timeInSystem, List<Device> devices, long tomeWorkingSystem) {
        startTextView.setVisibility(View.VISIBLE);
        countSourcesTextView.setVisibility(View.VISIBLE);
        countSourcesTextView.setText(getString(R.string.count_sources_result, countSources));
        countDevicesTextView.setVisibility(View.VISIBLE);
        countDevicesTextView.setText(getString(R.string.count_devices_result, countDevices));
        bufferCapacityTextView.setVisibility(View.VISIBLE);
        bufferCapacityTextView.setText(getString(R.string.buffer_capacity_result, bufferCapacity));
        lambdaTextView.setVisibility(View.VISIBLE);
        lambdaTextView.setText(getString(R.string.lambda_result, lambda));
        alphaTextView.setVisibility(View.VISIBLE);
        alphaTextView.setText(getString(R.string.alpha_result, alpha));
        betaTextView.setVisibility(View.VISIBLE);
        betaTextView.setText(getString(R.string.beta_result, beta));
        probabilityRefusalTextView.setVisibility(View.VISIBLE);
        probabilityRefusalTextView.setText(getString(R.string.probability_refusal_result, (float) 100 * probabilityRefusal));
        loadFactorTextView.setVisibility(View.VISIBLE);
        loadFactorTextView.setText(getString(R.string.load_factor_result, (float) 100 * loadFactor));
        timeInSystemTextView.setVisibility(View.VISIBLE);
        timeInSystemTextView.setText(getString(R.string.time_in_system_result, (float) timeInSystem));
        deviceUsableRecyclerView.setVisibility(View.VISIBLE);
        deviceKoefAdapter.setNewData(devices, tomeWorkingSystem);
        dividerSecond.setVisibility(View.VISIBLE);
        dividerThird.setVisibility(View.VISIBLE);
    }
}