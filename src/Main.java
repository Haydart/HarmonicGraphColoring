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
        for (int row = 0; row < N; row++)
            for (int col = 0; col < N; col++)
                graph[row][col] = -1;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i;
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
            graph[row][column] = currentColor;
            if (isValidMove(currentColor, row, column)) {
                saveUsedPairs(currentColor, row, column);
                solveWithBackTracking(index + 1);
                removeUsedPairs(currentColor, row, column);
            }
            graph[row][column] = -1;
        }
        return false;
    }

    private static boolean isValidMove(int lastInsertedColor, int lastInsertedRow, int lastInsertedColumn) {
        int neighborRow, neighborColumn;
        for (int i = 0; i < 12; i++) {
            neighborRow = lastInsertedRow + harmonic_neighbors_x_off[i];
            neighborColumn = lastInsertedColumn + harmonic_neighbors_y_off[i];
            if (neighborRow >= 0 && neighborRow < N && neighborColumn >= 0 && neighborColumn < N) {
                if (graph[lastInsertedRow][lastInsertedColumn] == graph[neighborRow][neighborColumn]) {
                    return false;
                }
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
        return usedPairsArray[color1][color2] && usedPairsArray[color2][color1];
    }

    private static void saveUsedPairs(int currentColor, int lastInsertedRow, int lastInsertedColumn) {
        int neighborRow, neighborColumn, neighborColor;
        for (int i = 0; i < 4; i++) {
            neighborRow = lastInsertedRow + direct_neighbors_x_off[i];
            neighborColumn = lastInsertedColumn + direct_neighbors_y_off[i];
            if (neighborRow >= 0 && neighborRow < N && neighborColumn >= 0 && neighborColumn < N) {
                neighborColor = graph[neighborRow][neighborColumn];
                if (neighborColor != -1) {
                    markColorPairAsUsed(currentColor, neighborColor);
                }
            }
        }
    }

    private static void markColorPairAsUsed(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor][neighborColor] = true;
        usedPairsArray[neighborColor][lastAddedColor] = true;
    }

    private static void removeUsedPairs(int lastAddedColor, int row, int column) {
        int neighborRow, neighborColumn, neighborColor;
        for (int i = 0; i < 4; i++) {
            neighborRow = row + direct_neighbors_x_off[i];
            neighborColumn = column + direct_neighbors_y_off[i];
            if (neighborRow >= 0 && neighborRow < N && neighborColumn >= 0 && neighborColumn < N) {
                neighborColor = graph[neighborRow][neighborColumn];
                if (neighborColor != -1) {
                    removeUsedPair(lastAddedColor, neighborColor);
                }
            }
        }
    }

    private static void removeUsedPair(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor][neighborColor] = false;
        usedPairsArray[neighborColor][lastAddedColor] = false;
    }

    private static void printSolution() {
        if (solutionsCount % 1000000 == 0)
            System.out.println(Arrays.deepToString(graph));
    }
}
