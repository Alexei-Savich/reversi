import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

public class Reversi {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    private static final int WHITE_DEPTH = 7;
    private static final int BLACK_DEPTH = 7;

    private static final int[][] POSITION_WEIGHTS = {
            {120, -20, 20, 5, 5, 20, -20, 120},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {120, -20, 20, 5, 5, 20, -20, 120}
    };

    private static final int[][] POSITION_WEIGHTS_NO_PENALTIES = {
            {120, 1, 20, 5, 5, 20, 1, 120},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {20, 1, 15, 3, 3, 15, 1, 20},
            {5, 1, 3, 3, 3, 3, 1, 5},
            {5, 1, 3, 3, 3, 3, 1, 5},
            {20, 1, 15, 3, 3, 15, 1, 20},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {120, 1, 20, 5, 5, 20, 1, 120}
    };

    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;

    public static void main(String[] args) {
        Random random = new Random();

        int[][] board = new int[8][8];
        initializeBoard(board);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter White player setup (0 - Player, 1 - Easy AI, 2 - Medium AI, 3 - Hard AI, 4 - Adaptive AI): ");
        int whitePlayer = scanner.nextInt();

        System.out.print("Enter Black player setup (0 - Player, 1 - Easy AI, 2 - Medium AI, 3 - Hard AI, 4 - Adaptive AI): ");
        int blackPlayer = scanner.nextInt();

        int currentPlayer = WHITE;
        while (true) {
            if (currentPlayer == WHITE) {
                System.out.println("WHITE TURN");
            } else {
                System.out.println("BLACK TURN");
            }
            printBoard(board, currentPlayer);
            List<Integer> validMoves = getValidMoves(board, currentPlayer);
            if (validMoves.isEmpty()) {
                int nextPlayer = currentPlayer == WHITE ? BLACK : WHITE;
                List<Integer> nextValidMoves = getValidMoves(board, nextPlayer);
                if (nextValidMoves.isEmpty()) {
                    break;
                } else {
                    System.out.println("No valid moves for the current player. Skipping");
                    currentPlayer = nextPlayer;
                    continue;
                }
            }

            if (currentPlayer == WHITE) {
                if (whitePlayer != 0) {
                    int aiMove;
                    if (whitePlayer == 1) {
                        aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoard, currentPlayer, random);
                    } else if (whitePlayer == 2) {
                        aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoardWithWeightsNoPenalties, currentPlayer, random);
                    } else if (whitePlayer == 3) {
                        aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoardWithWeights, currentPlayer, random);
                    } else {
                        int cornersNum = numOfOccupiedCorners(board, currentPlayer);
                        if (cornersNum == 2) {
                            aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoardWithWeightsNoPenalties, currentPlayer, random);
                        } else if (cornersNum > 2) {
                            aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoard, currentPlayer, random);
                        } else {
                            aiMove = minimax(board, currentPlayer, WHITE_DEPTH, Reversi::evaluateBoardWithWeights, currentPlayer, random);
                        }
                    }
                    int aiRow = aiMove / 8 + 1;
                    int aiColumn = aiMove - aiMove / 8 * 8;
                    System.out.println("AI move: " + aiColumn + aiRow);
                    makeMove(board, currentPlayer, aiMove);
                } else {
                    System.out.print("Enter your move (first column, then row, no spaces): ");
                    int move = scanner.nextInt();
                    int column = move / 10;
                    int row = (move - column * 10) % 9;
                    move = (row - 1) * 8 + column;
                    while (!validMoves.contains(move)) {
                        System.out.println("Invalid move. Please try again.");
                        System.out.print("Enter your move (first column, then row, no spaces): ");
                        move = scanner.nextInt();
                        column = move / 10;
                        row = (move - column * 10) % 8;
                        move = (row - 1) * 8 + column;
                    }
                    makeMove(board, currentPlayer, move);
                }
            } else {
                if (blackPlayer != 0) {
                    int aiMove;
                    if (blackPlayer == 1) {
                        aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoard, currentPlayer, random);
                    } else if (blackPlayer == 2) {
                        aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoardWithWeightsNoPenalties, currentPlayer, random);
                    } else if (whitePlayer == 3) {
                        aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoardWithWeights, currentPlayer, random);
                    } else {
                        int cornersNum = numOfOccupiedCorners(board, currentPlayer);
                        if (cornersNum == 2) {
                            aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoardWithWeightsNoPenalties, currentPlayer, random);
                        } else if (cornersNum > 2) {
                            aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoard, currentPlayer, random);
                        } else {
                            aiMove = minimax(board, currentPlayer, BLACK_DEPTH, Reversi::evaluateBoardWithWeights, currentPlayer, random);
                        }
                    }
                    int aiRow = aiMove / 8 + 1;
                    int aiColumn = aiMove - aiMove / 8 * 8;
                    System.out.println("AI move: " + aiColumn + aiRow);
                    makeMove(board, currentPlayer, aiMove);
                } else {
                    System.out.print("Enter your move (first column, then row, no spaces): ");
                    int move = scanner.nextInt();
                    int column = move / 10;
                    int row = (move - column * 10) % 9;
                    move = (row - 1) * 8 + column;
                    while (!validMoves.contains(move)) {
                        System.out.println("Invalid move. Please try again.");
                        System.out.print("Enter your move (first column, then row, no spaces): ");
                        move = scanner.nextInt();
                        column = move / 10;
                        row = (move - column * 10) % 8;
                        move = (row - 1) * 8 + column;
                    }
                    makeMove(board, currentPlayer, move);
                }
            }
            currentPlayer = currentPlayer == WHITE ? BLACK : WHITE;
            System.out.println();
        }

        printBoard(board, currentPlayer);
        printResult(board);
    }

    private static void initializeBoard(int[][] board) {
        for (int[] row : board) {
            Arrays.fill(row, EMPTY);
        }
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
    }

    private static void printBoard(int[][] board, int player) {
        System.out.println("\033[1;30m  1 2 3 4 5 6 7 8");
        for (int i = 0; i < board.length; i++) {
            System.out.print("\033[1;30m" + (i + 1) + " ");
            for (int j = 0; j < board[i].length; j++) {
                int cellNum = i * 8 + j + 1;
                if (board[i][j] == EMPTY) {
                    if (getValidMoves(board, player).contains(cellNum)) {
                        System.out.print("\033[1;96m" + "\u25A0 ");
                    } else {
                        System.out.print("\033[0;37m" + "\u25A0 ");
                    }
                } else if (board[i][j] == WHITE) {
                    System.out.print("\033[1;34m" + "\u25CF ");
                } else {
                    System.out.print("\033[1;30m" + "\u25CF ");
                }
            }
            System.out.println();
        }
    }

    private static List<Integer> getValidMoves(int[][] board, int player) {
        List<Integer> validMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int move = i * 8 + j + 1;
                if (isValidMove(board, player, move)) {
                    validMoves.add(move);
                }
            }
        }
        return validMoves;
    }

    private static boolean isValidMove(int[][] board, int player, int move) {
        if (move < 1 || move > 64) {
            return false;
        }
        int row = (move - 1) / 8;
        int col = (move - 1) % 8;

        if (board[row][col] != EMPTY) {
            return false;
        }

        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (isValidDirection(board, player, newRow, newCol, direction)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidDirection(int[][] board, int player, int row, int col, int[] direction) {
        int opponent = player == WHITE ? BLACK : WHITE;

        if (!isInBounds(board, row, col) || board[row][col] != opponent) {
            return false;
        }

        while (isInBounds(board, row, col)) {
            row += direction[0];
            col += direction[1];

            if (!isInBounds(board, row, col) || board[row][col] == EMPTY) {
                break;
            }
            if (board[row][col] == player) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInBounds(int[][] board, int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }

    private static void makeMove(int[][] board, int player, int move) {
        int row = (move - 1) / 8;
        int col = (move - 1) % 8;
        board[row][col] = player;

        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            if (isValidDirection(board, player, newRow, newCol, direction)) {
                flipPieces(board, player, newRow, newCol, direction);
            }
        }
    }

    private static void flipPieces(int[][] board, int player, int row, int col, int[] direction) {
        while (board[row][col] != player) {
            board[row][col] = player;
            row += direction[0];
            col += direction[1];
        }
    }

    private static void printResult(int[][] board) {
        int whiteCount = 0;
        int blackCount = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == WHITE) {
                    whiteCount++;
                } else if (cell == BLACK) {
                    blackCount++;
                }
            }
        }

        System.out.println();
        System.out.println("Game over!");
        System.out.println("White pieces: " + whiteCount);
        System.out.println("Black pieces: " + blackCount);

        if (whiteCount > blackCount) {
            System.out.println("White wins!");
        } else if (blackCount > whiteCount) {
            System.out.println("Black wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }

    private static int minimax(int[][] board, int currentPlayer, int depth, BiFunction<int[][], Integer, Integer> evaluationFunction, int playersMove, Random random) {
        int bestMove = -1;
        int bestMoveVal = Integer.MIN_VALUE;
        List<Integer> validMoves = getValidMoves(board, currentPlayer);
        for (int move : validMoves) {
            int[][] newBoard = copyBoard(board);
            makeMove(newBoard, currentPlayer, move);
            Future<Integer> future = executorService.submit(new MinimizeTask(newBoard, currentPlayer, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, evaluationFunction, playersMove));
            try {
                int currMoveVal = future.get();
                // add some random, so every game is different
                if (currMoveVal == bestMoveVal && random.nextInt() > 50) {
                    bestMove = move;
                    continue;
                }
                if (currMoveVal >= bestMoveVal) {
                    bestMoveVal = currMoveVal;
                    bestMove = move;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Move of the %d; score: %d; move: %d%n", currentPlayer, bestMoveVal, bestMove);
        return bestMove;
    }

    private static int maximize(int[][] board, int currentPlayer, int depth, int alpha, int beta, BiFunction<int[][], Integer, Integer> evaluationFunction, int playersMove) {
        if (depth == 0) {
            return evaluationFunction.apply(board, playersMove);
        }
        if (isGameOver(board)) {
            return evaluateBoard(board, playersMove);
        }

        List<Integer> validMoves = getValidMoves(board, currentPlayer);
        int maxEval = Integer.MIN_VALUE;

        for (int move : validMoves) {
            int[][] newBoard = copyBoard(board);
            makeMove(newBoard, currentPlayer, move);
            int eval = minimize(newBoard, opponent(currentPlayer), depth - 1, alpha, beta, evaluationFunction, playersMove);

            if (eval > maxEval) {
                maxEval = eval;
            }
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return maxEval;
    }

    private static int minimize(int[][] board, int currentPlayer, int depth, int alpha, int beta, BiFunction<int[][], Integer, Integer> evaluationFunction, int playersMove) {
        if (depth == 0) {
            return evaluationFunction.apply(board, playersMove);
        }
        if (isGameOver(board)) {
            return evaluateBoard(board, playersMove);
        }

        List<Integer> validMoves = getValidMoves(board, currentPlayer);
        int minEval = Integer.MAX_VALUE;

        for (int move : validMoves) {
            int[][] newBoard = copyBoard(board);
            makeMove(newBoard, currentPlayer, move);
            int eval = maximize(newBoard, opponent(currentPlayer), depth - 1, alpha, beta, evaluationFunction, playersMove);

            if (eval < minEval) {
                minEval = eval;
            }
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

    private static int opponent(int currentPlayer) {
        return currentPlayer == BLACK ? WHITE : BLACK;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return newBoard;
    }

    private static boolean isGameOver(int[][] board) {
        return getValidMoves(board, WHITE).isEmpty() && getValidMoves(board, BLACK).isEmpty();
    }

    private static int evaluateBoard(int[][] board, int player) {
        int playerCount = 0;
        int opponentCount = 0;
        int opponent = player == WHITE ? BLACK : WHITE;

        for (int[] row : board) {
            for (int cell : row) {
                if (cell == player) {
                    playerCount++;
                } else if (cell == opponent) {
                    opponentCount++;
                }
            }
        }
        return playerCount - opponentCount;
    }

    private static int evaluateBoardWithWeights(int[][] board, int player) {
        int playerCount = 0;
        int opponentCount = 0;
        int opponent = player == WHITE ? BLACK : WHITE;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == player) {
                    playerCount += POSITION_WEIGHTS[i][j];
                } else if (board[i][j] == opponent) {
                    opponentCount += POSITION_WEIGHTS[i][j];
                }
            }
        }
        return playerCount - opponentCount;
    }

    private static int evaluateBoardWithWeightsNoPenalties(int[][] board, int player) {
        int playerCount = 0;
        int opponentCount = 0;
        int opponent = player == WHITE ? BLACK : WHITE;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == player) {
                    playerCount += POSITION_WEIGHTS_NO_PENALTIES[i][j];
                } else if (board[i][j] == opponent) {
                    opponentCount += POSITION_WEIGHTS_NO_PENALTIES[i][j];
                }
            }
        }
        return playerCount - opponentCount;
    }

    private static int numOfOccupiedCorners(int[][] board, int player) {
        int counter = 0;
        if (board[0][0] == player) {
            counter++;
        }
        if (board[0][7] == player) {
            counter++;
        }
        if (board[7][0] == player) {
            counter++;
        }
        if (board[7][7] == player) {
            counter++;
        }
        return counter;
    }

    private static class MinimizeTask implements Callable<Integer> {
        private final int[][] board;
        private final int currentPlayer;
        private final int depth;
        private final int alpha;
        private int beta;
        private final BiFunction<int[][], Integer, Integer> evaluationFunction;
        private final int playersMove;

        public MinimizeTask(int[][] board, int currentPlayer, int depth, int alpha, int beta, BiFunction<int[][], Integer, Integer> evaluationFunction, int playersMove) {
            this.board = board;
            this.currentPlayer = currentPlayer;
            this.depth = depth;
            this.alpha = alpha;
            this.beta = beta;
            this.evaluationFunction = evaluationFunction;
            this.playersMove = playersMove;
        }

        @Override
        public Integer call() {
            if (depth == 0) {
                return evaluationFunction.apply(board, playersMove);
            }
            if (isGameOver(board)) {
                return evaluateBoard(board, playersMove);
            }

            List<Integer> validMoves = getValidMoves(board, currentPlayer);
            int minEval = Integer.MAX_VALUE;

            for (int move : validMoves) {
                int[][] newBoard = copyBoard(board);
                makeMove(newBoard, currentPlayer, move);
                int eval = maximize(newBoard, opponent(currentPlayer), depth - 1, alpha, beta, evaluationFunction, playersMove);

                if (eval < minEval) {
                    minEval = eval;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

}

