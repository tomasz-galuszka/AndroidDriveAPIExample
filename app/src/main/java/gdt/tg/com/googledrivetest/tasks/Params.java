package gdt.tg.com.googledrivetest.tasks;

import gdt.tg.com.googledrivetest.base.ErrorHandler;
import gdt.tg.com.googledrivetest.base.SuccessHandler;

/**
 * Created by tomasz on 08.07.15.
 */
public class Params<T> {

    private final T data;
    private final SuccessHandler<T> successHandler;
    private final ErrorHandler errorHandler;

    public Params(T data, ErrorHandler errorHandler, SuccessHandler<T> successHandler) {
        this.data = data;
        this.successHandler = successHandler;
        this.errorHandler = errorHandler;
    }

    public T getData() {
        return data;
    }

    public SuccessHandler<T> getSuccessHandler() {
        return successHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
