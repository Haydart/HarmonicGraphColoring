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
        System.out.println("Solutions found: " + statistics.solutionsCount + " in " + statistics.totalTime + " ms");
        return statistics;
    }

    boolean solveWithForwardChecking(int column, int row) {
        if (column >= graphSize) {
            statistics.solutionsCount++;
            return false;
        }

        for (int i = 0; i < colors.length; ++i) {
            if (!possibleColorsArray[row][column][i])
                continue;

            int color = colors[i];
            graph[row][column] = color;
            SavePairs(column, row, color);
            boolean moveToNextColumn = row >= graphSize - 1;
            boolean future = UpdateFuture(column, row, true);
            if (future && solveWithForwardChecking(moveToNextColumn ? column + 1 : column, moveToNextColumn ? 0 : row + 1))
                return true;

            RemovePairs(column, row, color);
            graph[row][column] = 0;
            UpdateFuture(column, row, false);
        }

        return false;
    }

    boolean IsColorPossible(int col, int row, int color) {
        int currentColors[] = new int[4];

        if (row > 0)
            currentColors[0] = graph[row - 1][col];

        if (row < graphSize - 1)
            currentColors[1] = graph[row + 1][col];

        if (col > 0)
            currentColors[2] = graph[row][col - 1];

        if (col < graphSize - 1)
            currentColors[3] = graph[row][col + 1];

        for (int i = 0; i < 4; i++)
            if (!IsColorPairPossible(currentColors[i], color))
                return false;

        return true;
    }

    private boolean IsColorPairPossible(int currentColor, int newColor) {
        if (currentColor == newColor)
            return false;

        return currentColor == 0 || !usedPairsArray[currentColor - 1][newColor - 1];
//        return color1 != color2 && (color2 == -1 || !usedPairsArray[color1][color2]);
    }

    boolean UpdateFuture(int col, int row, boolean forward) {
        if (row < graphSize - 1) {
            for (int i = row + 1; i < graphSize; ++i) {
                if (forward) {
                    boolean foundSafe = false;
                    for (int g = 0; g < colors.length; ++g) {
                        if (possibleColorsArray[i][col][g]) {
                            if (!IsColorPossible(col, i, colors[g]))
                                possibleColorsArray[i][col][g] = false;
                            else
                                foundSafe = true;
                        }
                    }

                    if (!foundSafe)
                        return false;
                } else {
                    for (int g = 0; g < colors.length; ++g)
                        possibleColorsArray[i][col][g] = true;
                }
            }
        }

        ++col;

        if (col < graphSize) {
            for (int i = 0; i < graphSize; ++i) {
                for (int j = col; j < graphSize; ++j) {
                    if (forward) {
                        boolean foundSafe = false;
                        for (int g = 0; g < colors.length; ++g) {
                            if (possibleColorsArray[i][j][g]) {
                                if (!IsColorPossible(j, i, colors[g]))
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

    void SavePairs(int col, int row, int color) {
        int currentColors[] = new int[4];

        if (row > 0)
            currentColors[0] = graph[row - 1][col];

        if (row < graphSize - 1)
            currentColors[1] = graph[row + 1][col];

        if (col > 0)
            currentColors[2] = graph[row][col - 1];

        if (col < graphSize - 1)
            currentColors[3] = graph[row][col + 1];

        for (int i = 0; i < 4; i++) {
            int currentColor = currentColors[i];
            if (currentColor != 0) {
                usedPairsArray[currentColor - 1][color - 1] = true;
                usedPairsArray[color - 1][currentColor - 1] = true;
            }
        }
    }

    void RemovePairs(int col, int row, int color) {
        int currentColors[] = new int[4];

        if (row > 0)
            currentColors[0] = graph[row - 1][col];

        if (row < graphSize - 1)
            currentColors[1] = graph[row + 1][col];

        if (col > 0)
            currentColors[2] = graph[row][col - 1];

        if (col < graphSize - 1)
            currentColors[3] = graph[row][col + 1];

        for (int i = 0; i < 4; ++i) {
            int currentColor = currentColors[i];
            if (currentColor != 0) {
                usedPairsArray[currentColor - 1][color - 1] = false;
                usedPairsArray[color - 1][currentColor - 1] = false;
            }
        }
    }
}

