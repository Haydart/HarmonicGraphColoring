/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class Runner {
    private static final int graphSize = 4;

    public static void main(String[] args) {
        new ForwardCheckingSolver(graphSize, false).solveHarmonicGraphColoring();
    }
}
