package com.pdd.db.mongo.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * mongo的bean定义
 * <br>实现此接口的类，可以进行mongo数据库操作
 **/
@Data
public class MongoBean implements Serializable {
    
    /**
     * mongo主键（对应数据库中自动生成的字段：_id）
     * 此字段不可设置（mongo自己生成）
     */
    private String id;
    
    /**
     * 删除标志（false：正常；true：删除）
     */
    private Boolean delFlag;
    
    /**
     * 创建人ID
     */
    private String createUserId;
    
    /**
     * 创建人姓名
     */
    private String createUserName;
    
    /**
     * 创建时间
     */
    private Integer createDate;
    
    /**
     * 更新人ID
     */
    private String updateUserId;
    
    /**
     * 更新人姓名
     */
    private String updateUserName;
    
    /**
     * 更新时间
     */
    private Integer updateDate;
}
