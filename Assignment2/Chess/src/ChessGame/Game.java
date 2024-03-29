package ChessGame;

import java.util.*;

class Game {
    private Board board;
    private Ui userInterface;
    private Player currentPlayer, black, white;
    private boolean isRunning;
    private String move;
    private ArrayList<String> log;

    Game() {
        userInterface = new Ui();
        board = new Board(this);

        isRunning = false;

        getPlayers();

        currentPlayer = black;

        log = new ArrayList<>();

        run();
    }

    private void getPlayers() {
        black = new Player(userInterface.getPlayerName(Color.BLACK), Color.BLACK);
        white = new Player(userInterface.getPlayerName(Color.WHITE), Color.WHITE);
    }

    Player getPlayer(Color color) {
        if (color == Color.BLACK){
            return black;
        }
        else{
            return white;
        }
    }

    private void run() {
        isRunning = true;

        userInterface.printBoard(board.toString());

        while (isRunning) {
            boolean isValidMove = false;

            board.refresh();

            if (checkCheck(white)) {
                if(checkCheckMate(white)) {
                    userInterface.celebrateWinner(black);
                    break;
                } else {

                    userInterface.check();
                }

            }

            if (checkCheck(black)) {
                if (checkCheckMate(black)) {
                    userInterface.celebrateWinner(white);
                    break;
                } else {
                    userInterface.check();
                }
            }

            while (!isValidMove) {
                isValidMove = move();
            }

            log.add(move);

            userInterface.printBoard(board.toString());
            userInterface.printScore(board.lostPieces());

            swapPlayer();
        }
    }

    // returns true if there is at least one remaining attacker for the king
    private boolean isKingTarget(Piece king,  ArrayList<Piece> enemyPieces){

        Iterator<Piece> ep = enemyPieces.iterator();
        Piece enemyPiece;

        while (ep.hasNext()){
            enemyPiece = ep.next();
            if(enemyPiece.getPossibleTargets().contains(king.current)){ // returns true if king is possible target
                return true;
            }
        }
        return false;
    }

    boolean checkCheck(Player p) {
        Piece king = board.getKingSquare(p).getCurrentPiece();
        ArrayList<Piece> enemyPieces = board.getEnemies(p);

        Iterator<Piece> ep = enemyPieces.iterator();
        Iterator<Square> ts;
        Square targetSquare;
        Piece piece;

        while (ep.hasNext()) {
            piece = ep.next();
            ts = piece.getPossibleTargets().iterator();
            while (ts.hasNext()) {
                targetSquare = ts.next();
                if (targetSquare == king.current) {

                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkCheckMate(Player p) {
        Piece king = board.getKingSquare(p).getCurrentPiece();
        ArrayList<Piece> friendlyPieces = board.getFriendlies(p);
        ArrayList<Piece> enemyPieces = board.getEnemies(p);
        Square tempSquare;
        Piece tempPiece;

        if (!isKingTarget(king, enemyPieces)) {
            return false;
        }
        Piece friendlyPiece;
        Square moveSquare;
        Square targetSquare;

        if (!isKingTarget(king, enemyPieces)) {
            return false;
        }

        Iterator<Piece> fp = friendlyPieces.iterator();

        // run through all friendly pieces
        while (fp.hasNext()) {
            friendlyPiece = fp.next();
            tempSquare = friendlyPiece.current;
            Iterator<Square> ms = friendlyPiece.getPossibleMoveSquares().iterator();
            while (ms.hasNext()) {
                moveSquare = ms.next();
                tempSquare.removePiece(); // temporarily remove piece for checking
                moveSquare.addPiece(friendlyPiece); // temporarily adds piece for checking
                friendlyPiece.current = moveSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive

                if (!isKingTarget(king, enemyPieces)) { // If the king can now move again he isn't in a checkMate
                    moveSquare.removePiece(); // remove piece
                    tempSquare.addPiece(friendlyPiece); // add piece back
                    friendlyPiece.current = tempSquare;
                    board.refresh(); // refresh the move & eat squares of all pieces alive
                    return false;
                }

                moveSquare.removePiece(); // remove piece
                tempSquare.addPiece(friendlyPiece); // add piece back
                friendlyPiece.current = tempSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive
            }

            Iterator<Square> ts = friendlyPiece.getPossibleTargets().iterator();
            while (ts.hasNext()) {
                targetSquare = ts.next();
                tempSquare.removePiece(); // temporarily remove piece for checking
                tempPiece = targetSquare.removePiece(); // temporarily remove target for checking
                enemyPieces.remove(tempPiece);
                targetSquare.addPiece(friendlyPiece); // temporarily adds piece for checking
                friendlyPiece.current = targetSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive

                if (!isKingTarget(king, enemyPieces)) { // If the king can now move again he isn't in a checkMate
                    targetSquare.removePiece(); // remove piece
                    targetSquare.addPiece(tempPiece); // add target back
                    enemyPieces.add(tempPiece);
                    tempSquare.addPiece(friendlyPiece); // add piece back
                    friendlyPiece.current = tempSquare;
                    board.refresh(); // refresh the move & eat squares of all pieces alive
                    return false;
                }

                targetSquare.removePiece(); // remove piece
                targetSquare.addPiece(tempPiece); // add target back
                enemyPieces.add(tempPiece);
                tempSquare.addPiece(friendlyPiece); // add piece back
                friendlyPiece.current = tempSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive
            }
        }
        return true;
    }

    private boolean move() {
        move = userInterface.getMove(currentPlayer);

        if (move.equals("o-o-o")) {
            if (board.castleLong(currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (move.equals("o-o")) {
            if (board.castleShort(currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (checkInput(move, "move")) {
            if (move.length() == 6 && board.move(translate(move.charAt(1)), translate(move.charAt(2)), translate(move.charAt(4)),
                    translate(move.charAt(5)), currentPlayer.getColor())) {
                return true;
            }else if (move.length() == 5 && board.move(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(3)),
                    translate(move.charAt(4)), currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (checkInput(move, "eat")) {
            if (move.length() == 6 && board.eat(translate(move.charAt(1)), translate(move.charAt(2)), translate(move.charAt(4)),
                    translate(move.charAt(5)), currentPlayer.getColor())) {
                return true;
            }else if (move.length() == 5 && board.eat(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(3)),
                    translate(move.charAt(4)), currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if(checkInput(move, "promotion")) {
            if (board.promotion(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(3)),
                    translate(move.charAt(4)), move.substring(4, 5), currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }
        }

        else if (checkInput(move, "en passant")) {
            if (enPassant(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(4)),
                    translate(move.charAt(5)))) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else {
            return userInterface.printWrongInput();
        }
    }

    private boolean enPassant(int x1, int y1, int x2, int y2) {
        String check = log.get(log.size() -1);

        if (!board.getSquare(x1, y1).isOccupied()) {
            return false;
        }

        if (currentPlayer.getColor() == Color.BLACK) {
            if (board.getSquare(x2, y2 + 1).isOccupied()) {
                if (board.getSquare(x2, y2 + 1).getCurrentPiece().getColor() == Color.BLACK) {
                    return false;
                }

                return false;
            }

            if (translate(check.charAt(1)) != (y2 + 1) && translate(check.charAt(0)) != x2) {
                return false;
            }

            if (translate(check.charAt(4)) != (y2 - 1) && translate(check.charAt(3)) != x2) {
                return false;
            }

        } else {
            if (board.getSquare(x2, y2 - 1).isOccupied()) {
                if (board.getSquare(x2, y2 - 1).getCurrentPiece().getColor() == Color.WHITE) {
                    return false;
                }

                return false;
            }

            if (translate(check.charAt(1)) != (y2 - 1) && translate(check.charAt(0)) != x2) {
                return false;
            }

            if (translate(check.charAt(4)) != (y2 + 1) && translate(check.charAt(3)) != x2) {
                return false;
            }

        }

        board.enPassant(x1, y1, x2, y2, currentPlayer.getColor());
        return true;
    }

    private void swapPlayer() {
        if (currentPlayer == black) {
            currentPlayer = white;
        } else {
            currentPlayer = black;
        }
    }

    private boolean checkInput(String move, String mode) {
        switch (mode) {
            case "move":
                if (move.length() == 6) {
                    return move.substring(0, 1).matches("[TNBKQ]") && move.substring(1, 2).matches("[a-h]") &&
                            move.substring(2, 3).matches("[0-9]") && move.substring(3, 4).matches("-") &&
                            move.substring(4, 5).matches("[a-h]") && move.substring(5, 6).matches("[0-9]");
                }else if (move.length() == 5) {
                    return move.substring(0, 1).matches("[a-h]") &&
                            move.substring(1, 2).matches("[0-9]") && move.substring(2, 3).matches("-") &&
                            move.substring(3, 4).matches("[a-h]") && move.substring(4, 5).matches("[0-9]") &&
                            (board.getSquare(translate(move.charAt(0)), translate(move.charAt(1))).getCurrentPiece().getClass() == Pawn.class);

                } else {
                    return false;
                }

            case "eat":
                if (move.length() == 6) {
                    return move.substring(0, 1).matches("[TNBKQ]") && move.substring(1, 2).matches("[a-h]") &&
                            move.substring(2, 3).matches("[0-9]") && move.substring(3, 4).matches("x") &&
                            move.substring(4, 5).matches("[a-h]") && move.substring(5, 6).matches("[0-9]");
                }else if (move.length() == 5) {
                    return move.substring(0, 1).matches("[a-h]") &&
                            move.substring(1, 2).matches("[0-9]") && move.substring(2, 3).matches("x") &&
                            move.substring(3, 4).matches("[a-h]") && move.substring(4, 5).matches("[0-9]") &&
                            (board.getSquare(translate(move.charAt(0)), translate(move.charAt(1))).getCurrentPiece().getClass() == Pawn.class);

                } else {
                    return false;
                }

            case "en passant":
                if (move.length() == 6) {
                    return move.substring(0, 1).matches("[a-h]") && move.substring(1, 2).matches("[0-9]") &&
                            move.substring(2, 4).matches("ep") &&
                            move.substring(4, 5).matches("[a-h]") && move.substring(5, 6).matches("[0-9]");
                } else {
                    return false;
                }

            case "promotion":
                if (move.length() == 7) {
                    return move.substring(0, 1).matches("[a-h]") && move.substring(1, 2).matches("[0-9]") &&
                            move.substring(2, 3).matches("-") &&
                            move.substring(3, 4).matches("[a-h]") && move.substring(4, 5).matches("[0-9]") &&
                            move.substring(5, 6).matches("=") && move.substring(6, 7).matches("[TNBQ]");
                } else {
                    return false;
                }

            default:
                return false;
        }
    }

    private int translate(char current) {
        int num = 0;

        switch (current) {
            case 'a':
                num = 0;
                break;
            case '8':
                num = 0;
                break;

            case 'b':
                num = 1;
                break;
            case '7':
                num = 1;
                break;

            case 'c':
                num = 2;
                break;
            case '6':
                num = 2;
                break;

            case 'd':
                num = 3;
                break;
            case '5':
                num = 3;
                break;

            case 'e':
                num = 4;
                break;
            case '4':
                num = 4;
                break;

            case 'f':
                num = 5;
                break;
            case '3':
                num = 5;
                break;

            case 'g':
                num = 6;
                break;
            case '2':
                num = 6;
                break;

            case 'h':
                num = 7;
                break;
            case '1':
                num = 7;
                break;
        }
        return num;
    }
}
