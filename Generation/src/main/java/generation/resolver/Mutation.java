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
        String imageUrl = imageStorageService.processAndUploadImage(newImage.getImageUrl(), newImage.getId());

        Map<String, String> response = new HashMap<>();
        response.put("id", newImage.getId());
        response.put("imageURL", imageUrl);
        
        return response;

        /*

        // Save the image to our repository
        sqlRepository.save(image);

         */
    }

    /*
    @MutationMapping
    public String updateImage(@Argument String imageUrl, @Argument String id) {

        // Get image
        Optional<Image> image = sqlRepository.findById(id);

        if (image.isPresent()) {

            Image foundImage = image.get();
            foundImage.setImageUrl(imageUrl);

            // Save the image to our repository
            sqlRepository.save(foundImage);

            return "Successfully updated image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

    @MutationMapping
    public String deleteImage(@Argument String id) {

        // Get image
        Optional<Image> image = sqlRepository.findById(id);

        if (image.isPresent()) {

            Image foundImage = image.get();

            // Save the image to our repository
            sqlRepository.delete(foundImage);

            return "Successfully deleted image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }
    */

}