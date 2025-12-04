package ips.club.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ips.club.model.WeatherForecast;
import ips.util.ApplicationException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WeatherService {

    private static final double LATITUDE = 43.36029;
    private static final double LONGITUDE = -5.84476;
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherForecast getDailyForecast(LocalDate date) {
        String dateStr = date.format(DATE_FORMAT);
        String url = BASE_URL
                + "?latitude=" + LATITUDE
                + "&longitude=" + LONGITUDE
                + "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum"
                + "&timezone=auto"
                + "&start_date=" + dateStr
                + "&end_date=" + dateStr;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            InputStream stream = status >= 200 && status < 300? connection.getInputStream() : connection.getErrorStream();

            if (stream == null) {throw new ApplicationException("No se pudo leer la respuesta de la API meteorológica");}

            try (InputStream in = stream) {
                JsonNode root = mapper.readTree(in);
                JsonNode daily = root.path("daily");
                JsonNode tempMaxArray = daily.path("temperature_2m_max");
                JsonNode tempMinArray = daily.path("temperature_2m_min");
                JsonNode precipitationArray = daily.path("precipitation_sum");

                if (!tempMaxArray.isArray() || tempMaxArray.size() == 0) {throw new ApplicationException("La API meteorológica no devolvió datos diarios");}

                double maxTemp = tempMaxArray.get(0).asDouble();
                double minTemp = tempMinArray.get(0).asDouble();
                double precipitation = precipitationArray.get(0).asDouble();

                return new WeatherForecast(date, minTemp, maxTemp, precipitation);
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            throw new ApplicationException("No se pudo obtener la previsión meteorológica: " + e.getMessage());
        }
    }
}
