package ru.ok.technopolis.training.smo.statistic;

import androidx.annotation.NonNull;

import java.util.List;

import ru.ok.technopolis.training.smo.activity.SmoActivity;
import ru.ok.technopolis.training.smo.device.Device;
import ru.ok.technopolis.training.smo.request.Request;

public class StatisticsCollector {

    private final SmoActivity smoActivity;

    private long processingTimeAllDevice;
    private long timeInBufferAllRequests;
    private long startTime;
    private int countRefusalRequests;
    private int countProcessedRequest;
    private int countSources;
    private int countDevices;
    private int countRequests;
    private int bufferCapacity;
    private float lambda;
    private float alpha;
    private float beta;

    public StatisticsCollector(@NonNull final SmoActivity smoActivity) {
        this.processingTimeAllDevice = 0;
        this.timeInBufferAllRequests = 0;
        this.countRefusalRequests = 0;
        this.countProcessedRequest = 0;
        this.smoActivity = smoActivity;
    }

    public void onStartSystem(int countSources,
                              int countDevices,
                              int countRequests,
                              int bufferCapacity,
                              float lambda,
                              float alpha,
                              float beta) {
        this.countSources = countSources;
        this.countDevices = countDevices;
        this.countRequests = countRequests;
        this.bufferCapacity = bufferCapacity;
        this.lambda = lambda;
        this.alpha = alpha;
        this.beta = beta;
        System.out.println("start system");
        startTime = System.currentTimeMillis();
    }

    public void onEndSystem(List<Device> devices, long sumDeltaT) {
        long endTime = System.currentTimeMillis() + sumDeltaT;
        smoActivity.showResult(countSources, countDevices, bufferCapacity, lambda, alpha, beta,
                (double) countRefusalRequests / (countRefusalRequests + countProcessedRequest),
                ((double) processingTimeAllDevice / countDevices) / (endTime - startTime),
                (((double) processingTimeAllDevice / countRequests) + ((double) timeInBufferAllRequests / (countRequests + countRefusalRequests))) / 1000,
                devices, endTime - startTime);
    }

    public void onDeviceEndProcessing(@NonNull Request request, long processingTime) {
        timeInBufferAllRequests += request.timeInBuffer() + request.getSumDeltaTBuffer();
        processingTimeAllDevice += processingTime;
        countProcessedRequest++;
    }

    public void onRequestRefusal(@NonNull Request request) {
        timeInBufferAllRequests += request.timeInBuffer() + request.getSumDeltaTBuffer();
        countRefusalRequests++;
    }

    public int getCountProcessedRequest() {
        return countProcessedRequest;
    }

    public void clear() {
        this.processingTimeAllDevice = 0;
        this.timeInBufferAllRequests = 0;
        this.countRefusalRequests = 0;
        this.countProcessedRequest = 0;
    }
}
