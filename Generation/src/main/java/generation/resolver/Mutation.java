package generation.resolver;

import generation.model.Image;
import generation.repository.ImageMySQLRepository;
import generation.service.ImageGenerationService;
import generation.service.ImageStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class Mutation {

    // This top part initializes the repository classes (the classes for accessing
    // the dbs) as dependencies for the
    // Mutation class, and "autowires" (essentially injects them) into this class.
    private final ImageMySQLRepository sqlRepository;
    private final ImageGenerationService imageGenerationService;
    private final ImageStorageService imageStorageService;

    @Autowired
    public Mutation(ImageMySQLRepository sqlRepository, ImageGenerationService imageGenerationService, ImageStorageService imageStorageService) {
        this.sqlRepository = sqlRepository;
        this.imageGenerationService = imageGenerationService;
        this.imageStorageService = imageStorageService;
    }

    @MutationMapping
    public Map<String, String> createImage(@Argument String prompt) {

        Image newImage =  imageGenerationService.generateImage(prompt);
        //String imageUrl = imageStorageService.processAndUploadImage(newImage.getImageUrl(), newImage.getId());

        Map<String, String> response = new HashMap<>();
        response.put("id", newImage.getId());
        response.put("imageURL", newImage.getImageUrl());
        
        return response;
    }

    @MutationMapping
    public Map<String, String> uploadImage(@Argument String url) {

        //Image newImage =  imageGenerationService.generateImage(prompt);
        String id = UUID.randomUUID().toString();
        String imageUrl = imageStorageService.processAndUploadImage(url, id);

        Map<String, String> response = new HashMap<>();
        response.put("id",id);
        response.put("imageURL", imageUrl);
        
        return response;
    }

    @MutationMapping
    public String deleteImage(@Argument String id) {
        return imageStorageService.deleteImage(id);
    }

}