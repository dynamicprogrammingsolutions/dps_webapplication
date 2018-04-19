package dps.webapplication.crudmodel.handlers;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ListFields {
    Class<? extends Annotation>[] value();
}
