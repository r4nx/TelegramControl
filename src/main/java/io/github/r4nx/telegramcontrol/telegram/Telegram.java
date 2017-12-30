package io.github.r4nx.telegramcontrol.telegram;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.r4nx.telegramcontrol.util.Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Telegram {
    private final String TELEGRAM_API_URL;
    private int lastUpdate = 0;
    private int lastMessage = 0;

    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(int lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Telegram (String botToken) {
        TELEGRAM_API_URL = String.format("https://api.telegram.org/bot%s/", botToken);
    }

    public List<HashMap> getUpdates() {
        List<HashMap> updates = new ArrayList<>();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("offset", Integer.toString(lastUpdate));
        parameters.put("allowed_updates", "[\"message\"]");
        String response = Http.sendPostRequest(TELEGRAM_API_URL + "getUpdates", parameters);
        Gson gson = new Gson();

        if (!requestOk(gson.fromJson(response, JsonObject.class).getAsJsonObject())) {
            return updates;
        }

        for (JsonElement update : gson.fromJson(response, JsonObject.class).get("result").getAsJsonArray()) {
            lastUpdate = update.getAsJsonObject().get("update_id").getAsInt();

            JsonObject message = update.getAsJsonObject().get("message").getAsJsonObject();

            if (message.get("message_id").getAsInt() <= lastMessage) continue;
            lastMessage = message.get("message_id").getAsInt();

            HashMap updateInfo = new HashMap();
            updateInfo.put("text", message.get("text").getAsString());
            updateInfo.put("from", message.get("from").getAsJsonObject().get("id").getAsInt());
            updates.add(updateInfo);
        }
        return updates;
    }

    public void sendMessage(int chatId, String text, boolean markdown) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("chat_id", Integer.toString(chatId));
        parameters.put("text", text);
        if (markdown) parameters.put("parse_mode", "markdown");
        Http.sendPostRequest(TELEGRAM_API_URL + "sendMessage", parameters);
    }

    public boolean testConnection() {
        String response = Http.sendPostRequest(TELEGRAM_API_URL + "getMe", new HashMap<>());
        Gson gson = new Gson();
        JsonObject result = gson.fromJson(response, JsonObject.class);
        return requestOk(result);
    }

    private boolean requestOk(JsonObject response) {
        return response != null && response.has("ok") && response.get("ok").getAsBoolean();
    }
}
