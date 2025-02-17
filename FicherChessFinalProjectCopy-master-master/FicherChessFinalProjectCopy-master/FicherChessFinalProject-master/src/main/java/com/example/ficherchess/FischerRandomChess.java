package com.example.ficherchess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FischerRandomChess {
    private int desiredId;
    private List<Integer> freeFields = new ArrayList<>();
    private char[] startingPosition = new char[8];
    private long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueen, whiteKing;
    private long blackPawns, blackKnights, blackBishops, blackRooks, blackQueen, blackKing;

    public FischerRandomChess() {
        generateStartingPosition();
        generateBitboards();
    }

    public char[] getStartingPosition() {
        return startingPosition;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getWhiteQueen() {
        return whiteQueen;
    }

    public long getWhiteKing() {
        return whiteKing;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getBlackQueen() {
        return blackQueen;
    }

    public long getBlackKing() {
        return blackKing;
    }

    private void generateStartingPosition() {
        // Initialize the starting position array with empty spaces
        for (int i = 0; i < 8; i++) {
            startingPosition[i] = ' ';
        }

        // Generate a random game ID between 0 and 959
        Random random = new Random();
        desiredId = random.nextInt(960);

        // Place the pieces according to the Chess960 rules
        placeFirstBishop();
        placeSecondBishop();
        placeQueen();
        placeKnights();
        placeRooksAndKing();
        char[] temp = new char[8];
        for(int i = 0; i < 8; i++) {
            temp[i] = startingPosition[7 - i];
        }
        startingPosition = temp;
    }

    private void generateBitboards() {
        whitePawns = 0xFF000000000000L;
        blackPawns = 0xFF00L;

        for (int i = 0; i < 8; i++) {
            switch (startingPosition[i]) {
                case 'R':
                    whiteRooks |= 1L << (56 + i);
                    blackRooks |= 1L << i;
                    break;
                case 'N':
                    whiteKnights |= 1L << (56 + i);
                    blackKnights |= 1L << i;
                    break;
                case 'B':
                    whiteBishops |= 1L << (56 + i);
                    blackBishops |= 1L << i;
                    break;
                case 'Q':
                    whiteQueen |= 1L << (56 + i);
                    blackQueen |= 1L << i;
                    break;
                case 'K':
                    whiteKing |= 1L << (56 + i);
                    blackKing |= 1L << i;
                    break;
            }
        }
    }

    private void updateFreeFields() {
        freeFields.clear();
        for (int i = 0; i < 8; i++) {
            if (startingPosition[i] == ' ') {
                freeFields.add(i);
            }
        }
    }

    private void placeFirstBishop() {
        int bishop1Pos = desiredId % 4;
        desiredId /= 4;
        startingPosition[1 + 2 * bishop1Pos] = 'B';
    }

    private void placeSecondBishop() {
        int bishop2Pos = desiredId % 4;
        desiredId /= 4;
        startingPosition[0 + 2 * bishop2Pos] = 'B';
    }

    private void placeQueen() {
        updateFreeFields();
        int queenPos = desiredId % 6;
        desiredId /= 6;
        startingPosition[freeFields.get(queenPos)] = 'Q';
    }

    private void placeKnights() {
        updateFreeFields();
        int knightPos = desiredId;
        switch (knightPos) {
            case 0:
                startingPosition[freeFields.get(0)] = 'N';
                startingPosition[freeFields.get(1)] = 'N';
                break;
            case 1:
                startingPosition[freeFields.get(0)] = 'N';
                startingPosition[freeFields.get(2)] = 'N';
                break;
            case 2:
                startingPosition[freeFields.get(0)] = 'N';
                startingPosition[freeFields.get(3)] = 'N';
                break;
            case 3:
                startingPosition[freeFields.get(0)] = 'N';
                startingPosition[freeFields.get(4)] = 'N';
                break;
            case 4:
                startingPosition[freeFields.get(1)] = 'N';
                startingPosition[freeFields.get(2)] = 'N';
                break;
            case 5:
                startingPosition[freeFields.get(1)] = 'N';
                startingPosition[freeFields.get(3)] = 'N';
                break;
            case 6:
                startingPosition[freeFields.get(1)] = 'N';
                startingPosition[freeFields.get(4)] = 'N';
                break;
            case 7:
                startingPosition[freeFields.get(2)] = 'N';
                startingPosition[freeFields.get(3)] = 'N';
                break;
            case 8:
                startingPosition[freeFields.get(2)] = 'N';
                startingPosition[freeFields.get(4)] = 'N';
                break;
            case 9:
                startingPosition[freeFields.get(3)] = 'N';
                startingPosition[freeFields.get(4)] = 'N';
                break;
        }
    }

    private void placeRooksAndKing() {
        updateFreeFields();
        startingPosition[freeFields.get(0)] = 'R';
        startingPosition[freeFields.get(1)] = 'K';
        startingPosition[freeFields.get(2)] = 'R';
    }
}