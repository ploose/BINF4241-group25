package ChessGame;

import java.util.ArrayList;

class PiecePot {
    private ArrayList<Piece> blackPlayerList;
    private ArrayList<Piece> whitePlayerList;
    private ArrayList<Piece> blackPlayerListOffBoard;
    private ArrayList<Piece> whitePlayerListOffBoard;

    PiecePot() {
        blackPlayerList = new ArrayList<>();
        whitePlayerList = new ArrayList<>();

        blackPlayerListOffBoard = new ArrayList<>();
        whitePlayerListOffBoard = new ArrayList<>();
    }

    Piece add(Piece piece) {
        if (piece.getColor() == Color.BLACK) {
            blackPlayerList.add(piece);
        } else {
            whitePlayerList.add(piece);
        }

        return piece;
    }

    void remove(Piece piece) {
        if (piece.getColor() == Color.BLACK) {
            blackPlayerList.remove(piece);
            blackPlayerListOffBoard.add(piece);
        } else {
            whitePlayerList.remove(piece);
            whitePlayerListOffBoard.add(piece);
        }
    }

    ArrayList<Piece> getPiecesAlive(Color c){
        if(c == Color.BLACK){
            return new ArrayList<>(blackPlayerList);
        }else{
            return new ArrayList<>(whitePlayerList);
        }
    }

    void replace(Piece o, Piece n){
        if(o.getColor() == n.getColor()){
            if(o.getColor() == Color.BLACK){
                blackPlayerList.remove(o);
                blackPlayerList.add(n);
            }else{
                whitePlayerList.remove(o);
                whitePlayerList.add(o);
            }
        }
    }

    String lostPieces() {
        StringBuilder list = new StringBuilder();

        list.append("Pieces lost: \n");

        list.append("Black: ");
        for (Piece element : blackPlayerListOffBoard) {
            list.append(element);
        }
        list.append("\n");

        list.append("White: ");
        for (Piece element : whitePlayerListOffBoard) {
            list.append(element);
        }
        list.append("\n");

        return list.toString();
    }
}

