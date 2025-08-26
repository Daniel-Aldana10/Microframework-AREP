/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.arep;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author daniel.aldana-b
 */
public class HttpServer {
    public static Map<String, Service> services = new HashMap();

    public static void runServer(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running = true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;
            String path = null;
            boolean firstLine = true;
            URI requri = null;
            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    requri = new URI(inputLine.split(" ")[1]);
                    System.out.println("Path: " + requri.getPath());
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            handleRequest(requri, out, clientSocket);
            //out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    /**
     * Handles an incoming HTTP request and generates the appropriate response.
     *
     * @param uri    the request URI containing the path and query parameters
     * @param out    the writer to send responses to the client
     * @param socket the client socket used for file streaming
     * @throws IOException if an I/O error occurs when handling the request
     */
    public static void handleRequest(URI uri, PrintWriter out, Socket socket) throws IOException {
        
        if(uri != null && uri.getPath().startsWith("/app/helloget")){
            String output = greetingService(uri, false);
            invokeService(uri);
            out.println(output);
        }else if(uri != null && uri.getPath().startsWith("/app/hellopost")) {
            String output = greetingService(uri, true);
            out.println(output);
        }
        else{
            Path directory =  Path.of("src/main/resources", uri.getPath());
            if(Files.isDirectory(directory)){
                directory = directory.resolve("index.html");
            }
            if(Files.exists(directory)){
                String output = "HTTP/1.1 200 OK\r\n" + "content-type" + getType(directory) + "\r\n"
                        +"content-length"+Files.size(directory) + "\r\n\r\n";
                try (OutputStream outputStream = socket.getOutputStream()) {
                    outputStream.write(output.getBytes());
                    Files.copy(directory, outputStream);
                }
            } else {
                String outputLine = "HTTP/1.1 404 Not Found\r\n"  + "content-type: text/plain; charset=utf-8\r\n"
                        + "\r\n" + "File not found";
                out.println(outputLine);
            }
        }

    }
    /**
     * Determines the MIME type of a given file based on its extension.
     *
     * @param path the file path whose content type is to be determined
     * @return the MIME type as a string (e.g., "text/html"), or "application/octet-stream" if unknown
     */
    public static String getType(Path path){
        if (path == null || path.getFileName() == null) {
            return "application/octet-stream";
        }

        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);

        return switch (extension) {
            case "html", "htm" -> "text/html; charset=utf-8";
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "application/javascript; charset=utf-8";
            case "json" -> "application/json; charset=utf-8";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml";
            case "ico" -> "image/x-icon";
            default -> "application/octet-stream";
        };
    }
    /**
     * Generates an HTTP response with a JSON greeting message.
     *
     * @param uri  the request URI containing the query parameter (?name=value)
     * @param time if true, includes the current date in the response
     * @return an HTTP response string with status, headers, and JSON body
     */
    public static String greetingService(URI uri, boolean time){
        String user;
        try{
            user = uri.getQuery().split("=")[1];
        } catch (Exception e) {
            return "HTTP/1.1 400 Bad Request\r\n" + "content-type: text/plain; charset=utf-8\r\n"
                    + "\r\n" + "{\"msg\": \"Name not found\"}";
        }
        String response = "HTTP/1.1 200 OK \r\n" + "content-type: application/json; charset=utf-8\r\n"
                + "\r\n";
        response = response + "{\"msg\": \"Hello " + user;
        response = time? response + "today's date is" + LocalDate.now() + "\"}":response+ "\"}";
        System.out.println(response);
        return response;
    }
    public static void get(String path, Service s){
        services.put(path,s);
    }
    public static void staticFiles(String localFilesPath){}
    public static void start(String[] args) throws IOException, URISyntaxException{
        runServer(args);
    }
    private static String invokeService(URI uri){
        String key= uri.getPath().substring(4);
        Service s = services.get(key);
        HttpRequest req = new HttpRequest(uri);
        HttpResponse res= new HttpResponse();
        String response = "HTTP/1.1 200 OK \r\n" + "content-type: text/plain; charset=utf-8\r\n"
                + "\r\n";
        return s.invoke(req, res);
    }
}
