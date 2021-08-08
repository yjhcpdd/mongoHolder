package com.pdd.db.mongo.test.bean;

import com.pdd.db.mongo.bean.MongoBean;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户表
 */
@Document("t_user_info")
public class TUserInfo extends MongoBean {
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 年龄
     */
    private Integer age;
}
