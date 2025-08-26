package com.mycompany.arep;
import static com.mycompany.arep.HttpServer.start;
import static com.mycompany.arep.HttpServer.get;
import static com.mycompany.arep.HttpServer.staticfiles;
import java.io.IOException;
import java.net.URISyntaxException;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author daniel.aldana-b
 */
public class WebApplication {
     public static void main(String[] args) throws IOException, URISyntaxException {
        staticfiles("/webroot");
        get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        get("/pi", (req, resp) -> {
            return String.valueOf(Math.PI); 
        });
        start(args);
    }
}
