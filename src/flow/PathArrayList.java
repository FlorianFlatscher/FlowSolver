package flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PathArrayList extends ArrayList<Location> {
    public PathArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public PathArrayList() {
    }

    public PathArrayList(Collection<? extends Location> c) {
        super(c);
    }

    @Override
    public String toString() {
        return Arrays.toString(super.toArray());
    }


}
