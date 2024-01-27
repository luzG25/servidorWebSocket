package servidorWebSocket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
