package dps.webapplication.crudmodel.converters;

import dps.webapplication.crudmodel.ConversionException;
import dps.webapplication.crudmodel.Converter;

public class DefaultConverter implements Converter {

    @Override
    public Class<?> convertedClass() {
        return Object.class;
    }

    @Override
    public String convert(Object value) {
        if (value == null) return "";
        return value.toString();
    }

    @Override
    public <T> T parse(String representation, Class<T> resultClass) throws ConversionException {
        if (resultClass == String.class) {
            return (T)representation;
        }
        if (resultClass == Integer.class || resultClass == int.class) {
            return (T)Integer.valueOf(representation);
        }
        if (resultClass == Long.class || resultClass == long.class) {
            return (T)Long.valueOf(representation);
        }
        if (resultClass == Boolean.class || resultClass == boolean.class) {
            return (T)Boolean.valueOf(representation);
        }
        throw new ConversionException();
    }
}
