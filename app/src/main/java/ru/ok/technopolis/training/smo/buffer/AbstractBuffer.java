package ru.ok.technopolis.training.smo.buffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.ok.technopolis.training.smo.request.Request;

public abstract class AbstractBuffer {

    protected final int capacity;
    protected final Request[] requests;

    protected AbstractBuffer(int capacity) {
        this.capacity = capacity;
        requests = new Request[capacity];
    }

    public boolean isEmpty() {
        for (Request request: requests) {
            if (request != null) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            requests[i] = null;
        }
    }

    public Request[] getRequests() {
        return requests;
    }

    @Nullable
    public abstract Request putRequest(@NonNull Request request);

    @Nullable
    public abstract Request getRequestForDevice();
}
