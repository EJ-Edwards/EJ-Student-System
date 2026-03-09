package edu.ej.student;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class API {
    private static final List<Student> students =
            Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/students", new StudentHandler());
        server.setExecutor(null);
        System.out.println("Server started on http://localhost:8080");
        server.start();
    }

    private static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                if ("GET".equalsIgnoreCase(method)) {
                    handleGet(exchange);
                } else if ("POST".equalsIgnoreCase(method)) {
                    handlePost(exchange);
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            String response = toJson(new ArrayList<>(students));
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);
            Student s = fromJson(body);
            if (s != null) {
                students.add(s);
                String resp = "{\"status\":\"created\"}";
                byte[] respBytes = resp.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } else {
                exchange.sendResponseHeaders(400, -1);
            }
        }

        private String toJson(List<Student> list) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < list.size(); i++) {
                Student s = list.get(i);
                sb.append("{\"name\":\"")
                  .append(escape(s.getName()))
                  .append("\",\"id\":")
                  .append(s.getId())
                  .append(",\"grade\":")
                  .append(s.getGrade())
                  .append("}");
                if (i < list.size() - 1) sb.append(',');
            }
            sb.append("]");
            return sb.toString();
        }

        private Student fromJson(String json) {
            try {
                String n = json.replaceAll(".*\"name\"\s*:\s*\"([^\"]+)\".*", "$1");
                String idStr = json.replaceAll(".*\"id\"\s*:\s*(\\d+).*", "$1");
                String gStr = json.replaceAll(".*\"grade\"\s*:\s*(\\d+).*", "$1");
                return new Student(n, Integer.parseInt(idStr), Integer.parseInt(gStr));
            } catch (Exception e) {
                return null;
            }
        }

        private String escape(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}

