package br.com.valemorar.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebRouteController {

    // Rotas amigáveis: encaminham internamente para os arquivos em /html/**.
    // Os arquivos em /html/** NÃO podem ser mapeados aqui, senão o forward cai no redirect e entra em loop.
    @GetMapping({ "/", "/imoveis" })
    public String home() {
        return "forward:/html/index.html";
    }

    @GetMapping("/entrar")
    public String entrar() {
        return "forward:/html/auth.html";
    }

    // Página de detalhes do anúncio; o ID é lido da URL pelo próprio JS da página
    @GetMapping("/anuncios/{id}")
    public String anuncio(@PathVariable String id) {
        return "forward:/html/anuncio.html";
    }

    @GetMapping("/painel")
    public String painel() {
        return "forward:/html/painel.html";
    }

    // Compatibilidade com URLs legadas (*.html) via redirecionamento permanente, preservando a query string
    @GetMapping("/index.html")
    public RedirectView legadoIndex(HttpServletRequest request) {
        return redirectPermanente("/", request);
    }

    @GetMapping("/auth.html")
    public RedirectView legadoAuth(HttpServletRequest request) {
        return redirectPermanente("/entrar", request);
    }

    @GetMapping("/painel.html")
    public RedirectView legadoPainel(HttpServletRequest request) {
        return redirectPermanente("/painel", request);
    }

    @GetMapping("/busca.html")
    public RedirectView legadoBusca(HttpServletRequest request) {
        return redirectPermanente("/imoveis", request);
    }

    private RedirectView redirectPermanente(String destino, HttpServletRequest request) {
        String query = request.getQueryString();
        RedirectView redirectView = new RedirectView(query == null ? destino : destino + "?" + query, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
