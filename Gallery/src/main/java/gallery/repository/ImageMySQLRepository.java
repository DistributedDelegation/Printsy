package gallery.repository;

import gallery.model.ImageSQL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMySQLRepository extends JpaRepository<ImageSQL, String> {
}