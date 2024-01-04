package gallery.repository;

import gallery.model.UserLikedImages;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserLikedImagesRepository extends JpaRepository<UserLikedImages, String> {
    List<UserLikedImages> findByUserId(Long userId);
    UserLikedImages findByImageIdAndUserId(String imageId, Long userId);
}