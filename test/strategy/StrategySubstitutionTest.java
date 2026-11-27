package strategy;

import client.OllamaClient;
import controller.Controller;
import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class StrategySubstitutionTest {

    @Test
    void testMockWorksLikeRealStrategy() throws Exception {

        //real and mock strategy in the same list
        LLMStrategy real = new LlamaStrategy(new OllamaClient());
        LLMStrategy mock = new MockLLMStrategy("Llama", "success");
        List<LLMStrategy> strategies = List.of(real, mock);

        for (LLMStrategy strategy : strategies) {
            assertEquals("Llama", strategy.getDisplayName());
        }

        //only call the mock so ollama doesnt need to run
        LLMResponse response = mock.generate("What is polymorphism?").get(1, TimeUnit.SECONDS);
        assertEquals("This is a mock answer.", response.getText());
    }

    @Test
    void testControllerWithMock() throws Exception {

        QuerySession session = new QuerySession();

        //mock replaces llama, controller doesnt know the difference
        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Llama", "success")),
                1000
        );

        controller.handleUserQuery("What is polymorphism?");

        long deadline = System.currentTimeMillis() + 2000;
        while (session.getResponse("Llama").getStatus() == ResponseStatus.RUNNING
                && System.currentTimeMillis() < deadline) {
            Thread.sleep(10);
        }

        LLMResponse response = session.getResponse("Llama");
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        assertEquals("This is a mock answer.", response.getText());
        assertEquals("Llama", response.getModelName());
    }
}
