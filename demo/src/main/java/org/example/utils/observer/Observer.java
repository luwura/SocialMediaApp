package org.example.utils.observer;


import org.example.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}