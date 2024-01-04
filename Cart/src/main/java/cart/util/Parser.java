package cart.util;

import cart.service.CartService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Component
public class Parser {

    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    public <T> T parseResponse(Class<T> instanceType, String requestName, String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.path("data").path(requestName);
            return objectMapper.treeToValue(dataNode, instanceType);

        } catch (Exception e) {
            LOGGER.severe("Error while parsing response: " + e);
            return null;
        }

    }
}
