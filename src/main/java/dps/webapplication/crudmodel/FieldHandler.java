package dps.webapplication.crudmodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface FieldHandler {
    void createHandler(CRUDModel model);
    boolean canHandle(String name, Method getterMethod, Method setterMethod, Field property);
    FieldHandler createHandler(String name, Method getterMethod, Method setterMethod, Field property);
    int compare(FieldHandler o1, FieldHandler o2);
}
