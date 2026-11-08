package strategy;

import client.OllamaClient;

public class LlamaStrategy extends OllamaStrategy {

    public LlamaStrategy(OllamaClient client) {

        super(client, "Llama", "llama3.2:1b");
    }
}
