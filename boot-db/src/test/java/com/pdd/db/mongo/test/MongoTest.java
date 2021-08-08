package com.pdd.db.mongo.test;

import com.pdd.db.DbApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DbApplication.class)
public class MongoTest {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Test
    public void myTest(){
        System.out.println(mongoTemplate.getCollectionNames());
    }
    
    
}
