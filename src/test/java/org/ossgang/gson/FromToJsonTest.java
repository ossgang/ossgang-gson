package org.ossgang.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.ossgang.gson.adapters.MapbackedGsonAdapter;
import org.ossgang.commons.mapbackeds.Mapbacked;
import org.ossgang.commons.mapbackeds.Mapbackeds;

import java.lang.reflect.Proxy;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class FromToJsonTest {

    private static final String MISTER_X_STRG = "{\"name\":\"misterX\",\"yearOfBirth\":1955}";
    public static final String CONTAINER_STRG = "{\"person\":" + MISTER_X_STRG + "}";
    private static final Person MISTER_X = Mapbackeds.builder(Person.class)//
            .field(Person::name, "misterX")//
            .field(Person::yearOfBirth, 1955)//
            .build();

    private Gson gson;

    @Before
    public void setUp() {
        this.gson = new GsonBuilder().registerTypeAdapterFactory(MapbackedGsonAdapter.FACTORY).create();
    }


    @Test
    public void bareToJsonWorks() {
        assertThat(gson.toJson(MISTER_X)).isEqualTo(MISTER_X_STRG);
    }

    @Test
    public void bareFromJsonWorks() {
        Person m = gson.fromJson(MISTER_X_STRG, Person.class);

        assertThat(m.name()).isEqualTo("misterX");
        assertThat(m.yearOfBirth()).isEqualTo(1955);
    }


    @Test
    public void toJsonWorksInContainer() {
        Container container = new Container();
        container.person = MISTER_X;

        assertThat(gson.toJson(container)).isEqualTo(CONTAINER_STRG);
    }

    @Test
    public void fromJsonWorksInContainer() {
        Container container = new Container();
        container.person = MISTER_X;

        assertThat(gson.fromJson(CONTAINER_STRG, Container.class)).isEqualTo(container);
    }

    @Test
    public void tryTypeResolve() {
        Class<? extends Person> c = MISTER_X.getClass();

        Assertions.assertThat(Proxy.isProxyClass(c)).isTrue();
        Assertions.assertThat(c.getInterfaces()).containsExactly(Person.class);
    }

    @Mapbacked
    private interface Person {
        String name();

        int yearOfBirth();
    }

    public class Container {
        public Person person;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Container container = (Container) o;
            return Objects.equals(person, container.person);
        }

        @Override
        public int hashCode() {
            return Objects.hash(person);
        }

        @Override
        public String toString() {
            return "Container{" +
                    "person=" + person +
                    '}';
        }
    }

}
