package servidorWebSocket;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class Security_handler {

    private static String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC7EzIrMlA8BhUDYsGU7g2CcTS0D3xxUJn+y2M0SRizyoFbQ5lueylzPdFxS9KdeHzM8yrVdk7UOT/RBCb3+9N2Z68yG4uwNGWXgbAYIVhWo+PObxAXV6qxDdkychEsnj6TMg3eZZDhCBLwPsLsGkEqJHkaazboQ7haCLldb1zLOk0enbE0qFcRa70z4OitpTbEp+h4Wyb9gPQzOKWhSrh6wMoEVS9gB+K0f5iITaXUASf5nlBFG/A7EcTbsmX1AHO4PMThTXf1rIbUYtsfpfwucFwbJS2tCxng/xSmXE8NV9rg7c9qTcCeWUuXsEqN+eR/+8bKlW7er+PTc3dRn/Q/AgMBAAECggEARkZYw0dcQqAaRrJTfAnNA2JZZQaoPb0EjGWE98A5ylhZLZqASbN0h7kCM6hDDyqS3z69qYCO7kQvR+piZSexcTpT4uPcHq4gN8DdEMN99tYMHqE0D0C8e1RzcfLNptAvO4ZDFXdb7FznGF7LRHw8XINlBtqdvrSpzRCX6ZSK7EBusfHDBDR+HzDUu6tzDZRSr4+RrxPb2AWEvhIVVsmcRKBRCFK9vRGeuFW3805rH6Rcs0pqL10yL3d6ZLO52gG9AoOGC2gIPEv41hZyTCQThFdBddS99rRuuTOh/aSQiDfc+JSqh4W/BiY7weomzN7Xpb5uiB9+WR4eAp7GwAnC8QKBgQDpjhQYRK8X0tWMTZJI169a246QeAo7r+n4m4Ztnlo52OcbqfxThZsGGdhkz4TT9hw5Tw/X8dWuL05ikFGSXoe81KNjZ4NUN8PbJ6wIRkiQWtVu7tZ1mi4aH+BJIVSjk5YekgywebvL/HSwrjrY376Z3ouqobC5GafcaMk65jxJuQKBgQDNDZ2wCwvDaZBmXQZLo5i/k28I91N/sMbsFRKGctEvLM82nkW0RKmnRVRAHdU90rX6yLim1F4V6G7F5kh1Bt92J+wFoxzCW1GcUMSWUuimYESHEc9hqGD41TIvp3Fw9zXSeRnX5+JxyhySF65Q934YqOyLsCAR7MpQDX80Ck3JtwKBgQDjZLGr7zOKvnusXtpOkbDphVsIzdcoxd3Hb6UylTrDl6tsA9TofOyyw/csL9/jf3t7UL7cQVkKfoSS8nB5UhBWibGK1v3GwYSvm7CjXcrIwaeMmn2zgqngRajZitodaVR/7zLrn5p4wRPb8cbZ4P5yUXsqZsLpdVE6B8vkzMVmAQKBgQCS/EmmoaRjBU7PMauLmM+rtAGJydB+3bcaD9jhq3gretFmc3m/yOBb2SSVeU0cLejoxe0nGWAAEEcncPVLfgu85M0ZFV1OLKuJg5QHtZKzgJpev/OKK2qntBtvcXWt5mccYlaWH9CAhMKKLpNz0pT/FEfLsgqrDrPPQn2GTkSIbQKBgQDiVpWB4yLhgxptVeTCSAIIKk3nzL3930A2J4PJQLZBVPX/SR7kxkVnKCaFV8o6nEtn6KrcbFKtyXXBuU/IMqtclJLzjQlaJ461haVwRVr7HdA7FUj7MpYDstrjFNHOQPxe9XDM8xDEnluK5R99ADh5gfB5Qn7I6BRiBi2nmAlQOQ==";

    //Definir o algoritimo de SHA
    //Para melhor segurança utilizar o sha256
    private static String sha_algoritimo = "SHA-256"; //disponivel o SHA-1, SHA-256

     
    /* 
    public static void main(String[] args) {
        String palavra = "exemplo";

        try {
            String sha1Hash = gerarSHA(palavra);
            System.out.println("SHA-1 Hash: " + sha1Hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */
    

    public static String gerarSHA(String palavra) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(sha_algoritimo);
            byte[] bytes = md.digest(palavra.getBytes());

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "00ERROR: "+ e.toString();
        }
        
    }
    public String encrypt(String text, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String encryptedText, PrivateKey key) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PublicKey stringToPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey stringToPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Gera um par de chaves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Tamanho da chave: 2048 bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obtém a chave privada e pública do par de chaves
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Converte as chaves para formatos de string
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        // Imprime as chaves
        System.out.println("Chave Privada:");
        System.out.println(privateKeyString);
        System.out.println();
        System.out.println("Chave Pública:");
        System.out.println(publicKeyString);
    }
}