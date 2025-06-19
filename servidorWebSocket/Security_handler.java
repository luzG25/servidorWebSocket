package servidorWebSocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

public class Security_handler {

    private static String sha_algoritimo = "SHA-256";

    public static String gerarSHA(String palavra) {
        try {
            MessageDigest md = MessageDigest.getInstance(sha_algoritimo);
            byte[] bytes = md.digest(palavra.getBytes());

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "00ERROR: " + e.toString();
        }
    }

       private static final String RSA = "RSA";

    private static KeyPair keyPair;

    static {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA);
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para pegar chave pública como objeto
    public static PublicKey getPublicKeyObject() {
        return keyPair.getPublic();
    }



    public static PublicKey getPublicKey() {
    return keyPair.getPublic();
}


    public static String encryptWithPublicKey(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptWithPrivateKey(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
    public static void main(String[] args) {
        try {
            String originalMessage = "Hello, world!";

            // Obter chave pública (em objeto)
            PublicKey publicKey = keyPair.getPublic();

            // Criptografar mensagem usando chave pública
            String encryptedString = encryptWithPublicKey(originalMessage, publicKey);

            // Decriptografar mensagem usando chave privada
            String decryptedString = decryptWithPrivateKey(encryptedString);

            System.out.println("Original: " + originalMessage);
            System.out.println("Encrypted (Base64): " + encryptedString);
            System.out.println("Decrypted: " + decryptedString);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
