package com.pvi.Bot;

import com.pvi.myClient.domain.ChatMessage;
import com.pvi.myClient.domain.MessageType;
import org.springframework.messaging.simp.stomp.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ChatStompSessionHandler extends StompSessionHandlerAdapter {
    private StompSession session;
    private Map<String, List<Integer>> articles = new HashMap<>();

    @Override
    public void afterConnected(StompSession session, StompHeaders headers) {
        this.session = session;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.JOIN);
        chatMessage.setName("BOT");
        session.send("/topic/messages", chatMessage);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        session.send("/topic/messages", new ChatMessage("BOT", "Привет! Я бот!", MessageType.CHAT));

    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return ChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        ChatMessage msg = (ChatMessage) payload;
        if (msg.getType() == MessageType.CHAT
                && !msg.getName().equals("BOT")
                && (msg.getText().toLowerCase().contains("статья") || msg.getText().toLowerCase().contains("article"))) {
            try {
                sendAnswer(msg.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(msg);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        if (exception instanceof ConnectionLostException) {
            System.out.println("Connection Lost!");
        }
    }

    private void sendAnswer(String name) throws IOException {
        int code = 404;
        Random random = new Random();
        URL url = null;
        int i = random.nextInt(500000);

        List<Integer> artNumb = articles.get(name);

        if (artNumb == null)
            artNumb = new ArrayList<>();
        articles.put(name, artNumb);


        session.send("/topic/messages", new ChatMessage("BOT",
                "I am looking for a good article. Please, wait!", MessageType.CHAT));

        while (code != 200) {
            while (artNumb.contains(i))
                i = random.nextInt(500000);
            url = new URL("https://habr.com/ru/post/" + i + "/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            code = connection.getResponseCode();
            artNumb.add(i);
        }

        session.send("/topic/messages", new ChatMessage("BOT", url.toString(), MessageType.LINK));

    }

}
