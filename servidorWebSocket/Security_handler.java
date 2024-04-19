package servidorWebSocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Security_handler {

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

    private static final String ALGORITHM = "AES";
    private static final String KEY = "5b0687ee567c9fe8"; // Chave de 16 caracteres para AES-128

    public static String encrypt(String valueToEncrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(valueToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedValue) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        try {
            // Mensagem original
            String originalMessage = "Hello, world!";

            // Criptografar a mensagem
            String encryptedString = encrypt(originalMessage);

            // Descriptografar a mensagem
            String decryptedString = decrypt(encryptedString);

            // Imprimir resultados
            System.out.println("Original: " + originalMessage);
            System.out.println("Encrypted: " + encryptedString);
            System.out.println("Decrypted: " + decryptedString);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


}