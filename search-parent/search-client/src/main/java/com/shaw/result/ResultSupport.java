package com.shaw.result;


import java.util.ArrayList;
import java.util.List;


public class ResultSupport<T> implements Result<T> {

    private static final long serialVersionUID = 4661096805690919752L;

    private boolean success = true;
    private String resultCode;
    private String message;
    private T model;


    private List<T> models = new ArrayList<T>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getModel() {
        return model;
    }

    /**
     * 创建一个result。
     */
    public ResultSupport() {
    }

    /**
     * 创建一个result。
     *
     * @param success 是否成功
     */
    public ResultSupport(boolean success) {
        this.success = success;
    }

    public ResultSupport(boolean success, String resultCode, String message) {
        this.success = success;
        this.resultCode = resultCode;
        this.message = message;
    }

    public ResultSupport(String resultCode, String message) {
        this.success = Boolean.FALSE;
        this.resultCode = resultCode;
        this.message = message;
    }

    public ResultSupport(ResultCode resultCode) {
        this.success = false;
        this.resultCode = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the resultCode
     */
    public String getResultCode() {
        if (!isSuccess() && isBlank(resultCode)) {
            return BaseResultCode.SYSTEM_DEFAULT.getCode();
        }
        return resultCode;
    }

    /**
     * @param resultCode the resultCode to set
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<T> getModels() {
        return models;
    }

    public void setModels(List<T> models) {
        this.models = models;
    }

    @Override
    public void setModel(T model) {
        this.model = model;
    }


}
