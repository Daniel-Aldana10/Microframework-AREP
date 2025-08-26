package com.mycompany.arep.classwork;
import java.net.MalformedURLException;
import java.net.URL;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author daniel.aldana-b
 */
public class URLParser {
    public static void main(String[] args) throws MalformedURLException{
        URL personalurl = new URL("http://ldbn.escuelaing.edu.co:80/personal/index.html?var=56&color=red#publicaciones");
        System.out.println("Protocol " +personalurl.getProtocol());
        System.out.println("Authority " +personalurl.getAuthority());
        System.out.println("Host " +personalurl.getHost());
        System.out.println("Port " +personalurl.getPort());
        System.out.println("Path " +personalurl.getPath());
        System.out.println("Query " +personalurl.getQuery());
        System.out.println("File " +personalurl.getFile());
        System.out.println("Ref " +personalurl.getRef());
        
    }
}
