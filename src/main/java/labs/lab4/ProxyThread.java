package labs.lab4;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

class ProxyThread extends Thread {
    private Socket clientSocket;
    private static final int BUFFER_SIZE = 8192;

    public ProxyThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream clientWriter = new BufferedOutputStream(clientSocket.getOutputStream());

            String requestLine = clientReader.readLine();
            System.out.println("Получен запрос от клиента: " + requestLine);

            // Выводим содержимое запроса полностью
            System.out.println("Содержимое запроса:");
            System.out.println(requestLine);
            String headerLine;
            while (!(headerLine = clientReader.readLine()).isEmpty()) {
                System.out.println(headerLine);
            }
            System.out.println();

            String[] requestParts = requestLine.split(" ");
            String filename = requestParts[1].split("/")[1];
            String fileToUse = "./" + filename;

            if (ProxyServer.cache.containsKey(fileToUse)) {
                System.out.println("Объект найден в кэше");
                clientWriter.write("HTTP/1.1 200 OK\r\n".getBytes());
                clientWriter.write("Content-Type: text/html\r\n\r\n".getBytes());
                clientWriter.write(ProxyServer.cache.get(fileToUse));
            } else {
                try {
                    Socket serverSocket = new Socket(filename.replace("www.", ""), 80);
                    OutputStream serverOutStream = serverSocket.getOutputStream();
                    PrintWriter serverWriter = new PrintWriter(serverOutStream, true);
                    serverWriter.println("GET http://" + filename + " HTTP/1.1\r\n");
                    serverWriter.println("Host: " + filename + "\r\n");
                    serverWriter.println("Connection: close\r\n");
                    serverWriter.println();

                    InputStream serverInStream = serverSocket.getInputStream();
                    ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = serverInStream.read(buffer)) != -1) {
                        responseBuffer.write(buffer, 0, bytesRead);
                        clientWriter.write(buffer, 0, bytesRead);
                    }

                    clientWriter.flush();
                    ProxyServer.cache.put(fileToUse, responseBuffer.toByteArray());
                    System.out.println("Объект добавлен в кэш");

                    // Выводим содержимое ответа полностью
                    System.out.println("Содержимое ответа:");
                    System.out.println(new String(responseBuffer.toByteArray()));
                    System.out.println();

                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Ошибка при обработке запроса: " + e.getMessage());
                    clientWriter.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
                    clientWriter.write("Content-Type: text/html\r\n\r\n".getBytes());
                }
            }

            clientWriter.flush();
            clientReader.close();
            clientWriter.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
