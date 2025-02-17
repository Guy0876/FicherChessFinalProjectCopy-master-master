package com.example.ficherchess.Pieces;

public class Rooks extends Piece {
    private boolean hasMovedRight = false;
    private boolean hasMovedLeft = false;

    public Rooks(long bitboard, boolean isWhite) {
        super(bitboard, isWhite);
    }

    @Override
    public long possibleMoves(long position) {
        long specificRook = bitboard & position;
        long moves = 0L;
        moves |= horizontalMoves(specificRook);
        moves |= verticalMoves(specificRook);

        return moves;
    }

    public void setHasMovedRight(boolean hasMovedRight) {
        this.hasMovedRight = hasMovedRight;
    }

    public void setHasMovedLeft(boolean hasMovedLeft) {
        this.hasMovedLeft = hasMovedLeft;
    }

    public boolean getHasMovedRight() {
        return hasMovedRight;
    }

    public boolean getHasMovedLeft() {
        return hasMovedLeft;
    }
}
