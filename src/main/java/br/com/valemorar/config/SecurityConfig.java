package br.com.valemorar.config;

import br.com.valemorar.infra.SecurityFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final SecurityFilter securityFilter;

        @Value("${cors.allowed-origins}")
        private List<String> allowedOrigins;

        public SecurityConfig(SecurityFilter securityFilter) {
                this.securityFilter = securityFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                // Liberar requisições OPTIONS (Preflight CORS)
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Rotas Web e Arquivos Estáticos do Front-end
                                                .requestMatchers(
                                                                "/",
                                                                "/entrar",
                                                                "/painel",
                                                                "/imoveis",
                                                                "/index.html",
                                                                "/auth.html",
                                                                "/painel.html",
                                                                "/busca.html",
                                                                "/*.html",
                                                                "/html/**",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/imagem/**")
                                                .permitAll()

                                                // Endpoints Públicos da API
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/anuncios", "/api/anuncios/**")
                                                .permitAll()

                                                // LIBERADO: Leitura de Endereços/Cidades no Cabeçalho e Vitrine
                                                .requestMatchers(HttpMethod.GET, "/api/enderecos", "/api/enderecos/**")
                                                .permitAll()

                                                // Swagger / Docs
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Endpoints Restritos para Administradores
                                                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PATCH, "/api/usuarios/{id}/bloquear")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                // Demais rotas exigem autenticação
                                                .anyRequest().authenticated())
                                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(allowedOrigins);
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}