import java.util.Arrays;

/**
 * Created by r.makowiecki on 07/05/2017.
 */
public class Main {
    private static final int N = 4;
    private static final int[] colors = new int[N * 2 + N % 2];
    private static final int[][] graph = new int[N][N];
    private static final boolean[][] usedPairsArray = new boolean[colors.length][colors.length];

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
        long startTime = System.currentTimeMillis();
        solveWithBackTracking(0);
        System.out.println("Solutions found: " + solutionsCount + " in " + (System.currentTimeMillis() - startTime) + " ms");
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
        return usedPairsArray[color1 - 1][color2 - 1] && usedPairsArray[color2 - 1][color1 - 1];
    }

    private static void saveUsedPairs(int currentColor, int lastInsertedRow, int lastInsertedColumn) {
        for (int i = 0; i < 4; i++) {
            int neighborRow = lastInsertedRow + direct_neighbors_x_off[i];
            int neighborColumn = lastInsertedColumn + direct_neighbors_y_off[i];
            if (neighborRow >= 0 && neighborRow < N && neighborColumn >= 0 && neighborColumn < N) {
                int neigborColor = graph[neighborRow][neighborColumn];
                if (neigborColor != 0) {
                    markColorPairAsUsed(currentColor, neigborColor);
                }
            }
        }
    }

    private static void markColorPairAsUsed(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor - 1][neighborColor - 1] = true;
        usedPairsArray[neighborColor - 1][lastAddedColor - 1] = true;
    }

    private static void removeUsedPairs(int lastAddedColor, int row, int column) {
        for (int i = 0; i < 4; i++) {
            try {
                int neighborColor = graph[row + direct_neighbors_x_off[i]][column + direct_neighbors_y_off[i]];

                if (neighborColor != 0) {
                    removeUsedPair(lastAddedColor, neighborColor);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }

    private static void removeUsedPair(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor - 1][neighborColor - 1] = false;
        usedPairsArray[neighborColor - 1][lastAddedColor - 1] = false;
    }

    private static void printSolution() {
        if (solutionsCount % 1000000 == 0)
            System.out.println(Arrays.deepToString(graph));
    }
}
