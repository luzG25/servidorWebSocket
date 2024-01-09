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

    public boolean criar_user(String nome, String email, String password){
        //criar objeto user
        User newUser = new User();

        newUser.nome = nome;

        //implementar verificação de dominio para somente '@uta.cv'
        newUser.email = email;
        
        //transformar em sha128
        newUser.password = password;

        //guardar user


        return true;

    }

    public String  autenticarUser(String email, String pw){
        //confirmar se email existe

        //converter pw em sha128
        //pw igual a db // confirmar pw

        //gerar token dispositivo
        //guardar token dispositivo

        //gerar token de sessão
        String sessaoToken = "";

        //retornar 
        // caso negativo retornar 0
        
        return sessaoToken;
    }

    public String retomarSessao(WebSocket client, String email, String dispToken){
        // confirmar se email existe

        // confirmar se token dispostivo igual

        // gerar token de sessao
        String sessaoToken = "";

        //retornar
        return sessaoToken;


    }


} 
