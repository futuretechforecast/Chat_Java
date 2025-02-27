package com.chat.Component;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.chat.Model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	private static final Key SECRET_KEY = Keys.secretKeyFor(SIGNATURE_ALGORITHM);
//	private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

	public String generateToken(UserModel model) {

		Claims claims = Jwts.claims();
		if (model != null && model.getRole().equalsIgnoreCase("Admin")) {
			claims.put("userId", model.getId());
			claims.put("Role", model.getRole());
		} else {
			claims.put("userId", model.getId());
			claims.put("Role", model.getRole());
		}

		Date now = new Date();
//		Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

		return Jwts.builder().setHeaderParam("typ", "JWT").setClaims(claims).setIssuedAt(now)
//				.setExpiration(expiryDate)
				.signWith(SECRET_KEY, SIGNATURE_ALGORITHM).compact();
	}

	public Claims verifyToken(String token) {
		return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();

	}
}