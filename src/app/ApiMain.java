package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApiMain {

    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/event_system_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 7070), 0);

        server.createContext("/ping", ex -> {
            try {
                addCors(ex);
                if (handleOptions(ex)) return;
                sendJson(ex, 200, "{\"status\":\"ok\"}");
            } catch (Exception e) {
                safe500(ex, e);
            }
        });

        // ✅ One context handles /organizers and /organizers/{id}
        server.createContext("/organizers", ex -> {
            addCors(ex);
            try {
                if (handleOptions(ex)) return;

                String path = ex.getRequestURI().getPath();   // /organizers OR /organizers/5
                String method = ex.getRequestMethod();

                // ---- /organizers ----
                if (path.equals("/organizers")) {

                    if ("GET".equalsIgnoreCase(method)) {
                        sendJson(ex, 200, dbListOrganizersJson());
                        return;
                    }

                    if ("POST".equalsIgnoreCase(method)) {
                        String body = readBody(ex);

                        String name = extractJsonField(body, "name");
                        String email = extractJsonField(body, "email");
                        String organization = extractJsonField(body, "organization");

                        validate(name, email, organization);

                        long id = dbCreateOrganizer(name, email, organization);
                        sendJson(ex, 201, "{\"status\":\"created\",\"id\":" + id + "}");
                        return;
                    }

                    sendText(ex, 405, "Method Not Allowed");
                    return;
                }

                // ---- /organizers/{id} ----
                if (path.startsWith("/organizers/")) {
                    long id = parseId(path);

                    if ("GET".equalsIgnoreCase(method)) {
                        String json = dbGetOrganizerJson(id);
                        if (json == null) {
                            sendJson(ex, 404, "{\"error\":\"Organizer not found\"}");
                        } else {
                            sendJson(ex, 200, json);
                        }
                        return;
                    }

                    if ("PUT".equalsIgnoreCase(method)) {
                        String body = readBody(ex);

                        String name = extractJsonField(body, "name");
                        String email = extractJsonField(body, "email");
                        String organization = extractJsonField(body, "organization");

                        validate(name, email, organization);

                        boolean ok = dbUpdateOrganizer(id, name, email, organization);
                        if (!ok) {
                            sendJson(ex, 404, "{\"error\":\"Organizer not found\"}");
                        } else {
                            sendJson(ex, 200, "{\"status\":\"updated\"}");
                        }
                        return;
                    }

                    if ("DELETE".equalsIgnoreCase(method)) {
                        boolean ok = dbDeleteOrganizer(id);
                        if (!ok) {
                            sendJson(ex, 404, "{\"error\":\"Organizer not found\"}");
                        } else {
                            sendJson(ex, 200, "{\"status\":\"deleted\"}");
                        }
                        return;
                    }

                    sendText(ex, 405, "Method Not Allowed");
                    return;
                }

                sendJson(ex, 404, "{\"error\":\"Not Found\"}");

            } catch (SQLException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("duplicate key")) {
                    safe409(ex, "email already exists");
                } else {
                    safe500(ex, e);
                }
            } catch (IllegalArgumentException e) {
                safe400(ex, e.getMessage());
            } catch (Exception e) {
                safe500(ex, e);
            }
        });

        server.start();
        System.out.println("✅ RUNNING:");
        System.out.println("➡ http://127.0.0.1:7070/ping");
        System.out.println("➡ http://127.0.0.1:7070/organizers");

        Thread.currentThread().join();
    }

    // ===================== DB =====================

    private static Connection db() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static long dbCreateOrganizer(String name, String email, String organization) throws SQLException {
        String sql = "INSERT INTO organizers(name, email, organization) VALUES (?,?,?) RETURNING id";
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, organization);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private static String dbListOrganizersJson() throws SQLException {
        String sql = "SELECT id, name, email, organization FROM organizers ORDER BY id";
        List<String> items = new ArrayList<>();

        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(rowToJson(rs));
            }
        }
        return "[" + String.join(",", items) + "]";
    }

    private static String dbGetOrganizerJson(long id) throws SQLException {
        String sql = "SELECT id, name, email, organization FROM organizers WHERE id=?";
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rowToJson(rs);
            }
        }
    }

    private static boolean dbUpdateOrganizer(long id, String name, String email, String organization) throws SQLException {
        String sql = "UPDATE organizers SET name=?, email=?, organization=? WHERE id=?";
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, organization);
            ps.setLong(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static boolean dbDeleteOrganizer(long id) throws SQLException {
        String sql = "DELETE FROM organizers WHERE id=?";
        try (Connection c = db();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static String rowToJson(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String org = rs.getString("organization");

        return "{"
                + "\"id\":" + id + ","
                + "\"name\":\"" + jsonSafe(name) + "\","
                + "\"email\":\"" + jsonSafe(email) + "\","
                + "\"organization\":\"" + jsonSafe(org) + "\""
                + "}";
    }

    // ===================== Helpers =====================

    private static void validate(String name, String email, String organization) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (organization == null || organization.isBlank()) throw new IllegalArgumentException("organization is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");
        if (!email.contains("@")) throw new IllegalArgumentException("email must contain @");
    }

    private static long parseId(String path) {
        // /organizers/12
        String[] parts = path.split("/");
        if (parts.length != 3) throw new IllegalArgumentException("invalid id path");
        return Long.parseLong(parts[2]);
    }

    private static String extractJsonField(String json, String key) {
        if (json == null) return "";
        String needle = "\"" + key + "\"";
        int i = json.indexOf(needle);
        if (i < 0) return "";
        int colon = json.indexOf(":", i);
        if (colon < 0) return "";
        int firstQuote = json.indexOf("\"", colon);
        if (firstQuote < 0) return "";
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        if (secondQuote < 0) return "";
        return json.substring(firstQuote + 1, secondQuote).trim();
    }

    private static String jsonSafe(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "'");
    }

    private static void addCors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static boolean handleOptions(HttpExchange ex) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private static String readBody(HttpExchange ex) throws Exception {
        try (InputStream is = ex.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void sendJson(HttpExchange ex, int code, String json) throws Exception {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(HttpExchange ex, int code, String text) throws Exception {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void safe400(HttpExchange ex, String msg) {
        try { sendJson(ex, 400, "{\"error\":\"" + jsonSafe(msg) + "\"}"); } catch (Exception ignored) {}
    }

    private static void safe409(HttpExchange ex, String msg) {
        try { sendJson(ex, 409, "{\"error\":\"" + jsonSafe(msg) + "\"}"); } catch (Exception ignored) {}
    }

    private static void safe500(HttpExchange ex, Exception e) {
        try { sendJson(ex, 500, "{\"error\":\"" + jsonSafe(e.getMessage()) + "\"}"); } catch (Exception ignored) {}
    }
}