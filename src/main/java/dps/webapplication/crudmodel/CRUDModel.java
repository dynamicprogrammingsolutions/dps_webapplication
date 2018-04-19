package dps.webapplication.crudmodel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface CRUDModel {

    void createModel(Class<?> entityClass, CRUDEntity crudEntityAnnotation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
    <T extends FieldHandler> Map<String, T> getModel(Class<T> handlerInterface);
    Map<String, List<FieldHandler>> getModel();
    <T extends FieldHandler> List<T> getSortedModel(Class<T> handlerInterface);
    <T extends FieldHandler> boolean checkModel(Class<T> handlerInterface);
    Object createNewObject() throws IllegalAccessException, InvocationTargetException, InstantiationException;

    Class<?> getEntityClass();
    CRUDEntity getCrudEntityAnnotation();
    Map<Class<?>, Converter> getConverters();
    List<FieldHandler> getHandlers();

}
