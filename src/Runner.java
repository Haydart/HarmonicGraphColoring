/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class Runner {
    private static final int graphSize = 3;

    public static void main(String[] args) {
        new BacktrackingSolver(graphSize, false).solveHarmonicGraphColoring();
        new ForwardCheckingSolver(graphSize, false).solveHarmonicGraphColoring();
    }
}
