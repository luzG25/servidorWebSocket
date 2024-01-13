import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class TokenGenerator {

    private static final String SECRET_KEY = "seu_segredo"; // Troque pelo seu próprio segredo

    public static String generateToken(String subject, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static void main(String[] args) {
        String token = generateToken("usuario123", 3600000); // Token válido por 1 hora (3600000 milissegundos)
        System.out.println("Token: " + token);
    }
}
