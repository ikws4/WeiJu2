package io.ikws4.weiju.api.openai;


import java.util.List;

public class ChatResponse {
    public String id;
    public String object;
    public long created;
    public List<Choice> choices;
    public Usage usage;

    public static class Choice {
        public int index;
        public Message message;
        public Delta delta;
        public String finish_reason;
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class Delta {
        public String role;
        public String content;
    }

    public static class Usage {
        public int promptTokens;
        public int completionTokens;
        public int totalTokens;
    }
}
