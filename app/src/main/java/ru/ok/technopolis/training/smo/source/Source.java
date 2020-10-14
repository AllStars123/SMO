package ru.ok.technopolis.training.smo.source;

import androidx.annotation.NonNull;

import ru.ok.technopolis.training.smo.controller.Controller;
import ru.ok.technopolis.training.smo.request.Request;

public class Source {

    @NonNull
    private final Controller controller;
    private final float lambda;
    private final int sourceNumber;

    private int countRefusedRequests;
    private int requestNumber;
    private long generateTimeLastRequest;
    private long startGenerateTimeLastRequest;
    private Request request;

    public Source(float lambda, int sourceNumber, @NonNull Controller controller) {
        this.lambda = lambda;
        this.sourceNumber = sourceNumber;
        this.controller = controller;
        this.countRefusedRequests = 0;
        this.requestNumber = 0;
        startGenerateNewRequest();
    }

    public void startGenerateNewRequest() {
        final long timeNow = System.currentTimeMillis();
        if (requestNumber != 0) {
            System.out.printf("generated requests %d.%d with time = %d\n", sourceNumber, requestNumber, timeNow - startGenerateTimeLastRequest);
        }
        startGenerateTimeLastRequest = timeNow;
        final long time = generateTime();
        generateTimeLastRequest = time + startGenerateTimeLastRequest;
    }

    public void submitRequest() {
        request = new Request(sourceNumber, requestNumber);
        generateTimeLastRequest = 0;
        requestNumber++;
    }

    public long getGenerateTimeLastRequest() {
        return generateTimeLastRequest;
    }

    public void minusGenerateTimeLastRequest(long deltaT) {
        if (generateTimeLastRequest != 0) {
            generateTimeLastRequest -= deltaT;
        }
    }

    public Request getRequest() {
        return request;
    }

    public void incrementRefusalRequests() {
        countRefusedRequests++;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public int getCountRefusedRequests() {
        return countRefusedRequests;
    }

    private long generateTime() {
        return (long) (-1 / lambda * Math.log(Math.random()) * 1000);
    }
}
