package net.katsuster.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ScoreBoardTest.class,
})
public class TestMain {
    protected TestMain() {
        //do nothing
    }

    public static void main(String[] args) {
        JUnitCore.main(TestMain.class.getName());
    }
}
