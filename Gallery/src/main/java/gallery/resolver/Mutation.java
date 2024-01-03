package gallery.resolver;

import gallery.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import gallery.model.ImageMongo;
import gallery.model.ImageSQL;
import gallery.model.UserLikedImages;
import gallery.repository.ImageMongoRepository;
import gallery.repository.ImageMySQLRepository;
import gallery.repository.UserLikedImagesRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Controller
public class Mutation {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    // This top part initializes the repository classes (the classes for accessing
    // the dbs) as dependencies for the
    // Mutation class, and "autowires" (essentially injects them) into this class.
    private final ImageMongoRepository mongoRepository;
    private final ImageMySQLRepository sqlRepository;
    private final UserLikedImagesRepository userLikedImagesRepository;

    @Autowired
    public Mutation(ImageMySQLRepository sqlRepository, ImageMongoRepository mongoRepository, UserLikedImagesRepository userLikedImagesRepository) {
        this.mongoRepository = mongoRepository;
        this.sqlRepository = sqlRepository;
        this.userLikedImagesRepository = userLikedImagesRepository;
    }

    @MutationMapping
    public String saveToImageAndGalleryTable(@Argument String imageUrl, @Argument String imageNameInput, @Argument String imageDescriptionInput, @Argument Boolean imagePublishedYN, @Argument String usrId) {

        // Generate an uuid for the image, this will be the same in both dbs
         String imageId = UUID.randomUUID().toString();
        logger.info("This is the Image ID: "+ imageId);

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        String imageTimestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timeStamp);

        // Create new image object
        ImageMongo imageMongo = new ImageMongo();
        // ImageMongo imageMongo = mongoRepository.findByImageId(imageId);
        imageMongo.setImageId(imageId);
        imageMongo.setImageUrl(imageUrl);
        imageMongo.setImageName(imageNameInput);
        imageMongo.setImageDescription(imageDescriptionInput);
        imageMongo.setIsImagePublishedYN(imagePublishedYN);
        imageMongo.setImageTimestamp(imageTimestamp);
        imageMongo.setLikeCount(0);

        ImageSQL imageSql = new ImageSQL();
        imageSql.setImageId(imageId);
        imageSql.setUsrId(usrId);
        imageSql.setIsImagePublishedYN(imagePublishedYN);
        imageSql.setLikeCount(0);

        // Save the image to our repository
        mongoRepository.save(imageMongo);
        sqlRepository.save(imageSql);

        return imageId;
    }

    @MutationMapping
    public Integer saveIncreasedLikeCount(@Argument String imageId, @Argument String userId) {
        
        ImageMongo imageMongo = mongoRepository.findByImageId(imageId);
        Integer imageLikeCount = imageMongo.getLikeCount() + 1;
        imageMongo.setLikeCount(imageLikeCount);
        mongoRepository.save(imageMongo);

        UserLikedImages userLikedImages = new UserLikedImages();
        userLikedImages.setUserId(userId);
        userLikedImages.setImageId(imageId);
        userLikedImagesRepository.save(userLikedImages);

        return imageLikeCount;
    }

    @MutationMapping
    public Integer saveDecreasedLikeCount(@Argument String imageId, @Argument String userId) {
        
        ImageMongo imageMongo = mongoRepository.findByImageId(imageId);
        Integer imageLikeCount = imageMongo.getLikeCount() - 1;
        imageMongo.setLikeCount(imageLikeCount);
        mongoRepository.save(imageMongo);

        UserLikedImages userLikedImage = userLikedImagesRepository.findByImageIdAndUserId(imageId, userId);
        if (userLikedImage != null) {
            userLikedImagesRepository.delete(userLikedImage);
        }

        return imageLikeCount;
    }


    @MutationMapping
    public Boolean makeImagePublic (@Argument String imageId) {

        ImageSQL imageSql = sqlRepository.findByImageId(imageId);
        imageSql.setIsImagePublishedYN(true);
        sqlRepository.save(imageSql);

        ImageMongo imageMongo = mongoRepository.findByImageId(imageId);
        imageMongo.setIsImagePublishedYN(true);
        mongoRepository.save(imageMongo);

        return true;
    }

}
