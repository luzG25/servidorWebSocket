package servidorWebSocket;

import org.java_websocket.WebSocket;


public class users {
    // em casos de erro, retornar 0 ou "0ERRO:"


    //*criar classe ou metodo para obter todos os usuarios
    
    private class  User {
        String nome; 
        String email;
        String password;
    }

    public String criar_user(String nome, String email, String password){
        //criar objeto user
        User newUser = new User();

        newUser.nome = nome;

        //implementar verificação de dominio para somente '@uta.cv' !> "00ERRORNOTUTAMAIL"
        newUser.email = email;
        
        //transformar em sha128
        newUser.password = password;

        String sessaoToken = "00ERROR";
        //try...catch
        //guardar user
        // caso der erro:
        sessaoToken = "00ERRORNOTPOSSIBLECREATEUSER"

        
        
        //caso positivo criar token de sessao

        return sessaoToken;

    }

    public String  autenticarUser(String email, String pw){
        //confirmar se email existe
        //caso negativo: "00ERROREMAILNOTEXISTS"

        //converter pw em sha128
        //pw igual a db // confirmar pw // caso contrario "00ERRORMAILORPASSWORDINCORRECT"

        //gerar token dispositivo
        //guardar token dispositivo

        //gerar token de sessão
        String sessaoToken = "";

        //retornar 
        // caso negativo retornar 0
        
        return sessaoToken;
    }

    public String retomarSessao(String email, String dispToken){
        // confirmar se email existe "00ERROREMAILNOTEXISTS"

        // confirmar se token dispostivo igual, > "00ERRORTOKENINCORRECT"

        // gerar token de sessao
        String sessaoToken = "";

        //retornar
        return sessaoToken;


    }


} 
