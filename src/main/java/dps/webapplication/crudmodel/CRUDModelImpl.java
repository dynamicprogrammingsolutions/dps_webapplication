package dps.webapplication.crudmodel;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CRUDModelImpl implements CRUDModel {

    private Class<?> entityClass;
    private CRUDEntity crudEntityAnnotation;
    private Map<Class<?>,Converter> converters = new LinkedHashMap<>();
    private List<FieldHandler> handlers = new ArrayList<>();
    private Map<String,List<FieldHandler>> model = new LinkedHashMap<>();
    private Constructor constructor = null;

    @Override
    public Object createNewObject() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance();
    }

    @Override
    public void createModel(Class<?> entityClass, CRUDEntity crudEntityAnnotation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        this.entityClass = entityClass;
        this.crudEntityAnnotation = crudEntityAnnotation;

        constructor = entityClass.getConstructor();

        Class<? extends Converter>[] converterClasses = crudEntityAnnotation.converters();

        for (Class<? extends Converter> converterClass: converterClasses) {
            try {
                Constructor<? extends Converter> constructor = converterClass.getConstructor();
                Converter converter = constructor.newInstance();
                converters.put(converter.convertedClass(),converter);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new WebApplicationException(e);
            }
        }

        for (Class<? extends FieldHandler> handlerClass: crudEntityAnnotation.handlers()) {
            Constructor<? extends FieldHandler> handlerConstructor = handlerClass.getConstructor();
            FieldHandler fieldHandler = handlerConstructor.newInstance();
            fieldHandler.createHandler(this);
            handlers.add(fieldHandler);
        }

        Set<String> fieldsProcessed = new HashSet<>();

        Field[] fields = entityClass.getDeclaredFields();
        for (Field field: fields) {
            String name = field.getName();
            fieldsProcessed.add(name);

            Class<?> type = field.getType();
            String setterName = "set"+name.substring(0,1).toUpperCase()+name.substring(1);
            String getterName = "get"+name.substring(0,1).toUpperCase()+name.substring(1);
            String booleanGetterName = null;
            if (type == Boolean.class || type == boolean.class) {
                booleanGetterName = "is"+name.substring(0,1).toUpperCase()+name.substring(1);
            }
            Method getterMethod = null;
            try {
                getterMethod = entityClass.getDeclaredMethod(getterName);
                if (getterMethod.getReturnType() != type) getterMethod = null;
            } catch (NoSuchMethodException e) {
                if (type == Boolean.class || type == boolean.class) {
                    try {
                        getterMethod = entityClass.getDeclaredMethod(booleanGetterName);
                        if (getterMethod.getReturnType() != type) getterMethod = null;
                    } catch (NoSuchMethodException e1) {

                    }
                }
            }
            Method setterMethod = null;
            try {
                setterMethod = entityClass.getDeclaredMethod(setterName,type);
                if (setterMethod.getReturnType() != void.class) setterMethod = null;
            } catch (NoSuchMethodException e) {
            }

            List<FieldHandler> fieldHandlers = null;
            for (FieldHandler handler: handlers) {
                if (handler.canHandle(name,getterMethod,setterMethod,field)) {
                    if (fieldHandlers == null) fieldHandlers = new ArrayList<>();
                    fieldHandlers.add(handler.createHandler(name,getterMethod,setterMethod,field));
                }
            }
            if (fieldHandlers != null) {
                model.put(name,fieldHandlers);
            }
        }

        Method[] methods = entityClass.getDeclaredMethods();
        for (Method method: methods) {
            String methodName = method.getName();
            String name;
            String getterName;
            String setterName;
            Class<?> type;
            String booleanGetterName = null;
            if (methodName.startsWith("get")) {
                name = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
                getterName = methodName;
                setterName = "set"+name.substring(0,1).toUpperCase()+name.substring(1);
                type = method.getReturnType();
            } else if (methodName.startsWith("set")) {
                name = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
                setterName = methodName;
                getterName = "get"+name.substring(0,1).toUpperCase()+name.substring(1);
                if (method.getParameterCount() != 1) continue;
                type = method.getParameterTypes()[0];
            } else {
                continue;
            }

            if (fieldsProcessed.contains(name)) continue;

            if (type == Boolean.class || type == boolean.class) {
                booleanGetterName = "is"+name.substring(0,1).toUpperCase()+name.substring(1);
            }

            // COPY

            Method getterMethod = null;
            try {
                getterMethod = entityClass.getDeclaredMethod(getterName);
                if (getterMethod.getReturnType() != type) getterMethod = null;
            } catch (NoSuchMethodException e) {
                if (type == Boolean.class || type == boolean.class) {
                    try {
                        getterMethod = entityClass.getDeclaredMethod(booleanGetterName);
                        if (getterMethod.getReturnType() != type) getterMethod = null;
                    } catch (NoSuchMethodException e1) {

                    }
                }
            }
            Method setterMethod = null;
            try {
                setterMethod = entityClass.getDeclaredMethod(setterName,type);
                if (setterMethod.getReturnType() != void.class) setterMethod = null;
            } catch (NoSuchMethodException e) {
            }

            List<FieldHandler> fieldHandlers = null;
            for (FieldHandler handler: handlers) {
                if (handler.canHandle(name,getterMethod,setterMethod,null)) {
                    if (fieldHandlers == null) fieldHandlers = new ArrayList<>();
                    fieldHandlers.add(handler.createHandler(name,getterMethod,setterMethod,null));
                }
            }
            if (fieldHandlers != null) {
                model.put(name,fieldHandlers);
            }

            // END
        }

    }

    public <T extends FieldHandler> boolean checkModel(Class<T> handlerInterface)
    {
        for (FieldHandler handler: handlers) {
            if (handlerInterface.isInstance(handler)) return true;
        }
        return false;
    }

    @Override
    public <T extends FieldHandler> Map<String, T> getModel(Class<T> handlerInterface) {
        LinkedHashMap<String,T> handlers = new LinkedHashMap<>();
        for (Map.Entry<String, List<FieldHandler>> entry: model.entrySet()) {
            List<FieldHandler> handlerList = entry.getValue();
            for (FieldHandler handler: handlerList) {
                if (handlerInterface.isInstance(handler)) {
                    handlers.put(entry.getKey(),(T)handler);
                }
            }
        }
        return handlers;
    }

    @Override
    public <T extends FieldHandler> List<T> getSortedModel(Class<T> handlerInterface) {
        LinkedList<T> handlers = new LinkedList<>();
        for (Map.Entry<String, List<FieldHandler>> entry: model.entrySet()) {
            List<FieldHandler> handlerList = entry.getValue();
            for (FieldHandler handler: handlerList) {
                if (handlerInterface.isInstance(handler)) {
                    handlers.add((T)handler);
                }
            }
        }
        for (FieldHandler handler: this.handlers) {
            if (handlerInterface.isInstance(handler)) {
                Collections.sort(handlers, new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return handler.compare(o1,o2);
                    }
                });
                break;
            }
        }
        return handlers;
    }

    @Override
    public Map<String, List<FieldHandler>> getModel() {
        return model;
    }

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public CRUDEntity getCrudEntityAnnotation() {
        return crudEntityAnnotation;
    }

    @Override
    public Map<Class<?>, Converter> getConverters() {
        return converters;
    }

    @Override
    public List<FieldHandler> getHandlers() {
        return handlers;
    }
}
