package gallery.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import gallery.model.Image;
import gallery.repository.ImageMongoRepository;

import java.util.Optional;
import java.util.UUID;

@Controller
public class Mutation {

    // This top part initializes the repository classes (the classes for accessing
    // the dbs) as dependencies for the
    // Mutation class, and "autowires" (essentially injects them) into this class.
    private final ImageMongoRepository mongoRepository;

    @Autowired
    public Mutation(ImageMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @MutationMapping
    public String createImage(@Argument String imageUrl) {

        // Generate a uuid for the image, this will be the same in both dbs
        String id = UUID.randomUUID().toString();

        // Create new image object
        Image image = new Image();
        image.setId(id);
        image.setImageUrl(imageUrl);

        // Save the image to our repository
        mongoRepository.save(image);

        return image.getId();
    }

    @MutationMapping
    public String updateImage(@Argument String imageUrl, @Argument String id) {

        // Get image
        Optional<Image> image = mongoRepository.findById(id);

        if (image.isPresent()) {

            Image foundImage = image.get();
            foundImage.setImageUrl(imageUrl);

            // Save the image to our repository
            mongoRepository.save(foundImage);

            return "Successfully updated image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

    @MutationMapping
    public String deleteImage(@Argument String id) {

        // Get image
        Optional<Image> image = mongoRepository.findById(id);

        if (image.isPresent()) {

            Image foundImage = image.get();

            // Save the image to our repository
            mongoRepository.delete(foundImage);

            return "Successfully deleted image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

}
