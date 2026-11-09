package test;

import controller.Controller;
import model.QuerySession;
import model.ResponseStatus;

import java.util.List;

public class ControllerTest {

    public static void main(String[] args) throws Exception {
        testSuccess();
        testInvalidResponse();
        testFailure();
        testTimeout();

        System.out.println("All tests passed.");
    }

    private static void testSuccess() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "success")),
                1000
        );

        controller.handleUserQuery("What is Java?");
        Thread.sleep(200);

        check(
                session.getResponse("Test").getStatus() == ResponseStatus.SUCCESS,
                "Success test"
        );
    }

    private static void testInvalidResponse() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "empty")),
                1000
        );

        controller.handleUserQuery("What is Java?");
        Thread.sleep(200);

        check(
                session.getResponse("Test").getStatus() == ResponseStatus.INVALID_RESPONSE,
                "Invalid response test"
        );
    }

    private static void testFailure() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "failure")),
                1000
        );

        controller.handleUserQuery("What is Java?");
        Thread.sleep(200);

        check(
                session.getResponse("Test").getStatus() == ResponseStatus.FAILED,
                "Failure test"
        );
    }

    private static void testTimeout() throws Exception {
        QuerySession session = new QuerySession();

        Controller controller = new Controller(
                session,
                List.of(new MockLLMStrategy("Test", "timeout", 500)),
                100
        );

        controller.handleUserQuery("What is Java?");
        Thread.sleep(300);

        check(
                session.getResponse("Test").getStatus() == ResponseStatus.TIMEOUT,
                "Timeout test"
        );
    }

    private static void check(boolean condition, String testName) {
        if (!condition) {
            throw new RuntimeException(testName + " failed");
        }

        System.out.println(testName + " passed");
    }
}