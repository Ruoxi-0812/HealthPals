package cn.kmbeast.aop;

import cn.kmbeast.pojo.em.RoleEnum;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Protector {
    RoleEnum[] roles() default {};
}
