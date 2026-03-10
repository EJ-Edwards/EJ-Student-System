package edu.ej.student;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class API {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try { port = Integer.parseInt(envPort); } catch (NumberFormatException ignored) {}
        }

        Database.createTable();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/students", new StudentHandler());
        server.createContext("/", exchange -> {
            String msg = "{\"endpoints\":[\"/students\",\"/students/{id}\"],\"methods\":[\"GET\",\"POST\",\"PUT\",\"DELETE\"]}";
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        });
        server.setExecutor(null); // default executor
        System.out.println("Server started on http://localhost:" + port);
        server.start();
    }

    private static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath(); // /students or /students/{id}
                String[] parts = path.split("/");

                if (parts.length == 2) { // /students
                    if ("GET".equalsIgnoreCase(method)) {
                        handleList(exchange);
                    } else if ("POST".equalsIgnoreCase(method)) {
                        handleCreate(exchange);
                    } else {
                        send405(exchange);
                    }
                } else if (parts.length == 3) {
                    int id;
                    try {
                        id = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        send400(exchange);
                        return;
                    }
                    switch (method.toUpperCase()) {
                        case "GET":
                            handleGetById(exchange, id);
                            break;
                        case "PUT":
                            handleUpdate(exchange, id);
                            break;
                        case "DELETE":
                            handleDelete(exchange, id);
                            break;
                        default:
                            send405(exchange);
                    }
                } else {
                    send404(exchange);
                }
            } catch (Exception e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        private void handleList(HttpExchange exchange) throws IOException {
            try {
                String name = parseQuery(exchange.getRequestURI().getQuery(), "name");
                List<Student> students;
                if (name != null) {
                    students = StudentDAO.searchByName(name);
                } else {
                    students = StudentDAO.getAll();
                }
                sendJson(exchange, 200, Student.listToJson(students));
            } catch (SQLException e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        private void handleGetById(HttpExchange exchange, int id) throws IOException {
            try {
                Student s = StudentDAO.getById(id);
                if (s == null) {
                    send404(exchange);
                } else {
                    sendJson(exchange,200, s.toJson());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        private void handleCreate(HttpExchange exchange) throws IOException {
            String body = readRequestBody(exchange);
            Student s = Student.fromJson(body);
            if (s == null) {
                send400(exchange);
                return;
            }
            try {
                boolean ok = StudentDAO.insert(s);
                if (ok) {
                    sendJson(exchange,201, "{\"status\":\"created\"}");
                } else {
                    sendJson(exchange,409, "{\"error\":\"already exists\"}");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        private void handleUpdate(HttpExchange exchange, int id) throws IOException {
            String body = readRequestBody(exchange);
            Student s = Student.fromJson(body);
            if (s == null || s.getId() != id) {
                send400(exchange);
                return;
            }
            try {
                boolean ok = StudentDAO.update(s);
                if (ok) {
                    sendJson(exchange,200, "{\"status\":\"updated\"}");
                } else {
                    send404(exchange);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        private void handleDelete(HttpExchange exchange, int id) throws IOException {
            try {
                boolean ok = StudentDAO.delete(id);
                if (ok) {
                    sendJson(exchange,200, "{\"status\":\"deleted\"}");
                } else {
                    send404(exchange);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                send500(exchange);
            }
        }

        // --- helpers --------------------------------------------------
        private String readRequestBody(HttpExchange exchange) throws IOException {
            byte[] bytes = exchange.getRequestBody().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }

        private void sendJson(HttpExchange exchange, int code, String body) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void send405(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(405, -1);
        }
        private void send404(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(404, -1);
        }
        private void send400(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(400, -1);
        }
        private void send500(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(500, -1);
        }

        private String parseQuery(String query, String key) {
            if (query == null) return null;
            for (String param : query.split("&")) {
                String[] kv = param.split("=",2);
                if (kv.length == 2 && kv[0].equals(key)) {
                    return kv[1];
                }
            }
            return null;
        }
    }
}

