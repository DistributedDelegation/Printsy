package generation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import generation.model.ImageMongo;

import java.util.List;
import java.util.Optional;

public interface ImageMongoRepository extends MongoRepository<ImageMongo, String> {
    
    List<ImageMongo> findByIsImagePublishedYN(boolean isImagePublishedYN);

    Optional<ImageMongo> findByImageUrl(String imageUrl);
}
