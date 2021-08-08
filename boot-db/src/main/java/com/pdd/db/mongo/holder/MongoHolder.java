package com.pdd.db.mongo.holder;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.pdd.db.mongo.bean.MongoBean;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义mongo句柄
 **/
@Component
public class MongoHolder {

    /**
     * 默认游标批量查询数量
     */
    private static final Integer DEFAULT_CURSOR_BATCH_SIZE=1000;
    
    /**
     * mongo句柄
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * mongo转换器
     */
    @Autowired
    private MongoConverter mongoConverter;
    
    /**
     * 获取类的表名
     * @param entityClass 
     * @return java.lang.String 
     */
    public <T extends MongoBean> String getCollectionName(Class<T> entityClass){
        return mongoTemplate.getCollectionName(entityClass);
    }
    
    /**
     * 插入数据
     * @param objectToSave 
     * @return T 
     */
    public <T extends MongoBean> T insert(T objectToSave){
        return mongoTemplate.insert(objectToSave);
    }

    /**
     * 批量插入数据
     * @param batchToSave 
     * @return java.util.Collection<T> 
     */
    public <T extends MongoBean> Collection<T> insertAll(Collection<? extends T> batchToSave){
        return mongoTemplate.insertAll(batchToSave);
    }
    
    /**
     * 条件删除
     * @param query
     * @param entityClass
     * @return
     */
    public <T extends MongoBean> DeleteResult remove(Query query, Class<T> entityClass){
        return mongoTemplate.remove(query,entityClass);
    }

    /**
     * 查询满足条件记录
     * @param query
     * @param entityClass
     * @return java.util.List<T>
     */
    public <T extends MongoBean> List<T> find(Query query, Class<T> entityClass){
        return mongoTemplate.find(query,entityClass);
    }
    
    /**
     * 查询第一条
     * @param query 
     * @param entityClass 
     * @return T 
     */
    public <T extends MongoBean> T findOne(Query query, Class<T> entityClass){
        return mongoTemplate.findOne(query,entityClass);
    }
    
    /**
     * 根据主键查询
     * @param id 
     * @param entityClass 
     * @return T 
     */
    public <T extends MongoBean> T findById(Object id, Class<T> entityClass){
        return mongoTemplate.findById(id,entityClass);
    }

    /**
     * 查询总数
     * @param query 
     * @param entityClass 
     * @return long 
     */
    public <T extends MongoBean> long count(Query query, Class<T> entityClass){
        return mongoTemplate.count(query,entityClass);
    }
    
    /**
     * 聚合操作
     * @param aggregation 
     * @param entityClass 
     * @param outputType 
     * @return org.springframework.data.mongodb.core.aggregation.AggregationResults<O> 
     */
    public <O extends MongoBean> AggregationResults<O> aggregate(Aggregation aggregation, Class<O> entityClass, Class<O> outputType){
        return mongoTemplate.aggregate(aggregation,entityClass,outputType);
    }
    
    //--------------------【扩展方法】--------------------

    /**
     * 主键查询
     * @param _id 
     * @param entityClass 
     * @return T 
     */
    public <T extends MongoBean> T extFindById(String _id, Class<T> entityClass){
        Query query=new Query(Criteria.where("_id").is(new ObjectId(_id)));
        return mongoTemplate.findOne(query,entityClass);
    }

    /**
     * 更新第一条
     * @param query 查询条件
     * @param mongoBean 要更新的实体
     * @param updateFields 
     * @return com.mongodb.client.result.UpdateResult 
     */
    public <T extends MongoBean> UpdateResult extUpdateFirst(Query query, T mongoBean,String... updateFields) throws Exception{
        Update update=getUpdateFromBean(mongoBean,updateFields);
        return mongoTemplate.updateFirst(query,update,mongoBean.getClass());
    }

    /**
     * 批量更新
     * @param query 查询条件
     * @param mongoBean 要更新的实体
     * @param updateFields 要更新的字段（有参数时，更新指定的字段；无此参数时，更新mongoBean所有不为空的字段）
     * @return com.mongodb.client.result.UpdateResult 
     */
    public <T extends MongoBean> UpdateResult extUpdateMulti(Query query, T mongoBean,String... updateFields) throws Exception{
        Update update=getUpdateFromBean(mongoBean,updateFields);
        return mongoTemplate.updateMulti(query,update,mongoBean.getClass());
    }
    
    /**
     * 根据主键更新数据（更新指定的字段/更新不为空的字段）
     * @param _id 主键
     * @param mongoBean 要更新的实体
     * @param updateFields 要更新的字段（有参数时，更新指定的字段；无此参数时，更新mongoBean所有不为空的字段）
     * @return com.mongodb.client.result.UpdateResult 
     */
    public <T extends MongoBean> UpdateResult extUpdateById(String _id,T mongoBean,String... updateFields) throws Exception{
        Query query=new Query(Criteria.where("_id").is(new ObjectId(_id)));
        Update update=getUpdateFromBean(mongoBean,updateFields);
        return mongoTemplate.updateFirst(query,update,mongoBean.getClass());
    }

    /**
     * 根据主键更新所有字段
     * @param _id 主键
     * @param mongoBean 要更新的实体
     * @return com.mongodb.client.result.UpdateResult
     */
    public <T extends MongoBean> UpdateResult extUpdateAllById(String _id,T mongoBean) throws Exception{
        if(StringUtils.isBlank(_id)){
            throw new Exception("更新主键不可为空");
        }
        Query query=new Query(Criteria.where("_id").is(new ObjectId(_id)));
        Update update=getAllUpdateFromBean(mongoBean);
        return mongoTemplate.updateFirst(query,update,mongoBean.getClass());
    }
    
    /**
     * 根据主键删除
     * @param _id 主键
     * @param entityClass mongo实体类
     * @return com.mongodb.client.result.DeleteResult 
     */
    public <T extends MongoBean> DeleteResult extRemoveById(String _id,Class<T> entityClass){
        Query query=new Query(Criteria.where("_id").is(new ObjectId(_id)));
        return this.remove(query,entityClass);
    }
        
    /**
     * 获取mongo游标（需要手动关闭）
     * @param query 查询对象
     * @param entityClass 查询实体
     * @param batchSize 批次大小（默认1000，需大于0）
     * @param pageNum 当前页数
     * @param pageSize 每页大小
     * @return com.mongodb.client.MongoCursor<org.bson.Document> 
     */
    <T extends MongoBean> MongoCursor<Document> extGetMongoCursor(Query query, Class<T> entityClass, Integer batchSize, Integer pageNum, Integer pageSize){
        if(query==null || entityClass==null){
            return null;
        }
        MongoCollection<Document> collection=mongoTemplate.getCollection(mongoTemplate.getCollectionName(entityClass));
        FindIterable<Document> findIterable=collection.find(query.getQueryObject());
        ////----------填充游标属性----------
        //（1）游标不超时
        findIterable.noCursorTimeout(true);
        //（2）批次拉取大小（默认1000）
        if(batchSize==null || batchSize<=0){
            batchSize=DEFAULT_CURSOR_BATCH_SIZE;
        }
        findIterable.batchSize(batchSize);
        //（3）排序
        findIterable.sort(query.getSortObject());
        //（4）跳过记录数
        if(pageNum!=null && pageSize!=null){
            findIterable.skip((pageNum - 1) * pageSize);
            findIterable.limit(pageSize);
        }
        
        return findIterable.cursor();
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param batchSize 批次大小
     * @param pageNum 当前页
     * @param pageSize 每次大小
     * @param executor 执行器
     * @return void 
     */
    public <T extends MongoBean> void extCursorQueryExe(Query query, Class<T> entityClass, Integer batchSize, Integer pageNum, Integer pageSize, Executor<T> executor) throws Exception{
        if(executor==null){
            return ;
        }
        try (MongoCursor<Document> cursor = this.extGetMongoCursor(query,entityClass,batchSize,pageNum,pageSize)) {
            if(cursor==null){
                return ;
            }
            T model;
            while (cursor.hasNext()) {
                model = mongoConverter.read(entityClass, cursor.next());
                executor.invoke(model);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param batchSize 批次大小
     * @param executor 执行器
     * @return void 
     */
    public <T extends MongoBean> void extCursorQueryExe(Query query, Class<T> entityClass, Integer batchSize, Executor<T> executor) throws Exception{
        this.extCursorQueryExe(query,entityClass,batchSize,null,null,executor);
    }

    /**
     * 执行游标查询
     * @param query 查询器
     * @param entityClass 查询实体
     * @param executor 执行器
     * @return void
     */
    public <T extends MongoBean> void extCursorQueryExe(Query query, Class<T> entityClass, Executor<T> executor) throws Exception{
        this.extCursorQueryExe(query,entityClass,null,null,null,executor);
    }

    /**
     * 获取实体类所有字段的Update（将更新整个实体类）
     * @param mongoBean mongo实体类
     * @return org.springframework.data.mongodb.core.query.Update 
     */
    protected Update getAllUpdateFromBean(MongoBean mongoBean){
        Document document = new Document();
        //转换实体为文档对象
        mongoConverter.write(mongoBean, document);
        //转换为更新对象
        return Update.fromDocument(document);
    }
    
    /**
     * 根据mongo的bean对象，生成update
     * @param mongoBean mongo实体类
     * @param updateFields 需要更新的字段
     * @return
     * @throws Exception
     */
    protected Update getUpdateFromBean(MongoBean mongoBean,String... updateFields) throws Exception{
        if(mongoBean==null){
            throw new Exception("要更新的对象为空");
        }
        Document document = new Document();
        //转换实体为文档对象
        mongoConverter.write(mongoBean, document);
        //组装要更新的字段
        Set<String> updateFieldSet=new HashSet<>();
        if(updateFields.length>0){
            //校验字段有消息
            for(String fieldTmp:updateFields){
                if (StringUtils.isBlank(fieldTmp)){
                    throw new Exception("更新的字段不可为空");
                }
            }
            CollectionUtils.addAll(updateFieldSet,updateFields);
        }
        //构建要更新的字段
        Update update=new Update();

        Object fieldValue;
        if(CollectionUtils.isNotEmpty(updateFieldSet)){
            //如果指定要更新的字段=>更新指定的字段
            for(String fieldName:updateFields){
                if(StringUtils.isNotEmpty(fieldName)){
                    if(document.containsKey(fieldName)){
                        //只更新要更新的字段
                        fieldValue=document.get(fieldName);
                        if(fieldValue!=null){
                            update.set(fieldName,fieldValue);
                        }else{
                            update.unset(fieldName);
                        }
                    }else{
                        //更新的字段，不存在默认unset
                        update.unset(fieldName);
                    }
                }
            }
        }else{
            //如果没有指定要更新的字段=>更新所有不为空的字段
            for(String fieldName:document.keySet()){
                fieldValue=document.get(fieldName);
                if(fieldValue!=null){
                    update.set(fieldName,fieldValue);
                }
            }
        }
        return update;
    }

}
