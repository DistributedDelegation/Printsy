package gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import gallery.model.ImageMongo;

public interface ImageMongoRepository extends MongoRepository<ImageMongo, String> {
}
