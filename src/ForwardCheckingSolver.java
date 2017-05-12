/**
 * Created by r.makowiecki on 11/05/2017.
 */
public class ForwardCheckingSolver implements ISolver {

    private final int graphSize; //a graph represented as 2d array, this variable states the dimension size
    private final ISolutionPrinter solutionPrinter;
    private RunStatistics statistics;

    private final int[] colors;
    private final int[][] graph;
    private final boolean[][] usedPairsArray;
    private final boolean[][][] possibleColorsArray;

    ForwardCheckingSolver(int graphSize, boolean shouldPrintSolutions) {
        this.graphSize = graphSize;
        statistics = new RunStatistics();
        colors = new int[graphSize * 2 + graphSize % 2];
        graph = new int[graphSize][graphSize];
        possibleColorsArray = new boolean[graphSize][graphSize][colors.length];
        usedPairsArray = new boolean[colors.length][colors.length];
        solutionPrinter = shouldPrintSolutions ?
                new SolutionPrinter() :
                placedColors -> {
                    //no-op
                };
        initGraphArray();
        initPossibleColorsArray();
        initColorsArray();
    }

    private void initGraphArray() {
        for (int row = 0; row < graphSize; row++)
            for (int col = 0; col < graphSize; col++)
                graph[row][col] = 0;
    }

    private void initPossibleColorsArray() {
        for (int row = 0; row < graphSize; row++)
            for (int column = 0; column < graphSize; column++)
                for (int color = 0; color < colors.length; color++)
                    possibleColorsArray[row][column][color] = true;
    }

    private void initColorsArray() {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i + 1;
        }
    }

    @Override
    public RunStatistics solveHarmonicGraphColoring() {
        long startTime = System.currentTimeMillis();
        solveWithForwardChecking(0, 0);
        statistics.totalTime = (System.currentTimeMillis() - startTime);
        System.out.println("Forward checking - Solutions found: " + statistics.solutionsCount + ", nodes visited: " + statistics.nodesEnteredCount + ", time: " + statistics.totalTime + " ms");
        return statistics;
    }

    boolean solveWithForwardChecking(int column, int row) {
        if (column >= graphSize) {
            statistics.solutionsCount++;
            return false;
        }

        statistics.nodesEnteredCount++;

        for (int i = 0; i < colors.length; ++i) {
            if (possibleColorsArray[row][column][i]) {
                int color = colors[i];
                graph[row][column] = color;
                applyUsedPairs(column, row, color);
                boolean moveToNextColumn = row >= graphSize - 1;
                if (isForwardCheckedFutureValid(column, row) && solveWithForwardChecking(moveToNextColumn ? column + 1 : column, moveToNextColumn ? 0 : row + 1))
                    return true;
                removeUsedPairs(column, row, color);
                graph[row][column] = 0;
                revertForwardCheck(column, row);
            }
        }
        return false;
    }

    boolean isColorValidForPosition(int col, int row, int color) {
        int neighborColors[] = new int[2];

        if (row > 0)
            neighborColors[0] = graph[row - 1][col];
        if (col > 0)
            neighborColors[1] = graph[row][col - 1];

        for (int i = 0; i < neighborColors.length; i++)
            if (!isColorPairValid(neighborColors[i], color))
                return false;

        return true;
    }

    private boolean isColorPairValid(int currentColor, int newColor) {
        if (currentColor == newColor)
            return false;

        return currentColor == 0 || !usedPairsArray[currentColor - 1][newColor - 1];
    }

    private boolean isForwardCheckedFutureValid(int column, int row) {
        return applyOrRevertForwardCheck(column, row, true);
    }

    private void revertForwardCheck(int column, int row) {
        applyOrRevertForwardCheck(column, row, false);
    }

    boolean applyOrRevertForwardCheck(int column, int row, boolean forward) {
        if (row < graphSize - 1) {
            for (int i = row + 1; i < graphSize; ++i) {
                if (forward) {
                    boolean foundSafe = false;
                    for (int g = 0; g < colors.length; ++g) {
                        if (possibleColorsArray[i][column][g]) {
                            if (!isColorValidForPosition(column, i, colors[g]))
                                possibleColorsArray[i][column][g] = false;
                            else
                                foundSafe = true;
                        }
                    }

                    if (!foundSafe)
                        return false;
                } else {
                    for (int g = 0; g < colors.length; ++g)
                        possibleColorsArray[i][column][g] = true;
                }
            }
        }

        column++;

        if (column < graphSize) {
            for (int i = 0; i < graphSize; ++i) {
                for (int j = column; j < graphSize; ++j) {
                    if (forward) {
                        boolean foundSafe = false;
                        for (int g = 0; g < colors.length; ++g) {
                            if (possibleColorsArray[i][j][g]) {
                                if (!isColorValidForPosition(j, i, colors[g]))
                                    possibleColorsArray[i][j][g] = false;
                                else
                                    foundSafe = true;
                            }
                        }

                        if (!foundSafe)
                            return false;
                    } else {
                        for (int g = 0; g < colors.length; ++g)
                            possibleColorsArray[i][j][g] = true;
                    }
                }
            }
        }
        return true;
    }

    void applyUsedPairs(int col, int row, int color) {
        updateUsedPairs(col, row, color, true);
    }

    void removeUsedPairs(int col, int row, int color) {
        updateUsedPairs(col, row, color, false);
    }

    private void updateUsedPairs(int col, int row, int color, boolean newValue) {
        int neighborColors[] = new int[2];

        if (row > 0)
            neighborColors[0] = graph[row - 1][col];
        if (col > 0)
            neighborColors[1] = graph[row][col - 1];

        for (int i = 0; i < neighborColors.length; i++) {
            int currentColor = neighborColors[i];
            if (currentColor != 0) {
                usedPairsArray[currentColor - 1][color - 1] = newValue;
                usedPairsArray[color - 1][currentColor - 1] = newValue;
            }
        }
    }
}

