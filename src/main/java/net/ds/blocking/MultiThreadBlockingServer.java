package net.ds.blocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadBlockingServer extends Thread {
    private int count;
    public static void main(String[] args) {
        new MultiThreadBlockingServer().start();
    }

    @Override
    public void run() {
        System.out.println("The server is started using port 1234");
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            while (true) {
                Socket socket = serverSocket.accept();
                ++count;
                new Conversation(socket, count).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    class Conversation extends Thread {
        private int connexionId;
        private Socket socket;
        public Conversation(Socket socket, int connexionId) {
            this.socket = socket;
            this.connexionId = connexionId;
        }
        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                String ip = socket.getRemoteSocketAddress().toString();
                System.out.println("New Client Connection => " + connexionId + " IP = " + ip);
                printWriter.println("Welcome, you ID is " + connexionId);
                String request;
                while ((request = bufferedReader.readLine()) != null) {
                    System.out.println("New request => IP = " + ip + " Request = " + request);
                    String response = "Size = " + request.length();
                    printWriter.println(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
