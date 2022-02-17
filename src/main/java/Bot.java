import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot {
    final private String NAME = "RobocodeWeatherBot";
    final private String API_TOKEN = "5195887445:AAHf30VgMx4bxwlhiidVvOHjYSlhdqDp1j0";

    final private String HELLO = "Привіт, цей бот передбачає майбутнє XD " +
            "\nМагічний прогноз погоди для котиків " +
            "\nСкористайся командою /get {Місто} і ти отримаєш актуальний прогноз погоди у своєму місті" +
            "\nТакож можеш просто відіслати мені свою геолокацію";

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        String text = msg.getText();
        Weather weather = new Weather();
        Location location = msg.getLocation();

        //System.out.println(location);
//        System.out.println("Loc: " + location.getLongitude().toString());


        if (text != null){
            if ( text.equals("/start")) {
                sendMessage(msg, HELLO);
            } else if (text.contains("/get")) {
                System.out.println("GET MET");
                String city = text.replaceAll("/get", "").trim();
                getForecast(msg, weather, weather.createUrl(city));
            }
        }else if (location != null){
            System.out.println("Location OK");
            getForecast(msg, weather, weather.createUrl(location));
        }







    }

    private void getForecast(Message msg, Weather weather, String url) {
        try {
            weather.writeFile(weather.createConnection(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sendMessage(msg, "Прогноз погоди");
            sendMessage(msg, weather.parseJSON());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg, String str) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());
        sendMessage.setText(str);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
