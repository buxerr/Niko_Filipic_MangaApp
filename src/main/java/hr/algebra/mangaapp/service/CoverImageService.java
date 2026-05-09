package hr.algebra.mangaapp.service;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoverImageService {

    private static final Logger log = LoggerFactory.getLogger(CoverImageService.class);
    private static final Path COVERS_DIRECTORY = Path.of("assets", "covers")
            .toAbsolutePath()
            .normalize();

    public Image loadCover(String imagePath, double requestedWidth, double requestedHeight) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            return null;
        }

        return new Image(
                imageFile.toURI().toString(),
                requestedWidth,
                requestedHeight,
                true,
                true
        );
    }

    public boolean deleteCoverIfExists(String imagePath) {
        Path coverPath = resolveCoverPath(imagePath);

        if (coverPath == null || !Files.isRegularFile(coverPath)) {
            return false;
        }

        try {
            boolean deleted = Files.deleteIfExists(coverPath);

            if (deleted) {
                log.info("Cover image deleted: {}", coverPath);
            }

            return deleted;
        } catch (IOException e) {
            log.warn("Failed to delete cover image: {}", coverPath, e);
            return false;
        }
    }

    private Path resolveCoverPath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        Path coverPath = Path.of(imagePath.trim()).normalize();

        if (!coverPath.isAbsolute()) {
            coverPath = Path.of("")
                    .toAbsolutePath()
                    .resolve(coverPath)
                    .normalize();
        }

        if (!coverPath.startsWith(COVERS_DIRECTORY)) {
            log.warn("Skipping cover deletion outside assets/covers: {}", imagePath);
            return null;
        }

        return coverPath;
    }
}
