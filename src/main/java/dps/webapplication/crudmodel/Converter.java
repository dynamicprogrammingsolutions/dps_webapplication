package dps.webapplication.crudmodel;

import javax.json.JsonArray;

public interface Converter {
    Class<?> convertedClass();
    String convert(Object value) throws ConversionException;
    <T> T parse(String representation, Class<T> resultClass) throws ConversionException;
}
