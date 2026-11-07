package model;

import observer.ModelObserver;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class QuerySession {

    private String question;
    private final Map<String, LLMResponse> responses;
    private final List<ModelObserver> observers;

    public QuerySession() {

        this.responses = new ConcurrentHashMap<>();
        this.observers = new CopyOnWriteArrayList<>();
    }

    public void addObserver(ModelObserver observer) {

        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ModelObserver observer) {

        observers.remove(observer);
    }

    public void notifyObservers() {

        for (ModelObserver observer : observers) {

            observer.modelChanged();
        }
    }

    public void startNewQuery(String question) {

        this.question = question;

        //init responses
        String[] modelNames = {"Llama", "Gemma", "Qwen"};
        for (String name : modelNames) {
            responses.put(name, new LLMResponse(name, "", ResponseStatus.RUNNING, 0, null));
        }
        notifyObservers();
    }

    public void updateResponse(String modelName, LLMResponse response) {

        responses.put(modelName, response);
        notifyObservers();
    }

    public LLMResponse getResponse(String modelName) {

        return responses.get(modelName);
    }

    public String getQuestion() {

        return question;
    }

    public Map<String, LLMResponse> getAllResponses() {

        return Collections.unmodifiableMap(responses);
    }
}