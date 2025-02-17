package com.example.ficherchess.Pieces;

public class King extends Piece {

    private boolean hasMoved = false;
    public King(long bitboard, boolean isWhite) {
        super(bitboard, isWhite);
    }

    @Override
    public long possibleMoves(long position){
        long specificKing = bitboard & position;
        long myPieces = isWhite ? whitePieces : blackPieces;

        // Move up
        long up = specificKing << 8 & ~myPieces;

        // Move down
        long down = specificKing >>> 8 & ~myPieces;

        // Move left
        long left = specificKing << 1 & ~myPieces;

        // Move right
        long right = specificKing >>> 1 & ~myPieces;

        // Move up-right
        long upRight = specificKing << 7 & ~myPieces;

        // Move up-left
        long upLeft = specificKing << 9 & ~myPieces;

        // Move down-right
        long downRight = specificKing >>> 9 & ~myPieces;

        //Move down-left
        long downLeft = specificKing >>> 7 & ~myPieces;

        return (up | down | left | right | upRight | upLeft | downRight | downLeft);
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }
}
