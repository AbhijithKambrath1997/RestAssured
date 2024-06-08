package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import endpoints.PetEndpoints;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import payloads.Pet;
import templates.TestTemplate;

import java.util.List;

public class PetTest {


    private final List<String> statuses = List.of("available", "pending", "sold");
    TestTemplate template = TestTemplate.create();
    Faker faker = template.getFaker();
    PetEndpoints petEndpoints = new PetEndpoints();

    @Test
    public void createPet() {

        Pet pet = Pet.builder()
                .id(faker.idNumber().hashCode())
                .name(faker.artist().name())
                .status(statuses.getFirst())
                .build();

        JsonNode categoryNode = pet.buildNode(faker.idNumber().hashCode(), faker.artist().name());
        JsonNode tagNode = pet.buildNode(faker.idNumber().hashCode(), faker.artist().name());
        pet.setCategory(categoryNode);
        pet.setTags(List.of(tagNode));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.valueToTree(pet);

        Response response = petEndpoints.createPet(node);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);


    }
}
