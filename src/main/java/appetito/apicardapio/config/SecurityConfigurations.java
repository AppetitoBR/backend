package appetito.apicardapio.config;

import appetito.apicardapio.security.SecurityFilter;
import appetito.apicardapio.service.AppUserDetailsService;
import appetito.apicardapio.service.DashboardUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

import java.util.Arrays;
import java.util.List;

/**
 * Classe de configuração de segurança da aplicação.
 * <p>
 * Configura o filtro de autenticação JWT, CORS, políticas de sessão, permissões de endpoints e dois
 * `DaoAuthenticationProvider` diferentes: um para usuários do Dashboard e outro para clientes do App.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private DashboardUserDetailsService dashboardUserDetailsService;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    /**
     * Configura a cadeia de filtros de segurança para a aplicação.
     * <p>
     * - Desabilita CSRF.
     * - Define a política de sessão como STATELESS.
     * - Permite requisições públicas para endpoints de login, cadastro, cardápio e documentação Swagger.
     * - Aplica filtro JWT antes do filtro padrão de autenticação.
     *
     * @param http objeto de configuração do Spring Security.
     * @return SecurityFilterChain configurada.
     * @throws Exception caso haja falha na configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login/dashboard").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login/app").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cliente/cadastrar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/cadastrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/estabelecimento/*/cardapio").permitAll()
                        .requestMatchers(HttpMethod.GET, "/produtos/*/imagem").permitAll()
                        .requestMatchers(HttpMethod.GET, "/estabelecimento/*").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configuração global de CORS para permitir requisições de origens específicas.
     *
     * @return CorsConfigurationSource com regras definidas.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://appetito.stargateit.com.br"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "content-type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean responsável por codificar senhas usando o algoritmo BCrypt.
     *
     * @return Instância de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider de autenticação para usuários do Dashboard.
     *
     * @return DaoAuthenticationProvider configurado.
     */
    @Bean
    public DaoAuthenticationProvider dashboardAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(dashboardUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Provider de autenticação para clientes do App.
     *
     * @return DaoAuthenticationProvider configurado.
     */
    @Bean
    public DaoAuthenticationProvider appAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
