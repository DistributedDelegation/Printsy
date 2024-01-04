package cart.service;

import cart.dto.CountResult;
import cart.dto.TransactionInput;
import cart.dto.TransactionResult;
import cart.util.Parser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class TransactionGatewayService {

    private static final Logger LOGGER = Logger.getLogger(TransactionGatewayService.class.getName());

    private final WebClient webClient;
    private final Parser parser;

    @Autowired
    public TransactionGatewayService(@Qualifier("transactionWebClient") WebClient webClient, Parser parser) {
        this.webClient = webClient;
        this.parser = parser;
    }

    // checked with: {"query": "query FindImageAvailability($imageId: ID!) { findImageAvailability(imageId: $imageId) }","variables": { "imageId": "1" } }
    public int getTransactionImageAvailability(String imageId) {
        String query = "{ \"query\": \"query checkImageTransactionCount($imageId: String!) { checkImageTransactionCount(imageId: $imageId) { count } }\", \"variables\": { \"imageId\": \"" + imageId + "\" } }";
        LOGGER.info("Query: "+ query);

        // Make a post request and retrieve the result
        String response = webClient.post()
                .uri("/graphql")
                .bodyValue(query)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            LOGGER.severe("Failed to send image count query to transaction-gateway");
            return -1;
        } else {
            CountResult countResult = parser.parseResponse(CountResult.class, "checkImageTransactionCount", response);
            LOGGER.info("Successfully sent image count query to transaction-gateway: " + response + ". Count = " + countResult.getCount());
            return countResult.getCount();
        }
    }


    public boolean completeTransaction(List<TransactionInput> transactions) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        String transactionJson = objectMapper.writeValueAsString(transactions);
        String mutation = "mutation ($transactionInputs: [TransactionInput!]!) { completeTransaction(transactionInputs: $transactionInputs) { success } }";
        String payload = "{\"query\": \"" + mutation.replace("\"", "\\\"") + "\", \"variables\": {\"transactionInputs\": " + transactionJson + "}}";
        LOGGER.info("Payload:" + payload);

        try {
            String rawResponse = webClient.post()
                    .uri("/graphql")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)  // Retrieve raw JSON string
                    .block();

            LOGGER.info("Raw Response: " + rawResponse);  // Print the raw JSON response
            // Then convert the raw JSON to TransactionResult object
            if (rawResponse == null) {
                LOGGER.severe("Failed to send transaction inputs to Transaction-Gateway");
                return false;
            } else {
                Boolean success = Objects.requireNonNull(parser.parseResponse(TransactionResult.class, "completeTransaction", rawResponse)).getSuccess();
                LOGGER.info("Successfully sent transaction inputs to Transaction-Gateway");
                return success;
            }
        } catch (Exception e) {
            LOGGER.severe("Error while sending request to Transaction-Gateway: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}



