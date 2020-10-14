package ru.ok.technopolis.training.smo.controller;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.ok.technopolis.training.smo.activity.SmoActivity;
import ru.ok.technopolis.training.smo.adapter.DeviceAdapter;
import ru.ok.technopolis.training.smo.adapter.SourceAdapter;
import ru.ok.technopolis.training.smo.buffer.AbstractBuffer;
import ru.ok.technopolis.training.smo.buffer.RingBuffer;
import ru.ok.technopolis.training.smo.device.Device;
import ru.ok.technopolis.training.smo.request.Request;
import ru.ok.technopolis.training.smo.source.Source;
import ru.ok.technopolis.training.smo.statistic.StatisticsCollector;

public class Controller implements Runnable {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private final List<Device> devices;
    private final List<Source> sources;
    private final SourceAdapter sourceAdapter;
    private final DeviceAdapter deviceAdapter;
    private final StatisticsCollector statisticsCollector;
    private final ExecutorService executorService;

    private int countRequests;
    private AbstractBuffer buffer;
    private long deltaT = 0;
    private long sumDeltaT = 0;

    public Controller(@NonNull final SourceAdapter sourceAdapter,
                      @NonNull final DeviceAdapter deviceAdapter,
                      @NonNull final SmoActivity smoActivity) {
        this.sourceAdapter = sourceAdapter;
        this.deviceAdapter = deviceAdapter;
        this.statisticsCollector = new StatisticsCollector(smoActivity);
        this.devices = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        while (statisticsCollector.getCountProcessedRequest() < countRequests) {
            sumDeltaT += deltaT;
            for (Request requestInBuffer : buffer.getRequests()) {
                if (requestInBuffer != null) {
                    requestInBuffer.addDeltaTBuffer(deltaT);
                }
            }
            for (Source source : sources) {
                source.minusGenerateTimeLastRequest(deltaT);
            }
            for (Device device : devices) {
                final Request request = device.getRequest();
                if (request != null) {
                    device.minusDeltaT(deltaT);
                }
            }
            boolean needRefresh = false;
            long newNearestTime = Long.MAX_VALUE;
            for (Source source : sources) {
                if (source.getGenerateTimeLastRequest() <= System.currentTimeMillis()) {
                    System.out.println("sources true");
                    needRefresh = true;
                    source.submitRequest();
                    final Request request = source.getRequest();
                    source.startGenerateNewRequest();
                    Request rejectedRequest = buffer.putRequest(request);
                    if (rejectedRequest != null) {
                        System.out.printf("request %d.%d was refused%n", rejectedRequest.getSourceNumber(), rejectedRequest.getRequestNumber());
                        sources.get(rejectedRequest.getSourceNumber() - 1).incrementRefusalRequests();
                        statisticsCollector.onRequestRefusal(rejectedRequest);
                    }
                    if (hasFreeDevice()) {
                        final Request requestForDevice = buffer.getRequestForDevice();
                        if (!sendRequestToDevice(requestForDevice) && requestForDevice != null) {
                            rejectedRequest = buffer.putRequest(requestForDevice);
                            if (rejectedRequest != null) {
                                System.out.printf("request %d.%d was refused%n", rejectedRequest.getSourceNumber(), rejectedRequest.getRequestNumber());
                                sources.get(rejectedRequest.getSourceNumber() - 1).incrementRefusalRequests();
                                statisticsCollector.onRequestRefusal(rejectedRequest);
                            }
                        }
                    }
                }
                if (source.getGenerateTimeLastRequest() != 0 && source.getGenerateTimeLastRequest() < newNearestTime) {
                    newNearestTime = source.getGenerateTimeLastRequest();
                }
            }

            for (Device device : devices) {
                if (device.getEndProcessingTime() - deltaT <= System.currentTimeMillis()) {
                    needRefresh = true;
                    final Request processedRequest = device.getRequest();
                    final long processingTime = device.submitEnd();
                    if (processingTime != 0) {
                        statisticsCollector.onDeviceEndProcessing(processedRequest, processingTime);
                    }
                    if (!buffer.isEmpty()) {
                        final Request requestForDevice = buffer.getRequestForDevice();
                        if (!sendRequestToDevice(requestForDevice) && requestForDevice != null) {
                            Request rejectedRequest = buffer.putRequest(requestForDevice);
                            if (rejectedRequest != null) {
                                System.out.printf("request %d.%d was refused%n", rejectedRequest.getSourceNumber(), rejectedRequest.getRequestNumber());
                                sources.get(rejectedRequest.getSourceNumber() - 1).incrementRefusalRequests();
                                statisticsCollector.onRequestRefusal(rejectedRequest);
                            }
                        }
                    }
                }
                if (device.getEndProcessingTime() != 0 && device.getEndProcessingTime() < newNearestTime) {
                    newNearestTime = device.getEndProcessingTime();
                }
            }
            if (needRefresh) {
                runOnMainThread(() -> {
                    sourceAdapter.notifyDataSetChanged();
                    deviceAdapter.notifyDataSetChanged();
                    System.out.println("notify");
                });
            }
            deltaT = newNearestTime - System.currentTimeMillis();
            System.out.println("main cycle iteration");
        }
        endSystem();
    }

    public void startSystem(int countSources,
                            int countDevices,
                            int countRequests,
                            int bufferCapacity,
                            float lambda,
                            float alpha,
                            float beta) {
        this.countRequests = countRequests;
        this.buffer = new RingBuffer(bufferCapacity);
        for (int i = 1; i <= countDevices; i++) {
            this.devices.add(new Device(i, alpha, beta, this));
        }
        deviceAdapter.setNewData(devices);
        statisticsCollector.onStartSystem(countSources, countDevices, countRequests, bufferCapacity, lambda, alpha, beta);
        for (int i = 1; i <= countSources; i++) {
            final Source source = new Source(lambda, i, this);
            sources.add(source);
        }
        sourceAdapter.setNewData(sources);
        executorService.submit(this);
    }

    public void endSystem() {
        runOnMainThread(() -> {
            for (Request request : buffer.getRequests()) {
                if (request != null) {
                    sources.get(request.getSourceNumber() - 1).incrementRefusalRequests();
                }
            }

            sourceAdapter.notifyDataSetChanged();
            deviceAdapter.notifyDataSetChanged();
            buffer.clear();
            statisticsCollector.onEndSystem(devices, sumDeltaT);
        });
    }

    public void clear() {
        devices.clear();
        sources.clear();
        statisticsCollector.clear();
        deltaT = 0;
        sumDeltaT = 0;
    }

    private boolean sendRequestToDevice(@Nullable Request request) {
        if (request == null) {
            return false;
        }
        for (Device device : devices) {
            if (device.setNewRequest(request)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFreeDevice() {
        for (Device device : devices) {
            if (!device.isBusy()) {
                return true;
            }
        }
        return false;
    }

    public static void runOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }
}
