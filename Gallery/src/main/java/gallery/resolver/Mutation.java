package gallery.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import gallery.model.ImageMongo;
import gallery.model.ImageSQL;
import gallery.repository.ImageMongoRepository;
import gallery.repository.ImageMySQLRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
    public String saveToImageAndGalleryTable(@Argument String imageUrl, @Argument String imageNameInput, @Argument String imageDescriptionInput, @Argument Boolean imagePublishedYN, @Argument String usrId) {

        // Generate a uuid for the image, this will be the same in both dbs
        String imageId = UUID.randomUUID().toString();
        System.out.println("This is the Image ID: "+ imageId);

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        String imageTimestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timeStamp);

        // Create new image object
        ImageMongo imageMongo = new ImageMongo();
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
    public Integer saveIncreasedLikeCount(@Argument String imageId) {
        
        ImageMongo imageMongo = mongoRepository.findByImageId(imageId);
        Integer imageLikeCount = imageMongo.getLikeCount() + 1;
        imageMongo.setLikeCount(imageLikeCount);
        mongoRepository.save(imageMongo);

        ImageSQL imageSql = sqlRepository.findByImageId(imageId);
        imageSql.setLikeCount(imageLikeCount);
        sqlRepository.save(imageSql);

        return imageLikeCount;
    }

}
