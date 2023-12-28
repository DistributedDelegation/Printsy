package generation.service;

import generation.model.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.UUID;
import java.io.IOException;

@Service
public class ImageGenerationService {

    private final RestTemplate restTemplate;

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Autowired
    public ImageGenerationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Image generateImage(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        String requestBody = buildRequestBody(prompt);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response;

        try {
            response = restTemplate.postForEntity(
                "https://api.openai.com/v1/images/generations",
                request,
                String.class
            );
        } catch (Exception e) {
            // Handle the exception (e.g., logging, rethrowing)
            throw new RuntimeException("Failed to call OpenAI API", e);
        }

        String imageUrl = extractImageUrl(response.getBody());

        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setImageUrl(imageUrl);
        return image;
    }

    private String buildRequestBody(String prompt) {
        return String.format(
            "{\"model\": \"dall-e-2\", \"prompt\": \"%s\", \"n\": 1, \"size\": \"1024x1024\"}",
            prompt
        );
    }

    // Parse the response body and extract the image URL
    private String extractImageUrl(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataArray = rootNode.path("data");
            if (dataArray.isArray() && dataArray.has(0)) {
                JsonNode firstImage = dataArray.get(0);
                return firstImage.path("url").asText();
            }
        } catch (IOException e) {
            // Log error and/or rethrow as needed
            throw new RuntimeException("Failed to parse JSON response", e);
        }
        return null;
    }
    

}