package br.com.valemorar.config;

import br.com.valemorar.infra.ArmazenamentoArquivos;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ArmazenamentoArquivos armazenamento;

    public WebConfig(ArmazenamentoArquivos armazenamento) {
        this.armazenamento = armazenamento;
    }

    // Serve as imagens enviadas; os nomes são UUIDs imutáveis, então podem ter cache longo
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(ArmazenamentoArquivos.PREFIXO_URL + "**")
                .addResourceLocations(armazenamento.getRaiz().toUri().toString())
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic());
    }
}
