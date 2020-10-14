package ru.ok.technopolis.training.smo.device;

import androidx.annotation.NonNull;

import ru.ok.technopolis.training.smo.controller.Controller;
import ru.ok.technopolis.training.smo.request.Request;

public class Device {

    private final float alpha;
    private final float beta;
    private final int deviceNumber;
    private final Controller controller;

    private int countProcessedRequests;
    private long allProcessingTime;
    private long endProcessingTime;
    private long startProcessingTime;
    private Request request;
    private long deltaT = 0;

    public Device(int deviceNumber, float alpha, float beta, @NonNull Controller controller) {
        this.alpha = alpha;
        this.beta = beta;
        this.deviceNumber = deviceNumber;
        this.controller = controller;
    }

    public boolean isBusy() {
        return endProcessingTime != 0 && endProcessingTime >= System.currentTimeMillis();
    }

    public long submitEnd() {
        if (endProcessingTime != 0 && request != null) {
            countProcessedRequests++;
            final long duration = (System.currentTimeMillis() - startProcessingTime) + deltaT;
            allProcessingTime += duration;
            deltaT = 0;
            endProcessingTime = 0;
            System.out.printf("request %d.%d was processed in device %d with processing time %d%n",
                    request.getSourceNumber(), request.getRequestNumber(), deviceNumber, duration);
            request = null;
            return duration;
        }
        return 0;
    }

    public boolean setNewRequest(@NonNull Request newRequest) {
        if (isBusy()) {
            return false;
        } else {
            startProcessingTime = System.currentTimeMillis();
            long processingTime = generateProcessingTime();
            endProcessingTime = processingTime + startProcessingTime;
            request = newRequest;
            return true;
        }
    }

    public void minusDeltaT(long deltaT) {
        if (endProcessingTime != 0) {
            endProcessingTime -= deltaT;
            this.deltaT += deltaT;
        }
    }

    private long generateProcessingTime() {
        return (long) ((Math.random() * (beta - alpha) + alpha) * 1000);
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public int getCountProcessedRequests() {
        return countProcessedRequests;
    }

    public long getAllProcessingTime() {
        return allProcessingTime;
    }

    public long getEndProcessingTime() {
        return endProcessingTime;
    }

    public Request getRequest() {
        return request;
    }
}
