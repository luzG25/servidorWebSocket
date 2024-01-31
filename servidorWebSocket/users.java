package servidorWebSocket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class users {
    

    private static String path_users_db = "./DATA/users.json";
    private static String userDetails_db = "./DATA/usersDetails.json";
    
    protected static class  User {
        String nome; 
        String email;
        String pw;
        String tokenDisp; // token do dispositivo
    }

    //metodo para obter todos os usuarios
    private static HashMap<String, User> allUsers() {
        String json = ficheiroHandler.LoadFile(path_users_db);

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

    private static String saveUsers(HashMap<String, User> userList) {
        // Configurar o GsonBuilder para criar um JsonWriter com formato bonito
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Converter o HashMap em um JsonObject
        JsonObject jsonObject = new JsonObject();
        for (HashMap.Entry<String, User> entry : userList.entrySet()) {
            String userName = entry.getKey();
            User user = entry.getValue();
            jsonObject.add(userName, gson.toJsonTree(user));
        }

        // Escrever o JsonObject em um arquivo usando um JsonWriter
        try (FileWriter fileWriter = new FileWriter(path_users_db)) {
            gson.toJson(jsonObject, fileWriter);
            System.out.println("JSON gravado com sucesso no arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
            return "00ERROR: " + e.toString();
        }
        
        return "SUCESS";

    }

    /* Guardar de maneira feia
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

        ficheiroHandler.SaveFile(jsonString, path_users_db);
        
     */
    

    public static String criar_user(String nome, String email, String password){
        //criar objeto user
        User newUser = new User();

        newUser.nome = nome;

        //TODO:implementar verificação de dominio para somente '@uta.cv' !> "00ERRORNOTUTAMAIL"
        
        //verificar se o email já existe
        HashMap<String, User> users;
        try {
            users = allUsers();
        } catch (Exception e) {
            //handle exception
            return "00ERRORSERVER: " + e.toString();
        }
        
        //confirmar se email existe
        if (users.keySet().contains(email)) {
            return "00ERRORMAILEXISTS";
        }
        

        newUser.email = email;
        
        //transformar em sha
        newUser.pw = Security_handler.gerarSHA(password);
        
        //gerar token dispositivo
        newUser.tokenDisp = tokenHandler.generateToken(email+nome, 0);

        String sessaoToken = "SUCESS";
        
        //guardar user
        users.put(email, newUser);
        String rsp = saveUsers(users);
        if (rsp.startsWith("00ERROR"))
            return "00ERRORNOTPOSSIBLECREATEUSER";

        
        //caso positivo criar token de sessao
        //sessaoToken = tokenHandler.generateToken(email, 0);

        return newUser.tokenDisp;

    }

    public static String  autenticarUser(String email, String pw){
        // carregar usuarios
        HashMap<String, User> users;
        try {
            users = allUsers();
        } catch (Exception e) {
            //handle exception
            e.printStackTrace();
            return "00ERRORSERVER: " + e.toString();
        }
        
        //converter pw em sha
        pw = Security_handler.gerarSHA(pw);
        
        //confirmar se email existe
        if (!users.keySet().contains(email)) {
            //caso negativo: "00ERROREMAILNOTEXISTS"
            return "00ERROREMAILNOTEXISTS";
        }
        
        //pw igual a db // passar para gerar token // caso contrario "00ERRORMAILORPASSWORDINCORRECT"
        if (users.get(email).pw.equals(pw)){
            //TODO:gerar token dispositivo

            //TODO:guardar token dispositivo

            //gerar token de sessão
            String sessaoToken = tokenHandler.generateToken(email, 0);

            //retornar 
            //return sessaoToken;
            return users.get(email).tokenDisp;

        } 

        return "00ERRORMAILORPASSWORDINCORRECT";
        
    }

    public static String retomarSessao(String email, String dispToken){
        // carregar usuarios
        HashMap<String, User> users;
        try {
            users = allUsers();
        } catch (Exception e) {
            // handle exception
            e.printStackTrace();
            return "00ERRORSERVER: " + e.toString();
        }
        
        //confirmar se email existe
        if (!users.keySet().contains(email)) {
            //caso negativo: "00ERROREMAILNOTEXISTS"
            return "00ERROREMAILNOTEXISTS";
        }
        
        //pw igual a db // passar para gerar token // caso contrario "00ERRORMAILORPASSWORDINCORRECT"
        if (users.get(email).tokenDisp.equals(dispToken)){
            //TODO:gerar token dispositivo
            //TODO:guardar token dispositivo

            //gerar token de sessão
            String sessaoToken = tokenHandler.generateToken(email, 0);

            //retornar 
            return sessaoToken;

        } 

        return "00ERRORTOKENINCORRECT";
    }

    // obter nome
    public static String getNome(String email)
    {
        
        HashMap<String, User> users;
        try {
            users = allUsers();
            User user = users.get(email);
            return user.nome;

            
        } catch (Exception e) {
            //handle exception
            e.printStackTrace();
            return "00ERROR:NOTFOUND";
      }
    }

    // obter contactos 
    public static void getContacts(){
        //TODO: implementar Usersdetails e procurar informação nele
        HashMap<String, User> users;
        try {
            users = allUsers();
            
            for (String user : users.keySet()){
                //TODO: armazenar contacto em um linha csv
                System.out.println(users.get(user).email + ":" + users.get(user).nome);
            }

        } catch (Exception e) {
            //handle exception
            e.printStackTrace();
            //return "00ERRORSERVER: " + e.toString();
        }

    }

} 
