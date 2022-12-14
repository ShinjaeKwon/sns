package com.sjk.sns.configuration.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sjk.sns.model.User;
import com.sjk.sns.service.UserService;
import com.sjk.sns.util.JwtTokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final String key;
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith("Bearer ")) {
			log.error("Error occurs while getting header, header is null or invalid");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String token = header.split(" ")[1].trim();
			if (JwtTokenUtils.isExpired(token, key)) {
				log.error("Key is expired");
				filterChain.doFilter(request, response);
				return;
			}
			String username = JwtTokenUtils.getUserName(token, key);
			User user = userService.loadUserByUserName(username);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user, null, user.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (RuntimeException e) {
			log.error("Error occurs while validating. {}", e.toString());
			filterChain.doFilter(request, response);
			return;
		}
		filterChain.doFilter(request, response);
	}

}
