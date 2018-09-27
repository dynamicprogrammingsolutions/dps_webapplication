package dps.commons.reflect;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ferenci84
 */
public class ReflectHelper {
    public static Constructor findConstructor(Class<?> clazz, Object... args) throws NoSuchMethodException
    {
        for(Constructor constructor: clazz.getConstructors()) {
            Boolean compatible = true;
            Parameter[] parameters = constructor.getParameters();
            if (args.length == parameters.length) {
                for (int i = 0; i != parameters.length; i++) {
                    if (!parameters[i].getType().isInstance(args[i])) compatible = false;
                }
            } else {
                compatible = false;
            }
            if (compatible) return constructor;
        }
        throw new NoSuchMethodException();
    }
    public static Object newInstance(Class<?> clazz, Object... args)
    {
        Constructor constructor;
        try {
            constructor = findConstructor(clazz, args);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchConstructorError();
        }
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new NoSuchConstructorError("Couldn't invoke constructor",ex);
        }
    }
    public static Class<?> getTypeParameter(Class<?> clazz)
    {
        return getTypeParameter(clazz,0);
    }
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTypeParameter(Class<?> clazz, int idx)
    {
        Type superClass = clazz.getGenericSuperclass();
        while (!(superClass instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
            if (clazz == null) throw new IllegalArgumentException("Cannot get ParametrizedType");
            superClass = clazz.getGenericSuperclass();
        }
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type typeArgument = parameterizedType.getActualTypeArguments()[idx];
            return (Class<T>) typeArgument;
    }

    public static Type[] getTypeParameters(Class<?> clazz)
    {
        Type superClass = clazz.getGenericSuperclass();
        while (!(superClass instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
            if (clazz == null) throw new IllegalArgumentException("Cannot get ParametrizedType");
            superClass = clazz.getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) superClass;
        return parameterizedType.getActualTypeArguments();
    }

    public static Object getEntityId(EntityManager em, Object entity)
    {
        Metamodel metamodel = em.getMetamodel();
        EntityType entityType = metamodel.entity(entity.getClass());
        Member javaMember = entityType.getId(entityType.getIdType().getJavaType()).getJavaMember();
        if (javaMember instanceof Field) {
            Field field = (Field)javaMember;
            field.setAccessible(true);
            try {
                Object id = field.get(entity);
                return id;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (javaMember instanceof Method) {
            Method method = (Method)javaMember;
            method.setAccessible(true);
            try {
                Object id = method.invoke(entity);
                return id;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Class<?> getEntityIdType(EntityManager em, Class<?> entityClass)
    {
        Metamodel metamodel = em.getMetamodel();
        EntityType entityType = metamodel.entity(entityClass);
        javax.persistence.metamodel.Type<?> idType = entityType.getIdType();
        return idType.getJavaType();
    }
    public static String getGetterName(String field)
    {
        return "get".concat(field.substring(0, 1).toUpperCase()).concat(field.substring(1));
    }
    public static String getSetterName(String field)
    {
        return "set".concat(field.substring(0, 1).toUpperCase()).concat(field.substring(1));
    }
    public static Method findMethod(Class<?> clazz, String name, Object... args) throws NoSuchMethodException
    {
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if (method.getName().equals(name)) {
                Boolean compatible = true;
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i != parameters.length; i++) {
                    if (!parameters[i].getType().isInstance(args[i])) compatible = false;
                }
                if (compatible) return method;
            }
        }
        throw new NoSuchMethodException();
    }
    public static Method findMethodWithName(Class<?> clazz, String name, int noOfParams) throws NoSuchMethodException
    {
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if (method.getName().equals(name) && method.getParameters().length == noOfParams) {
                return method;
            }
        }
        throw new NoSuchMethodException();
    }

    public static Method[] findMethodsWithName(Class<?> clazz, String name, int noOfParams) throws NoSuchMethodException
    {
        Set<Method> foundMethods = new HashSet<>();
        Method[] methods = clazz.getMethods();
        for (Method method: methods) {
            if (method.getName().equals(name) && method.getParameters().length == noOfParams) {
                foundMethods.add(method);
                //return method;
            }
        }
        if (foundMethods.isEmpty()) throw new NoSuchMethodException();
        else return foundMethods.toArray(new Method[foundMethods.size()]);
    }

    public static Object invokeMethod(Class<?> clazz, String name, Object obj, Object... args)
    {
        Method method;
        try {
            method = findMethod(clazz, name, args);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError();
        }
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new NoSuchMethodError();
        }
    }
    public static Object invokeMethod(Method method, Object obj, Object... args)
    {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new NoSuchMethodError();
        }
    }



    public static Class<?> getProxiedClass(Class<?> clazz)
    {
        while (clazz.getCanonicalName().contains("$Proxy")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass == null) break;
            else {
                clazz = superclass;
            }
        }
        return clazz;
    }

    public static List<Class<?>> getClassChain(Class<?> clazz)
    {
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(clazz);
        Class<?> superclass = null;
        while((superclass = clazz.getSuperclass()) != null) {
            clazz = superclass;
            list.add(clazz);
        }
        return list;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationClass)
    {
        Class<?> base = getProxiedClass(clazz);
        for (Class<?> cl: getClassChain(base)) {
            A anno = cl.getAnnotation(annotationClass);
            if (anno != null) return anno;
        }
        return null;
    }
    public static Boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        return clazz.getAnnotation(annotationClass) != null;
    }
}
