package com.kl.grooveo.base.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.kl.grooveo.base.handler.UserLoginFailHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final UserLoginFailHandler userLoginFailHandler;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.formLogin(
				formLogin -> formLogin
					.loginPage("/usr/member/login")
					.failureHandler(userLoginFailHandler)
					.defaultSuccessUrl("/")
			)

			.oauth2Login(
				oauth2Login -> oauth2Login
					.loginPage("/usr/member/login")
			)
			.logout(
				logout -> logout
					.logoutUrl("/usr/member/logout")
			);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}