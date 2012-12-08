package org.kari.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Assert method return values
 * 
 * @author kari
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface KAssert {
    AssertType value();
}
