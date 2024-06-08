package templates;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.Getter;

public class TestTemplate {

    @Getter(AccessLevel.PROTECTED)
    public TestLogger logger = new TestLogger();

    public void print(String value) {
        logger.getLogger(value);
    }

    public static TestTemplate create() {
        return new TestTemplate();
    }

    public Faker getFaker(){
        return new Faker();
    }
}
