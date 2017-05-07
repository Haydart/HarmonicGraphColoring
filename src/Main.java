import com.sun.tools.javac.util.Pair;

/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class Main {
    private static final int N = 3;
    private static final int[] colors = new int[N * 2 + N % 2];
    private static final int[][] graph = new int[N][N];
    private static final Pair<Integer, Integer>[] usedPairs = new Pair[N*N];
    private static int usedPairsCount = 0;
    private static int solutionsCount;
    private static int nodesEnteredCount;

    static final int[] x_off = {-2, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2};
    static final int[] y_off = {0, 0, 1, -1, -1, 1, -2, 2, 0, 1, -1, 0};

    public static void main(String[] args) {
        //solveWithBackTracking();
    }

    private static boolean solveWithBackTracking(int column, int row) {
        if (column == N - 1 && row == N - 1) {
            solutionsCount++;
            printSolution();
            return true;
        }

        nodesEnteredCount++;

        for (int i = 0; i < colors.length; i++) {
            graph[column][row] = colors[i];
        }
        return false;
    }

    private static boolean isValidMove(int color, int lastInsertedRow, int lastInsertedColumn) {
        boolean result = false;
        for (int i = 0; i < 12; i++) {
            try {
                if (graph[lastInsertedRow + x_off[i]][lastInsertedColumn + y_off[i]] == graph[lastInsertedRow][lastInsertedColumn]) { //if there is the same color among too close neighbprs
                    result = false;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        for (int i = -1; i < 2; i += 2) {
            for (int j = -1; j < 2; j += 2) {
                int color1 = graph[lastInsertedRow][lastInsertedColumn];
                int color2 = graph[lastInsertedRow + i][lastInsertedColumn + j];
                if (isColorPairAlreadyUsed(color1, color2)) {
                    result = false;
                } else {
                    markColorPairAsUsed(color1, color2);
                    result = true;
                }
            }
        }
        return result;
    }

    private static boolean isColorPairAlreadyUsed(int color1, int color2) {
        for (int i = 0; i < usedPairsCount; i++) {
            if(usedPairs[i].fst == color1 && usedPairs[i].snd == color2 ||
                    usedPairs[i].fst == color2 && usedPairs[i].snd == color1) {
                return true;
            }
        }
        return false;
    }

    private static void markColorPairAsUsed(int color1, int color2) {
        usedPairs[usedPairsCount] = new Pair<>(color1, color2);
        usedPairsCount++;
    }

    private static void printSolution() {
        //no-op
    }
}
