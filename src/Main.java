import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class Main {
    private static final int N = 3;
    private static final int[] colors = new int[N * 2 + N % 2];
    private static final int[][] graph = new int[N][N];
    private static final Set<ColorPair<Integer>> usedPairs = new HashSet<>(N * N);

    private static int solutionsCount;
    private static int nodesEnteredCount;

    static final int[] harmonic_neighbors_x_off = {-2, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2};
    static final int[] harmonic_neighbors_y_off = {0, 0, 1, -1, -1, 1, -2, 2, 0, 1, -1, 0};
    static final int[] direct_neighbors_x_off = {0, -1, 0, 1};
    static final int[] direct_neighbors_y_off = {-1, 0, 1, 0};

    public static void main(String[] args) {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i + 1;
        }
        solveWithBackTracking(0);
        System.out.println("Solutions found: " + solutionsCount);
    }

    private static boolean solveWithBackTracking(int index) {
        int row = index / N;
        int column = index % N;
        if (index == N * N) {
            solutionsCount++;
            printSolution();
            return true;
        }

        nodesEnteredCount++;

        for (int i = 0; i < colors.length; i++) {
            int currentColor = colors[i];
            graph[row][column] = colors[i];
            if (isValidMove(currentColor, row, column)) {
                saveUsedPairs(currentColor, row, column);
                solveWithBackTracking(index + 1);
                removeUsedPairs(currentColor, row, column);
            }
            graph[row][column] = 0;
        }
        return false;
    }

    private static void saveUsedPairs(int currentColor, int lastInsertedRow, int lastInsertedColumn) {
        int[][] markedPairs = new int[4][2]; //pairs to be marked as used
        int newConstrainingPairsCount = 0;
        for (int i = 0; i < 4; i++) {
            try {
                int color2 = graph[lastInsertedRow + direct_neighbors_x_off[i]][lastInsertedColumn + direct_neighbors_y_off[i]];

                if (color2 != 0) {
                    markedPairs[newConstrainingPairsCount][0] = currentColor;
                    markedPairs[newConstrainingPairsCount][1] = color2;
                    newConstrainingPairsCount++;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        for (int i = 0; i < newConstrainingPairsCount; i++) {
            markColorPairAsUsed(markedPairs[i][0], markedPairs[i][1]);
        }
    }

    private static void removeUsedPairs(int lastAddedColor, int row, int column) {
        for (int i = 0; i < direct_neighbors_x_off.length; i++) {
            try {
                int color2 = graph[row + direct_neighbors_x_off[i]][column + direct_neighbors_y_off[i]];

                if (color2 != 0) {
                    usedPairs.remove(new ColorPair<>(lastAddedColor, color2));
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }

    private static boolean isValidMove(int lastInsertedColor, int lastInsertedRow, int lastInsertedColumn) {
        for (int i = 0; i < 12; i++) {
            try {
                if (graph[lastInsertedRow + harmonic_neighbors_x_off[i]][lastInsertedColumn + harmonic_neighbors_y_off[i]] == graph[lastInsertedRow][lastInsertedColumn]) { //if there is the same color among too close neighbors
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        for (int i = 0; i < 4; i++) {
            try {
                if (isColorPairAlreadyUsed(lastInsertedColor, graph[lastInsertedRow + direct_neighbors_x_off[i]][lastInsertedColumn + direct_neighbors_y_off[i]])) {
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return true;
    }

    private static boolean isColorPairAlreadyUsed(int color1, int color2) {
        return usedPairs.contains(new ColorPair<>(color1, color2)) || usedPairs.contains(new ColorPair<>(color2, color1));
    }

    private static void markColorPairAsUsed(int color1, int color2) {
        usedPairs.add(new ColorPair<>(color1, color2));
    }

    private static void printSolution() {
        if (solutionsCount % 10000 == 0)
            System.out.println(Arrays.deepToString(graph));
    }
}
