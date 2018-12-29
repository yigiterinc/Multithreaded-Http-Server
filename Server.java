import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server implements Runnable {

    private Socket socket;
    private InputStream inputStream;
    private BufferedReader buffer;
    private OutputStream outputStream;
    private String documentFolderRoot;
    private int tcpPortNumber;
    ServerSocket server;

    public Server(int tcpPortNumber, String documentFolderRoot) {
        this.documentFolderRoot = documentFolderRoot;
        this.tcpPortNumber = tcpPortNumber;

        try {
            this.server = new ServerSocket(tcpPortNumber);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println("Server is up.");
            System.out.println("Waiting for a client...");

            while (true) {
                socket = server.accept();

                System.out.println("Client connected.");

                setStreams();
                String httpRequest = readRequest();
                Thread clientHandler = new Thread(
                                            new ClientHandler(socket, documentFolderRoot, httpRequest, tcpPortNumber));
                clientHandler.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            closeConnection();

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                closeConnection();
            }
        }
    }

    private String readRequest() throws IOException {
        String stringRepresentation = "";

        try {
            stringRepresentation = buffer.readLine();
        } catch (UTFDataFormatException dataFormatException) {
            System.out.println("Invalid input stream error. \n");
            dataFormatException.printStackTrace();
        }

        return stringRepresentation;
    }

    private void setStreams() throws IOException {
        outputStream = socket.getOutputStream();
        outputStream.flush();
        inputStream = socket.getInputStream();
        buffer = new BufferedReader(new InputStreamReader(inputStream));
    }

    private void closeConnection() {
        try {
            this.outputStream.close();
            this.inputStream.close();
            this.buffer.close();
            this.socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("TCP Port Number: ");  // try 63000
        int tcpPortNumber = scanner.nextInt();

        System.out.print("Enter the folder root from which the files are served. ");
        String folderRoot = scanner.next();     // try D:\Desktop\NetworkTest

        Thread logger = new Thread(new RequestLogger());
        Thread server = new Thread(new Server(tcpPortNumber, folderRoot));

        logger.start();
        server.start();
    }
}
