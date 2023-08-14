package utils;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonUtils {
    public static JSONObject readJson(HttpServletRequest request) throws IOException {
        StringBuilder jb = new StringBuilder();
        String line;

        BufferedReader reader = request.getReader();
        while((line = reader.readLine()) != null) {
            jb.append(line);
        }

        try {
            return new JSONObject(jb.toString());
        } catch (JSONException e) {
            throw new IOException("Error parsing JSON request string");
        }
    }

}