package ru.ok.technopolis.training.smo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import ru.ok.technopolis.training.smo.R;

public class LaunchActivity extends AppCompatActivity {

    private Slider countSourceSlider;
    private Slider countDevicesSlider;
    private Slider countRequestsSlider;
    private Slider bufferCapacitySlider;
    private EditText lambdaEditText;
    private EditText alphaEditText;
    private EditText betaEditText;
    private TextView startTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        countSourceSlider = findViewById(R.id.count_source);
        countDevicesSlider = findViewById(R.id.count_devices);
        countRequestsSlider = findViewById(R.id.count_requests);
        bufferCapacitySlider = findViewById(R.id.buffer_capacity);
        lambdaEditText = findViewById(R.id.lambda);
        alphaEditText = findViewById(R.id.alpha);
        betaEditText = findViewById(R.id.beta);
        startTextView = findViewById(R.id.start_tv);
        startTextView.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SmoActivity.class);
            final int countSources = (int) countSourceSlider.getValue();
            intent.putExtra(SmoActivity.COUNT_SOURCES_EXTRA, countSources);
            final int countDevices = (int) countDevicesSlider.getValue();
            intent.putExtra(SmoActivity.COUNT_DEVICES_EXTRA, countDevices);
            final int countRequests = (int) countRequestsSlider.getValue();
            intent.putExtra(SmoActivity.COUNT_REQUESTS_EXTRA, countRequests);
            final int bufferCapacity = (int) bufferCapacitySlider.getValue();
            intent.putExtra(SmoActivity.BUFFER_CAPACITY_EXTRA, bufferCapacity);
            final String lambda = lambdaEditText.getText().toString();
            if (!TextUtils.isEmpty(lambda)) {
                intent.putExtra(SmoActivity.LAMBDA_EXTRA, Float.parseFloat(lambda));
            }
            final String alpha = alphaEditText.getText().toString();
            if (!TextUtils.isEmpty(alpha)) {
                intent.putExtra(SmoActivity.ALPHA_EXTRA, Float.parseFloat(alpha));
            }
            final String beta = betaEditText.getText().toString();
            if (!TextUtils.isEmpty(beta)) {
                intent.putExtra(SmoActivity.BETA_EXTRA, Float.parseFloat(beta));
            }
            startActivity(intent);
        });
    }
}