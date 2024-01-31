import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebSocketClientExample extends WebSocketClient {

    public WebSocketClientExample(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conectado ao servidor WebSocket");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Mensagem recebida do servidor: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Conexão fechada. Código: " + code + ", Razão: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Erro ocorrido: " + ex.getMessage());
    }

    public static void main(String[] args) {
        try {
            URI serverUri = new URI("//192.168.1.100:99"); // Substitua pela URL do seu servidor WebSocket

            WebSocketClientExample client = new WebSocketClientExample(serverUri);
            client.connect();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Digite uma mensagem para enviar ao servidor (ou 'exit' para sair): ");
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    client.close();
                    break;
                } else {
                    client.send(userInput);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
