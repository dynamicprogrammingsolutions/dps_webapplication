package dps.webapplication.crudmodel.handlers;

import dps.webapplication.crudmodel.CRUDModel;
import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.Converter;
import dps.webapplication.crudmodel.FieldHandler;
import dps.webapplication.crudmodel.converters.DefaultConverter;
import dps.webapplication.persistence.annotation.FormField;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Lob;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class EntityWriterHandler implements FieldHandler {

    private CRUDModel model;
    private GETFields getFieldsAnnotation;
    private boolean lobAnnotationPresent;

    private String name;
    private Method getterMethod;
    private Method setterMethod;
    private Converter getterConverter;
    private Converter setterConverter;
    private FormField formFieldAnnotation;

    @Override
    public void createHandler(CRUDModel model) {
        this.model = model;
        getFieldsAnnotation = model.getEntityClass().getAnnotation(GETFields.class);
    }

    @Override
    public boolean canHandle(String name, Method getterMethod, Method setterMethod, Field property) {
        //System.out.println("check field: "+name+" getter: "+getterMethod+" setter: "+setterMethod);
        //System.out.println("check field: "+name);
        if (getFieldsAnnotation == null) return false;
        Class<? extends Annotation>[] annotations = getFieldsAnnotation.value();
        for (Class<? extends Annotation> annotation: annotations) {
            //System.out.println("check annotation: "+annotation);
            if (property != null && property.isAnnotationPresent(annotation)) return true;
            if (getterMethod != null && getterMethod.isAnnotationPresent(annotation)) return true;
            if (setterMethod != null && setterMethod.isAnnotationPresent(annotation)) return true;
        }
        return false;
    }

    @Override
    public FieldHandler createHandler(String name, Method getterMethod, Method setterMethod, Field property) {
        //System.out.println("create handler: "+name);
        EntityWriterHandler handler = new EntityWriterHandler();
        handler.model = model;
        handler.getFieldsAnnotation = getFieldsAnnotation;
        if (property != null) {
            handler.formFieldAnnotation = property.getAnnotation(FormField.class);
            handler.lobAnnotationPresent = property.isAnnotationPresent(Lob.class);
        }
        handler.name = name;
        handler.getterMethod = getterMethod;
        handler.setterMethod = setterMethod;

        handler.getterConverter = null;
        handler.setterConverter = null;

        for (Converter converter: model.getConverters().values()) {

            if (handler.getterMethod != null && handler.getterConverter == null && converter.convertedClass().isAssignableFrom(getterMethod.getReturnType())) {
                handler.getterConverter = converter;
            }

            if (handler.setterMethod != null && handler.setterConverter == null && setterMethod.getParameterTypes()[0].isAssignableFrom(converter.convertedClass())) {
                handler.setterConverter = converter;
            }

        }
        if (handler.getterConverter == null) handler.getterConverter = new DefaultConverter();
        if (handler.setterConverter == null) handler.setterConverter = new DefaultConverter();

        return handler;
    }

    public void handle(Object o, JsonObjectBuilder jsonObjectBuilder) throws InvocationTargetException, IllegalAccessException, ConversionException {
        //System.out.println("handle: "+this.name+" getter: "+this.getterMethod);
        if (this.getterMethod == null) return;
        //System.out.println("getter return type: "+this.getterMethod.getReturnType()+" converter: "+getterConverter);
        Object value = this.getterMethod.invoke(o);
        jsonObjectBuilder.add(this.name,getterConverter.convert(value));
    }

    public void handle(Object o, JsonObject jsonObject) throws ConversionException, InvocationTargetException, IllegalAccessException {
        if (this.setterMethod == null) return;
        try {
            String strValue = jsonObject.getString(name);
            setterMethod.invoke(o, setterConverter.parse(strValue, setterMethod.getParameterTypes()[0]));
        } catch (NullPointerException e) {
            /*try {
                JsonArray jsonArray = jsonObject.getJsonArray(name);

            } catch (NullPointerException e) {

            }*/
        }
    }
    public void handle(JspWriter out) throws IOException {
        if (formFieldAnnotation == null) return;
        Class<?> type = this.getterMethod.getReturnType();
        out.print("<label>"+this.formFieldAnnotation.label()+"</label>");
        if (type.equals(String.class)) {
            if (this.lobAnnotationPresent) {
                out.print("<textarea name=\""+this.name+"\"></textarea>");
            } else {
                out.print("<input type=\"text\" name=\"" + this.name + "\" />");
            }
        }
        if (type.equals(Date.class)) {
            out.print("<input type=\"text\" name=\""+this.name+"\" />");
        }
    }

    @Override
    public int compare(FieldHandler o1, FieldHandler o2) {
        if (o1 == null || o2 == null) return 0;
        if (o1 instanceof EntityWriterHandler && o2 instanceof EntityWriterHandler) {
            EntityWriterHandler handler1 = (EntityWriterHandler) o1;
            EntityWriterHandler handler2 = (EntityWriterHandler) o2;
            int order1 = 0;
            int order2 = 0;
            if (handler1.formFieldAnnotation != null) order1 = handler1.formFieldAnnotation.order();
            if (handler2.formFieldAnnotation != null) order2 = handler2.formFieldAnnotation.order();
            if (order1 == order2) return 0;
            if (order1 > order2) return 1;
            return -1;
        }
        return 0;
    }
}
