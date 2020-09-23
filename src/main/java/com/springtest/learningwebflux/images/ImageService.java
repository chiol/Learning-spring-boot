package com.springtest.learningwebflux.images;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    private static String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;

    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
    }

    public Flux<Image> findAllImages() {
        return imageRepository.findAll();
    }
    public Mono<Resource> findOneImage(String filename) {
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(
                        "file:" + UPLOAD_ROOT + "/" + filename));
    }

    public Mono<Void> createImage(Flux<FilePart> files) {
        return files.flatMap(file -> {
                    Mono<Image> saveDatabaseImage = imageRepository.save(
                            new Image(
                                    UUID.randomUUID().toString(),
                                    file.filename()
                            ));
                    Mono<Void> copyFile = Mono.just(
                            Paths.get(UPLOAD_ROOT,file.filename())
                            .toFile())
                            .log("createImage-picktarget")
                            .map(destFile -> {
                                try {
                                    destFile.createNewFile();
                                    return destFile;
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .log("createImage-newfile")
                            .flatMap(file::transferTo)
                            .log("createImage-copy");
                    return Mono.when(saveDatabaseImage,copyFile);
                }).then();
    }

    public Mono<Void> deleteImage(String filename) {
        Mono<Void> deleteDatabaseImage = imageRepository.findByName(filename)
                .flatMap(imageRepository::delete);
        Mono<Void> deleteFile = Mono.fromRunnable(() -> {
            try {
                Files.deleteIfExists(
                        Paths.get(UPLOAD_ROOT,filename));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return Mono.when(deleteDatabaseImage,deleteFile).then();
    }
}
