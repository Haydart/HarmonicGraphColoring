/**
 * Created by r.makowiecki ographSize 10/05/2017.
 */
public class BacktrackingSolver implements ISolver {

    private final int graphSize; //a graph represented as 2d array, this variable states the dimension size
    private final ISolutionPrinter solutionPrinter;
    private RunStatistics statistics;

    private final int[] colors;
    private final int[][] graph;
    private final boolean[][] usedPairsArray;

    /*final int[] harmonic_neighbors_x_off = {-2, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2};
    final int[] harmonic_neighbors_y_off = {0, 0, 1, -1, -1, 1, -2, 2, 0, 1, -1, 0};
    final int[] direct_neighbors_x_off = {0, -1, 0, 1};
    final int[] direct_neighbors_y_off = {-1, 0, 1, 0};*/

    BacktrackingSolver(int graphSize, boolean shouldPrintSolutions) {
        this.graphSize = graphSize;
        statistics = new RunStatistics();
        colors = new int[graphSize * 2 + graphSize % 2];
        graph = new int[graphSize][graphSize];
        usedPairsArray = new boolean[colors.length][colors.length];
        solutionPrinter = shouldPrintSolutions ?
                new SolutionPrinter() :
                placedColors -> {
                    //no-op
                };
        initGraphArray(graphSize);
        initColorsArray();
    }

    private void initGraphArray(int graphSize) {
        for (int row = 0; row < graphSize; row++)
            for (int col = 0; col < graphSize; col++)
                graph[row][col] = -1;
    }

    private void initColorsArray() {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i;
        }
    }

    @Override
    public RunStatistics solveHarmonicGraphColoring() {
        long startTime = System.currentTimeMillis();
        solveWithBackTracking(0);
        statistics.totalTime = (System.currentTimeMillis() - startTime);
        System.out.println("Solutions found: " + statistics.solutionsCount + " in " + statistics.totalTime + " ms");
        return statistics;
    }

    private boolean solveWithBackTracking(int index) {
        int row = index / graphSize;
        int column = index % graphSize;
        if (index == graphSize * graphSize) {
            statistics.solutionsCount++;
            solutionPrinter.printSolution(graph);
            return true;
        }

        statistics.nodesEnteredCount++;

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

    private boolean isValidMove(int lastInsertedColor, int lastInsertedRow, int lastInsertedColumn) {
        int neighborColors[] = new int[]{-1, -1, -1, -1};

        if (lastInsertedRow > 0)
            neighborColors[0] = graph[lastInsertedRow - 1][lastInsertedColumn];

        if (lastInsertedColumn > 0)
            neighborColors[1] = graph[lastInsertedRow][lastInsertedColumn - 1];

        if (lastInsertedRow < graphSize - 1)
            neighborColors[2] = graph[lastInsertedRow + 1][lastInsertedColumn];

        if (lastInsertedColumn < graphSize - 1)
            neighborColors[3] = graph[lastInsertedRow][lastInsertedColumn + 1];

        for (int i = 0; i < 4; i++)
            if (!isColorPairValid(lastInsertedColor, neighborColors[i]))
                return false;
        return true;
    }

    private boolean isColorPairValid(int color1, int color2) {
        return color1 != color2 && (color2 == -1 || !usedPairsArray[color1][color2]);
    }

    private void saveUsedPairs(int currentColor, int lastInsertedRow, int lastInsertedColumn) {
        int neighborColors[] = new int[]{-1, -1, -1, -1};

        if (lastInsertedRow > 0)
            neighborColors[0] = graph[lastInsertedRow - 1][lastInsertedColumn];

        if (lastInsertedRow < graphSize - 1)
            neighborColors[1] = graph[lastInsertedRow + 1][lastInsertedColumn];

        if (lastInsertedColumn > 0)
            neighborColors[2] = graph[lastInsertedRow][lastInsertedColumn - 1];

        if (lastInsertedColumn < graphSize - 1)
            neighborColors[3] = graph[lastInsertedRow][lastInsertedColumn + 1];

        for (int i = 0; i < 4; i++) {
            if (neighborColors[i] != -1) {
                markColorPairAsUsed(currentColor, neighborColors[i]);
            }
        }
    }

    private void markColorPairAsUsed(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor][neighborColor] = true;
        usedPairsArray[neighborColor][lastAddedColor] = true;
    }

    private void removeUsedPairs(int lastAddedColor, int lastInsertedRow, int lastInsertedColumn) {
        int neighborColors[] = new int[]{-1, -1, -1, -1};

        if (lastInsertedRow > 0)
            neighborColors[0] = graph[lastInsertedRow - 1][lastInsertedColumn];

        if (lastInsertedRow < graphSize - 1)
            neighborColors[1] = graph[lastInsertedRow + 1][lastInsertedColumn];

        if (lastInsertedColumn > 0)
            neighborColors[2] = graph[lastInsertedRow][lastInsertedColumn - 1];

        if (lastInsertedColumn < graphSize - 1)
            neighborColors[3] = graph[lastInsertedRow][lastInsertedColumn + 1];

        for (int i = 0; i < 4; i++) {
            if (neighborColors[i] != -1) {
                removeUsedPair(lastAddedColor, neighborColors[i]);
            }
        }
    }

    private void removeUsedPair(int lastAddedColor, int neighborColor) {
        usedPairsArray[lastAddedColor][neighborColor] = false;
        usedPairsArray[neighborColor][lastAddedColor] = false;
    }
}
