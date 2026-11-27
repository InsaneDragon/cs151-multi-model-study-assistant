package controller;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import org.junit.jupiter.api.Test;
import strategy.MockLLMStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void testSuccess() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "success")),
                1000
        );

        controller.handleUserQuery("What is Java?");

        LLMResponse response = waitForResult(session, "Test");
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        assertEquals("This is a mock answer.", response.getText());
        assertNull(response.getErrorMessage());
    }

    @Test
    void testInvalidResponse() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "empty")),
                1000
        );

        controller.handleUserQuery("What is Java?");

        LLMResponse response = waitForResult(session, "Test");
        assertEquals(ResponseStatus.INVALID_RESPONSE, response.getStatus());
    }

    @Test
    void testFailure() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "failure")),
                1000
        );

        controller.handleUserQuery("What is Java?");

        LLMResponse response = waitForResult(session, "Test");
        assertEquals(ResponseStatus.FAILED, response.getStatus());
        assertEquals("Mock failure", response.getErrorMessage());
    }

    @Test
    void testTimeout() throws Exception {
        QuerySession session = new QuerySession();

        //mock takes 500ms but timeout is 100ms
        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "timeout", 500)),
                100
        );

        controller.handleUserQuery("What is Java?");

        LLMResponse response = waitForResult(session, "Test");
        assertEquals(ResponseStatus.TIMEOUT, response.getStatus());
    }

    @Test
    void testEmptyQuestion() {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "success")),
                1000
        );

        controller.handleUserQuery("   ");

        assertNull(session.getQuestion());
        assertNull(session.getResponse("Test"));
    }

    //wait until its not RUNNING anymore (max 2 sec)
    private static LLMResponse waitForResult(QuerySession session, String name) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000;

        while (System.currentTimeMillis() < deadline) {
            LLMResponse response = session.getResponse(name);
            if (response != null && response.getStatus() != ResponseStatus.RUNNING
                    && response.getStatus() != ResponseStatus.WAITING) {
                return response;
            }
            Thread.sleep(10);
        }
        fail("no result for " + name);
        return null;
    }
}
