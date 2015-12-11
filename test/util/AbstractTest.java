package util;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import models.dao.CategoryDAO;
import models.dao.CommentDAO;
import models.dao.MediaDAO;
import models.dao.RecipeDAO;
import models.dao.TagDAO;
import models.dao.UserDAO;
import play.test.FakeApplication;
import play.test.WithApplication;

public abstract class AbstractTest extends WithApplication {
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BLACK  = "\u001B[30m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_WHITE  = "\u001B[37m";
    protected UserDAO          userDAO;
    protected RecipeDAO        recipeDAO;
    protected CategoryDAO      categoryDAO;
    protected CommentDAO       commentDAO;
    protected MediaDAO         mediaDAO;
    protected TagDAO           tagDAO;
    protected String           token;
    protected String           OS;
    protected boolean		   isJenkins;

    public AbstractTest() {
        userDAO = new UserDAO();
        recipeDAO = new RecipeDAO();
        categoryDAO = new CategoryDAO();
        commentDAO = new CommentDAO();
        mediaDAO = new MediaDAO();
        tagDAO = new TagDAO();
        OS = System.getProperty("os.name");
        isJenkins = (System.getenv("JOB_NAME") != null);
    }

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public void initializeDataController() {
        InitDataLoader.initializeData();
        if (OS.equals("Linux")) {
            System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE
                    + Thread.currentThread().getStackTrace()[5].getMethodName() + ANSI_RESET + "\t\t");
        } else if (isJenkins) {
            System.out.print("Test Name: " + Thread.currentThread().getStackTrace()[4].getMethodName() + "\t\t");
        } else {
            System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE
                    + Thread.currentThread().getStackTrace()[4].getMethodName() + ANSI_RESET + "\t\t");
        }
    }

    public void initializeDataModel() {
        InitDataLoader.initializeData();
        if (OS.equals("Linux")) {
            System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE
                    + Thread.currentThread().getStackTrace()[12].getMethodName() + ANSI_RESET + "\t\t");
        } else if (isJenkins) {
            System.out.print("Test Name: " + Thread.currentThread().getStackTrace()[9].getMethodName() + "\t\t");
        } else {
            System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE
                    + Thread.currentThread().getStackTrace()[9].getMethodName() + ANSI_RESET + "\t\t");
        }
    }

    public void successTest() {
    	if (isJenkins) {
            System.out.print("[success]");
        } else {
        	System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        }
    }
}
