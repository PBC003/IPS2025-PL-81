package ips.club.model;

import java.time.LocalDate;

public class WeatherForecast {

    private final LocalDate date;
    private final double minTemperatureCelsius;
    private final double maxTemperatureCelsius;
    private final double precipitationMm;

    public WeatherForecast(LocalDate date, double minTemperatureCelsius, double maxTemperatureCelsius, double precipitationMm) {
        this.date = date;
        this.minTemperatureCelsius = minTemperatureCelsius;
        this.maxTemperatureCelsius = maxTemperatureCelsius;
        this.precipitationMm = precipitationMm;
    }

    public LocalDate getDate() {return date;}
    public double getMinTemperatureCelsius() {return minTemperatureCelsius;}
    public double getMaxTemperatureCelsius() {return maxTemperatureCelsius;}
    public double getPrecipitationMm() {return precipitationMm;}
}
