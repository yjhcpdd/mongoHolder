package com.pdd.db.mongo.converter;

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
