import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Weather {

    final private String API_KEY = "f01fa6a8d934f2610e28e62b4c093239";
    final private String URL = "https://api.openweathermap.org/data/2.5/forecast?";

    String createUrl(String city) {

        StringBuilder stringBuilder = new StringBuilder(URL);
        Map<String, String> map = new HashMap<>();

        map.put("q", city);
        return getString(stringBuilder, map);
    }



    String createUrl(Location location) {

        StringBuilder stringBuilder = new StringBuilder(URL);
        Map<String, String> map = new HashMap<>();

        map.put("lat", location.getLatitude().toString());
        map.put("lon", location.getLongitude().toString());
        return getString(stringBuilder, map);
    }

    private String getString(StringBuilder stringBuilder, Map<String, String> map) {
        map.put("appid", API_KEY);
        map.put("units", "metric");
        map.put("lang", "UA");
        map.put("cnt", "20");

        map.forEach((k, v) -> stringBuilder.append(k).append("=").append(v).append("&"));

        System.out.println(stringBuilder.toString());

        return stringBuilder.toString();
    }

    String createConnection(String url) throws IOException {
        URL obj = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");

        Scanner fileScanner = new Scanner(connection.getInputStream());

        StringBuilder response = new StringBuilder();

        while (fileScanner.hasNext()) {
            response.append(fileScanner.nextLine());
        }

        fileScanner.close();
        return response.toString();
    }


    void writeFile(String response) {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/data.json")) {
            fileWriter.write(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String parseJSON() throws IOException, ParseException {

        List<Forecast> list = new ArrayList<>();

        FileReader fileReader = new FileReader("src/main/resources/data.json");
        Scanner scanner = new Scanner(fileReader);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine()).append("\n");
        }
        fileReader.close();
        scanner.close();

        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(stringBuilder.toString());
        JSONArray weatherArray = (JSONArray) jsonObject.get("list");

        for (int i = 0; i < 20; i++) {
            JSONObject weatherData = (JSONObject) weatherArray.get(i);
            JSONArray weatherList = (JSONArray) weatherData.get("weather");
            JSONObject weather = (JSONObject) weatherList.get(0);
            String description = weather.get("description").toString();
            String icon = weather.get("icon").toString();

            JSONObject main = (JSONObject) weatherData.get("main");

            String temp = main.get("temp").toString();
            String date = weatherData.get("dt_txt").toString();

            list.add(new Forecast(date, temp, description));
        }

        return parserMessage(list);
    }

    String parserMessage(List<Forecast> list) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd ");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        String todayDate = today.format(formatter);
        String tomorrowDate = tomorrow.format(formatter);

        StringBuilder todayList = new StringBuilder("Прогноз погоди на сьогодні \uD83C\uDF24 \n");
        StringBuilder tomorrowList = new StringBuilder("\nПрогноз погоди на завтра \uD83C\uDF24 \n");

        for (Forecast forecast : list) {
            if (forecast.getDate().contains(todayDate)) {
                todayList.append("\n----- ").append(forecast.getDate().replaceAll(todayDate, "")).append(" -----");
                todayList.append("\nТемпература: ").append(forecast.getTemp()).append("°C");
                todayList.append("\nДеталі: ").append(forecast.getDescription()).append("\n");
            } else if (forecast.getDate().contains(tomorrowDate)){
                tomorrowList.append("\n----- ").append(forecast.getDate().replaceAll(tomorrowDate, "")).append(" -----");
                tomorrowList.append("\nТемпература: ").append(forecast.getTemp()).append("°C");
                tomorrowList.append("\nДеталі: ").append(forecast.getDescription()).append("\n");
            }
        }



        return todayList.append(tomorrowList).toString();
    }
}

