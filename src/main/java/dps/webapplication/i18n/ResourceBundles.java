package dps.webapplication.i18n;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@ApplicationScoped
public class ResourceBundles {

    Map<Locale,Map<String,ResourceBundle>> resourceBundles = new HashMap<>();

    ResourceBundle getBundle(Locale locale, String name)
    {
        ResourceBundle bundle = null;
        Map<String,ResourceBundle> resourceBundlesMap = resourceBundles.get(locale);
        if (resourceBundlesMap == null) {
            resourceBundlesMap = new HashMap<>();
            resourceBundles.put(locale,resourceBundlesMap);
        } else {
            bundle = resourceBundlesMap.get(name);
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(name, locale);
            resourceBundlesMap.put(name,bundle);
        }
        return bundle;
    }

}
