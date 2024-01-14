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
        System.out.println("Conexão fechada: " + clientIP + " Code: " + code + " Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
        String clientIP = remoteAddress.getAddress().getHostAddress();

        //System.out.println("Mensagem recebida de " + clientIP + ": " + message);

        //TODO: Decriptografar mensagem

        //ao receber mensgem será em json
        //converter json para objeto mensagem
        Gson gson = new Gson();
        messagem msg = gson.fromJson(message, messagem.class);

        System.out.println("Mensagem recebida de " + clientIP + ": " + message);
        
        // se a mensagem for de autenticação
        if (msg.destino == "LoginService"){

            if (msg.tipo.equals("Login")) {
                
                msg.token = users.autenticarUser(msg.emissor, msg.aux1);
                /// caso o login der negativo, token.startsWith("00ERROR") == TRUE
                // aqui pode se fazer log de quantas vezes foi feito o login na conta do usuario
                // contagens de tentativa de iniciar a sessão etc...

                //adicionar conexao na lista de conexoes
                addConexao(msg, conn);
            }

            else if (msg.tipo.equals("RetomarSessao")) {
                //confirmar token dispositivo
                msg.token = users.retomarSessao(msg.emissor, msg.aux1);
                // caso o login der negativo, token.startsWith("00ERROR") == TRUE 
                
                //adicionar conexao na lista de conexoes
                addConexao(msg, conn);
            }

            else if (msg.tipo == "Incricao") {
                //Increver usuario na database
                msg.token = users.criar_user(msg.aux2 ,msg.emissor, msg.aux1);

                //adicionar conexão na lista de conexoes
                addConexao(msg, conn);
            }
            
            msg.destino = msg.emissor;
            msg.emissor = "root";
            msg.aux1 = null;
            msg.aux2 = null;
            conn.send(gson.toJson(msg));
            
        }

        //TODO: verificar token -> caso negativo mandar msg erro

        // implementar search no DB
        else if (msg.tipo.equals("Get")){
            
            

            //o query do search estara no msg

            // obter mensagens não entregues
            if (msg.msg.equals("getMensagens")){
                ArrayList<messagem> mensagens = listaEspera.pullListaEspera(msg.emissor);
                
                if (mensagens.size() > 0){
                    for (messagem ms: mensagens){
                        //TODO: criptografar msg
                        String msString =gson.toJson(ms);
                        
                        conn.send(msString);
                    }
                        
                }
            }

            //TODO: obter todos os contactos 

            //TODO: obter informação de um contacto

            //TODO: fazer pesquisar personalizada 
            // pelo nome, pelo curso, pela categoria


        }

        else if (msg.tipo.equals("msg")) {
            //se for deste tipo
            //quer dizer que é mensagem para algum usuario

            //procurar se o usuario esta disponivel
            boolean Ndisponivel = true;
            for (UserSessao conexao: conexoes){

                if (conexao.email.equals(msg.destino)) {
                    Ndisponivel = false;

                    // se houver alguma alteração na mensagem enviada, aqui é a linha
                    String RedircMsg = message;

                    //TODO: criptografar mensagem 

                    conexao.client.send(RedircMsg); // enviar msg ao destino
                }

                // Redistribuir a mensagem para todos os outros clientes (broadcast)
                broadcast("Nova mensagem de " + clientIP + ": " + message);
            }

            //caso contrario, adicionar na lista de espera
            if (Ndisponivel) {
                // guardar na lista de espera
                listaEspera.addListaEspera(message);

            }
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

    public void broadcast(String message) {
        Collection<WebSocket> connections = getConnections();
        synchronized (connections) {
            for (WebSocket client : connections) {
                client.send(message);
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