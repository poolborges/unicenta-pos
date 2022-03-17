/*
 * Copyright (C) 2022 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.kriolos.opos.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * KriolOS POS Business Server API (KriolOS POS Biz API)
 * 
 * Provide Business API as Web API (RES/OpenAPI)
 * 
 * @author poolborges
 */
public class WebServer {

    HttpServer server;

    public void sa() {
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/", new HomeHandler());
            server.createContext("/api/v1", new InvHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void so() {
        if(server != null) {
            server.stop(0);
        }
        //server.st
    }
    
    public static void main(final String args[]) {
    
        new WebServer().sa();
    }

    class HomeHandler implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            read(is); // .. read the request body
            String response = "Server is runing";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        void read(InputStream is){
        
        }
        
        
    }
    
    class InvHandler implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            read(is);
            String response = "InvHandler";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        void read(InputStream is){
        
        }
    }
}
