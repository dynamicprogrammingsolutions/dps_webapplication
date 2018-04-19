package dps.webapplication.crudmodel.handlers;

import dps.webapplication.crudmodel.CRUDModel;
import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.Converter;
import dps.webapplication.crudmodel.FieldHandler;
import dps.webapplication.crudmodel.converters.DefaultConverter;
import dps.webapplication.persistence.annotation.TableField;

import javax.json.stream.JsonGenerator;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityListWriterHandler implements FieldHandler {

    private CRUDModel model;
    private ListFields listFieldsAnnotation;

    private String name;
    private Method method;
    private Converter converter;
    private TableField tableFieldAnnotation;

    @Override
    public void createHandler(CRUDModel model) {
        this.model = model;
        listFieldsAnnotation = model.getEntityClass().getAnnotation(ListFields.class);
    }

    @Override
    public boolean canHandle(String name, Method getterMethod, Method setterMethod, Field property) {
        if (listFieldsAnnotation == null) return false;
        if (getterMethod == null) return false;
        Class<? extends Annotation>[] annotations = listFieldsAnnotation.value();
        for (Class<? extends Annotation> annotation: annotations) {
            if (property != null && property.isAnnotationPresent(annotation)) return true;
            if (getterMethod != null && getterMethod.isAnnotationPresent(annotation)) return true;
            if (setterMethod != null && setterMethod.isAnnotationPresent(annotation)) return true;
        }
        return false;
    }

    @Override
    public FieldHandler createHandler(String name, Method getterMethod, Method setterMethod, Field property) {

        EntityListWriterHandler handler = new EntityListWriterHandler();
        handler.model = model;
        handler.listFieldsAnnotation = listFieldsAnnotation;
        handler.name = name;
        handler.method = getterMethod;
        handler.converter = null;
        handler.tableFieldAnnotation = property.getAnnotation(TableField.class);

        for (Converter converter: model.getConverters().values()) {
            if (converter.convertedClass().isAssignableFrom(handler.method.getReturnType())) {
                handler.converter = converter;
            }
        }
        if (handler.converter == null) handler.converter = new DefaultConverter();

        return handler;
    }

    public void handle(Object o, JsonGenerator jsonObjectBuilder) throws InvocationTargetException, IllegalAccessException, ConversionException {
        Object value = this.method.invoke(o);
        jsonObjectBuilder.write(this.name,converter.convert(value));
    }

    public boolean canWriteHead() {
        return tableFieldAnnotation != null;
    }

    public boolean canWriteCell() {
        return tableFieldAnnotation != null;
    }

    public void writeHead(JspWriter out) throws IOException {
        if (tableFieldAnnotation == null) return;
        out.print("<td>"+tableFieldAnnotation.name()+"</td>");
    }

    public void writeCell(JspWriter out) throws IOException {
        if (tableFieldAnnotation == null) return;
        out.print("<td>${"+name+"}</td>");
    }

    @Override
    public int compare(FieldHandler o1, FieldHandler o2) {
        return 0;
    }
}
