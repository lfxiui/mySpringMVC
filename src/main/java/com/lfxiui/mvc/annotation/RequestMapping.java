package com.lfxiui.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author Fuxi
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String path();
}
