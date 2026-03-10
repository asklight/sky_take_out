package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，完成公共字段自动填充功能
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
      1、切面类需要使用@Aspect注解进行标注
      2、切面类需要交给Spring容器管理，使用@Component注解进行标注
      3、切面类中需要定义切点和通知，在通知中完成公共字段的自动填充
      4、切点表达式需要根据实际情况进行编写，通常是针对Mapper接口中的方法进行切入
      5、在通知中可以通过反射获取到当前操作的实体对象，然后对公共字段进行赋值
      6、公共字段的值可以从BaseContext中获取到当前用户的id，以及当前时间等信息
     */

    //切入点表达式，针对Mapper接口中的方法进行切入
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在执行Mapper接口中的方法之前执行，在通知中完成公共字段的自动填充
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("公共字段自动填充...");

        //获取到当前被拦截方法上的AutoFill注解对象，通过注解对象获取到当前操作的类型（新增或修改）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); //获取到方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //获取到方法上的AutoFill注解对象
        OperationType operationType = autoFill.value(); //获取到当前操作的类型（新增或修改）

        //通过反射获取到当前操作的实体对象，然后对公共字段进行赋值
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity =  args[0];

        //准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据操作类型进行不同的赋值
        if(operationType == OperationType.INSERT){
            //为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE){
            //为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //公共字段的值可以从BaseContext中获取到当前用户的id，以及当前时间等信息
    }
}

//TODO: 学习AOP相关知识，完成公共字段自动填充功能