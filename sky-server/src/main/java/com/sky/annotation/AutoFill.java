package com.sky.annotation;

import com.sky.enumeration.OperationType;
import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充注解
 */
@Target({ElementType.METHOD})  //注解作用于方法
@Retention(RetentionPolicy.RUNTIME)  //注解在运行时有效
public @interface AutoFill {
    // 数据库操作类型：update、insert
    OperationType value();
}
