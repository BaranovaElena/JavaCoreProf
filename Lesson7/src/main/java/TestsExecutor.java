import Annotation.AfterSuite;
import Annotation.BeforeSuite;
import Annotation.Test;
import ClassUnderTest.FirstClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestsExecutor {
    private static final int BEFORE_SUITE_MAX = 1;
    private static final int AFTER_SUITE_MAX = 1;

    public static void main(String[] args) {
        start(FirstClass.class);
    }

    public static void start(Class testClass){
        List<Method> beforeSuiteMethods = getMethodsWithAnnotation(testClass, BeforeSuite.class);
        List<Method> afterSuiteMethods = getMethodsWithAnnotation(testClass, AfterSuite.class);
        List<Method> testMethods = getMethodsWithAnnotation(testClass, Test.class);

        if (beforeSuiteMethods.size() > BEFORE_SUITE_MAX || afterSuiteMethods.size() > AFTER_SUITE_MAX)
            throw new RuntimeException();

        executeMethodsWithoutPriority(beforeSuiteMethods);

        executeTestMethods(testMethods);

        executeMethodsWithoutPriority(afterSuiteMethods);
    }

    private static List<Method> getMethodsWithAnnotation(Class testClass, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(annotation))
                methods.add(m);
        }
        return methods;
    }

    private static void executeMethodsWithoutPriority(List<Method> methods) {
        if (!methods.isEmpty()) {
            try {
                for (Method m : methods)
                    m.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void executeTestMethods(List<Method> methods) {
        if (!methods.isEmpty()) {
            methods.sort(Comparator.comparing(m -> -m.getAnnotation(Test.class).priority()));

            try {
                for (Method m: methods)
                    m.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
