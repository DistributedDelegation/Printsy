package gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import gallery.model.Image;

public interface ImageMongoRepository extends MongoRepository<Image, String> {
}
