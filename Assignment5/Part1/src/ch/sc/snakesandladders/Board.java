package ch.sc.snakesandladders;

import ch.sc.squares.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

// TODO: Mention change to public in Javadoc / Readme
public class Board {

    private ArrayList<Square> squareList;
    private int size, numTuples;
    private Player winner;
    private Players players;
    private Queue<Point> tupleQueue;

    public Board(int size, Players players) {   //Constructor
        // TODO: mention fix in Javadoc
        if(size <= 2){
            throw new IllegalArgumentException("Board.board");
        }

        this.size = size;
        this.players = players;

        numTuples = ((this.size - 2) / 3);

        tupleQueue = tupleQueueGenerator();
        squareList = new ArrayList<>();

        initBoard();
    }

    public int getSize() {  //Returns size
        return size;
    }

    private void initBoard() {  // Initializes board with given size: Creates a list with all squares in order
        squareList.add(0, new FirstSquare(this, 0));    // Add first square

        for (int i = 1; i < (size - 1); i++) {  // fill the board with normal squares
            this.squareList.add(i, new NormalSquare(this, i));
        }

        squareList.add(size - 1, new LastSquare(this, size - 1));   // Add last square

        Random r = new Random();

        for (int i = 0; i < numTuples; i++) {    // Add ladders & snakes
            Point tuple = tupleQueue.remove();
            int x = (int) tuple.getX();
            int y = (int) tuple.getY();

            if (r.nextBoolean()) { // Probability p = 0.5 for ladders & snakes
                this.squareList.set(x, new LadderSquare(this, x, y));
            } else {
                this.squareList.set(y, new SnakeSquare(this, y, x));
            }
        }

        for (Player elem : players.getQueue()) {    //Sets every player on first square
            // Add first square to players
            elem.setCurrentSquare(findSquare(0));
            // Add players to first square
            findSquare(0).addPlayer(elem);
        }
    }


    public Square findSquare(int index) {
        return squareList.get(index);
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    Player getWinner() {
        return winner;
    }

    // Improved & fixed tupleQueueGenerator added by PM
    private Queue<Point> tupleQueueGenerator() {
        int result1, result2;
        int low = 1; // lower limit is 2. square
        int high = size - 1; // upper limit is 2. last square
        Random r = new Random();
        Queue<Point> myQueue = new LinkedList<>();
        boolean[] usedSquares = new boolean[size]; // For checking which values are already used
        usedSquares[0] = true; // set first square as used
        usedSquares[size - 1] = true; // set last square as used

        // Generate tuples
        for (int i = 0; i < numTuples; i++) {
            Point point = new Point();

            // calculating 1. tuple point
            do {
                result1 = r.nextInt(high - low) + low;
            } while (usedSquares[result1]);
            usedSquares[result1] = true;

            // calculating 2. tuple point
            do {
                result2 = r.nextInt(high - low) + low;
            } while (usedSquares[result2]);
            usedSquares[result2] = true;

            point.setLocation(Math.min(result1, result2), Math.max(result1, result2));
            myQueue.add(point);
        }

        return myQueue;
    }

}

