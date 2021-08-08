package com.pdd.db.mongo;

import com.mongodb.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * mongo配置
 **/
@Setter
@Configuration
@ConfigurationProperties(prefix = "mongodb.dbconfig")
public class MongoConfiguration {

    /**
     * 数据库
     */
    private String database;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接地址（IP:端口）
     */
    private ArrayList<String> addresses;

    /**
     * 连接配置
     */
    @Autowired
    private MongoOptionProperties mongoOptionProperties;
    
    /**
     * mongo工厂
     * @return org.springframework.data.mongodb.MongoDbFactory 
     */
    @Bean
    public MongoDbFactory mongoDbFactory(){
        //==========客户端配置==========
        MongoClientOptions.Builder builder=new MongoClientOptions.Builder();
        builder.connectionsPerHost(mongoOptionProperties.getMaxConnectionsPerHost());
        builder.minConnectionsPerHost(mongoOptionProperties.getMinConnectionsPerHost());
        builder.threadsAllowedToBlockForConnectionMultiplier(mongoOptionProperties.getThreadsAllowedToBlockForConnectionMultiplier());
        builder.serverSelectionTimeout(mongoOptionProperties.getServerSelectionTimeout());
        builder.maxWaitTime(mongoOptionProperties.getMaxWaitTime());
        builder.maxConnectionIdleTime(mongoOptionProperties.getMaxConnectionIdleTime());
        builder.maxConnectionLifeTime(mongoOptionProperties.getMaxConnectionLifeTime());
        builder.connectTimeout(mongoOptionProperties.getConnectTimeout());
        builder.socketTimeout(mongoOptionProperties.getSocketTimeout());
        builder.socketKeepAlive(mongoOptionProperties.getSocketKeepAlive());
        builder.sslEnabled(mongoOptionProperties.getSslEnabled());
        builder.sslInvalidHostNameAllowed(mongoOptionProperties.getSslInvalidHostNameAllowed());
        builder.alwaysUseMBeans(mongoOptionProperties.getAlwaysUseMBeans());
        builder.heartbeatFrequency(mongoOptionProperties.getHeartbeatFrequency());
        builder.minHeartbeatFrequency(mongoOptionProperties.getMinHeartbeatFrequency());
        builder.heartbeatConnectTimeout(mongoOptionProperties.getHeartbeatConnectTimeout());
        builder.heartbeatSocketTimeout(mongoOptionProperties.getHeartbeatSocketTimeout());
        builder.localThreshold(mongoOptionProperties.getLocalThreshold());
        MongoClientOptions mongoClientOptions=builder.build();
        
        //==========地址配置==========
        List<ServerAddress> serverAddressArrayList = new ArrayList<>();
        for (String address : addresses) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddressArrayList.add(serverAddress);
        }
        
        //连接认证
        MongoCredential mongoCredential=MongoCredential.createScramSha1Credential(username, database, password.toCharArray());
        //创建认证客户端
        MongoClient mongoClient = new MongoClient(serverAddressArrayList, mongoCredential, mongoClientOptions);
        // 创建MongoDbFactory
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, database);
        return mongoDbFactory;
    }
    
}
