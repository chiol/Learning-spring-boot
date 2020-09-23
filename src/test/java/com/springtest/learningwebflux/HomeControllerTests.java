package com.springtest.learningwebflux;

import com.springtest.learningwebflux.images.Image;
import com.springtest.learningwebflux.images.ImageRepository;
import com.springtest.learningwebflux.images.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {
    @Autowired
    WebTestClient webClient;
    @MockBean
    ImageService imageService;

    @MockBean
    ImageRepository imageRepository;

    @Test
    public void baseRouteShouldListAllImages() {
        Image alphaImage = new Image("1", "alpha.png");
        Image bravoImage = new Image("2", "bravo.png");
        given(imageService.findAllImages())
                .willReturn(Flux.just(alphaImage,bravoImage));

        //when
        EntityExchangeResult<String> result = webClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult();

        verify(imageService).findAllImages();
        verifyNoMoreInteractions(imageService);
        assertThat(result.getResponseBody()).contains("Learning Spring Boot")
                .contains("\"/images/alpha.png\"")
                .contains("\"/images/bravo.png\"");
    }

    @Test
    public void fetchingImageShouldWork() {
        given(imageService.findOneImage(any()))
                .willReturn(Mono.just(
                        new ByteArrayResource("data".getBytes())));
        webClient
                .get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("data");
        verify(imageService).findOneImage("alpha.png");
        verifyNoMoreInteractions(imageService);
    }
    @Test
    public void fetchingNullImageShouldFail() throws IOException {
        Resource resource = mock(Resource.class);
        given(resource.getInputStream())
                .willThrow(new IOException("Bad file"));
        given(imageService.findOneImage(any()))
                .willReturn(Mono.just(resource));

        webClient
                .get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Couldn't find alpha.png => Bad file");
        verify(imageService).findOneImage("alpha.png");
        verifyNoMoreInteractions(imageService);
    }

    @Test
    public void deleteImageShouldWork(){
        Image alphaImage = new Image("1","alpha.png");
        given(imageService.deleteImage(any())).willReturn(Mono.empty());

        webClient
                .delete().uri("/images/alpha.png")
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals(HttpHeaders.LOCATION,"/");
        verify(imageService).deleteImage(alphaImage.getName());
        verifyNoMoreInteractions(imageService);

    }
}
