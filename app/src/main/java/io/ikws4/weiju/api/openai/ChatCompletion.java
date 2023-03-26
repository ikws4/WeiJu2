package io.ikws4.weiju.api.openai;


import java.util.List;

public class ChatCompletion {
    public String id;
    public String object;
    public long created;
    public List<Choice> choices;
    public Usage usage;

    public static class Choice {
        public int index;
        public Message message;
        public String finish_reason;
    }

    public static class Message {
        public String role;
        public String content;
    }

    public static class Usage {
        public int promptTokens;
        public int completionTokens;
        public int totalTokens;
    }
}
