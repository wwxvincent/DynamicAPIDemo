package com.vincent.dynamicapidemo.entity.VO;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/1/24
 * @Description:
 */
public class ResponseVO {
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

    public static ResponseVO apiSuccess(Object data) {
        ResponseVO dto = new ResponseVO();
        dto.setData(data);
        dto.setSuccess(true);
        dto.setMsg("接口访问成功");
        return dto;

    }

    public static ResponseVO successWithMsg(String msg) {
        ResponseVO dto = new ResponseVO();
        dto.setData(null);
        dto.setSuccess(true);
        dto.setMsg(msg);
        return dto;
    }

    public static ResponseVO successWithData(Object data) {
        ResponseVO dto = new ResponseVO();
        dto.setData(data);
        dto.setSuccess(true);
        return dto;
    }

    public static ResponseVO fail(String msg) {
        ResponseVO dto = new ResponseVO();
        dto.setSuccess(false);
        dto.setMsg(msg);
        return dto;

    }
}
