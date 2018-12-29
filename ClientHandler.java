import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler implements Runnable {

    private Socket socket;
    private String documentFolderRoot;
    private String httpRequest;
    private Path requestedFilePath;
    private OutputStream outputStream;
    private int serverPortNumber;

    public ClientHandler(Socket socket, String documentFolderRoot,
                         String httpRequest, int serverPortNumber) throws IOException {
        this.socket = socket;
        this.documentFolderRoot = documentFolderRoot;
        this.httpRequest = httpRequest;
        this.outputStream = socket.getOutputStream();
        this.serverPortNumber = serverPortNumber;
    }

    @Override
    public void run() {
        String requestType = parseHtmlRequestType(this.httpRequest);

        try {
            setRequestedFilePath(this.httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (requestType.equals("GET") && requestedFilePath != null) {
                addToLog();

                if (Files.isRegularFile(requestedFilePath)) {
                    sendFile(requestedFilePath);
                } else if (Files.isDirectory(requestedFilePath)) {
                    FileFinder fileFinder = new FileFinder(requestedFilePath);

                    if (fileFinder.checkExistenceOf("index.html")) {
                        File htmlFile = fileFinder.getFile("index.html");
                        sendFile(htmlFile.toPath());
                    } else {
                        System.out.println("File not found.");
                        socket.close();
                    }
                } else
                    socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String parseHtmlRequestType(final String htmlRequest) {
        if (htmlRequest != null)
            return htmlRequest.split(" ")[0];

        return "";
    }

    private void setRequestedFilePath(String htmlRequest) throws IOException {
        if (htmlRequest != null && htmlRequest.contains(" "))
            documentFolderRoot += htmlRequest.split(" ")[1];

        try {
            File file = new File(documentFolderRoot);
            this.requestedFilePath = file.toPath().toAbsolutePath();
        } catch (InvalidPathException invPath) {
            socket.close();
        }
    }

    private void addToLog() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        RequestLogger.addRequest(dateFormat.format(date) + "   " +
                httpRequest.split(" ")[0] + httpRequest.split(" ")[1]
                + " " + socket.getInetAddress().getHostAddress() + ":" + serverPortNumber);
    }

    private void sendFile(final Path pathToSend) throws IOException {
        String httpResponse = "HTTP/0.9 200 OK\r\n\r\n";
        byte[] file = Files.readAllBytes(pathToSend);
        outputStream.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.write(file);
        outputStream.flush();
        System.out.println("File sent.");
    }
}

