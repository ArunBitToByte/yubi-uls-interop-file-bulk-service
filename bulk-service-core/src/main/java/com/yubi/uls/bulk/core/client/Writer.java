package com.yubi.uls.bulk.core.client;

public interface Writer<T> {
    public void write(T item);
}
