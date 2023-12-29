package gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import gallery.model.ImageMongo;

import java.util.List;

public interface ImageMongoRepository extends MongoRepository<ImageMongo, String> {
    List<ImageMongo> findByIsImagePublishedYN(boolean isImagePublishedYN);
    ImageMongo findByImageId(String imageId);
}
