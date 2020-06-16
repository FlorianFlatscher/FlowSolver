package flow;

import javafx.scene.shape.PathElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PathArrayList extends ArrayList<PathElement> {
    public PathArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public PathArrayList() {
    }

    public PathArrayList(Collection<? extends PathElement> c) {
        super(c);
    }

    @Override
    public String toString() {
        return Arrays.toString(super.toArray());
    }
}
