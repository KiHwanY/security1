package com.cos.security1.config;

//1.코드받기(인증) 2.엑세스토큰(권한)
// 3.사용자 프로필 정보를 가져오고
// 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
// 4-2. (이메일,전화번호,이름,아이디) 쇼핑몰->(집 주소), 백화점몰 --> (vip등급, 일반등급) 등 추가적인 구성이 필요하다.

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
//secured 어노테이션 활성화 , preAuthorize 어노테이션 활성화
public class SecurityConfig { // 스프링 필터 역할

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소!!
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")// login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") //구글 로그인이 완료된 뒤의 후처리가 필요함. Tip. 코드x,(액세스토큰 + 사용자프로필정보 o)
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
                .and().and().build();

    }
}

