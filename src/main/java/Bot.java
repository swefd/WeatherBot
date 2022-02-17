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
    final private String API_TOKEN = "GENERATE YOUR OWN KEY :P";

    final private String HELLO = "Привіт, цей бот передбачає майбутнє." +
            "☀️\uD83C\uDF24⛅️☁️\uD83C\uDF27⛈\uD83C\uDF28\uD83D\uDCA8\uD83C\uDF0A \n" +
            "\nМагічний прогноз погоди для котиків \uD83D\uDC08" +
            "\n \nСкористайся командою /get {Місто} і ти отримаєш актуальний прогноз погоди у своєму місті." +
            "\n \nТакож можеш відіслати мені свою геолокацію і я зроблю день сонячним у твоєму районі :D" +
            "\nАбо просто розкажу яка буде погода)";

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
