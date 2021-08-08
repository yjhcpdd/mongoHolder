package com.pdd.db.mongo.holder;

/**
 * mongo游标执行器
 **/
public interface Executor<T> {
    
    /**
     * 执行
     * @param cModel 执行实体类
     * @return void 
     */
    void invoke(T cModel) throws Exception;
}
