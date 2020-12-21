package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.io.IOException;

public class WebduinoService {
    public static void requestGet(String state) {
        ObjectMapper mapper = new ObjectMapper();
        String color;
        if(state.equals("ON")) color = "FFFFFF";
        else color = "000000";

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpGet request = new HttpGet("http://localhost:3000/control?color=" + color);

            CloseableHttpResponse response = client.execute(request);
            String httpStr = EntityUtils.toString(response.getEntity(), "UTF-8");

            System.out.println(httpStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
