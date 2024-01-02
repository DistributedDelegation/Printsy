package gallery.repository;

import gallery.model.ImageMongo;
import gallery.model.ImageSQL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMySQLRepository extends JpaRepository<ImageSQL, String> {
    ImageSQL findByImageId(String imageId);
}