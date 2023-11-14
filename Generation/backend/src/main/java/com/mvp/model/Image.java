package com.mvp.model;

import java.util.Arrays;
import java.util.List;

import java.util.Random;

public class Image {
    private String id;
    private String link;

    public Image(String id, String link) {
        this.id = id;
        this.link = link;
    }

    private static List<Image> images = Arrays.asList(
        new Image("image-1", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Artificial_Intelligence_%26_AI_%26_Machine_Learning_-_30212411048.jpg/1200px-Artificial_Intelligence_%26_AI_%26_Machine_Learning_-_30212411048.jpg"),
        new Image("image-2", "https://upload.wikimedia.org/wikipedia/commons/d/d6/AI_Humans_and_Robots.jpg"),
        new Image("image-3", "https://upload.wikimedia.org/wikipedia/en/e/e6/AI_Poster.jpg"),
        new Image("image-4", "https://www.macobserver.com/wp-content/uploads/2018/04/AI-concept-human-head-1200x632.jpg")
    );

    public static Image getById(String id) {
        return images.stream().filter(image -> image.getId().equals(id)).findFirst().orElse(null);
    }

    public static List<Image> getAllImages() {
        return images;
    }

    public static Image getRandom() {
        Random rand = new Random();
        int ranInt = rand.nextInt(Image.getAllImages().size());
        //logger.info("Random index: " + ranInt);
        return Image.getAllImages().get(ranInt);
        //return images.get(1);
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }
}
