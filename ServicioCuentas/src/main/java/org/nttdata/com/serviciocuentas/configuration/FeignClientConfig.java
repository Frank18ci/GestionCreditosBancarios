package org.nttdata.com.serviciocuentas.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth instanceof JwtAuthenticationToken jwtAuth){
                String token = jwtAuth.getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
