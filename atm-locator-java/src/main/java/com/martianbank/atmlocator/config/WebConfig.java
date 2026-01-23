package com.martianbank.atmlocator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web configuration for the ATM Locator service.
 * Configures CORS settings to allow cross-origin requests from the frontend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings to enable cross-origin requests.
     * Allows the frontend UI to communicate with this service.
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * Filter to handle trailing slash requests by forwarding them to the non-trailing-slash path.
     * This enables /api/atm/ to match the same as /api/atm.
     * Required for NGINX proxy compatibility.
     *
     * @return the trailing slash filter
     */
    @Bean
    public OncePerRequestFilter trailingSlashFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String path = new UrlPathHelper().getPathWithinApplication(request);

                if (path.length() > 1 && path.endsWith("/")) {
                    String newPath = path.substring(0, path.length() - 1);
                    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getRequestURI() {
                            return newPath;
                        }

                        @Override
                        public String getServletPath() {
                            return newPath;
                        }
                    };
                    filterChain.doFilter(wrappedRequest, response);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
}
