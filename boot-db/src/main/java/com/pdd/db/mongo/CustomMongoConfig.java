package com.pdd.db.mongo;

import com.mongodb.WriteConcern;
import com.pdd.db.mongo.converter.BigDecimalToDoubleConverter;
import com.pdd.db.mongo.converter.DoubleToBigDecimalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义mongo配置
 **/
@Configuration
public class CustomMongoConfig {

    /**
     * 自定义转换器
     * @return org.springframework.data.mongodb.core.convert.CustomConversions
     */
    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new BigDecimalToDoubleConverter());
        converterList.add(new DoubleToBigDecimalConverter());
        return new CustomConversions(converterList);
    }
    
    /**
     * mongo映射转换器
     * @param mongoDbFactory mongo工厂
     * @param mappingContext 映射命名空间
     * @param customConversions 自定义转换器
     * @return org.springframework.data.mongodb.core.convert.MappingMongoConverter 
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory, MongoMappingContext mappingContext, CustomConversions customConversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        //添加自定义的转换器
        mappingConverter.setCustomConversions(customConversions);
        //去掉默认mapper添加的_class
        //mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }

    /**
     * 默认mongo句柄（写关注为SAFE）
     * @param mongoDbFactory mongo工厂
     * @param mappingMongoConverter 映射命名空间
     * @return org.springframework.data.mongodb.core.MongoTemplate 
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MappingMongoConverter mappingMongoConverter) {
        MongoTemplate mongoTemplate=new MongoTemplate(mongoDbFactory, mappingMongoConverter);
        mongoTemplate.setWriteConcern(WriteConcern.SAFE);
        return mongoTemplate;
    }
    
}
