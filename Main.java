package minesweeper;


import java.util.Scanner;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_RED = "\u001B[31m";

    public static final int SIZE = 6; // edit this number for a bigger board

    public static final int MINES_COUNT = (SIZE*3)/2;
    public static final int BOMB = 9;

    static boolean firstInput = true;

    static int[][] playArea = new int[SIZE + 2][SIZE + 2];

    static int[][] isChecked = new int[SIZE][SIZE];

    static int[][] bombExclusionZone = new int[SIZE][SIZE];

    static int[] temp = new int[2];

    public static void main(String[] args) {

        int[][] gameMatrix = new int[SIZE][SIZE];

        boolean lost;

        boolean successful;

        int[] input = new int[2];
        // input[0] for row, input[1] for column

        Scanner scanner = new Scanner(System.in);

        setPlayArea(playArea);
        System.out.println("SIZE: " + SIZE);
        System.out.println("Bomb count: " + (MINES_COUNT-1));

        while (true) {
            successful = false;
            while (!successful) {
                try {
                    System.out.print("Enter row: ");
                    input[0] = scanner.nextInt();
                    System.out.print("Enter column: ");
                    input[1] = scanner.nextInt();
                    if (input[0] >= 0 && input[0] <= SIZE-1 && input[1] >= 0 && input[1] <= SIZE-1) {
                        successful = true;
                    }
                    else
                        System.out.println("Enter a valid number (0-" + (SIZE-1) + ")");
                } catch (Exception e) {
                    System.out.println("Enter a valid number (0-" + (SIZE-1) + ")");
                    scanner.next();
                }
            }

            if (firstInput) {
                firstInput = false;
                placeBombs(gameMatrix, input);
                evaluateBoard(gameMatrix);
            }

            if (gameMatrix[input[0]][input[1]] == BOMB) {
                lost = true;
                break;
            }
            else {
                openZeros(gameMatrix, input);
                openNumbers(gameMatrix);
                isChecked[input[0]][input[1]] = 1;
                printOpened(gameMatrix);
            }

            int winNum = SIZE*SIZE-MINES_COUNT;
            int counter = 0;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (isChecked[i][j] == 1) {
                        counter++;
                    }
                }
            }
            if (counter == winNum) {
                lost = false;
                break;
            }
        }
        if (lost) {
            System.out.println("You lost");
        }
        else {
            System.out.println("You win");
        }
        printMatrix(gameMatrix);
    }

    public static void openZeros(int[][] gameMatrix, int[] input) {

        int k = input[0] + 1;
        int l = input[1] + 1;

        for (int m = k - 1; m < k + 2; m++) {
            for (int n = l - 1; n < l + 2; n++) {
                if (playArea[m][n] == 1) {
                    if (gameMatrix[m-1][n-1] == 0) {
                        if (isChecked[m - 1][n - 1] == 0) {
                            isChecked[m - 1][n - 1] = 1;
                            temp[0] = m-1;
                            temp[1] = n-1;
                            openZeros(gameMatrix, temp);
                        }
                    }
                }
            }
        }
    }

    public static void openNumbers(int[][] gameMatrix){
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (gameMatrix[i][j] == 0 && isChecked[i][j] == 1) {
                    int k = i + 1;
                    int l = j + 1;
                    for (int m = k-1; m < k+2; m++) {
                        for (int n = l-1; n < l+2; n++) {
                            if (playArea[m][n] == 1) {
                                isChecked[m-1][n-1] = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void placeBombs(int[][] gameMatrix, int[] input) {
        gameMatrix[input[0]][input[1]] = 0;
        int x;
        int y;

        x = (int)(Math.random() * gameMatrix.length);
        y = (int)(Math.random() * gameMatrix.length);

        int k = input[0] + 1;
        int l = input[1] + 1;

        for (int m = k - 1; m < k + 2; m++) {
            for (int n = l - 1; n < l + 2; n++) {
                if (playArea[m][n] == 1) {
                    bombExclusionZone[m - 1][n - 1] = 1;
                }
            }
        }

        for (int i = 0; i < MINES_COUNT; i++) {
            while (bombExclusionZone[x][y] == 1) {
                x = randomizeValues();
                y = randomizeValues();
            }
            gameMatrix[x][y] = BOMB;
            x = randomizeValues();
            y = randomizeValues();
        }
    }

    public static void evaluateBoard(int[][] gameMatrix) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (gameMatrix[i][j] == BOMB) {
                    int k = i + 1;
                    int l = j + 1;
                    for (int m = k - 1; m < k + 2; m++) {
                        for (int n = l - 1; n < l + 2; n++) {
                            if (playArea[m][n] == 1) {
                                if (gameMatrix[m-1][n-1] == gameMatrix[i][j])
                                    continue;
                                gameMatrix[m-1][n-1]++;
                            }
                        }
                    }
                }
            }
        }
    }

    public static int randomizeValues() {
        return (int)(Math.random() * SIZE);
    }

    public static void setPlayArea(int[][] playArea) {
        for (int i = 0; i < SIZE + 2; i++) {
            for (int j = 0; j < SIZE + 2; j++) {
                if (i > 0 && j > 0){
                    if (i < SIZE + 1 && j < SIZE + 1) {
                        playArea[i][j] = 1;
                    }
                }
                else {
                    playArea[i][j] = 0;
                }
            }
        }
    }

    public static void printMatrix(int[][] gameMatrix) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (gameMatrix[i][j] == BOMB) {
                    System.out.print(ANSI_RED+"B  "+ANSI_RESET);
                }
                else
                    System.out.print(gameMatrix[i][j] + "  ");
            }
            System.out.println();
        }
    }

    public static void printOpened(int[][] gameMatrix) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isChecked[i][j] == 1) {
                    System.out.print(gameMatrix[i][j]+"  ");
                }
                else
                    System.out.print(ANSI_RED+"X  "+ANSI_RESET);
            }
            System.out.println();
        }
    }

}

