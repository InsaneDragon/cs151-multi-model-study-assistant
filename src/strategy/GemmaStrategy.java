package strategy;

import client.OllamaClient;

public class GemmaStrategy extends OllamaStrategy {

    public GemmaStrategy(OllamaClient client) {

        super(client, "Gemma", "gemma3:1b");
    }
}
