package generation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import generation.model.ImageMongo;

import java.util.List;

public interface ImageMongoRepository extends MongoRepository<ImageMongo, String> {
    List<ImageMongo> findByIsImagePublishedYN(boolean isImagePublishedYN);
}
