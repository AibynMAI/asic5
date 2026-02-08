package api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {}

    // VERY simple JSON parser: {"name":"Aibyn","email":"a@b.com","organization":"AITU"}
    public static Map<String, String> parseJsonObject(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null) return map;
        body = body.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}")) body = body.substring(0, body.length() - 1);

        // split by commas not perfect, but OK for simple fields (no commas inside strings)
        String[] pairs = body.split(",");
        for (String p : pairs) {
            p = p.trim();
            if (p.isEmpty()) continue;
            String[] kv = p.split(":", 2);
            if (kv.length != 2) continue;
            String key = strip(kv[0]);
            String val = strip(kv[1]);
            map.put(key, val);
        }
        return map;
    }

    private static String strip(String s) {
        s = s.trim();
        if (s.startsWith("\"")) s = s.substring(1);
        if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
        // decode just in case
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    public static String jsonError(String msg) {
        return "{\"error\":\"" + escape(msg) + "\"}";
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
