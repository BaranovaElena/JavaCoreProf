import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //task1
        String[] arrString = {"s1", "s2", "s3"};
        Double[] arrDouble = {1D, 2D, 3D};

        if (changeElements(arrString, 0, 2))
            System.out.println(Arrays.toString(arrString));

        if (changeElements(arrDouble, 1, 3))
            System.out.println(Arrays.toString(arrDouble));

        //task2
        ArrayList<String> listString = arrayToList(arrString);
        System.out.println("list: " + listString);

        ArrayList<Double> listDouble = arrayToList(arrDouble);
        System.out.println("list: " + listDouble);
        System.out.println();

        //task3
        Box<Apple> appleBox = new Box<>();
        Box<Orange> orangeBox = new Box<>();

        //add 15 apples to appleBox
        for (int i=0; i<15; i++)
            appleBox.addFruit(new Apple());

        //add 10 oranges to orangeBox
        for (int i=0; i<10; i++)
            orangeBox.addFruit(new Orange());

        System.out.println("appleBox weight: "+appleBox.getWeight());
        System.out.println("orangeBox weight: "+orangeBox.getWeight());
        System.out.println("boxes weights are equal? " + appleBox.compare(orangeBox));

        orangeBox.addFruit(new Orange());
        System.out.println("one orange added. boxes weights are equal? " + appleBox.compare(orangeBox));

        //transfer apples
        Box<Apple> newAppleBox = new Box<>(new Apple(), new Apple()); //2 apples in new box
        appleBox.transferFruitsToBox(newAppleBox);
        System.out.println("newAppleBox weight: " + newAppleBox.getWeight());
        System.out.println("appleBox is empty now? " + appleBox.isEmpty());
    }

    //task1 - swap two elements
    private static <T> boolean changeElements(T[] array, int first, int second) {
        try {
            T buf = array[second];
            array[second] = array[first];
            array[first] = buf;
            return true;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("wrong indexes!");
            return false;
        }
    }

    //task2 - convert array to ArrayList
    private static <T> ArrayList<T> arrayToList(T[] array) {
        return new ArrayList<>(Arrays.asList(array));
    }
}
