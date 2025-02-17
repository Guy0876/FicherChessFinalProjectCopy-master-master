package com.example.ficherchess;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class Controller implements IView {
    @FXML
    private GridPane chessBoard;

    private ImageView selectedPiece;
    private int selectedRow;
    private int selectedCol;
    private Presenter presenter;
    private Circle selectionHalo;

    public void initialize() {
        FischerRandomChess fischerRandomChess = new FischerRandomChess();
        presenter = new Presenter(this, fischerRandomChess);

        char[] startingPosition = fischerRandomChess.getStartingPosition();

        // Add white pieces
        for (int i = 0; i < 8; i++) {
            addPiece(getImageName(startingPosition[i], true), 7, i);
            addPiece("white-pawn.png", 6, i);
        }

        // Add black pieces
        for (int i = 0; i < 8; i++) {
            addPiece(getImageName(startingPosition[i], false), 0, i);
            addPiece("black-pawn.png", 1, i);
        }

        selectionHalo = new Circle(25, Color.TRANSPARENT);
        selectionHalo.setStroke(Color.RED);
        selectionHalo.setStrokeWidth(3);
    }

    private String getImageName(char piece, boolean isWhite) {
        String colorPrefix = isWhite ? "white" : "black";
        switch (piece) {
            case 'R': return colorPrefix + "-rook.png";
            case 'N': return colorPrefix + "-knight.png";
            case 'B': return colorPrefix + "-bishop.png";
            case 'Q': return colorPrefix + "-queen.png";
            case 'K': return colorPrefix + "-king.png";
            default: return "";
        }
    }

    private void addPiece(String imageName, int row, int col) {
        String imagePath = "/pieces/" + imageName;
        Image pieceImage = new Image(getClass().getResourceAsStream(imagePath));
        if (pieceImage.isError()) {
            System.err.println("Error loading image: " + imagePath);
            return;
        }
        ImageView imageView = new ImageView(pieceImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        StackPane stackPane = getNodeByRowColumnIndex(row, col, chessBoard);
        if (stackPane != null) {
            stackPane.getChildren().add(imageView);
        } else {
            System.err.println("No StackPane found at row: " + row + ", col: " + col);
        }
    }

    @FXML
    private void handleSquareClick(MouseEvent event) {
        StackPane square = (StackPane) event.getSource();
        Integer row = GridPane.getRowIndex(square);
        Integer col = GridPane.getColumnIndex(square);

        if (row == null || col == null) {
            return;
        }

        if (selectedPiece != null) {
            // Clicked on an empty square
            presenter.handlePieceMove(selectedRow, selectedCol, row, col);
            removeSelectionHalo();
            selectedPiece = null;
        } else if (square.getChildren().size() > 1 && square.getChildren().get(1) instanceof ImageView) {
            // Clicked on a piece
            selectedPiece = (ImageView) square.getChildren().get(1);
            selectedRow = row;
            selectedCol = col;
            presenter.handlePieceSelection(selectedRow, selectedCol);
            addSelectionHalo(selectedRow, selectedCol);
        }
    }

    private void addSelectionHalo(int row, int col) {
        StackPane square = getNodeByRowColumnIndex(row, col, chessBoard);
        if (square != null) {
            square.getChildren().add(0, selectionHalo);
        }
    }

    private void removeSelectionHalo() {
        StackPane square = getNodeByRowColumnIndex(selectedRow, selectedCol, chessBoard);
        if (square != null) {
            square.getChildren().remove(selectionHalo);
        }
    }

    @Override
    public void movePiece(int oldRow, int oldCol, int newRow, int newCol) {
        System.out.println("movePiece called: " + oldRow + "," + oldCol + " to " + newRow + "," + newCol);
        StackPane oldSquare = getNodeByRowColumnIndex(oldRow, oldCol, chessBoard);
        StackPane newSquare = getNodeByRowColumnIndex(newRow, newCol, chessBoard);
        if (oldSquare != null && newSquare != null) {
            System.out.println("Old square and new square found.");
            ImageView piece = null;
            for (javafx.scene.Node node : oldSquare.getChildren()) {
                if (node instanceof ImageView) {
                    piece = (ImageView) node;
                    break;
                }
            }

            if (piece != null) {
                System.out.println("Piece found and moved.");
                oldSquare.getChildren().remove(piece);

                // Remove any existing ImageView in the new square
                ImageView existingPiece = null;
                for (javafx.scene.Node node : newSquare.getChildren()) {
                    if (node instanceof ImageView) {
                        existingPiece = (ImageView) node;
                        break;
                    }
                }
                ImageView rook = null;
                if (existingPiece != null) {
                    if(existingPiece.getImage().getUrl().contains("rook") && existingPiece.getImage().getUrl().contains("king")) {
                        rook = existingPiece;
                    }
                    newSquare.getChildren().remove(existingPiece);
                }
                if(rook != null){
                    if(!piece.getImage().getUrl().contains("white")){
                        int temp = newCol;
                        newCol = oldCol;
                        oldCol = temp;
                    }
                    StackPane newRookSquare, newKingSquare;
                    if(oldCol > newCol){
                         newRookSquare = getNodeByRowColumnIndex(newRow, 3, chessBoard);
                         newKingSquare = getNodeByRowColumnIndex(newRow, 2, chessBoard);

                    } else {
                         newRookSquare = getNodeByRowColumnIndex(newRow, 5, chessBoard);
                         newKingSquare = getNodeByRowColumnIndex(newRow, 6, chessBoard);
                    }
                    newRookSquare.getChildren().add(rook);
                    newKingSquare.getChildren().add(piece);
                }
                else newSquare.getChildren().add(piece);
            } else {
                System.out.println("No piece found in the old square.");
            }
        } else {
            System.out.println("Old square or new square not found.");
        }
    }

    private StackPane getNodeByRowColumnIndex(int row, int col, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow != null && nodeCol != null && nodeRow == row && nodeCol == col) {
                return (StackPane) node;
            }
        }
        return null;
    }
}