package dps.webapplication.i18n;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

@Named("localized")
@RequestScoped
public class LocalizedStrings implements Map<String,Map<String,String>> {

    @Inject CurrentLocale locale;
    @Inject ResourceBundles bundles;

    @Override
    public boolean containsKey(Object key) {
        return bundles.getBundle(locale.getLocale(),(String)key) != null;
    }

    @Override
    public Map<String, String> get(Object key) {

        ResourceBundle bundle = bundles.getBundle(locale.getLocale(),(String)key);

        return new Map<String,String>() {

            @Override
            public boolean containsKey(Object key) {
                return bundle.containsKey((String)key);
            }

            @Override
            public String get(Object key) {
                return bundle.getString((String)key);
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public String put(String key, String value) {
                return null;
            }

            @Override
            public String remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ? extends String> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<String> keySet() {
                return bundle.keySet();
            }

            @Override
            public Collection<String> values() {
                return null;
            }

            @Override
            public Set<Entry<String, String>> entrySet() {
                return null;
            }

        };
    }



    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Map<String, String> put(String key, Map<String, String> value) {
        return null;
    }

    @Override
    public Map<String, String> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Map<String, String>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Map<String, String>> values() {
        return null;
    }

    @Override
    public Set<Entry<String, Map<String, String>>> entrySet() {
        return null;
    }


}
