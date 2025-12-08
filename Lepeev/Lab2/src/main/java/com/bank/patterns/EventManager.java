package com.bank.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {
    private final List<Consumer<String>> listeners = new ArrayList<>();

    public void subscribe(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void notify(String message) {
        for (Consumer<String> listener : listeners) {
            listener.accept(message);
        }
    }
}