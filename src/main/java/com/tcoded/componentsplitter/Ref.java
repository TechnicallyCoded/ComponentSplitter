package com.tcoded.componentsplitter;

public class Ref<T> {

    private T value;

    public Ref(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Ref{" +
                "value=" + value +
                '}';
    }

}
