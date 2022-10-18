package tictactoe;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final char[][] grid = new char[3][3];
    private static String[] parameters;
    private static char turn;


    public static void main(String[] args) {
        while (parameters == null) {
            getInput();
        }
        setGrid();
        displayGrid();
        gameLoop();
    }

    private static void getInput() {
        System.out.print("Input command: ");
        String[] input = scanner.nextLine().split("\\s+");

        try {
            if (Objects.equals(input[0], "exit")) {
                System.exit(0);
            } else if (Objects.equals(input[0], "start") && checkPlayerParameter(input[1]) && checkPlayerParameter(input[2])) {
                parameters = input;
            } else {
                System.out.println("Bad parameters!");
            }
        } catch (Exception e) {
            System.out.println("Bad parameters!");
        }
    }

    private static boolean checkPlayerParameter(String parameter) {
        return (Objects.equals(parameter, "easy") || Objects.equals(parameter, "medium") || Objects.equals(parameter, "hard") || Objects.equals(parameter, "user"));
    }

    private static void setGrid() {
        System.out.println("Enter the cells: ");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    private static void displayGrid() {
        System.out.println("---------");
        for (char[] i : grid) {
            System.out.print("| ");
            for (char j : i) {
                System.out.print(j + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private static void gameLoop() {
        while (true) {
            turn = (getTurn()) ? 'O' : 'X';

            if (turn == 'X') makeMove(parameters[1]);
            if (turn == 'O') makeMove(parameters[2]);

            displayGrid();

            if (win(grid, turn)) {
                System.out.println(turn + " wins");
                return;
            }

            if (draw(grid)) {
                System.out.println("Draw");
                return;
            }
        }
    }

    private static void makeMove(String parameter) {
        if (Objects.equals(parameter, "user")) playerMove();
        if (Objects.equals(parameter, "easy")) easyAIMove();
        if (Objects.equals(parameter, "medium")) mediumAIMove();
        if (Objects.equals(parameter, "hard")) hardAIMove();
    }


    private static boolean getTurn() {
        int x = 0;
        int o = 0;

        for (char[] chars : grid) {
            for (char aChar : chars) {
                if (aChar == 'X') x++;
                if (aChar == 'O') o++;
            }
        }

        return x > o;
    }

    private static void playerMove() {
        System.out.print("Enter the coordinates: ");
        String[] coordinates = scanner.nextLine().split("\\s+");

        try {
            int firstCoordinate = Integer.parseInt(coordinates[0]) - 1;
            int secondCoordinate = Integer.parseInt(coordinates[1]) - 1;

            if (grid[firstCoordinate][secondCoordinate] != ' ') {
                System.out.println("This cell is occupied! Choose another one!");
            } else {
                grid[firstCoordinate][secondCoordinate] = turn;
            }
        } catch (NumberFormatException numberFormatException) {
            System.out.println("You should enter numbers!");
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            System.out.println("Coordinates should be from 1 to 3!");
        }
    }

    private static void easyAIMove() {
        System.out.println("Making move level \"easy\"\n");

        Random random = new Random();

        while (true) {
            int firstCoordinate = random.nextInt(3);
            int secondCoordinate = random.nextInt(3);

            if (grid[firstCoordinate][secondCoordinate] == ' ') {
                grid[firstCoordinate][secondCoordinate] = turn;
                break;
            }
        }
    }

    private static void mediumAIMove() {
        System.out.println("Making move level \"medium\"\n");
        char enemyMark = getOpponent(turn);
        boolean mediumAIMove = (winAttempt(turn) || winAttempt(enemyMark));
        if (!mediumAIMove) easyAIMove();
    }

    private static void hardAIMove() {
        System.out.println("Making move level \"hard\"");
        var moves = possibleMoves(grid);
        int bestScore = Integer.MIN_VALUE;
        List<Integer> bestMove = null;

        char[][] newBoard = grid.clone();

        for (List<Integer> move : moves) {
            newBoard[move.get(0)][move.get(1)] =  turn;
            int score = minimax(newBoard, getOpponent(turn), 0, turn);
            newBoard[move.get(0)][move.get(1)] = ' ';
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        assert bestMove != null;
        grid[bestMove.get(0)][bestMove.get(1)] = turn;
    }

    private static char getOpponent(char symbol) {
        return symbol == 'X' ? 'O' : 'X';
    }

    private static int minimax(char[][] board, char symbol, int depth, char startPlayer) {

        char[][] newBoard = board.clone();

        if (win(newBoard, startPlayer)) {
            return 10 - depth;
        } else if (win(newBoard, getOpponent(startPlayer))) {
            return depth - 10;
        } else if (draw(newBoard)) {
            return 0;
        } else {
            int bestScore;
            var moves = possibleMoves(newBoard);
            if (depth % 2 == 1) {
                //maximizing player
                bestScore = Integer.MIN_VALUE;
                for (List<Integer> move : moves) {
                    newBoard[move.get(0)][move.get(1)] = symbol;
                    int score = minimax(newBoard, getOpponent(symbol), depth + 1, startPlayer);
                    newBoard[move.get(0)][move.get(1)] = ' ';
                    bestScore = Math.max(bestScore, score);
                }
            } else {
                //minimizing player
                bestScore = Integer.MAX_VALUE;
                for (List<Integer> move : moves) {
                    newBoard[move.get(0)][move.get(1)] = symbol;
                    int score = minimax(newBoard, getOpponent(symbol), depth + 1, startPlayer);
                    newBoard[move.get(0)][move.get(1)] = ' ';
                    bestScore = Math.min(bestScore, score);
                }
            }
            return bestScore;
        }
    }

    public static List<List<Integer>> possibleMoves(char[][] grid) {
        List<List<Integer>> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == ' ') {
                    List<Integer> move = new ArrayList<>();
                    move.add(i);
                    move.add(j);
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private static boolean winAttempt(char mark) {
        //rows
        if (checkMarksInLine(0, 0, 0, 1, 0, 2, mark)) return true;
        if (checkMarksInLine(1, 0, 1, 1, 1, 2, mark)) return true;
        if (checkMarksInLine(2, 0, 2, 1, 2, 2, mark)) return true;
        //columns
        if (checkMarksInLine(0, 0, 1, 0, 2, 0, mark)) return true;
        if (checkMarksInLine(0, 1, 1, 1, 2, 1, mark)) return true;
        if (checkMarksInLine(0, 2, 1, 2, 2, 2, mark)) return true;
        //diagonals
        if (checkMarksInLine(0, 0, 1, 1, 2, 2, mark)) return true;
        return checkMarksInLine(0, 2, 1, 1, 2, 0, mark);
    }

    private static boolean checkMarksInLine(int i1, int j1, int i2, int j2, int i3, int j3, char mark) {

        int placeCounter = 0;
        int markCounter = 0;
        int rowHolder = 0;
        int columnHolder = 0;

        if (grid[i1][j1] == mark) markCounter++;
        if (grid[i2][j2] == mark) markCounter++;
        if (grid[i3][j3] == mark) markCounter++;
        if (grid[i1][j1] == ' ') {
            placeCounter++;
            rowHolder = i1;
            columnHolder = j1;
        }
        if (grid[i2][j2] == ' ') {
            placeCounter++;
            rowHolder = i2;
            columnHolder = j2;
        }
        if (grid[i3][j3] == ' ') {
            placeCounter++;
            rowHolder = i3;
            columnHolder = j3;
        }

        if ((markCounter == 2 && placeCounter == 1)) {
            grid[rowHolder][columnHolder] = turn;
            return true;
        } else {
            return false;
        }
    }

    private static boolean win(char[][] grid, char mark) {
        boolean win = false;

        for (int i = 0; i < grid.length; i++) {
            win = win || Arrays.equals(grid[i], new char[]{mark, mark, mark}) || (grid[0][i] == mark && grid[1][i] == mark && grid[2][i] == mark);
        }

        return win || (grid[0][0] == mark && grid[1][1] == mark && grid[2][2] == mark) || (grid[0][2] == mark && grid[1][1] == mark && grid[2][0] == mark);
    }

    private static boolean draw(char[][] grid) {
        int emptyPlaces = 0;

        for (char[] row : grid) {
            for (char mark : row) {
                emptyPlaces += (mark == ' ') ? 1 : 0;
            }
        }

        return emptyPlaces == 0;
    }
}