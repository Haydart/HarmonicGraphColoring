import com.sun.tools.javac.util.Pair;

import java.util.Objects;

/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class ColorPair<T> extends Pair {
    public ColorPair(T o, T o2) {
        super(o, o2);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ColorPair && ((ColorPair) o).fst.equals(fst) && ((ColorPair) o).snd.equals(snd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fst, snd
        );
    }
}
