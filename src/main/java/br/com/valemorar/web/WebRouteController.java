package br.com.valemorar.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebRouteController {

    @GetMapping("/")
    public String home() {
        return "forward:/html/index.html";
    }

    @GetMapping("/entrar")
    public String entrar() {
        return "forward:/html/auth.html";
    }

    @GetMapping("/painel")
    public String painel() {
        return "forward:/html/painel.html";
    }

    @GetMapping("/imoveis")
    public String imoveis() {
        return "forward:/html/index.html";
    }

    @GetMapping({ "/index.html", "/html/index.html" })
    public RedirectView redirectIndex() {
        return redirectPermanente("/");
    }

    @GetMapping({ "/auth.html", "/html/auth.html" })
    public RedirectView redirectAuth() {
        return redirectPermanente("/entrar");
    }

    @GetMapping({ "/painel.html", "/html/painel.html" })
    public RedirectView redirectPainel() {
        return redirectPermanente("/painel");
    }

    @GetMapping({ "/busca.html", "/html/busca.html" })
    public RedirectView redirectBusca(HttpServletRequest request) {
        String query = request.getQueryString();
        String destino = query == null || query.isBlank() ? "/imoveis" : "/imoveis?" + query;
        return redirectPermanente(destino);
    }

    private RedirectView redirectPermanente(String destino) {
        RedirectView redirectView = new RedirectView(destino, true);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
