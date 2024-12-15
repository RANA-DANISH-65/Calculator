import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket = null;
    private static final int PORT = 12345;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is waiting for client connections...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Welcome! Please enter a mathematical expression to evaluate (or type 'over' to exit):");

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equalsIgnoreCase("over")) {
                        out.println("Goodbye!");
                        break;
                    }
                    try {
                        double result = MathEvaluator.evaluate(input);
                        out.println("Result: " + result);
                    } catch (RuntimeException e) {
                        out.println("Invalid expression. Please enter a valid mathematical expression or type 'over' to exit.");
                    }
                    out.println("Enter another expression (or type 'over' to exit):");
                }
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
