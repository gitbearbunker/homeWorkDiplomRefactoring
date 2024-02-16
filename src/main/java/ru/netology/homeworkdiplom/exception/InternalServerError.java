package ru.netology.homeworkdiplom.exception;

public class InternalServerError extends RuntimeException {

    private final long id;

    public InternalServerError(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}