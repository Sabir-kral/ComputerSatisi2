package az.computer.demo.Entity;

import az.computer.demo.Service.CustomUserDetailsService;
import az.computer.demo.Utility.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Pre-flight (CORS OPTIONS) sorğularına tam icazə
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Açıq Endpoint-lər (Autentifikasiya tələb olunmur)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/resendOTP").permitAll()
                        .requestMatchers("/api/users/verify").permitAll()
                        .requestMatchers("/api/upload/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // İctimai baxış Endpoint-ləri
                        .requestMatchers(HttpMethod.POST, "/api/customers").permitAll() // Qeydiyyat
                        .requestMatchers(HttpMethod.GET, "/api/customers/v2").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/computers/**").permitAll()
                        .requestMatchers("/api/admin/check").permitAll()

                        // YALNIZ ADMIN üçün olan endpoint-lər
                        .requestMatchers(HttpMethod.POST, "/api/computers/add").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/computers/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/computers/**").hasAnyAuthority("ROLE_ADMIN","ROLE_CUSTOMER")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN")

                        // MÜŞTƏRİ VƏ ADMİN üçün autentifikasiya tələb olunan endpoint-lər
                        .requestMatchers("/api/customers/profile/**").authenticated()
                        .requestMatchers("/api/customers/profile").authenticated()
                        .requestMatchers("/api/customers/v1").authenticated()
                        .requestMatchers("/api/customers/selling").authenticated()
                        .requestMatchers("/api/customers/buy").authenticated()
                        .requestMatchers("/api/customers/delete").authenticated()
                        .requestMatchers("/api/customers/contact/**").authenticated()

                        // Ödəniş və Səbət əməliyyatları (Mütləq autentifikasiya olunmalıdır)
                        .requestMatchers("/api/payments/**").authenticated()
                        .requestMatchers("/api/cart/**").authenticated()

                        // Qalan bütün sorğular təhlükəsizlik üçün autentifikasiya tələb edir
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Həm local, həm də server domenlərinə tam icazə
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}