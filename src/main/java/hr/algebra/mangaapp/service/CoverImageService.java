package hr.algebra.mangaapp.service;

import javafx.scene.image.Image;

import java.io.File;

public class CoverImageService {

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
}
