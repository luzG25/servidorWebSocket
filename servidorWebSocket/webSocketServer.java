package servidorWebSocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class webSocketServer extends WebSocketServer {

    private String serverName = "ROOT";

    private class UserSessao {
        String email;
        WebSocket client;
        String sessaoToken;
    }

    private void addConexao(messagem msg, WebSocket conn){
        if (!msg.token.startsWith("00ERROR")){
            UserSessao us = new UserSessao();
            us.email = msg.emissor;
            us.client = conn;
            us.sessaoToken = msg.token;

            conexoes.add(us);
        }
    }

    private void killConexao(WebSocket conn){
        for (UserSessao conexao: conexoes){

            InetSocketAddress remoteAddress = conexao.client.getRemoteSocketAddress();
            String conexaoIP = remoteAddress.getAddress().getHostAddress();
            remoteAddress = conn.getRemoteSocketAddress();
            String clientIP = remoteAddress.getAddress().getHostAddress();
            
            if (conexaoIP.equals(clientIP)) {
                conexoes.remove(conexao);
                System.out.println(conexao.email + "-" + clientIP + "-> Fechou a Ligação");
            }
        }
    }

    List<UserSessao> conexoes = new ArrayList<>();

    public webSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
        String clientIP = remoteAddress.getAddress().getHostAddress();
        System.out.println("Nova conexão: " + clientIP);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
        String clientIP = remoteAddress.getAddress().getHostAddress();
        killConexao(conn);
        System.out.println("Conexão fechada: " + clientIP + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
        String clientIP = remoteAddress.getAddress().getHostAddress();
        System.out.println(message);

        //System.out.println("Mensagem recebida de " + clientIP + ": " + message);

        //TODO: Decriptografar mensagem
        try {
            message = Security_handler.decrypt(message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //ao receber mensgem será em json
        //converter json para objeto mensagem
        Gson gson = new Gson();
        messagem msg = gson.fromJson(message, messagem.class);

        System.out.println("Mensagem recebida de " + clientIP + ": " + message);

        if (msg.tipo.equals("msg")) {
            //se for deste tipo
            //quer dizer que é mensagem para algum usuario

            // adicionar hora gmt
            msg.data = hora.getTimeString();
            msg.aux2 = "DM"; // para corrigir erro; TODO: Indetificador de DM ou conversa de grupo
            // se for preciso mais alterações na mensagem
            //

            message = gson.toJson(msg);
            
            //TODO: criptografar mensagem 
            
            if (msg.destino.equals("geral@uta.cv"))
            {
                // Redistribuir a mensagem para todos os outros clientes (broadcast)
                // caso for direcionado para geral@uta.cv
                broadcast(message); // 
            } 
            //TODO: mensagens de grupo

            else {
                broadcast(message); // para fins de text
                //procurar se o usuario esta disponivel
                boolean Ndisponivel = true;
                for (UserSessao conexao: conexoes){

                    if (conexao.email.equals(msg.destino)) {
                        Ndisponivel = false;

                        send(conexao.client, message); // enviar msg ao destino
                    }
                }
                
                //caso contrario, adicionar na lista de espera
                if (Ndisponivel) {
                    // guardar na lista de espera
                    listaEspera.addListaEspera(message, msg.destino);

                }
            }
        }
        
        // se a mensagem for de autenticação
        else if (msg.destino.equals("LoginService")){

            if (msg.tipo.equals("Login")) {
                
                msg.token = users.autenticarUser(msg.emissor, msg.aux1);
                msg.aux1 = users.getNome(msg.emissor); // TODO: depois tirar essa informação diretamente de autenticarUser
                /// caso o login der negativo, token.startsWith("00ERROR") == TRUE
                // aqui pode se fazer log de quantas vezes foi feito o login na conta do usuario
                // contagens de tentativa de iniciar a sessão etc...

                //adicionar conexao na lista de conexoes
                addConexao(msg, conn);
            }

            else if (msg.tipo.equals("RetomarSessao")) {
                //confirmar token dispositivo
                //msg.token = users.retomarSessao(msg.emissor, msg.aux1);
                // caso o login der negativo, token.startsWith("00ERROR") == TRUE 
                
                //adicionar conexao na lista de conexoes
                addConexao(msg, conn);
            }

            else if (msg.tipo.equals("Incricao")) {
                //Increver usuario na database
                msg.token = users.criar_user(msg);
                msg.aux1 = msg.aux2;
                //adicionar conexão na lista de conexoes
                //addConexao(msg, conn);
            }
            
            msg.destino = msg.emissor;
            msg.emissor = "LoginService";
            msg.aux2 = null;
            send(conn, gson.toJson(msg));
            
        }

        //TODO: verificar token -> caso negativo mandar msg erro

        // implementar search no DB
        else if (msg.tipo.equals("GET")){
            
            //o query do search estara no msg

            // obter mensagens não entregues
            if (msg.msg.equals("getMensagens")){
                ArrayList<messagem> mensagens = listaEspera.pullListaEspera(msg.emissor);
                
                if (mensagens.size() > 0){
                    for (messagem ms: mensagens){
                        //TODO: criptografar msg
                        ms.aux2 = "FROMLISTADEESPERA";
                        String msString =gson.toJson(ms);
                        
                        send(conn, msString);
                    }
                }
            }

            //obter nome de um contacto
            //getName:Fulano  
            if (msg.msg.startsWith("GETNAME"))
            {
                String email = msg.msg.split(":")[1];
                String nome = users.getNome(email);
                
                msg.emissor = serverName;
                msg.msg = "GETNAME";
                msg.destino = msg.emissor;
                msg.aux1 = email;
                msg.aux2 = nome;

                send(conn, gson.toJson(msg));
            }

            //TODO: obter todos os contactos
            else if (msg.msg.equals("GETCONTACTS")) 
            {
                //obter todos os contactos
                msg.tipo = msg.msg;
                msg.msg = users.getContacts(msg.emissor); //email:nome:curso;
                msg.destino = msg.emissor;
                msg.emissor = serverName;
                msg.aux1 = null;
                msg.aux2 = null;
                send(conn, gson.toJson(msg));
                System.out.println(gson.toJson(msg));

                
            }

            //TODO: obter informação de um contacto

            //TODO: fazer pesquisar personalizada 
            // pelo nome, pelo curso, pela categoria

        }       
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //System.err.println("Erro ocorrido em uma conexão: " + clientIP + " " + ex);
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado na porta " + getPort());
    }

    public void send(WebSocket conn, String msg)
    {
        try {
            msg = Security_handler.encrypt(msg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn.send(msg);
    }

    public void broadcast(String message) {
        Collection<WebSocket> connections = getConnections();
        synchronized (connections) {
            for (WebSocket client : connections) {
                send(client, message);
            }
        }
    }
    
}


/*
public synchronized void sendMessage(WebSocket client, String message) {
    client.send(message);
} 

...........

private final ReentrantLock lock = new ReentrantLock();

public void sendMessage(WebSocket client, String message) {
    lock.lock();
    try {
        client.send(message);
    } finally {
        lock.unlock();
    }
}
*/