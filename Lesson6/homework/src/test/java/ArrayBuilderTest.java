import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ArrayBuilderTest {
    private ArrayBuilder ab;

    @BeforeEach
    public void init() {
        ab = new ArrayBuilder();
    }

    @ParameterizedTest
    @MethodSource("dataForCheckOperation")
    void checkOneFour(int[] arr, boolean result) {
        Assertions.assertEquals(result, ab.checkOneFour(arr));
    }
    public static Stream<Arguments> dataForCheckOperation() {
        List<Arguments> out = new ArrayList<>();
        int[] arr1 = {1, 1, 1, 4, 4, 1, 4, 4};
        out.add(Arguments.arguments(arr1, true));
        int[] arr2 = {1, 1, 1, 1, 1, 1};
        out.add(Arguments.arguments(arr2, false));
        int[] arr3 = {4, 4, 4, 4};
        out.add(Arguments.arguments(arr3, false));
        int[] arr4 = {1, 4, 4, 1, 1, 4, 3};
        out.add(Arguments.arguments(arr4, false));
        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForGetTailOperation")
    void getTail(int[] arr, int[] result) {
        Assertions.assertArrayEquals(result, ab.getTail(arr));
    }
    public static Stream<Arguments> dataForGetTailOperation() {
        List<Arguments> out = new ArrayList<>();
        int[] arr1 = {1, 2, 4, 4, 2, 3, 4, 1, 7};
        int[] res1 = {1, 7};
        out.add(Arguments.arguments(arr1, res1));
        int[] arr2 = {1, 6, 4, 1, 6, 8, 4};
        int[] res2 = {};
        out.add(Arguments.arguments(arr2, res2));
        int[] arr3 = {1, 4, 4, 1, 1, 4, 3};
        int[] res3 = {3};
        out.add(Arguments.arguments(arr3, res3));
        return out.stream();
    }

    @Test
    void getTailException() {
        int[] arr = {3, 5, 7, 8, 1, 9};
        Assertions.assertThrows(RuntimeException.class,
                () -> ab.getTail(arr));
    }
}