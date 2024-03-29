package gallery.resolver;

import gallery.dto.ImageIdList;
import gallery.dto.ImageUrlList;
import gallery.dto.PublishedImage;
import gallery.dto.UserImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import gallery.model.ImageMongo;
import gallery.model.UserLikedImages;
import gallery.repository.ImageMongoRepository;
import gallery.repository.UserLikedImagesRepository;

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
    private final UserLikedImagesRepository userLikedImagesRepository;

    @Autowired
    public Query(ImageMongoRepository mongoRepository, UserLikedImagesRepository userLikedImagesRepository) {
        this.mongoRepository = mongoRepository;
        this.userLikedImagesRepository = userLikedImagesRepository;
    }

    @QueryMapping
    public List<PublishedImage> getAllPublishedImages() {
        return mongoRepository.findByIsImagePublishedYN(true)
                .stream()
                .map(image -> {
                    LOGGER.info(image.toString());
                    LOGGER.info("ID: " + image.getImageId() + " URL: " + image.getImageUrl(), " LikeCount: " + image.getLikeCount());
                    PublishedImage img = new PublishedImage(image.getImageId(), image.getImageUrl(), image.getLikeCount());
                    LOGGER.info("New published image: " + img);
                    return img;
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

    @QueryMapping List<String> getUserLikedImages(@Argument Long userId){
        List<UserLikedImages> imageList = userLikedImagesRepository.findByUserId(userId);
        List<String> imageIdList = new ArrayList<>();
        for (UserLikedImages image : imageList) {
            imageIdList.add(image.getImageId());
        }
        return imageIdList;
    }

    @QueryMapping
    public List<Map<String, Object>> getUserImages(@Argument Long userId) {
        return mongoRepository.findByUserId(userId)
                                .stream()
                                .map(image -> {
                                Map<String, Object> imageMap = new HashMap<>();
                                imageMap.put("imageId", image.getImageId());
                                imageMap.put("imageUrl", image.getImageUrl());
                                return imageMap;
                            })
                                .collect(Collectors.toList());
    }
}
