package cart.service;

import cart.dto.ImageIdList;
import cart.dto.ImageUrlList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.logging.Logger;

@Service
public class GalleryService {
    private static final Logger LOGGER = Logger.getLogger(GalleryService.class.getName());
    private final WebClient webClient;
    private final Map<String, String> imageUrlCache = new HashMap<>();  // Cache for storing image URLs

    @Autowired
    public GalleryService(@Qualifier("galleryWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String getImageUrl(String imageId) {
        return imageUrlCache.computeIfAbsent(imageId, this::fetchImageUrl);
    }

    public Map<String, String> getImageUrlsByImageIds(List<String> imageIds) {
        Map<String, String> result = new HashMap<>();
        List<String> uncachedImageIds = new ArrayList<>();

        for (String imageId : imageIds) {
            result.put(imageId, imageUrlCache.getOrDefault(imageId, null));
            if (!imageUrlCache.containsKey(imageId)) {
                uncachedImageIds.add(imageId);
            }
        }

        if (!uncachedImageIds.isEmpty()) {
            fetchAndCacheImageUrls(uncachedImageIds, result);
        }
        return result;
    }

    private String fetchImageUrl(String imageId) {
        String query = createImageIdQuery(imageId);
        LOGGER.info("Query: " + query);

        try {
            String response = webClient.post()
                    .uri("/graphql")
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                LOGGER.severe("Failed to send image url query to gallery");
                return null;
            } else {
                LOGGER.info("Successfully sent image url query to gallery: " + response);
                return response;
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve image URL: " + e.getMessage());
            return null;
        }
    }

    private void fetchAndCacheImageUrls(List<String> uncachedImageIds, Map<String, String> result) {
        String query = createImageIdsQuery(uncachedImageIds);
        LOGGER.info("Query: " + query);

        try {
            String response = webClient.post()
                    .uri("/graphql")
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            LOGGER.info("Response: " + response);

            ImageUrlList imageUrlList = parseImageUrlList(response);
            if (imageUrlList != null && imageUrlList.getImageUrls() != null) {
                List<String> imageUrls = imageUrlList.getImageUrls();
                for (int i = 0; i < uncachedImageIds.size(); i++) {
                    String url = imageUrls.get(i);
                    String imageId = uncachedImageIds.get(i);
                    imageUrlCache.put(imageId, url);
                    result.put(imageId, url);
                }
                LOGGER.info("Successfully fetched image URLs ");
            } else {
                LOGGER.severe("Failed to retrieve image URLs, response is null or malformed");
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve image URLs: " + e.getMessage());
        }
    }

    private String createImageIdQuery(String imageId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode variablesNode = objectMapper.createObjectNode().put("imageId", imageId);
        return objectMapper.createObjectNode()
                .put("query", "query GetImageUrlByImageId($imageId: String!) { getImageUrlByImageId(imageId: $imageId) }")
                .set("variables", variablesNode)
                .toString();
    }

    private String createImageIdsQuery(List<String> imageIds) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode imageIdsValue = objectMapper.valueToTree(new ImageIdList(imageIds));
        ObjectNode variablesNode = objectMapper.createObjectNode().set("imageIds", imageIdsValue);

        return objectMapper.createObjectNode()
                .put("query", "query GetImageUrlsByImageIds($imageIds: ImageIdList!) { getImageUrlsByImageIds(imageIds: $imageIds) {imageUrls}}")
                .set("variables", variablesNode)
                .toString();
    }

    private ImageUrlList parseImageUrlList(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode imageUrlsNode = rootNode.path("data").path("getImageUrlsByImageIds");
            return objectMapper.treeToValue(imageUrlsNode, ImageUrlList.class);

        } catch (Exception e) {
            LOGGER.severe("Error while parsing response: " + e);
            return null;
        }
    }
}
