package constant;

import com.github.javafaker.Faker;

public class RandomData {
    private static final Faker faker = new Faker();
    public static final String EMAIL = faker.internet().emailAddress();
    public static final String PASSWORD = faker.internet().password(8, 16, true, true, true);
    public static final String NAME = faker.name().firstName();
    public static final String HASH_CODE = faker.regexify("[0-9a-f]{20}");
    public static final String EMPTY = "";
}
