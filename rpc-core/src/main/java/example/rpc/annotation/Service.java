package example.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个服务提供类，用于远程接口的实现类
 * @Author admin
 * @Date 2022/8/13 22:14
 * @Version 1.0
 */
/**
 * ElementType.TYPE：能修饰类、接口或枚举类型
 * ElementType.FIELD：能修饰成员变量
 * ElementType.METHOD：能修饰方法
 * ElementType.PARAMETER：能修饰参数
 * ElementType.CONSTRUCTOR：能修饰构造器
 * ElementType.LOCAL_VARIABLE：能修饰局部变量
 * ElementType.ANNOTATION_TYPE：能修饰注解
 * ElementType.PACKAGE：能修饰包
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    public String name() default "";

}
