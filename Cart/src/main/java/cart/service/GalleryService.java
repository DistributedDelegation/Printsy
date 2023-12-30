package cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        // First, check the cache
        if (imageUrlCache.containsKey(imageId)) {
            LOGGER.info("Cache hit for imageId: " + imageId);
            return imageUrlCache.get(imageId);
        }

        // GraphQL Query
        String query = "{\"query\":\"query GetImageUrl($imageId: String!) { getImageUrl(imageId: $imageId) }\",\"variables\":{\"imageId\":\"" + imageId + "\"}}";

        try {
            // Make a post request and retrieve the result as a String
            Mono<String> response = webClient.post()
                    .uri("/graphql")
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(String.class);

            String url = response.block();  // This blocks until the mono is complete
            LOGGER.info("Successfully retrieved image URL: " + url);

            // Cache the URL for future use
            imageUrlCache.put(imageId, url);

            return url;  // You might need to parse this string to extract just the URL part

        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve image URL: " + e.getMessage());
            return null;  // Or handle this with a more sophisticated error handling
        }
    }

    public Map<String, String> getImageUrlsByImageIds(List<String> imageIds) {
        Map<String, String> result = new HashMap<>();
        List<String> uncachedImageIds = new ArrayList<>();

        // Check cache first
        for (String imageId : imageIds) {
            String cachedUrl = imageUrlCache.get(imageId);
            if (cachedUrl != null) {
                result.put(imageId, cachedUrl);  // Use the cached URL
            } else {
                uncachedImageIds.add(imageId);  // Prepare to fetch this ID
            }
        }

        // Fetch only uncached image IDs
        if (!uncachedImageIds.isEmpty()) {
            String query = "{\"query\":\"query GetImageUrlsByImageIds($imageIds: [String]!) { getImageUrlsByImageIds(imageIds: $imageIds) }\",\"variables\":{\"imageIds\":" + uncachedImageIds + "}}";

            try {
                Mono<List<String>> response = webClient.post()
                        .uri("/graphql")
                        .bodyValue(query)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
                List<String> urls = response.block(); // This blocks until the mono is complete

                if (urls != null) {
                    for (int i = 0; i < uncachedImageIds.size(); i++) {
                        String url = urls.get(i);
                        String imageId = uncachedImageIds.get(i);
                        imageUrlCache.put(imageId, url);  // Cache the new URL
                        result.put(imageId, url);        // Add to result
                    }
                }

            } catch (Exception e) {
                LOGGER.severe("Failed to retrieve image URLs: " + e.getMessage());
            }
        }

        return result; // Return combined results from cache and new fetch
    }

}
