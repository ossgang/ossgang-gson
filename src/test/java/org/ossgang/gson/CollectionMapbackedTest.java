package org.ossgang.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.ossgang.commons.mapbackeds.Mapbacked;
import org.ossgang.commons.mapbackeds.Mapbackeds;
import org.ossgang.gson.adapters.MapbackedGsonAdapter;

import java.util.Arrays;
import java.util.List;

public class CollectionMapbackedTest {

    private static final String MISTER_X_STRG = "{\"name\":\"misterX\",\"favoriteNumbers\":[5,7,13],\"factors\":[0.5,0.3,0.2,0.1]}";
    public static final String CONTAINER_STRG = "{\"person\":" + MISTER_X_STRG + "}";
    private static final Person MISTER_X = Mapbackeds.builder(Person.class)//
            .field(Person::name, "misterX")//
            .field(Person::favoriteNumbers, Arrays.asList(5, 7, 13))//
            .field(Person::factors, Arrays.asList(0.5, 0.3, 0.2, 0.1)) //
            .build();

    private Gson gson;

    @Before
    public void setUp() {
        this.gson = new GsonBuilder().registerTypeAdapterFactory(MapbackedGsonAdapter.FACTORY).create();
    }

    @Test
    public void toJsonWorks() {
        String s = gson.toJson(MISTER_X);
        Assertions.assertThat(s).isEqualTo(MISTER_X_STRG);
    }

    @Test
    public void fromJsonWorks() {
        Person p = gson.fromJson(MISTER_X_STRG, Person.class);
        Assertions.assertThat(p).isEqualTo(MISTER_X);
    }

    @Mapbacked
    private interface Person {
        String name();

        List<Integer> favoriteNumbers();

        List<Double> factors();
    }
}
