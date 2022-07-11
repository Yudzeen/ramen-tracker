package ebj.yujinkun.ramentracker.util;

import androidx.core.util.Consumer;

public class Resource<T> {

    public enum Status {
        LOADING, SUCCESS, ERROR
    }

    private final Status status;
    private final T data;
    private final Throwable error;
    private boolean consumed = false;

    private Resource(Status status, T data, Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> success() {
        return success(null);
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(Throwable error) {
        return new Resource<>(Status.ERROR, null, error);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public void doOnSuccess(Consumer<T> consumer) {
        if (consumed) {
            return;
        }
        if (status == Status.SUCCESS) {
            consumed = true;
            consumer.accept(data);
        }
    }

    public void doOnError(Consumer<Throwable> consumer) {
        if (consumed) {
            return;
        }
        if (status == Status.ERROR) {
            consumed = true;
            consumer.accept(error);
        }
    }

    public void doOnLoading(Runnable runnable) {
        if (consumed) {
            return;
        }
        if (status == Status.LOADING) {
            consumed = true;
            runnable.run();
        }
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }
}