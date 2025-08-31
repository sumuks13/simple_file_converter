package dev.sumuks.simplefileconverter.beans;

public class ApiResponseBean<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponseBean() {}

    public ApiResponseBean(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseBean<T> success(T data, String message) {
        return new ApiResponseBean<>(true, message, data);
    }

    public static <T> ApiResponseBean<T> success(T data) {
        return new ApiResponseBean<>(true, "Success", data);
    }

    public static <T> ApiResponseBean<T> failure(String message) {
        return new ApiResponseBean<>(false, message, null);
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
