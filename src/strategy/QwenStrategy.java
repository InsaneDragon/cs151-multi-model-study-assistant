package strategy;

import client.OllamaClient;

public class QwenStrategy extends OllamaStrategy {

    public QwenStrategy(OllamaClient client) {

        super(client, "Qwen", "qwen3:1.7b");
    }

    @Override
    protected String cleanText(String text) {

        if (text == null) {
            return null;
        }

        int end = text.indexOf("</think>");
        if (text.trim().startsWith("<think>") && end >= 0) {
            return text.substring(end + "</think>".length()).trim();
        }
        return text;
    }
}
