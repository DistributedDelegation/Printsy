package generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import generation.model.Image;

public interface ImageMySQLRepository extends JpaRepository<Image, String> {
}