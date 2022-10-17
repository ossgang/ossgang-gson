# ossgang-gson

Contains some classes which can be used in the combination of oosgang-commons and gson. The main feature (and currently
the only one ;-) is a gson converter for mapbacked objects.

## Example Usage

Construct a gson instance, registering the mapbacked Adapter Factory:

```java
Gson gson=new GsonBuilder().registerTypeAdapterFactory(MapbackedGsonAdapter.FACTORY).create();
```

Create an interface for your data object and annotate it with the `@Mapbacked` annotation. E.g. a person:

```java
@Mapbacked
public interface Person {
    String name();
    int yearOfBirth();
}
```

NOTE: The `@Mapbacked` annotation is obligatory here (while it is not for mapbackeds in general), as otherwise, the Type
adapter factory cannot determine that this should represent a mapbacked object.

Having this 2 steps in place, you can convert your mapbacked objects into json and vice versa:

```java
Person misterX = Mapbackeds.builder(Person.class)//
        .field(Person::name, "misterX")//
        .field(Person::yearOfBirth, 1955)//
        .build();

String jsonString = gson.toJson(misterX);
/* Results in something like: '{"name":"misterX","yearOfBirth":1955}' */

Person m = gson.fromJson(jsonString, Person.class);
/* Results in a new person instance, equivalent to misterX */
```

This should allow to use (truly immutable!) mapbacked objects for data objects, e.g. in json APIs.