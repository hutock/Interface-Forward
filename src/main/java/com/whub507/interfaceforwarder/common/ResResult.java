package com.whub507.interfaceforwarder.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResResult<T> {
    static String OK = "ok";
    private int code;
    private String message;

    private List<Field> fields;

    private T data;

    public ResResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ResResult<T> ok(T data, List<Field> fields) {
        return new ResResult<T>(0, "ok", fields, data);
    }

    public static <T> ResResult<T> ok(T data) {
        return new ResResult<T>(0, "ok", null, data);
    }
    public static <T> ResResult<T> ok(T data ,String msg) {
        return new ResResult<T>(0, msg);
    }

    public static <T> ResResult<T> ok(T data ,List<Field> fields,String msg){
        return new ResResult<T>(0, msg,fields,data);
    }

    public static <T> ResResult<T> error(int code, String errmsg) {
        return new ResResult<T>(code, errmsg);
    }
}
