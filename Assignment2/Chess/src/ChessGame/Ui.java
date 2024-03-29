package ChessGame;

import java.util.Scanner;

class Ui {
    private Scanner input;

    Ui() {
        input = new Scanner(System.in);
    }

    void celebrateWinner(Player winner) {
        System.out.println("Hurrah, the player " + winner.getName() + " (" + winner.getColor() + ") has won!");
    }

    void check() {
        System.out.println("Check!");
    }

    void printBoard(String board) {
        System.out.print(board);
    }

    void printScore(String score) {
        System.out.print(score);
    }

    void printInvalidMove() {
        System.out.println("Your piece is not allowed to do that! Please enter another move.");
    }

    boolean printWrongInput() {
        System.out.println("Invalid Input!");
        return false;
    }

    String getPlayerName(Color color) {
        System.out.println("What's the name of the player with the color " + color + "? ");
        return input.nextLine();
    }

    String getMove(Player player) {
        System.out.print("It's " + player.getName() + "'s move. Please enter " + player.getColor() + " move: ");
        return input.nextLine();
    }
}
