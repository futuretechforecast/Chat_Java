package com.chat.Component;

import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor implements HandlerInterceptor {

	private JwtUtil jwtUtil;

	public AuthenticationInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		String token = authHeader.substring(7); // Remove "Bearer " prefix

		try {
			Claims claims = jwtUtil.verifyToken(token);
			request.setAttribute("userId", claims.get("userId", Integer.class));
			request.setAttribute("role", claims.get("Role", String.class));
			request.setAttribute("AppId", claims.get("AppId", String.class));
			return true;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
	}
}