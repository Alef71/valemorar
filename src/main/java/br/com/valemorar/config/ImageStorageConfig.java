package br.com.valemorar.config;

import br.com.valemorar.infra.ImageStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageStorageConfig implements WebMvcConfigurer {

    private final ImageStorageService imageStorageService;

    public ImageStorageConfig(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(imageStorageService.getUploadDirectory().toUri().toString());
    }
}
