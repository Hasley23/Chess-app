package com.esause.chess;

import java.util.ArrayList;

/**
 * Game core
 */
class Core {

    // Game board
    Board board;

    // Temp board
    private Board temp;

    // Board history
    ArrayList<Figure[]> history;


    // core game status
    private GameStatus gameStatus = GameStatus.CONTINUE;

    /**
     * Core constructor
     * @param whiteOnTop the location of the colors
     */
    Core(boolean whiteOnTop, Player player, Player player1){
        board = new Board();
        board.placeFigures(whiteOnTop);
        history = new ArrayList<>();
    }

    GameStatus getGameStatus() {
        return gameStatus;
    }

    void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    Figure[][] getBoard(){
        return board.getFigures();
    }

    /**
     * main chess function
     * @param path is a move path
     * @param player is a current player
     * @param status_check is a current game status
     */
    boolean mainAction(vec path, Player player, Player player1, boolean status_check) {
        if (path == null)
            return false;
        MoveStatus ms;

        boolean end = false;

        if (board.getFigures()[path.getS().getY()][path.getS().getX()].getFigure() == FigureType.KING) {
            end = true;
        }

        if (!status_check)
            ms = board.makeMove(path, player);
        else
        {
            temp = new Board(board.getFigures());
            temp.makeMove(path, player);
            if (checkGame(player1, false) == GameStatus.CHECK) {
                return false;
            }
            else {
                ms = board.makeMove(path, player);
            }
        }

        if (ms == MoveStatus.ELIMINATION && end) {
            gameStatus = GameStatus.NORMAL;
            return true;
        }

        if (ms != MoveStatus.FORBIDDEN) {
            history.add(copyArray(board.getFigures()));

            if (checkArrays()) {
                gameStatus = GameStatus.DRAW;
                return true;
            }
        }
        return !(ms == MoveStatus.FORBIDDEN);
    }

    ArrayList<pos> getInsets(pos position, Player player) {
        return board.findAnyMoves(position, player);
    }

    /**
     * Check equal boards
     * @return true if equals
     */
    private boolean checkArrays(){
        for (int i = 0; i < history.size(); i++) {
            int count = 0;
            for (int j = 0; j < history.size(); j++) {
                if (i != j) {
                    boolean isEqual = true;
                    for (int g = 0; g < 64; g++) {
                        if (history.get(i)[g].getFigure() != history.get(j)[g].getFigure() ||
                                history.get(i)[g].getColor() != history.get(j)[g].getColor()) {
                            isEqual = false;
                            break;
                        }
                    }

                    if (isEqual) {
                        count++;
                    }
                }
                if (count >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * copy of cells array
     * @param cells source array
     * @return new array
     */
    private Figure[] copyArray(Figure[][] cells) {
        Figure[] temp = new Figure[64];
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                temp[counter] = new Figure(cells[i][j].getColor(), cells[i][j].getFigure());
                counter++;
            }
        }
        return temp;
    }

    /**
     * check current game status
     * @param player is a current player
     * @param isRealBoard is a check param
     * @return current game status
     */
    GameStatus checkGame(Player player, boolean isRealBoard) {
        if (isRealBoard)
            return board.checkRules(player);
        else
            return temp.checkRules(player);
    }
}
