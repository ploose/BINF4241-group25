package ChessGame;

import java.util.ArrayList;

class Pawn extends Piece implements MovementStrategy, IPawn {
    private ArrayList<Square> possibleMoveSquares;
    private boolean hasMoved;

    private ArrayList<Square> possibleAttackSquares;

    Pawn(Color color, Square current) {
        super(color, current);
    }

    public boolean CanBeCaptured(){
        return true;
    }

    //TODO:
    public void move() {}

    public boolean checkPromote() {
        return true;
    }

    // TODO: Current system allows player to land & eat own pieces, needs fixing!
    public boolean isValidMove(Board board, Square current, Square next) {
        Square temp;
        if (this.color == Color.WHITE) { // only moves up
            // capture
            temp = board.getSquare(current.x - 1, current.y - 1);
            if (temp == next && temp.isOccupied()) {
                return true;
            }
            temp = board.getSquare(current.x + 1, current.y - 1);
            if (temp == next && temp.isOccupied()) {
                return true;
            }
            // move
            temp = board.getSquare(current.x, current.y - 1);
            if (temp == next) {
                return true;
            } else if (temp.isOccupied()) {
                return false;
            }
            if (!hasMoved) {
                temp = board.getSquare(current.x, current.y - 2);
                if (temp == next) {
                    return true;
                }
            }

        } else if (this.color == Color.BLACK) { // only moves down
            // capture
            temp = board.getSquare(current.x - 1, current.y + 1);
            if (temp == next && temp.isOccupied()) {
                return true;
            }
            temp = board.getSquare(current.x + 1, current.y + 1);
            if (temp == next && temp.isOccupied()) {
                return true;
            }
            // move
            temp = board.getSquare(current.x, current.y + 1);
            if (temp == next) {
                return true;
            } else if (temp.isOccupied()) {
                return false;
            }
            if (!hasMoved) {
                temp = board.getSquare(current.x, current.y + 2);
                if (temp == next) {
                    return true;
                }
            }

        }

        if (!hasMoved) {
            hasMoved = true;
        }
        return false;
    }

    public boolean canBePromoted() {
        return true;
    }

    //TODO: Important: seperate the movesquares from the attacksquares for the pawn!

    public ArrayList<Square> getMoveSquares(Board board) {

        Square current = this.current;
        Square temp;
        int x, y;
        if (this.color == Color.WHITE) { // only moves up
            //TODO: Check if out of board
            // capture
            temp = board.getSquare(current.x - 1, current.y - 1);
            if (!temp.isOccupied()) {
                possibleAttackSquares.add(temp);
            }

            temp = board.getSquare(current.x + 1, current.y - 1);
            if (!temp.isOccupied()) {
                possibleAttackSquares.add(temp);
            }

            // move
            temp = board.getSquare(current.x, current.y - 1);
            if (!temp.isOccupied()) {
                possibleMoveSquares.add(temp);
            }

            if (!hasMoved) {
                temp = board.getSquare(current.x, current.y - 2);
                if (!temp.isOccupied()) {
                    possibleMoveSquares.add(temp);
                }
            }

        } else if (this.color == Color.BLACK) { // only moves down
            // capture
            temp = board.getSquare(current.x - 1, current.y + 1);
            if (!temp.isOccupied()) {
                possibleAttackSquares.add(temp);
            }

            temp = board.getSquare(current.x + 1, current.y + 1);
            if (!temp.isOccupied()) {
                possibleAttackSquares.add(temp);
            }

            // move
            temp = board.getSquare(current.x, current.y + 1);
            if (!temp.isOccupied()) {
                possibleMoveSquares.add(temp);
            }

            if (!hasMoved) {
                temp = board.getSquare(current.x, current.y + 2);
                if (!temp.isOccupied()) {
                    possibleMoveSquares.add(temp);
                }
            }

        }
        return possibleMoveSquares;
    }

    public String toString() {
        return color.toString().charAt(0) + "P";
    }
}
