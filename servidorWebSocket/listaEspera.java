package servidorWebSocket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class listaEspera {
    private static String listaEsperaDB = "listaEspera.json";

    public static void addListaEspera(String msg){

        // esta função pega na string msg e guarda no ficheiro
        // guardar de forma LIFO

        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(listaEsperaDB))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
            conteudo.append("");
        }

        String data = msg + "\n" + conteudo.toString();
        ficheiroHandler.SaveFile(data, listaEsperaDB);
    }

    public static ArrayList<messagem> pullListaEspera(String dest){
        // esta função recebe o email do destino
        // procura por todas as msg endereçadas para esse destino

        ArrayList<messagem> mensagens = new ArrayList<>();
        Gson gson = new Gson();

        try (BufferedReader br = new BufferedReader(new FileReader(listaEsperaDB))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                
                if (linha.contains(dest)){
                    messagem msg = gson.fromJson(linha, messagem.class);
                    
                    if (msg.destino.equals(dest))
                        mensagens.add(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // se não houver msg, o array será vazio
        return mensagens;
    }
}
