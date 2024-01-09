package servidorWebSocket;

import java.util.Map;

import com.google.gson.Gson;

public class test {

    public static class MeuObjeto {
        private Map<String, Object> a;
        private String b;

        public Map<String, Object> getA() {
            return a;
        }

        public void setA(Map<String, Object> a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }

    public static void main(String[] args) {
        String json = "{\"a\":{\"b\":\"c\",\"d\":\"e\"},\"b\":\"r\"}";

        Gson gson = new Gson();
        MeuObjeto meuObjeto = gson.fromJson(json, MeuObjeto.class);

        // Agora você pode acessar os campos do objeto Java
        System.out.println("a.b: " + meuObjeto.getA().get("b"));
        System.out.println("a.d: " + meuObjeto.getA().get("d"));
        System.out.println("b: " + meuObjeto.getB());
    }
}

