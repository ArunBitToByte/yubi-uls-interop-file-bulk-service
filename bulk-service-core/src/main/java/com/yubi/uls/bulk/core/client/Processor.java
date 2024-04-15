package com.yubi.uls.bulk.core.client;


public interface Processor<T> {
    public void process(T item);
}
