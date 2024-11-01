package com.vincent.dynamicapidemo.common;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
public class ResponseDTO {
    String msg;
    Object data;
    boolean success;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static ResponseDTO apiSuccess(Object data) {
        ResponseDTO dto = new ResponseDTO();
        dto.setData(data);
        dto.setSuccess(true);
        dto.setMsg("接口访问成功");
        return dto;

    }

    public static ResponseDTO successWithMsg(String msg) {
        ResponseDTO dto = new ResponseDTO();
        dto.setData(null);
        dto.setSuccess(true);
        dto.setMsg(msg);
        return dto;
    }

    public static ResponseDTO successWithData(Object data) {
        ResponseDTO dto = new ResponseDTO();
        dto.setData(data);
        dto.setSuccess(true);
        return dto;
    }

    public static ResponseDTO fail(String msg) {
        ResponseDTO dto = new ResponseDTO();
        dto.setSuccess(false);
        dto.setMsg(msg);
        return dto;

    }
}
