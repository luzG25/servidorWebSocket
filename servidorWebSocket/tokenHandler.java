package servidorWebSocket;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class tokenHandler {

    private static final String SECRET_KEY = "CHATUTA"; // Troque pelo seu próprio segredo

    // Método para gerar um token JWT
    public static String generateToken(String subject, long expirationMillis) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationMillis);
            //Date expiration = new Date(Long.MAX_VALUE); //  sem data de expiração

            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withSubject(subject)
                    .withIssuedAt(now)
                    .withExpiresAt(expiration)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return "00ERROR: " + e.toString();
        }
        
    }

    // Método para verificar e obter as informações do token JWT
    public static Claim verifyToken(String token, String claimKey) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
            return decodedJWT.getClaim(claimKey);
        } catch (JWTDecodeException e) {
            // A exceção será lançada se o token for inválido
            return null;
        }
    }

    public static void main(String[] args) {
        String token;
        
        token = generateToken(SECRET_KEY, 3000);
            //token = "93j wdhgfun";
        System.out.println(token);
        
        
    }
}
