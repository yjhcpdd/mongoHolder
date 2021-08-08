## springboot中mongodb自定义类型转换器

### 1 场景

#### 1.1 BigDecimal写入mongo

如在springboot中集成mongoDb，将使用`mongoTemplate`操作mongo数据库。

java中使用mongoTemplate面临一个问题， 向数据库写入数据时，如果`java`中映射的属性类型为`BigDecimal`，该属性映射到`mongo`中对应的的类型为`String`，显然这是不符合我们期待的。

**会导致如下问题：**

（1）查询排序将按照`字符串排序`，而不是按照数值排序

（2）大于、大于等于类似的`数值查询`方式，会受影响，仍然按照字符串进行匹配

原因是mongo`不支持`BigDecimal类似的科学计数法的类型，java客户端默认将BigDecimal类型转换成了String。

#### 1.2 人工转换

当前有的研发，为了解决此问题，在java程序中人工手动做了转换，向mongo存储数据时，在程序中将`每个`数值类型由BigDecimal均转换为java中的Double类型。

**此种情况，面临如下问题：**

每个向数据库写插入或者更新数据时，`均需要人工做数据类型的转换`。如果漏掉了某次转换，mongo同一个表中的同一个字段出现不同类型（mongoDb允许此类存储方式），类型出现字符串和Double两种类型的情况，后期会导致很多不可预料的问题。

#### 1.3 自定义转换器

mongo的数据类型`支持浮点型的Double`，mongo的java客户端可以`自定义类型转换器`。于是我们在spring整合mongo时，自定义mongo类型转换器。

**达到如下效果：**

（1）java程序向mongo`写入`数据时，BigDecimal`自动`转换成Double类型。

（2）java程序从mongo中`读取`数据时，Double类型`自动`转换成BigDecimal类型。

### 2 版本

**springBoot：**2.2.9.RELEASE

**mongodb：**4.2

### 3 步骤

#### 3.1 定义转换器

**BigDecimal转Double：**

```java
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * mongo数字转换器(BigDecimal转Double)
 **/
@Component
@WritingConverter
public class BigDecimalToDoubleConverter implements Converter<BigDecimal, Double> {
    @Override
    public Double convert(BigDecimal source) {
        return source.doubleValue();
    }
}
```

**Double转BigDecimal：**

```java
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * mongo数字转换器(Double转BigDecimal)
 **/
@Component
@ReadingConverter
public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {
    @Override
    public BigDecimal convert(Double source) {
        return new BigDecimal(String.valueOf(source));
    }
}
```

#### 3.2 配置mongoDb工厂类

```java
@Bean
public MongoDbFactory mongoDbFactory(){
	......
}
```

mongoDb工厂配置，详细见文章：

[springboot集成mongodb](https://www.jianshu.com/p/be56be4624d7)

#### 3.3 加载自定义转换器

```java
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
```

#### 3.4 加载映射转换器

```java
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
```

#### 3.5 配置mongo句柄

```java
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
```