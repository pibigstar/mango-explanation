package mango.core.extension;

import java.lang.annotation.*;

/**
 * SPI 实例别称
 * @author Ricky Fung
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Alias {
    
    String value() default "";
}
