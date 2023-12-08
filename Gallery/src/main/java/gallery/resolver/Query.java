package gallery.resolver;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import gallery.model.ImageMongo;
import gallery.model.ImageSQL;
import gallery.repository.ImageMongoRepository;
import gallery.repository.ImageMySQLRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

// The query should match the Query defined in the schema.
// This is similar to a controller for only GET requests.
@Controller
public class Query {

    // This top part initializes the repository class and "autowires" (essentially
    // injects them) into this class.
    private final ImageMongoRepository mongoRepository;
    private final ImageMySQLRepository sqlRepository;

    @Autowired
    public Query(ImageMySQLRepository sqlRepository, ImageMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
        this.sqlRepository = sqlRepository;
    }

    @QueryMapping
    public List<ImageMongo> getMongoImages() {
        // Getting images from database to demonstrate interaction
        // These findAll() methods come from the Repository interface that we defined
        return mongoRepository.findAll();
    }

    @QueryMapping
    public ImageMongo getMongoImage(@Argument String id) {
        // Getting image from database to demonstrate interaction
        // We use Optional<Image> here in case there is no image in the db with that id
        // These findById() methods come from the Repository interface
        Optional<ImageMongo> oImage = mongoRepository.findById(id);

        if (oImage.isPresent()) {
            return oImage.get();
        } else {
            throw new RuntimeException("No image found with id: " + id);
        }
    }

    @QueryMapping
    public String fetchMongoImage(@Argument String url) {

        System.out.println("Attempting to fetch image from: " + url);

        try {
            // Check if the URL is valid by trying to parse it as a URI
            new URI(url);
            // If no exception is thrown, the URL is valid, so return it
            System.out.println("Successfully retrieved image!");
            return url;
        } catch (URISyntaxException e) {
            // If an exception is thrown, the URL is invalid, so throw a RuntimeException
            throw new RuntimeException("Invalid URL: " + url, e);
        }
    }

    @QueryMapping
    public List<ImageSQL> getSQLImages() {
        // Getting images from database to demonstrate interaction
        // These findAll() methods come from the Repository interface that we defined
        return sqlRepository.findAll();
    }

    @QueryMapping
    public ImageSQL getSQLImage(@Argument String id) {
        // Getting image from database to demonstrate interaction
        // We use Optional<Image> here in case there is no image in the db with that id
        // These findById() methods come from the Repository interface
        Optional<ImageSQL> oImage = sqlRepository.findById(id);

        if (oImage.isPresent()) {
            return oImage.get();
        } else {
            throw new RuntimeException("No image found with id: " + id);
        }
    }

    @QueryMapping
    public String fetchSQLImage(@Argument String url) {

        System.out.println("Attempting to fetch image from: " + url);

        try {
            // Check if the URL is valid by trying to parse it as a URI
            new URI(url);
            // If no exception is thrown, the URL is valid, so return it
            System.out.println("Successfully retrieved image!");
            return url;
        } catch (URISyntaxException e) {
            // If an exception is thrown, the URL is invalid, so throw a RuntimeException
            throw new RuntimeException("Invalid URL: " + url, e);
        }
    }
}
