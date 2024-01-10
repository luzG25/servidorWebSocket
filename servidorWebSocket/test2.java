package servidorWebSocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;

public class test2 {
    public static void main(String[] args) {
        //String json = "{\"user1\":{\"name\":\"John\",\"age\":25},\"user2\":{\"name\":\"Alice\",\"age\":30},\"user3\":{\"name\":\"Bob\",\"age\":22}}";
        //String json = "";
        String json = ficheiroHandler.LoadFile("./users.json");

        Gson gson = new Gson();
        Map<String, User> userList = convertJsonToList(json);

        // Agora você pode iterar sobre a lista e acessar os usuários como HashMaps
        for (String user : userList.keySet()) {
            System.out.println("Name: " + userList.get(user).nome + ", Email: " + userList.get(user).email);
        }
    }

    /* 
    private class User {
        String name;
        int age;
    }
    */

    private static class  User {
        String nome; 
        String email;
        String pw;
    }

    private static Map<String, User> convertJsonToList(String json) {
        HashMap<String, User> userList = new HashMap<>();

        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String userName = entry.getKey();
            JsonObject userJson = entry.getValue().getAsJsonObject();

            User user = new Gson().fromJson(userJson, User.class);
            //userList.add(userName, user);

            userList.put(userName, user);
        }

        return userList;
    }

    
}

