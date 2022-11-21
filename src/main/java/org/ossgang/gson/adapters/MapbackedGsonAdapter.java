package org.ossgang.gson.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.ossgang.commons.mapbackeds.Mapbackeds;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An adapter for gson, that can serialize/deserialize mapbacked objects.
 *
 * @param <T> the type of the mapbacked object (the backed interface)
 */
public class MapbackedGsonAdapter<T> extends TypeAdapter<T> {

    public static final TypeAdapterFactory FACTORY = new MapbackedGsonAdapterFactory();

    private final Gson context;
    private final Class<T> backedInterface;

    /* Only called from factory */
    MapbackedGsonAdapter(Gson context, Class<T> backedInterface) {
        this.context = requireNonNull(context, "context must not be null.");
        this.backedInterface = requireNonNull(backedInterface, "backedInterface must not be null.");
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        /* Here it seems to be sufficient to use the adapter for the map, as the types are defined well */
        TypeAdapter<Map<String, Object>> mapTypeAdapter = context.getAdapter(new TypeToken<Map<String, Object>>() {
        });
        mapTypeAdapter.write(out, Mapbackeds.mapOf(value));
    }

    @Override
    public T read(JsonReader in) throws IOException {
        /* For reading we have to treat the fields separately, in order to have the types well defined. */

        Set<Method> fieldMethods = Mapbackeds.fieldMethods(backedInterface);
        Map<String, Method> nameToFields = fieldMethods.stream().collect(Collectors.toMap(Method::getName, m -> m));

        Map<String, Object> backingMap = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();

            Method fieldMethod = nameToFields.get(fieldName);
            if (fieldMethod == null) {
                in.skipValue();
                continue;
            }

            TypeToken<?> token = TypeToken.get(fieldMethod.getGenericReturnType());
            TypeAdapter<?> fieldAdapter = context.getAdapter(token);
            Object fieldValue = fieldAdapter.read(in);

            backingMap.put(fieldName, fieldValue);
        }
        in.endObject();

        return Mapbackeds.from(backedInterface, backingMap);
    }
}
