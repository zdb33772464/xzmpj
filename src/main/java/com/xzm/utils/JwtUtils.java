package com.xzm.utils;

import com.xzm.domain.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Leon.Yu
 * @date 2017/9/21
 */
public class JwtUtils {
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    /**
     * remove 'Bearer ' string
     *
     * @param authorizationHeader
     * @return
     */
    public static String getRawToken(String authorizationHeader) {
        return authorizationHeader.substring(AUTHORIZATION_HEADER_PREFIX.length());
    }

    public static String getTokenHeader(String rawToken) {
        return AUTHORIZATION_HEADER_PREFIX + rawToken;
    }

    public static boolean validate(String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(AUTHORIZATION_HEADER_PREFIX);
    }

    public static String getAuthorizationHeaderPrefix() {
        return AUTHORIZATION_HEADER_PREFIX;
    }
    
    private static final long   EXPIRATIONTIME=1000*60*60*24*30;
	private static final String SECRET_KEY="yourkey1";
	private static final String TOKEN_PREFIX="prefix";
	private static final String HEADER="JAuthorization";
	
	/*public SysUser parseToken(String token){
		try{
			Claims body=Jwts.parser()
					.setSigningKey(SECRET_KEY)
					.parseClaimsJws(token.substring(TOKEN_PREFIX.length()))
					.getBody();
			SysUser u=new SysUser();
			u.setUsername(body.getSubject());
	        u.setId(1L);
	        u.setRole("user");
	        return u;
		}catch (JwtException e){
			return null;
		}
	}*/
	
	public String generateToken(HttpServletResponse response, SysUser user) {
		Claims claims=Jwts.claims().setSubject(user.getUsername());
		claims.put("userId", user.getId() + "");
	    claims.put("role", user.getRoles());
	    String token=TOKEN_PREFIX+Jwts.builder().setClaims(claims)
					.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
					.compact();
	    response.addHeader(HEADER, token);
		return token;
	}
	public static String generateToken(String userName){
		Claims claims=Jwts.claims().setSubject(userName);
		return TOKEN_PREFIX + Jwts.builder().setClaims(claims).
				signWith(SignatureAlgorithm.HS512, SECRET_KEY).
				compact();
	}
}