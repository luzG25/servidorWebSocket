package servidorWebSocket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class listaEspera {
    private static String listaEsperaDB = "./DATA/tmp/";

    public static void addListaEspera(String msg,String destino){

        // esta função pega na string msg e guarda no ficheiro
        // guardar de forma LIFO
        destino = destino.substring(0, destino.length() - 3) + ".json";
        String path = listaEsperaDB + destino;

        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
            conteudo.append("");
        }

        String data = msg + "\n" + conteudo.toString();
        ficheiroHandler.SaveFile(data, path);
    }

    public static ArrayList<messagem> pullListaEspera(String dest){
        // esta função recebe o email do destino
        // procura por todas as msg endereçadas para esse destino

        ArrayList<messagem> mensagens = new ArrayList<>();
        Gson gson = new Gson();
        String path = listaEsperaDB + destino.substring(0, destino.length() - 3) + ".json";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                
                if (linha.contains(dest)){
                    messagem msg = gson.fromJson(linha, messagem.class);
                    
                    mensagens.add(msg);
                }

                ficheiroHandler.SaveFile("", path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // se não houver msg, o array será vazio
        return mensagens;
    }
}
