package servidorWebSocket;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class hora{

    private static String dataFormat = "dd/MM/yyyy HH:mm:ss";

    public static String getTimeString(){
        // Obtém a hora atual
        LocalDateTime horaAtual = LocalDateTime.now();

        // Define o fuso horário para GMT
        ZoneId fusoHorarioGMT = ZoneId.of("GMT");

        // Converte a hora atual para GMT
        LocalDateTime horaGMT = horaAtual.atZone(ZoneId.systemDefault()).withZoneSameInstant(fusoHorarioGMT).toLocalDateTime();

        // Formata a hora GMT para exibição
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern(dataFormat);
        String horaFormatada = horaGMT.format(formatador);

        return horaFormatada;

    }
    /* 
    public static void main(String[] args) {
        
        // Exibe a hora GMT
        System.out.println("Hora GMT: " + getTimeString());
    }
    */
}

