package com.example.ficherchess;

import com.example.ficherchess.Pieces.*;

import java.util.ArrayList;

public class Model {
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private long possibleMoves;
    private Piece selectedPiece;
    private boolean isWhiteTurn;
    private FischerRandomChess frc;

    public Model(FischerRandomChess fischerRandomChess) {
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        possibleMoves = 0L;
        isWhiteTurn = true;
        frc = fischerRandomChess;
        initializeBoard();
    }
    public void initializeBoard() {
        whitePieces.add(new WhitePawns(frc.getWhitePawns(), true));
        whitePieces.add(new Knights(frc.getWhiteKnights(), true));
        whitePieces.add(new Bishops(frc.getWhiteBishops(), true));
        whitePieces.add(new Rooks(frc.getWhiteRooks(), true));
        whitePieces.add(new Queen(frc.getWhiteQueen(), true));
        whitePieces.add(new King(frc.getWhiteKing(), true));
        blackPieces.add(new BlackPawns(frc.getBlackPawns(), false));
        blackPieces.add(new Knights(frc.getBlackKnights(), false));
        blackPieces.add(new Bishops(frc.getBlackBishops(), false));
        blackPieces.add(new Rooks(frc.getBlackRooks(), false));
        blackPieces.add(new Queen(frc.getBlackQueen(), false));
        blackPieces.add(new King(frc.getBlackKing(), false));
    }
    public static long indexToBitboard(int row, int col) {
        int index = row * 8 + col;
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Index must be between 0 and 63");
        }
        return 1L << index;
    }
    public void setSelectedPiece(int row, int col) {
        long specificPiece = indexToBitboard(row, col);
        possibleMoves = getPossibleMoves(specificPiece);
    }

    public boolean isLegalMove(int oldRow, int oldCol, int newRow, int newCol) {
        long movePosition = indexToBitboard(newRow, newCol);
        Rooks rooks = (Rooks)(isWhiteTurn ? whitePieces.get(3) : blackPieces.get(3));
        if((rooks.getBitboard() & movePosition) != 0) {
            long kingPosition = findKingPosition(selectedPiece.isWhite());
            boolean isLeftRook = selectedPiece.isWhite() ? kingPosition > movePosition : kingPosition < movePosition;
            King king = (King)(isWhiteTurn ? whitePieces.get(5) : blackPieces.get(5));
            if(king.getHasMoved() && (isLeftRook ? rooks.getHasMovedLeft() : rooks.getHasMovedRight())) {
                return false;
            }
            long temp = king.getBitboard();
            while((temp & rooks.getBitboard()) == 0){
                if(isLeftRook) temp <<= 1;
                else temp >>>= 1;
                if((temp & Piece.allPieces) != 0) return false;
            }
            castle(movePosition, indexToBitboard(oldRow, oldCol), isLeftRook, king, rooks);
            return true;
        }
        boolean isLegal = (possibleMoves & movePosition) != 0;
        if(isLegal) updateTurn(oldRow, oldCol, newRow, newCol);
        return isLegal;
    }

    private void castle(long movePosition, long oldPosition, boolean isLeftRook, King king, Rooks rooks) {
        long kingPosition = oldPosition;
        long rookPosition = movePosition;
        int newKingPositionCol = isLeftRook ? 2 : 6;
        int newKingPositionRow = isWhiteTurn ? 7 : 0;
        long newKingPosition = indexToBitboard(newKingPositionRow, newKingPositionCol);
        long newRookPosition = isLeftRook ? newKingPosition << 1 : newKingPosition >>> 1;
        king.setBitboard((king.getBitboard() & ~kingPosition) | newKingPosition);
        rooks.setBitboard((rooks.getBitboard() & ~rookPosition) | newRookPosition);
        Piece.allPieces = (Piece.allPieces & ~kingPosition & ~rookPosition) | newKingPosition | newRookPosition;
        if(isWhiteTurn) {
            Piece.whitePieces = (Piece.whitePieces & ~kingPosition & ~rookPosition) | newKingPosition | newRookPosition;
        } else {
            Piece.blackPieces = (Piece.blackPieces & ~kingPosition & ~rookPosition) | newKingPosition | newRookPosition;
        }
        if(isLeftRook) rooks.setHasMovedLeft(true);
        else rooks.setHasMovedRight(true);
        king.setHasMoved(true);
    }

    private void updateTurn(int oldRow, int oldCol, int newRow, int newCol) {
        ArrayList<Piece> opponentPieces = isWhiteTurn ? blackPieces : whitePieces;
        long oldPosition = indexToBitboard(oldRow, oldCol);
        long newPosition = indexToBitboard(newRow, newCol);
        selectedPiece.setBitboard((selectedPiece.getBitboard() & ~oldPosition) | newPosition);
        Piece.allPieces &= ~oldPosition;
        Piece.allPieces |= newPosition;
        if(isWhiteTurn) {
            Piece.whitePieces &= ~oldPosition;
            Piece.whitePieces |= newPosition;
        } else {
            Piece.blackPieces &= ~oldPosition;
            Piece.blackPieces |= newPosition;
        }
        King king = (King)(isWhiteTurn ? whitePieces.get(5) : blackPieces.get(5));
        Rooks rooks = (Rooks)(isWhiteTurn ? whitePieces.get(3) : blackPieces.get(3));
        if(selectedPiece instanceof King && !king.getHasMoved()) {
            king.setHasMoved(true);
        }
        else if(selectedPiece instanceof Rooks && !king.getHasMoved() && (!rooks.getHasMovedLeft() || !rooks.getHasMovedRight())) {
            long kingPosition = findKingPosition(!selectedPiece.isWhite());
            boolean isLeftRook = selectedPiece.isWhite() ? kingPosition > oldPosition : kingPosition < oldPosition;
            if(isLeftRook) rooks.setHasMovedLeft(true);
            else rooks.setHasMovedRight(true);
        }
        for (Piece opponentPiece : opponentPieces) {
            if ((opponentPiece.getBitboard() & newPosition) != 0) {
                opponentPiece.setBitboard(opponentPiece.getBitboard() & ~newPosition);
                if(isWhiteTurn) {
                    Piece.blackPieces &= ~newPosition;
                } else {
                    Piece.whitePieces &= ~newPosition;
                }
                break;
            }
        }
        isWhiteTurn = !isWhiteTurn;
        possibleMoves = 0L;
        selectedPiece = null;

        // Check if the move puts the opponent's king in check
        if (isKingInCheck(!isWhiteTurn)) {
            System.out.println((isWhiteTurn ? "Black" : "White") + " king is in check!");
            Piece.check = true;

            if(isCheckmate(isWhiteTurn)) {
                System.out.println((isWhiteTurn ? "Black" : "White") + " won the game !!!");
            }
        }
    }

    public long getPossibleMoves(long specificPiece) {
        long moves = 0L;
        if(isWhiteTurn) {
            for (Piece piece : whitePieces) {
                if ((piece.getBitboard() & specificPiece) != 0) {
                    selectedPiece = piece;
                    moves = piece.possibleMoves(specificPiece);
                }
            }
        } else {
            for (Piece piece : blackPieces) {
                if ((piece.getBitboard() & specificPiece) != 0) {
                    selectedPiece = piece;
                    moves = piece.possibleMoves(specificPiece);
                }
            }
        }
        if(Piece.check) {
            moves = filterMovesThatResolveCheck(selectedPiece, moves, specificPiece);
        }
        return moves;
    }
    public long findKingPosition(boolean isWhite) {
        ArrayList<Piece> pieces = isWhite ? blackPieces : whitePieces;
        for (Piece piece : pieces) {
            if (piece instanceof King) {
                return piece.getBitboard();
            }
        }
        return 0L;
    }
    public boolean isKingInCheck(boolean isWhite) {
        long kingPosition = findKingPosition(isWhite);
        ArrayList<Piece> opponentPieces = isWhite ? whitePieces : blackPieces;
        for (Piece piece : opponentPieces) {
            if(piece instanceof Rooks || piece instanceof Bishops) {
                long temp = piece.getBitboard();
                for (int i = 0; i < 64; i++) {
                    long movePosition = 1L << i;
                    if ((temp & movePosition) != 0) {
                        long possibleMoves = piece.possibleMoves(movePosition);
                        if ((possibleMoves & kingPosition) != 0) {
                            return true;
                        }
                    }
                }
            }
            else if ((piece.possibleMoves(piece.getBitboard()) & kingPosition) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean doesMoveResolveCheck(Piece piece, long movePosition, long specificPiece) {
        long originalPosition = piece.getBitboard();
        long originalAllPieces = Piece.allPieces;
        long originalWhitePieces = Piece.whitePieces;
        long originalBlackPieces = Piece.blackPieces;

        // Save the state of the opponent piece that might be captured
        Piece capturedPiece = null;
        ArrayList<Piece> opponentPieces = piece.isWhite() ? blackPieces : whitePieces;
        for (Piece opponentPiece : opponentPieces) {
            if ((opponentPiece.getBitboard() & movePosition) != 0) {
                capturedPiece = opponentPiece;
                break;
            }
        }

        // Make the move
        piece.setBitboard(movePosition | (piece.getBitboard() & ~specificPiece));
        Piece.allPieces = (Piece.allPieces & ~specificPiece) | movePosition;
        if (piece.isWhite()) {
            Piece.whitePieces = (Piece.whitePieces & ~specificPiece) | movePosition;
        } else {
            Piece.blackPieces = (Piece.blackPieces & ~specificPiece) | movePosition;
        }
        if (capturedPiece != null) {
            capturedPiece.setBitboard(capturedPiece.getBitboard() & ~movePosition);
            if (piece.isWhite()) {
                Piece.blackPieces &= ~movePosition;
            } else {
                Piece.whitePieces &= ~movePosition;
            }
        }

        boolean isInCheck = isKingInCheck(!piece.isWhite());

        // Revert the move
        piece.setBitboard(originalPosition);
        Piece.allPieces = originalAllPieces;
        if (piece.isWhite()) {
            Piece.whitePieces = originalWhitePieces;
        } else {
            Piece.blackPieces = originalBlackPieces;
        }
        if (capturedPiece != null) {
            capturedPiece.setBitboard(capturedPiece.getBitboard() | movePosition);
            if (piece.isWhite()) {
                Piece.blackPieces |= movePosition;
            } else {
                Piece.whitePieces |= movePosition;
            }
        }

        return !isInCheck;
    }

    public long filterMovesThatResolveCheck(Piece piece, long moves, long specificPiece) {

        long validMoves = 0L;
        for (int i = 0; i < 64; i++) {
            long movePosition = 1L << i;
            if ((moves & movePosition) != 0 && doesMoveResolveCheck(piece, movePosition, specificPiece)) {
                validMoves |= movePosition;
            }
        }
        return validMoves;
    }

    public boolean isCheckmate(boolean isWhite) {
        ArrayList<Piece> pieces = isWhite ? whitePieces : blackPieces;
        long validMoves = 0L;
        for (Piece piece : pieces) {
            long temp = piece.getBitboard();
            for(int i = 0; i < 64; i++) {
                long movePosition = 1L << i;
                if((temp & movePosition) != 0) {
                    long possibleMoves = piece.possibleMoves(movePosition);
                    validMoves = filterMovesThatResolveCheck(piece, possibleMoves, movePosition);
//                    if (possibleMoves != 0) {
//                        return false;
//                    }
                }
            }

        }
        if(validMoves != 0)
            return false;
        return true;
    }



}