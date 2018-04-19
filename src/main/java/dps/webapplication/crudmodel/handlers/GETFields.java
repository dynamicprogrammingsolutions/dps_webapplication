package dps.webapplication.crudmodel.handlers;

import dps.webapplication.crudmodel.Converter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GETFields {
    Class<? extends Annotation>[] value();
    Class<? extends Converter>[] converters() default {};
}
