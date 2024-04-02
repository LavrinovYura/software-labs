package labs.lab4;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer {
    // Создаем словарь для хранения кэшированных объектов
    static Map<String, byte[]> cache = new HashMap<>();

    public static void main(String[] args) {
        if (args.length <= 1) {
            System.out.println("Используйте: java ProxyServer server_ip server_port");
            System.exit(2);
        }

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[1]), 100);
            System.out.println("Прокси-сервер запущен на порту " + args[1]);

            while (true) {
                System.out.println("Готов к обслуживанию...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Установлено соединение с: " + clientSocket.getInetAddress());

                ProxyThread thread = new ProxyThread(clientSocket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}