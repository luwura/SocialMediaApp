package org.example.service;

import java.util.List;

public interface ServiceInterface<ID,E> {
    E add(E e);

    E update(E e);

    E delete(E e);

    E find(ID id);

    List<E> getAll();
}

