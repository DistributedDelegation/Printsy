package gallery.resolver;

import gallery.dto.ImageIdList;
import gallery.dto.ImageUrlList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// The query should match the Query defined in the schema.
// This is similar to a controller for only GET requests.
@Controller
public class Query {

    private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);

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

    @QueryMapping String getImageUrlByImageId(@Argument String imageId){
        Optional<ImageMongo> image = mongoRepository.findById(imageId);
        if (image.isEmpty()){
            return "No image found for imageId :" + imageId;
        }
        return image.get().getImageUrl();
    }

    @QueryMapping ImageUrlList getImageUrlsByImageIds(@Argument ImageIdList imageIds){
        LOGGER.info("Received request for image urls for the following image ids: " + imageIds.getImageIds());
        List<String> imageUrls = new ArrayList<>();
        for (String imageId : imageIds.getImageIds()) {
            LOGGER.info("Getting image url for " + imageId);
            String url = getImageUrlByImageId(imageId);
            imageUrls.add(url);
            LOGGER.info("Image url for " + imageId + " is " + url);
        }
        ImageUrlList imageUrlList = new ImageUrlList(imageUrls);
        LOGGER.info("Created ImageUrlList object " + imageUrlList);
        return imageUrlList;
    }
}
