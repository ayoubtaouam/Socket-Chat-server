package net.ds.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1234);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Number: ");
        int nb = scanner.nextInt();
        outputStream.write(nb);
        int res = inputStream.read();
        System.out.println("Response: " + res);
    }
}
