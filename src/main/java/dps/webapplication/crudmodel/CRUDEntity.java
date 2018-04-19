package dps.webapplication.crudmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CRUDEntity {
    Class<? extends FieldHandler>[] handlers() default {};
    Class<? extends Converter>[] converters() default {};
    Class<? extends CRUDModel> model() default CRUDModelImpl.class;
}
