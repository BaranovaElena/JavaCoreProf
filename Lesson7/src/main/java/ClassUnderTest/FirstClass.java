package ClassUnderTest;

import Annotation.AfterSuite;
import Annotation.BeforeSuite;
import Annotation.Test;

public class FirstClass {
    @AfterSuite
    public static void after() {
        System.out.println("After suite");
    }

    @BeforeSuite
    public static void before(){
        System.out.println("Before Suite");
    }

    @Test(priority = 2)
    public static void test1() {
        System.out.println("test1 - priority 2");
    }

    @Test(priority = 6)
    public static void test2() {
        System.out.println("test2 - priority 6");
    }

    @Test(priority = 8)
    public static void test3() {
        System.out.println("test3 - priority 8");
    }

    @Test(priority = 3)
    public static void test4() {
        System.out.println("test4 - priority 3");
    }

    @Test(priority = 6)
    public static void test5() {
        System.out.println("test5 - priority 6");
    }

    @Test
    public static void test6() {
        System.out.println("test6 - default priority 5");
    }

}
