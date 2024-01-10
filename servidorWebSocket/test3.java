package servidorWebSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class test3 {

    private static final String path_users_db = "caminho/do/seu/arquivo.json";

    public static void main(String[] args) {
        HashMap<String, users.User> userList = new HashMap<>();
        // Suponha que você tenha uma instância da classe User chamada 'user'
        // Adicione alguns dados de exemplo ao userList para fins de demonstração
        userList.put("usuario1", new users.User("usuario1", "senha1"));
        userList.put("usuario2", new users.User("usuario2", "senha2"));

        // Chame o método para gravar o JSON no arquivo
        saveToJsonFile(userList);
    }

    private static void saveToJsonFile(HashMap<String, User> userList) {
        // Converter o HashMap em um JsonObject
        JsonObject jsonObject = new JsonObject();
        for (HashMap.Entry<String, User> entry : userList.entrySet()) {
            String userName = entry.getKey();
            User user = entry.getValue();
            jsonObject.add(userName, new Gson().toJsonTree(user));
        }

        // Converter o JsonObject em uma string JSON
        String jsonString = jsonObject.toString();

        try (FileWriter fileWriter = new FileWriter(path_users_db)) {
            // Escrever a string JSON no arquivo
            fileWriter.write(jsonString);
            System.out.println("JSON gravado com sucesso no arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
