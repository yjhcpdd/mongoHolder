package com.pdd.db.mongo.converter;

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
