package ru.ok.technopolis.training.smo.request;

import java.util.Objects;

public class Request {

    private final int sourceNumber;
    private final int requestNumber;
    private final long startTineInSystem;

    private long startTimeInBuffer;
    private long endTimeInBuffer;
    private long sumDeltaTBuffer = 0;
    private long sumDeltaTDevice = 0;

    public Request(int sourceNumber, int requestNumber) {
        this.sourceNumber = sourceNumber;
        this.requestNumber = requestNumber;
        this.startTineInSystem = System.currentTimeMillis();
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public long getStartTineInSystem() {
        return startTineInSystem;
    }

    public void onPutRequestInBuffer() {
        startTimeInBuffer = System.currentTimeMillis();
    }

    public void onGetRequestFromBuffer() {
        endTimeInBuffer = System.currentTimeMillis();
    }

    public long timeInBuffer() {
        return startTimeInBuffer > 0 && endTimeInBuffer >= 0 ? endTimeInBuffer - startTimeInBuffer : 0;
    }

    public void addDeltaTBuffer(long deltaT) {
        sumDeltaTBuffer += deltaT;
    }

    public long getSumDeltaTBuffer() {
        return sumDeltaTBuffer;
    }

    public long getSumDeltaTDevice() {
        return sumDeltaTDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return sourceNumber == request.sourceNumber &&
                requestNumber == request.requestNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceNumber, requestNumber);
    }
}
