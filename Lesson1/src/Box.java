import java.util.ArrayList;
import java.util.Collections;

public class Box<T extends Fruit> {
    ArrayList<T> fruits = new ArrayList<>();

    Box(){}
    Box(T ... fruits){
        Collections.addAll(this.fruits, fruits);
    }

    public float getWeight(){
        float totalWeight = 0f;
        for (T fruit : fruits) {
            totalWeight += fruit.getWeight();
        }
        return totalWeight;
    }

    public boolean compare(Box<?> otherBox){
        return (this.getWeight() == otherBox.getWeight());
    }

    public void transferFruitsToBox(Box<T> destinationBox){
        for (T fruit : fruits) {
            destinationBox.addFruit(fruit);
        }
        fruits.clear();
    }

    public void addFruit(T fruit) {
        fruits.add(fruit);
    }

    public boolean isEmpty() {
        return fruits.isEmpty();
    }
}
