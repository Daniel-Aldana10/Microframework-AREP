/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.arep;

import java.net.URI;

/**
 *
 * @author daniel.aldana-b
 */
class HttpRequest {
    URI requri = null;
    HttpRequest(URI uri) {
        uri = requri;
    }
    public String getValue(String paramName){
        String paramValue = requri.getQuery().split("=")[1];
        return paramValue;
    }
}
