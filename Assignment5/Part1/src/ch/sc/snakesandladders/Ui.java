package ch.sc.snakesandladders;

import java.util.ArrayList;
import java.util.Scanner;

class Ui {
    private Scanner input;
    private int spacing; // Determines length until board printing (to ensure consistent output formatting)

    Ui() {  //Constructor for Ui class
        input = new Scanner(System.in);
        setSpacing("Initial state");
        System.out.println("Hello and Welcome to a new Game of Snakes & Ladders!\n");
    }

    int getBoardSize(){ // Takes user-input and returns given size Added by PM
        int boardSize = 0;
        System.out.println("How many squares should the gameboard have?");

        while (boardSize < 2) {
            System.out.print("Please enter a number greater or equal to 2: ");
            Scanner input = new Scanner(System.in);
            while(!input.hasNextInt()){
                System.out.print("Please enter a number greater or equal to 2: ");
                input.next();
            }
            boardSize = input.nextInt();
        }
        System.out.println();
        return boardSize;
    }

    ArrayList<Player> getPlayers() {    //Takes user-input and creates players
        int numberOfPlayers = 0;
        System.out.println("How many players want to play?");

        // Checks for invalid input
        while (numberOfPlayers < 2 || numberOfPlayers > 4) {
            System.out.print("Please enter a number between 2 and 4: ");
            Scanner input = new Scanner(System.in);
            while(!input.hasNextInt()){
                System.out.print("Please enter a number between 2 and 4: ");
                input.next();
            }
            numberOfPlayers = input.nextInt();
        }
        ArrayList<Player> playerList = new ArrayList<>();

        for (int i = 1; i <= numberOfPlayers; i++) {
            System.out.print("Name of the " + i + ". player: ");
            Player player = new Player(input.next());
            setSpacing(player.getName() + " rolls X");
            playerList.add(player);
        }
        System.out.println();
        return playerList;
    }

    private void setSpacing(String input){
        spacing = Math.max(spacing, (input).length());
    }

    void celebrateWinner(Board board, Player winner) {   //Prints the winner of the game
        System.out.print(String.format("%-" + spacing + "s", "Final state")+": ");
        printBoard(board);
        System.out.println(winner.getName() + " wins!");
    }

    void printTurn(Board board, int steps, Player current){
        System.out.print(String.format("%-" + spacing + "s", (current.getName() + " rolls " + steps))+": ");
        printBoard(board);
    }

    void printInitialState(Board board){
        System.out.print(String.format("%-" + spacing + "s", "Initial State")+": ");
        printBoard(board);
    }

    private void printBoard(Board board){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < board.getSize(); i++){
            output.append(board.findSquare(i).toString());
        }
        System.out.println(output);

    }
}