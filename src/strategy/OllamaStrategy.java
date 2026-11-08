package strategy;

import client.OllamaClient;
import model.LLMResponse;
import model.ResponseStatus;

import java.util.concurrent.CompletableFuture;


public abstract class OllamaStrategy implements LLMStrategy {

    private final OllamaClient client;
    private final String displayName;
    private final String modelId;

    protected OllamaStrategy(OllamaClient client, String displayName, String modelId) {

        this.client = client;
        this.displayName = displayName;
        this.modelId = modelId;
    }

    @Override
    public String getDisplayName() {

        return displayName;
    }

    public String getModelId() {

        return modelId;
    }

    @Override
    public CompletableFuture<LLMResponse> generate(String question) {

        //raw text -> LLMResponse; controller sets the final status and time
        return client.generate(modelId, question)
                .thenApply(text -> new LLMResponse(
                        displayName,
                        cleanText(text),
                        ResponseStatus.SUCCESS,
                        0,
                        null));
    }

    //hook for model-specific cleanup, default leaves text as is
    protected String cleanText(String text) {

        return text;
    }
}
