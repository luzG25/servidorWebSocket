package servidorWebSocket;

import org.java_websocket.server.WebSocketServer;

public class Main {
    public static void main(String[] args) {
        int port = 5098; // Altere o número da porta conforme necessário
        WebSocketServer server = new webSocketServer(port);
        server.start();
        System.out.println("Servidor WebSocket iniciado na porta " + port);
    }
}
