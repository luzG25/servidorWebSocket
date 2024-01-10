package servidorWebSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ficheiroHandler {
    
    public static String LoadFile(String caminhoArquivo) {
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "00ERROR: " + e.toString();
        }
        return conteudo.toString();
    }

    public static boolean SaveFile(String json, String caminhoArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write(json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
