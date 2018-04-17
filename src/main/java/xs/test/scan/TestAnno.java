package xs.test.scan;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by xs on 2018/3/13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TestAnno {
}
