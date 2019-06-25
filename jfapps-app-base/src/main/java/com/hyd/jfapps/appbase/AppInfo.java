package com.hyd.jfapps.appbase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AppInfo {

    /**
     * App 名称
     */
    String name();

    /**
     * 作者名字
     */
    String author() default "";

    /**
     * App 页面或作者个人主页
     */
    String url() default "";

    /**
     * App 所属分类
     */
    AppCategory category() default AppCategory.OTHER;
}
