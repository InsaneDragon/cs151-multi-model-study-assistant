package model;

public class LLMResponse {

    private String modelName;
    private String text;
    private ResponseStatus status;
    private long responseTimeMs;
    private String errorMessage;

    public LLMResponse(String modelName, String text, ResponseStatus status, long responseTimeMs, String errorMessage) {

        this.modelName = modelName;
        this.text = text;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
    }

    public String getModelName() { return modelName; }
    public String getText() { return text; }
    public ResponseStatus getStatus() { return status; }
    public long getResponseTimeMs() { return responseTimeMs; }
    public String getErrorMessage() { return errorMessage; }

    public void setModelName(String modelName) { this.modelName = modelName; }
    public void setText(String text) { this.text = text; }
    public void setStatus(ResponseStatus status) { this.status = status; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}