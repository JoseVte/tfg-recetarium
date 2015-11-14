package util;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import play.test.FakeApplication;
import play.test.WithApplication;

public abstract class AbstractTest extends WithApplication {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }
    
    public void initializeData() {
        InitDataLoader.initializeData();
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[4].getMethodName() + ANSI_RESET + "\t\t");
    }
    
    public void successTest() {
        System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
    }
}
