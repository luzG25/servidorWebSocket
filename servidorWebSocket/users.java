package servidorWebSocket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class users {
    

    private static String path_users_db = "./DATA/users.json";
    private static String userDetails_db = "./DATA/usersDetails.json";
    
    protected static class  User {
        // detalhes de segurança
        String nome; 
        String email;
        String pw;
        String tokenDisp; // token do dispositivo

        // adicionar os atributos/detalhes adicionais de cada usuario
        String curso;
        String funcao; 
    }

    //metodo para obter todos os usuarios
    private static HashMap<String, User> allUsers(String path) {
        String json = ficheiroHandler.LoadFile(path);

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

    private static String saveUsers(HashMap<String, User> userList, String path) {
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
        try (FileWriter fileWriter = new FileWriter(path)) {
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
    

    public static String criar_user(messagem msg){
        //criar objeto user
        User newUser = new User(); //objeto com dados de acesso/seguranca
        
        //Verificação de caracteres inadequados
        //esta parte é para verificação
        // caso algum atributo tiver com caracter que dá problema ao sistema
        
        // Lista de caracteres ilegais
        List<Character> caracteresIlegais = Arrays.asList(':', '#', '/', '$', '%', '&', '*', '+', ';');
        
        // Lista de atributos a serem verificados
        List<String> atributos = Arrays.asList(msg.msg, msg.aux2, msg.emissor);
        // Verificação de caracteres ilegais
        for (String atributo : atributos) {
            for (char caractereIlegal : caracteresIlegais) {
                if (atributo.contains(String.valueOf(caractereIlegal))) {
                    return "00ERRORCARACTSINVALID";
                }
            }
        }

        //guardar detalhes
        User newUserDets = new User(); // objeto com dados adicionais
        newUserDets.nome = msg.aux2;
        newUserDets.curso = msg.msg; // se tiver dados adicionais, incorporar elas no atributo msg
        
        //transformar em sha
        newUser.pw = Security_handler.gerarSHA(msg.aux1);
        
        //gerar token dispositivo
        newUser.tokenDisp = tokenHandler.generateToken(newUser.email+newUserDets.nome, 0);
        
        //TODO:implementar verificação de dominio para somente '@uta.cv' !> "00ERRORNOTUTAMAIL"
        
        //verificar se o email já existe
        // verificar o ficheiro de segurança
        HashMap<String, User> users;
        HashMap<String, User> usersDets;
        try {
            users = allUsers(path_users_db);
            usersDets = allUsers(userDetails_db);
        } catch (Exception e) {
            //handle exception
            return "00ERRORSERVER: " + e.toString();
        }
        
        //confirmar se email existe
        if (users.keySet().contains(msg.emissor)) {
            return "00ERRORMAILEXISTS";
        }
        

        newUser.email = msg.emissor;
        
        String sessaoToken = "SUCESS";
        
        //guardar user (dados de acesso)
        users.put(newUser.email, newUser);
        usersDets.put(newUser.email, newUserDets);
        String rsp = saveUsers(usersDets, userDetails_db);
        rsp = saveUsers(users, path_users_db);
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
            users = allUsers(path_users_db);
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
            users = allUsers(path_users_db);
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
            users = allUsers(userDetails_db);
            User user = users.get(email);
            return user.nome;

            
        } catch (Exception e) {
            //handle exception
            e.printStackTrace();
            return "00ERROR:NOTFOUND";
      }
    }

    // obter contactos 
    public static String getContacts(String emissor){
        String out = "";
        //TODO: implementar Usersdetails e procurar informação nele
        HashMap<String, User> users;
        try {
            users = allUsers(userDetails_db);
            
            for (String user : users.keySet()){
                //armazenar contacto em um linha separado por ":" e delitado por ";"

                if (!user.equals(emissor)) // obter todos os comentarios, excepto requisitor da busca
                    out += users.get(user).email + ":" + users.get(user).nome + ":" + users.get(user).curso + ";";
                
                System.out.println(users.get(user).email + ":" + users.get(user).nome + ":" + users.get(user).curso);

                // para obter conctato: split("/")
                //atributos do contacto: split(":")
            }

        } catch (Exception e) {
            //handle exception
            e.printStackTrace();
            //return "00ERRORSERVER: " + e.toString();
        }

        return out;

    }

} 
