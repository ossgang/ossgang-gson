package org.ossgang.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.ossgang.commons.mapbackeds.Mapbacked;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * A adapter factory for gson, that creates an adapter, in case the passed in type is an interface that is annotated
 * with the @{@link Mapbacked} annotation. To use the mapbacked objects, this annotation is mandatory, otherwise there
 * is no means to detect from the class only, if the adapter can be applied or not.
 */
/* package visibility, as it is only used from the static field of Mapbacked json adapter.*/
class MapbackedGsonAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return backedInterface(type).map(intfc -> newAdapter(gson, intfc)).orElse(null);
    }

    private static final <T> MapbackedGsonAdapter newAdapter(Gson gson, Class<T> backedInterface) {
        return new MapbackedGsonAdapter(gson, backedInterface);
    }

    private static final <T> Optional<Class<T>> backedInterface(TypeToken<T> type) {
        Type t = type.getType();
        if (!(t instanceof Class)) {
            return Optional.empty();
        }
        return backedInterfaceFrom((Class<?>) t);
    }

    private static <T> Optional<Class<T>> backedInterfaceFrom(Class<?> c) {
        Optional<Class<T>> direct = backedInterface(c);
        if (direct.isPresent()) {
            return direct;
        }
        return backedInterfaceFromProxy(c);
    }

    private static <T> Optional<Class<T>> backedInterfaceFromProxy(Class<?> c) {
        if (Proxy.isProxyClass(c)) {
            Class<?>[] interfaces = c.getInterfaces();
            if (interfaces.length == 1) {
                return backedInterface(interfaces[0]);
            }
        }
        return Optional.empty();
    }

    private static final <T> Optional<Class<T>> backedInterface(Class<?> backedInterfaceCandidate) {
        if (backedInterfaceCandidate.isInterface() && (backedInterfaceCandidate.getAnnotation(Mapbacked.class) != null)) {
            return Optional.of((Class<T>) backedInterfaceCandidate);
        }
        return Optional.empty();
    }
}
