package generation.resolver;

import generation.model.Image;
import generation.model.Feature;
import generation.model.ImageMongo;
import generation.repository.ImageMySQLRepository;
import generation.repository.ImageMongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// The query should match the Query defined in the schema.
// This is similar to a controller for only GET requests.
@Controller
public class Query {

    // This top part initializes the repository class and "autowires" (essentially
    // injects them) into this class.
    private final ImageMongoRepository mongoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);

    @Autowired
    public Query(ImageMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @QueryMapping
    public List<Feature> fetchFeatures() {
        return Arrays.asList(
                new Feature("Style", Arrays.asList("Abstract", "Realistic", "Cartoon", "Minimalist")),
                new Feature("Color Palette", Arrays.asList("Vibrant", "Pastel", "Monochrome", "Earth Tones")),
                new Feature("Theme", Arrays.asList("Nature", "Urban", "Space", "Fantasy")),
                new Feature("Mood", Arrays.asList("Joyful", "Serene", "Mysterious", "Energetic")),
                new Feature("Subject Focus", Arrays.asList("Animals", "People", "Landscapes", "Objects")),
                new Feature("Composition", Arrays.asList("Centralized", "Asymmetrical", "Layered", "Framed")),
                new Feature("Texture", Arrays.asList("Smooth", "Rough", "Patterned", "Glowing")),
                new Feature("Era", Arrays.asList("Vintage", "Contemporary", "Futuristic", "Historical")),
                new Feature("Cultural Influence", Arrays.asList("Asian", "Western", "African", "Indigenous")),
                new Feature("Graphic Elements",
                        Arrays.asList("Geometric Shapes", "Floral Patterns", "Gradients", "Line Art")));
    }

    // checked with: {"query": "query getImageByUrl($imageUrl: String!) {
    // getImageByUrl(imageUrl: $imageUrl) { imageId imageUrl imageName
    // imageDescription isImagePublishedYN imageTimestamp } }","variables":
    // {"imageUrl":
    // "https://s3.eu-de.cloud-object-storage.appdomain.cloud/printsy-images/c09af5ff-d1bf-4f56-a934-059912a34f00.png"}}
    @QueryMapping
    public ImageMongo getImageByUrl(@Argument String imageUrl) {
        Optional<ImageMongo> imageMongo = mongoRepository.findByImageUrl(imageUrl);
        if (imageMongo.isPresent()) {
            LOGGER.info("Image details for image with url " + imageUrl + " found");
            return imageMongo.get();
        } else {
            return null;
        }
    }
}