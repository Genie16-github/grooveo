package com.kl.grooveo.base.config.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kl.grooveo.boundedContext.member.entity.Member;
import com.kl.grooveo.boundedContext.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final String ROLE_ADMIN = "admin";
	private final String ROLE_USER = "user";

	@Override
	public UserDetails loadUserByUsername(String username) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		Member member = memberRepository.findByUsername(username).orElseThrow(
			() -> new UsernameNotFoundException("username(%s) not found".formatted(username)));

		if (ROLE_ADMIN.equals(username)) { // TODO: 추가 설정 필요
			authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
		} else {
			authorities.add(new SimpleGrantedAuthority(ROLE_USER));
		}

		return new User(member.getUsername(), member.getPassword(), authorities);
	}
}
