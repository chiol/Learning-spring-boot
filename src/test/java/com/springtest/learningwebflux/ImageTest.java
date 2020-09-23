package com.springtest.learningwebflux;

import com.springtest.learningwebflux.images.Image;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {
    @Test
    public void imagesManagedByLombokShouldWork() {
        Image image = new Image("id", "file-name.jpg");
        assertThat(image.getId()).isEqualTo("id");
        assertThat(image.getName()).isEqualTo("file-name.jpg");

    }

}