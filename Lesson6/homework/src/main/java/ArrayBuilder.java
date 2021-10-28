import java.util.Arrays;

public class ArrayBuilder {
    public int[] getTail(int[] array) throws RuntimeException {
        int pos = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 4)
                pos = i+1;
        }

        if (pos == 0)
            throw new RuntimeException();

        int resSize = array.length-pos;
        int[] resArray = new int[resSize];
        System.arraycopy(array, pos, resArray, 0, resSize);
        return resArray;
    }

    public boolean checkOneFour(int[] array) {
        boolean contains1 = false;
        boolean contains4 = false;
        for (int i : array) {
            if (i == 1)
                contains1 = true;
            else if (i == 4)
                contains4 = true;
            else
                return false;
        }

        return (contains1 && contains4);
    }

    public static void main(String[] args) {
        ArrayBuilder arrayBuilder = new ArrayBuilder();
        int[] arr = {1, 4, 1, 1, 2, 1, 1};
        System.out.println(Arrays.toString(arrayBuilder.getTail(arr)));
        System.out.println(arrayBuilder.checkOneFour(arr));
    }
}


