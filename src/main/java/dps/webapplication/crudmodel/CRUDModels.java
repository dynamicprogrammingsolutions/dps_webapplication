package dps.webapplication.crudmodel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CRUDModels {

    private CRUDModels() {

    }
    static private CRUDModels INSTANCE;

    static public CRUDModels getInstance() {
        if (INSTANCE == null) INSTANCE = new CRUDModels();
        return INSTANCE;
    }

    Map<Class<?>,CRUDModel> models = new HashMap<>();

    public CRUDModel getModel(Class<?> entityClass) throws CRUDModelException {
        CRUDModel crudModel = models.get(entityClass);
        if (crudModel == null) {
            try {
                crudModel = this.createModel(entityClass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new CRUDModelException();
            }
            if (crudModel == null) return null;
            models.put(entityClass,crudModel);
        }
        return crudModel;
    }

    public boolean checkModel(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(CRUDEntity.class)) {
            return true;
        }
        return false;
    }

    private CRUDModel createModel(Class<?> entityClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        CRUDEntity crudEntityAnnotation = entityClass.getAnnotation(CRUDEntity.class);
        Class<? extends CRUDModel> modelClass = crudEntityAnnotation.model();
        Constructor<? extends CRUDModel> modelConstructor = modelClass.getConstructor();
        CRUDModel crudModel = modelConstructor.newInstance();
        crudModel.createModel(entityClass,crudEntityAnnotation);
        return crudModel;
    }

}
