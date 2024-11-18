package com.vincent.dynamicapidemo.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Vincent(Wenxuan) Wang
 * @Date: 11/8/24
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO implements Serializable {
    private String data;
    private String title;
    private String content;
}
