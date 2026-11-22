package test;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import observer.ModelObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class Person1ModelTest {

    private QuerySession session;

    @BeforeEach
    void setUp() {
        session = new QuerySession();
    }

    @Test
    void updateResponseStoresExpectedLLMResponse() {
        LLMResponse expected = new LLMResponse(
                "Llama",
                "Polymorphism lets objects share a common interface.",
                ResponseStatus.SUCCESS,
                420L,
                null
        );

        session.updateResponse("Llama", expected);
        LLMResponse actual = session.getResponse("Llama");

        assertNotNull(actual);
        assertEquals("Llama", actual.getModelName());
        assertEquals("Polymorphism lets objects share a common interface.", actual.getText());
        assertEquals(ResponseStatus.SUCCESS, actual.getStatus());
        assertEquals(420L, actual.getResponseTimeMs());
        assertNull(actual.getErrorMessage());
    }

    @Test
    void registeredObserverIsNotifiedAfterStateChanges() {
        AtomicInteger notifications = new AtomicInteger(0);
        ModelObserver observer = notifications::incrementAndGet;

        session.addObserver(observer);
        assertEquals(0, notifications.get());

        session.startNewQuery("What is polymorphism?");
        assertEquals(1, notifications.get());
        assertEquals("What is polymorphism?", session.getQuestion());
        assertEquals(ResponseStatus.RUNNING, session.getResponse("Llama").getStatus());
        assertEquals(ResponseStatus.RUNNING, session.getResponse("Gemma").getStatus());
        assertEquals(ResponseStatus.RUNNING, session.getResponse("Qwen").getStatus());

        session.updateResponse(
                "Gemma",
                new LLMResponse("Gemma", "Answer from Gemma", ResponseStatus.SUCCESS, 310L, null)
        );
        assertEquals(2, notifications.get());
    }
}