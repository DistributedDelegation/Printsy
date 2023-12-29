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
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<Map<String, Object>> getAllPublishedImages() {
        return mongoRepository.findByIsImagePublishedYN(true)
                                .stream()
                                .map(image -> {
                                Map<String, Object> imageMap = new HashMap<>();
                                imageMap.put("imageId", image.getImageId());
                                imageMap.put("imageUrl", image.getImageUrl());
                                imageMap.put("likeCount", image.getLikeCount());
                                return imageMap;
                            })
                                .collect(Collectors.toList());
    }
}
