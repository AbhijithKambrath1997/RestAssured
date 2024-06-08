package payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.Ignore;

import java.util.List;

@Getter
@Setter
@Builder
public class Pet {

    public int id;
    public JsonNode category;
    public String name;
    public List<String> photoUrls;
    public List<JsonNode> tags;
    public String status;


    @JsonIgnore
    public JsonNode buildNode(int id, String name){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode categoryNode = objectMapper.createObjectNode();
        categoryNode.put("id", id);
        categoryNode.put("name", name);
        return categoryNode;
    }

}
