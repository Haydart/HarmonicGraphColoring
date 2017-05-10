import java.util.Arrays;

/**
 * Created by r.makowiecki on 06/05/2017.
 */
public class SolutionPrinter implements ISolutionPrinter {
    @Override
    public void printSolution(int[][] placedColorsGraph) {
        System.out.println(Arrays.deepToString(placedColorsGraph));
    }
}
