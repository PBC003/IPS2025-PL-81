package ips.club.model;

public class WeatherPolicy {

    public static final double MIN_TEMPERATURE_ALLOWED = 0.0;
    public static final double MAX_TEMPERATURE_ALLOWED = 40.0;
    public static final double MAX_PRECIPITATION_ALLOWED = 40.0;

    public static boolean isSuitable(WeatherForecast forecast) {
        if (forecast == null) {return true;}

        return forecast.getMinTemperatureCelsius() >= MIN_TEMPERATURE_ALLOWED
                && forecast.getMaxTemperatureCelsius() <= MAX_TEMPERATURE_ALLOWED
                && forecast.getPrecipitationMm() <= MAX_PRECIPITATION_ALLOWED;
    }

    public static boolean isSuitableForLocation(Location location, WeatherForecast forecast) {
        if (location == null) {return isSuitable(forecast);}
        if (!location.isOutdoor()) {return true;}
        return isSuitable(forecast);
    }
}
