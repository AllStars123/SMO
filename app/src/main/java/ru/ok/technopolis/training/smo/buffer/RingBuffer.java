package ru.ok.technopolis.training.smo.buffer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

import ru.ok.technopolis.training.smo.request.Request;

public class RingBuffer extends AbstractBuffer {

    private final AtomicInteger putPointer;
    private final AtomicInteger devicePointer;
    private final Deque<Request> deque;

    public RingBuffer(int capacity) {
        super(capacity);
        deque = new ArrayDeque<>(capacity);
        putPointer = new AtomicInteger(0);
        devicePointer = new AtomicInteger(0);
    }

    @Override
    @Nullable
    public Request putRequest(@NonNull Request request) {
        final int oldPointerPosition = putPointer.get();
        do {
            final Request requestFromBuffer = requests[putPointer.get()];
            if (requestFromBuffer == null) {
                System.out.printf("request %d.%d in buffer on %d position%n", request.getSourceNumber(), request.getRequestNumber(), putPointer.get());
                requests[putPointer.getAndIncrement()] = request;
                deque.push(request);
                request.onPutRequestInBuffer();
                if (putPointer.get() == capacity) {
                    putPointer.set(0);
                }
                return null;
            } else {
                putPointer.incrementAndGet();
                if (putPointer.get() == capacity) {
                    putPointer.set(0);
                }
            }
        } while (oldPointerPosition != putPointer.get());
        System.out.println("buffer is busy, searching place");
        return getRequestForRefusal(request);
    }

    @Nullable
    private Request getRequestForRefusal(@NonNull Request newRequest) {
        if (!deque.isEmpty()) {
            final Request refusalRequest = deque.pop();
            for (int i = 0; i < capacity; i++) {
                final Request request = requests[i];
                if (request.equals(refusalRequest)) {
                    requests[i] = newRequest;
                    deque.push(newRequest);
                    newRequest.onPutRequestInBuffer();
                    System.out.printf("request %d.%d in buffer on %d position%n", newRequest.getSourceNumber(), newRequest.getRequestNumber(), i);
                }
            }
            refusalRequest.onGetRequestFromBuffer();
            return refusalRequest;
        }
        return null;
    }

    @Override
    @Nullable
    public Request getRequestForDevice() {
        final int oldPointerPosition = devicePointer.get();
        if (!deque.isEmpty()) {
            do {
                final Request requestFromBuffer = requests[devicePointer.get()];
                if (requestFromBuffer != null) {
                    deque.remove(requestFromBuffer);
                    requests[devicePointer.get()] = null;
                    requestFromBuffer.onGetRequestFromBuffer();
                    if (devicePointer.incrementAndGet() == capacity) {
                        devicePointer.set(0);
                    }
                    return requestFromBuffer;
                } else {
                    if (devicePointer.incrementAndGet() == capacity) {
                        devicePointer.set(0);
                    }
                }
            } while (oldPointerPosition != devicePointer.get());
        }
        return null;
    }

    @Override
    public void clear() {
        super.clear();
        putPointer.set(0);
        devicePointer.set(0);
        deque.clear();
    }
}
