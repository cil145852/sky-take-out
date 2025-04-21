package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-16:02
 * @Description 自定义注解，添加该注解的insert和update方法可以对对象
 * 自动注入createTime、updateTime、createUser、updateUser属性
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoFill {
    /**
     * 指明方法的数据库操作类型用于注入不同的属性
     * @return
     */
    OperationType value();
}
