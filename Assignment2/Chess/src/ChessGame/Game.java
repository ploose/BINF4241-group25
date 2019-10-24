package ChessGame;

import java.util.ArrayList;

class Game {
    private Board board;
    private Ui userInterface;
    private Player currentPlayer, black, white;
    private boolean isRunning;
    private String move;
    private ArrayList<String> log;

    Game() {
        board = new Board(this);

        userInterface = new Ui();
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

    private void run() {
        isRunning = true;

        userInterface.printBoard(board.toString());
        while (isRunning) {
            boolean isValidMove = false;

            board.refresh();

            while (!isValidMove) {
                isValidMove = move();
            }
            log.add(move);
            System.out.println(log);

            userInterface.printBoard(board.toString());
            userInterface.printScore(board.lostPieces());

            if(checkCheckMate(white)){
                userInterface.celebrateWinner(black);
                break;
            }else if(checkCheckMate(black)){
                userInterface.celebrateWinner(white);
                break;
            }

            swapPlayer();
        }
    }

    private boolean checkCheck(Player p) {
        Piece king = board.getKingSquare(p).getCurrentPiece();
        ArrayList<Piece> enemyPieces = board.getEnemies(p);

        for (Piece enemyPiece : enemyPieces) { // go through all enemy pieces
            for(Square targetSquare : enemyPiece.getPossibleTargets()) { // go through all their possible targets
                // squares
                if(targetSquare == king.current){
                    return true;
                }
            }
        }
        return false;
    }

    // returns true if there is at least one remaining attacker for the king
    private boolean isKingTarget(Piece king,  ArrayList<Piece> enemyPieces){
        for (Piece enemyPiece : enemyPieces){ // go through all enemy pieces
            if(enemyPiece.getPossibleTargets().contains(king.current)){ // returns true if king is possible target
                return true;
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

        if (!isKingTarget(king, enemyPieces)){
            return false;
        }

        // run through all friendly pieces
        for(Piece friendlyPiece : friendlyPieces){
            tempSquare = friendlyPiece.current; // Save current square
            // Test whether friendly piece can block attacker
            for(Square moveSquare : friendlyPiece.getPossibleMoveSquares()){
                tempSquare.removePiece(); // temporarily remove piece for checking
                moveSquare.addPiece(friendlyPiece); // temporarily adds piece for checking
                friendlyPiece.current = moveSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive

                if(!isKingTarget(king, enemyPieces)){ // If the king can now move again he isn't in a checkMate
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

            // Test whether friendly piece can eat attacker
            for(Square targetSquare : friendlyPiece.getPossibleTargets()){
                tempSquare.removePiece(); // temporarily remove piece for checking
                tempPiece = targetSquare.removePiece(); // temporarily remove target for checking
                enemyPieces.remove(tempPiece);
                targetSquare.addPiece(friendlyPiece); // temporarily adds piece for checking
                friendlyPiece.current = targetSquare;
                board.refresh(); // refresh the move & eat squares of all pieces alive

                if(!isKingTarget(king, enemyPieces)){ // If the king can now move again he isn't in a checkMate
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
        return true; // If no possible action could open up a movement option for the king, he's in a checkMate
    }

    private boolean move() {
        move = userInterface.getMove(currentPlayer);

        if (move.equals("o-o-o")) {
            if (castleLong()) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (move.equals("o-o")) {
            if (castleShort()) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (checkInput(move, "en passant")) {
            if (enPassant(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(4)), translate(move.charAt(5)))) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (checkInput(move, "move")) {
            if (board.move(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(3)), translate(move.charAt(4)), currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else if (checkInput(move, "eat")) {
            if (board.eat(translate(move.charAt(0)), translate(move.charAt(1)), translate(move.charAt(3)), translate(move.charAt(4)), currentPlayer.getColor())) {
                return true;
            } else {
                userInterface.printInvalidMove();
                return false;
            }

        } else {
            return userInterface.printWrongInput();
        }
    }

    //TODO: add danger check
    private boolean castleLong() {
        if (currentPlayer.getColor() == Color.BLACK) {
            Square squareOne = board.getSquare(4, 0), squareTwo = board.getSquare(0, 0);

            if (!(squareOne.hasType("class ChessGame.King") && squareTwo.hasType("class ChessGame.Rook"))) {
                return false;
            }

            if (squareOne.getCurrentPiece().hasMoved() || squareTwo.getCurrentPiece().hasMoved()) {
                return false;
            }

            for (int i = 1; i < 4; i++) {
                if (board.getSquare(i, 0).isOccupied()) {
                    return false;
                }
            }

            Piece tmp = squareOne.removePiece();

            board.move(0, 0, 3, 0, currentPlayer.getColor());
            board.getSquare(2, 0).addPiece(tmp);
            tmp.hasMoved();

            return true;

        } else {
            Square squareOne = board.getSquare(4, 7), squareTwo = board.getSquare(0, 7);

            if (!(squareOne.hasType("class ChessGame.King") && squareTwo.hasType("class ChessGame.Rook"))) {
                return false;
            }

            if (squareOne.getCurrentPiece().hasMoved() || squareTwo.getCurrentPiece().hasMoved()) {
                return false;
            }

            for (int i = 1; i < 4; i++) {
                if (board.getSquare(i, 7).isOccupied()) {
                    return false;
                }
            }

            Piece tmp = squareOne.removePiece();

            board.move(0, 7, 3, 7, currentPlayer.getColor());
            board.getSquare(2, 7).addPiece(tmp);
            tmp.hasMoved();

            return true;
        }
    }

    //TODO: add danger check
    private boolean castleShort() {
        if (currentPlayer.getColor() == Color.BLACK) {
            Square squareOne = board.getSquare(4, 0), squareTwo = board.getSquare(7, 0);

            if (!(squareOne.hasType("class ChessGame.King") && squareTwo.hasType("class ChessGame.Rook"))) {
                return false;
            }

            if (squareOne.getCurrentPiece().hasMoved() || squareTwo.getCurrentPiece().hasMoved()) {
                return false;
            }

            for (int i = 5; i < 7; i++) {
                if (board.getSquare(i, 0).isOccupied()) {
                    return false;
                }
            }

            Piece tmp = squareOne.removePiece();

            board.move(7, 0, 5, 0, currentPlayer.getColor());
            board.getSquare(6, 0).addPiece(tmp);
            tmp.hasMoved();

            return true;

        } else {
            Square squareOne = board.getSquare(4, 7), squareTwo = board.getSquare(7, 7);

            if (!(squareOne.hasType("class ChessGame.King") && squareTwo.hasType("class ChessGame.Rook"))) {
                return false;
            }

            if (squareOne.getCurrentPiece().hasMoved() || squareTwo.getCurrentPiece().hasMoved()) {
                return false;
            }

            for (int i = 5; i < 7; i++) {
                if (board.getSquare(i, 7).isOccupied()) {
                    return false;
                }
            }

            Piece tmp = squareOne.removePiece();

            board.move(7, 7, 5, 7, currentPlayer.getColor());
            board.getSquare(6, 7).addPiece(tmp);
            tmp.hasMoved();

            return true;
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

    void setWinner(Player winner) {
        isRunning = false;
        userInterface.celebrateWinner(winner);
    }

    private boolean checkInput(String move, String mode) {
        if (move.length() < 5 || move.length() > 6) {
            return false;
        }

        switch (mode) {
            case "move":
                return move.substring(0, 1).matches("[a-h]") && move.substring(1, 2).matches("[0-9]") &&
                        move.substring(2, 3).matches("-") &&
                        move.substring(3, 4).matches("[a-h]") && move.substring(4, 5).matches("[0-9]");

            case "eat":
                return move.substring(0, 1).matches("[a-h]") && move.substring(1, 2).matches("[0-9]") &&
                        move.substring(2, 3).matches("x") &&
                        move.substring(3, 4).matches("[a-h]") && move.substring(4, 5).matches("[0-9]");
            case "en passant":
                return move.substring(0, 1).matches("[a-h]") && move.substring(1, 2).matches("[0-9]") &&
                        move.substring(2, 4).matches("ep") &&
                        move.substring(4, 5).matches("[a-h]") && move.substring(5, 6).matches("[0-9]");
            default:
                return false;
        }
    }

    private int translate(char current) {
        int num = 0;

        switch (current) {
            case 'a':
            case '8':
                num = 0;
                break;

            case 'b':
            case '7':
                num = 1;
                break;

            case 'c':
            case '6':
                num = 2;
                break;

            case 'd':
            case '5':
                num = 3;
                break;

            case 'e':
            case '4':
                num = 4;
                break;

            case 'f':
            case '3':
                num = 5;
                break;

            case 'g':
            case '2':
                num = 6;
                break;

            case 'h':
            case '1':
                num = 7;
                break;
        }
        return num;
    }
}
