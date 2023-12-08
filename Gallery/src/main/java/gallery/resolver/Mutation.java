package gallery.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import gallery.model.ImageMongo;
import gallery.model.ImageSQL;
import gallery.repository.ImageMongoRepository;
import gallery.repository.ImageMySQLRepository;

import java.util.Optional;
import java.util.UUID;

@Controller
public class Mutation {

    // This top part initializes the repository classes (the classes for accessing
    // the dbs) as dependencies for the
    // Mutation class, and "autowires" (essentially injects them) into this class.
    private final ImageMongoRepository mongoRepository;
    private final ImageMySQLRepository sqlRepository;

    @Autowired
    public Mutation(ImageMySQLRepository sqlRepository, ImageMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
        this.sqlRepository = sqlRepository;
    }

    @MutationMapping
    public String createMongoImage(@Argument String imageUrl) {

        // Generate a uuid for the image, this will be the same in both dbs
        String id = UUID.randomUUID().toString();
        System.out.println("This is the Mongo id: "+id);

        // Create new image object
        ImageMongo image = new ImageMongo();
        image.setId(id);
        image.setImageUrl(imageUrl);

        // Save the image to our repository
        mongoRepository.save(image);

        return image.getId();
    }

    @MutationMapping
    public String updateMongoImage(@Argument String imageUrl, @Argument String id) {

        // Get image
        Optional<ImageMongo> image = mongoRepository.findById(id);

        if (image.isPresent()) {

            ImageMongo foundImage = image.get();
            foundImage.setImageUrl(imageUrl);

            // Save the image to our repository
            mongoRepository.save(foundImage);

            return "Successfully updated image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

    @MutationMapping
    public String deleteMongoImage(@Argument String id) {

        // Get image
        Optional<ImageMongo> image = mongoRepository.findById(id);

        if (image.isPresent()) {

            ImageMongo foundImage = image.get();

            // Save the image to our repository
            mongoRepository.delete(foundImage);

            return "Successfully deleted image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

    @MutationMapping
    public String createSQLImage(@Argument String imageUrl) {

        // Generate a uuid for the image, this will be the same in both dbs
        String id = UUID.randomUUID().toString();
        System.out.println("This is the SQL id: "+id);

        // Create new image object
        ImageSQL image = new ImageSQL();
        image.setId(id);
        image.setImageUrl(imageUrl);

        // Save the image to our repository
        sqlRepository.save(image);

        return image.getId();
    }

    @MutationMapping
    public String updateSQLImage(@Argument String imageUrl, @Argument String id) {

        // Get image
        Optional<ImageSQL> image = sqlRepository.findById(id);

        if (image.isPresent()) {

            ImageSQL foundImage = image.get();
            foundImage.setImageUrl(imageUrl);

            // Save the image to our repository
            sqlRepository.save(foundImage);

            return "Successfully updated image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

    @MutationMapping
    public String deleteSQLImage(@Argument String id) {

        // Get image
        Optional<ImageSQL> image = sqlRepository.findById(id);

        if (image.isPresent()) {

            ImageSQL foundImage = image.get();

            // Save the image to our repository
            sqlRepository.delete(foundImage);

            return "Successfully deleted image!";
        }

        // Should implement more sophisticated error handling
        return "Image not found!";

    }

}
