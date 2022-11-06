package com.esause.chess;

import java.util.ArrayList;

/**
 * Describes a chessboard
 */
class Board {
    //Chess board array
    private Figure[][] chessboard;

    // Array for moves generator
    private final ArrayList<pos> temp;

    // for 50 moves rule
    private int moveCounter = 0;

    // for AI
    private boolean kingDied = false;

    boolean getKingDied() {
        return kingDied;
    }

    int getMoveCounter() {return moveCounter;}

    Figure[][] getFigures() {
        return chessboard;
    }

    Board() {
        temp = new ArrayList<>();
    }


    Board (Figure[][] chessboard) {
        this.chessboard = new Figure[8][8];
        temp = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessboard[i][j] = new Figure(chessboard[i][j].getColor(), chessboard[i][j].getFigure());
            }
        }
    }

    Board (Figure[][] chessboard, int moveCounter, boolean kingDied) {
        this.kingDied = kingDied;
        this.chessboard = new Figure[8][8];
        this.moveCounter = moveCounter;
        temp = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.chessboard[i][j] = new Figure(chessboard[i][j].getColor(), chessboard[i][j].getFigure());
            }
        }
    }

    /**
     * Set start position of pieces
     * @param whiteOnTop the location of the colors
     */
    void placeFigures(boolean whiteOnTop) {
        Color color = whiteOnTop ? Color.WHITE : Color.BLACK;
        Color color2 = whiteOnTop ? Color.BLACK : Color.WHITE;

        if (!whiteOnTop) {
            // Init start chess position
            chessboard = new Figure[][] {{new Figure(color, FigureType.ROOK),
                    new Figure(color, FigureType.KNIGHT),
                    new Figure(color, FigureType.BISHOP),
                    new Figure(color, FigureType.QUEEN),
                    new Figure(color, FigureType.KING),
                    new Figure(color, FigureType.BISHOP),
                    new Figure(color, FigureType.KNIGHT),
                    new Figure(color, FigureType.ROOK)},
                    {new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN)},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN)},
                    {new Figure(color2, FigureType.ROOK),
                            new Figure(color2, FigureType.KNIGHT),
                            new Figure(color2, FigureType.BISHOP),
                            new Figure(color2, FigureType.QUEEN),
                            new Figure(color2, FigureType.KING),
                            new Figure(color2, FigureType.BISHOP),
                            new Figure(color2, FigureType.KNIGHT),
                            new Figure(color2, FigureType.ROOK)}};
        } else {
            // Init start chess
            chessboard = new Figure[][] {{new Figure(color, FigureType.ROOK),
                    new Figure(color, FigureType.KNIGHT),
                    new Figure(color, FigureType.BISHOP),
                    new Figure(color, FigureType.KING),
                    new Figure(color, FigureType.QUEEN),
                    new Figure(color, FigureType.BISHOP),
                    new Figure(color, FigureType.KNIGHT),
                    new Figure(color, FigureType.ROOK)},
                    {new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN),
                            new Figure(color, FigureType.PAWN)},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure(), new Figure(),
                            new Figure(), new Figure()},
                    {new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN),
                            new Figure(color2, FigureType.PAWN)},
                    {new Figure(color2, FigureType.ROOK),
                            new Figure(color2, FigureType.KNIGHT),
                            new Figure(color2, FigureType.BISHOP),
                            new Figure(color2, FigureType.KING),
                            new Figure(color2, FigureType.QUEEN),
                            new Figure(color2, FigureType.BISHOP),
                            new Figure(color2, FigureType.KNIGHT),
                            new Figure(color2, FigureType.ROOK)}};
        }


    }

    public MoveStatus checkMove(vec path, Player player) {

        if (path.getS().getY() < 0 || path.getS().getY() > 7 ||
        path.getS().getX() < 0 || path.getS().getX() > 7) {
            return MoveStatus.FORBIDDEN;
        }

        final int dY = path.getS().getY() - path.getF().getY();
        final int dX = path.getS().getX() - path.getF().getX();


        if (chessboard[path.getF().getY()][path.getF().getX()].getColor() != player.getColor()) {
            return MoveStatus.FORBIDDEN;
        }

        if (chessboard[path.getS().getY()][path.getS().getX()].getColor() == player.getColor()) {
            return MoveStatus.FORBIDDEN;
        }

        switch (chessboard[path.getF().getY()][path.getF().getX()].getFigure()) {
            case PAWN:
                if (player.getDirection() && dY < 0 || !player.getDirection() && dY > 0)
                    return MoveStatus.FORBIDDEN;
                if (Math.abs(dX) == 0 && Math.abs(dY) < 3 &&
                        chessboard[path.getS().getY()][path.getS().getX()].getFigure() == FigureType.NONE){
                    if (Math.abs(dY) == 2 && (path.getF().getY() == 1 && player.getDirection() ||
                            path.getF().getY() == 6 && !player.getDirection())) {
                        int y = (path.getF().getY() + path.getS().getY()) / 2;

                        if (chessboard[y][path.getF().getX()].getFigure() != FigureType.NONE) {
                            return MoveStatus.FORBIDDEN;
                        }
                    }else {
                        if (Math.abs(dY) != 1)
                        return MoveStatus.FORBIDDEN;}
                    return MoveStatus.REGULAR;
                }
                else if (Math.abs(dX) == 1 && Math.abs(dY) == 1 &&
                        chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                        chessboard[path.getS().getY()][path.getS().getX()].getFigure() != FigureType.NONE)
                    return MoveStatus.ELIMINATION;
                return MoveStatus.FORBIDDEN;
            case KING:
                if (Math.abs(dX) < 2 && Math.abs(dY) < 2) {
                    // Current figure color
                    Color color = chessboard[path.getF().getY()][path.getF().getX()].getColor();
                    // opponent
                    Player p = new Player(false, !player.getDirection(), color == Color.WHITE ? Color.BLACK : Color.WHITE);
                    // copy of board
                    Board brd = new Board(this.getFigures(), this.getMoveCounter(), this.getKingDied());
                    // check on check situation
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (brd.chessboard[i][j].getColor() != color) {
                                // enemy's possible move
                                vec possibleEnemyPath = new vec(new pos(i,j), new pos(path.getS().getY(), path.getS().getX()));

                                brd.chessboard[path.getS().getY()][path.getS().getX()] = new Figure(color, FigureType.KING);
                                // future checkmate check (must be recursion)
                                MoveStatus ms = brd.checkMove2(possibleEnemyPath, p);
                                brd.chessboard[path.getS().getY()][path.getS().getX()] = new Figure();
                                if (ms == MoveStatus.ELIMINATION)
                                    return MoveStatus.FORBIDDEN;
                            }
                        }
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getFigure() != FigureType.NONE) {
                        if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                                chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                            return MoveStatus.ELIMINATION;
                        }
                    }
                    else
                        return MoveStatus.REGULAR;
                }
                return MoveStatus.FORBIDDEN;
            case QUEEN:
                if (Math.abs(dX) <= 7 && Math.abs(dY) == 0 || Math.abs(dX) == 0 && Math.abs(dY) <= 7 ||
                        Math.abs(dX) == Math.abs(dY)) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                        if (chessboard[i][j].getFigure() != FigureType.NONE) {
                            return MoveStatus.FORBIDDEN;
                        }

                        if (dX != 0)
                        if (path.getS().getX() > path.getF().getX())
                            j++;
                        else j--;

                        if (dY != 0)
                        if (path.getS().getY() > path.getF().getY())
                            i++;
                        else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case ROOK:
                if (Math.abs(dX) <= 7 && Math.abs(dY) == 0 || Math.abs(dX) == 0 && Math.abs(dY) <= 7) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                            if (chessboard[i][j].getFigure() != FigureType.NONE) {
                                return MoveStatus.FORBIDDEN;
                            }

                        if (dX != 0)
                            if (path.getS().getX() > path.getF().getX())
                                j++;
                            else j--;

                        if (dY != 0)
                            if (path.getS().getY() > path.getF().getY())
                                i++;
                            else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case BISHOP:
                if (Math.abs(dX) == Math.abs(dY)) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                            if (chessboard[i][j].getFigure() != FigureType.NONE) {
                                return MoveStatus.FORBIDDEN;
                            }

                        if (dX != 0)
                            if (path.getS().getX() > path.getF().getX())
                                j++;
                            else j--;

                        if (dY != 0)
                            if (path.getS().getY() > path.getF().getY())
                                i++;
                            else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case KNIGHT:
                if (Math.abs(dX) == 1 && Math.abs(dY) == 2 || Math.abs(dX) == 2 && Math.abs(dY) == 1) {
                    Color c = chessboard[path.getS().getY()][path.getS().getX()].getColor();
                    if (c == Color.NONE) {
                        return MoveStatus.REGULAR;
                    }
                    else if (c != player.getColor()){
                        return MoveStatus.ELIMINATION;
                    }

                    return MoveStatus.FORBIDDEN;
                }
                return MoveStatus.FORBIDDEN;
            default:
                return MoveStatus.FORBIDDEN;
        }
    }

    /*
    // return board back
    void unMakeMove(vec move){


    }
    */

    // must be one recursion case. In cause of heap overflow made function
    private MoveStatus checkMove2(vec path, Player player) {

        if (path.getS().getY() < 0 || path.getS().getY() > 7 ||
                path.getS().getX() < 0 || path.getS().getX() > 7) {
            return MoveStatus.FORBIDDEN;
        }

        final int dY = path.getS().getY() - path.getF().getY();
        final int dX = path.getS().getX() - path.getF().getX();


        if (chessboard[path.getF().getY()][path.getF().getX()].getColor() != player.getColor()) {
            return MoveStatus.FORBIDDEN;
        }

        if (chessboard[path.getS().getY()][path.getS().getX()].getColor() == player.getColor()) {
            return MoveStatus.FORBIDDEN;
        }

        switch (chessboard[path.getF().getY()][path.getF().getX()].getFigure()) {
            case PAWN:
                if (player.getDirection() && dY < 0 || !player.getDirection() && dY > 0)
                    return MoveStatus.FORBIDDEN;
                if (Math.abs(dX) == 0 && Math.abs(dY) < 3 &&
                        chessboard[path.getS().getY()][path.getS().getX()].getFigure() == FigureType.NONE){
                    if (Math.abs(dY) == 2 && (path.getF().getY() == 1 && player.getDirection() ||
                            path.getF().getY() == 6 && !player.getDirection())) {
                        int y = (path.getF().getY() + path.getS().getY()) / 2;

                        if (chessboard[y][path.getF().getX()].getFigure() != FigureType.NONE) {
                            return MoveStatus.FORBIDDEN;
                        }
                    }else {
                        if (Math.abs(dY) != 1)
                            return MoveStatus.FORBIDDEN;}
                    return MoveStatus.REGULAR;
                }
                else if (Math.abs(dX) == 1 && Math.abs(dY) == 1 &&
                        chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                        chessboard[path.getS().getY()][path.getS().getX()].getFigure() != FigureType.NONE)
                    return MoveStatus.ELIMINATION;
                return MoveStatus.FORBIDDEN;
            case KING:
                if (Math.abs(dX) < 2 && Math.abs(dY) < 2) {
                    if (chessboard[path.getS().getY()][path.getS().getX()].getFigure() != FigureType.NONE) {
                        if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                                chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                            return MoveStatus.ELIMINATION;
                        }
                    }
                    else
                        return MoveStatus.REGULAR;
                }
                return MoveStatus.FORBIDDEN;
            case QUEEN:
                if (Math.abs(dX) <= 7 && Math.abs(dY) == 0 || Math.abs(dX) == 0 && Math.abs(dY) <= 7 ||
                        Math.abs(dX) == Math.abs(dY)) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                            if (chessboard[i][j].getFigure() != FigureType.NONE) {
                                return MoveStatus.FORBIDDEN;
                            }

                        if (dX != 0)
                            if (path.getS().getX() > path.getF().getX())
                                j++;
                            else j--;

                        if (dY != 0)
                            if (path.getS().getY() > path.getF().getY())
                                i++;
                            else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case ROOK:
                if (Math.abs(dX) <= 7 && Math.abs(dY) == 0 || Math.abs(dX) == 0 && Math.abs(dY) <= 7) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                            if (chessboard[i][j].getFigure() != FigureType.NONE) {
                                return MoveStatus.FORBIDDEN;
                            }

                        if (dX != 0)
                            if (path.getS().getX() > path.getF().getX())
                                j++;
                            else j--;

                        if (dY != 0)
                            if (path.getS().getY() > path.getF().getY())
                                i++;
                            else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case BISHOP:
                if (Math.abs(dX) == Math.abs(dY)) {
                    int j = path.getF().getX();
                    int i = path.getF().getY();

                    while (j != path.getS().getX() || i != path.getS().getY()) {
                        if (j != path.getF().getX() || i != path.getF().getY())
                            if (chessboard[i][j].getFigure() != FigureType.NONE) {
                                return MoveStatus.FORBIDDEN;
                            }

                        if (dX != 0)
                            if (path.getS().getX() > path.getF().getX())
                                j++;
                            else j--;

                        if (dY != 0)
                            if (path.getS().getY() > path.getF().getY())
                                i++;
                            else i--;
                    }

                    if (chessboard[path.getS().getY()][path.getS().getX()].getColor() != player.getColor() &&
                            chessboard[path.getS().getY()][path.getS().getX()].getColor() != Color.NONE) {
                        return MoveStatus.ELIMINATION;
                    }
                    else {
                        return MoveStatus.REGULAR;
                    }
                }
                return MoveStatus.FORBIDDEN;
            case KNIGHT:
                if (Math.abs(dX) == 1 && Math.abs(dY) == 2 || Math.abs(dX) == 2 && Math.abs(dY) == 1) {
                    Color c = chessboard[path.getS().getY()][path.getS().getX()].getColor();
                    if (c == Color.NONE) {
                        return MoveStatus.REGULAR;
                    }
                    else if (c != player.getColor()){
                        return MoveStatus.ELIMINATION;
                    }

                    return MoveStatus.FORBIDDEN;
                }
                return MoveStatus.FORBIDDEN;
            default:
                return MoveStatus.FORBIDDEN;
        }
    }

    ArrayList<pos> findAnyMoves(pos position, Player player) {
        if (chessboard[position.getY()][position.getX()].getColor() == player.getColor()){
            FigureType ft = chessboard[position.getY()][position.getX()].getFigure();

            ArrayList<pos> res = new ArrayList<>();

            switch (ft) {
                case PAWN:
                    int inc = 0;
                    if (player.getDirection()) {
                        inc++;
                    } else {
                        inc--;
                    }

                    temp.add(new pos(position.getY() + inc, position.getX())); // one cell
                    temp.add(new pos(position.getY() + inc + inc, position.getX())); // two cells
                    temp.add(new pos(position.getY() + inc, position.getX() + 1)); // fight 1
                    temp.add(new pos(position.getY() + inc, position.getX() - 1)); // fight 2
                    break;
                case KING:
                    temp.add(new pos(position.getY() + 1, position.getX())); // down
                    temp.add(new pos(position.getY() + 1, position.getX() + 1)); // down right
                    temp.add(new pos(position.getY(), position.getX() + 1)); // right
                    temp.add(new pos(position.getY() - 1, position.getX() + 1)); // up right
                    temp.add(new pos(position.getY() - 1, position.getX())); // up
                    temp.add(new pos(position.getY() + 1, position.getX() - 1)); // down left
                    temp.add(new pos(position.getY(), position.getX() - 1)); // left
                    temp.add(new pos(position.getY() - 1, position.getX() - 1)); // up left
                    break;
                case KNIGHT:
                    temp.add(new pos(position.getY() + 2, position.getX() + 1)); // down right
                    temp.add(new pos(position.getY() + 2, position.getX() - 1)); // down left
                    temp.add(new pos(position.getY() + 1, position.getX() - 2)); // middle down left
                    temp.add(new pos(position.getY() - 1, position.getX() - 2)); // middle up left
                    temp.add(new pos(position.getY() + 1, position.getX() + 2)); // middle down right
                    temp.add(new pos(position.getY() - 1, position.getX() + 2)); // middle up right
                    temp.add(new pos(position.getY() - 2, position.getX() + 1)); // up right
                    temp.add(new pos(position.getY() - 2, position.getX() - 1)); // up left
                    break;
                case QUEEN:
                    for (int j = position.getX(); j <= 7; ++j) {
                        temp.add(new pos(position.getY(), j)); // right
                    }

                    for (int j = position.getX(); j >= 0; --j) {
                        temp.add(new pos(position.getY(), j)); // left
                    }

                    for (int i = position.getY(); i <= 7; ++i) {
                        temp.add(new pos(i, position.getX())); // down
                    }

                    for (int i = position.getY(); i >= 0; --i) {
                        temp.add(new pos(i, position.getX())); // up
                    }
                case BISHOP:
                    int row = position.getX();
                    int col = position.getY();
                    do {
                        row++;
                        col++;
                        temp.add(new pos(col, row)); // down right
                    } while (row <= 7 || col <= 7);

                    row = position.getX();
                    col = position.getY();
                    do {
                        row--;
                        col++;
                        temp.add(new pos(col, row)); // up right
                    } while (col <= 7 || row >= 0);

                    row = position.getX();
                    col = position.getY();
                    do {
                        row++;
                        col--;
                        temp.add(new pos(col, row)); // down right
                    } while (row <= 7 || col >= 0);

                    row = position.getX();
                    col = position.getY();
                    do {
                        row--;
                        col--;
                        temp.add(new pos(col, row)); // up left
                    } while (col >= 0 || row >= 0);
                    break;
                case ROOK:
                    for (int j = position.getX(); j <= 7; ++j) {
                        temp.add(new pos(position.getY(), j)); // right
                    }

                    for (int j = position.getX(); j >= 0; --j) {
                        temp.add(new pos(position.getY(), j)); // left
                    }

                    for (int i = position.getY(); i <= 7; ++i) {
                        temp.add(new pos(i, position.getX())); // down
                    }

                    for (int i = position.getY(); i >= 0; --i) {
                        temp.add(new pos(i, position.getX())); // up
                    }
                    break;
            }

            for (pos p : temp) {
                if (checkMove(new vec(position, p), player) != MoveStatus.FORBIDDEN) {
                    res.add(p);
                }
            }

            temp.clear();
            return res;
        } else return new ArrayList<>();
    }

    MoveStatus makeMove(vec path, Player player) {
        MoveStatus ms = checkMove(path, player);

        if (ms == MoveStatus.REGULAR) {
            moveCounter++;
        }
        if (chessboard[path.getF().getY()][path.getF().getX()].getFigure() == FigureType.PAWN ||
                ms == MoveStatus.ELIMINATION) {
            moveCounter = 0;
        }

        boolean isChange = false;

        if (chessboard[path.getF().getY()][path.getF().getX()].getFigure() == FigureType.PAWN) {
            // Смена getX на getY и добавление проверки на ферзя, исправление бага
            if (path.getS().getY() == 0 || path.getS().getY() == 7) {
                Color col = chessboard[path.getF().getY()][path.getF().getX()].getColor();
                boolean isQueenAlive = false;
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++) {
                        if (chessboard[i][j].getColor() == col){
                            if(chessboard[i][j].getFigure() == FigureType.QUEEN) {
                                isQueenAlive = true;
                            }
                        }
                    }
                }
                if (!isQueenAlive) {
                    isChange = true;
                }

            }
        }

        switch (ms) {
            case ELIMINATION:
                player.increaseScore();
            case REGULAR:
                if (!isChange) {
                    chessboard[path.getS().getY()][path.getS().getX()] =
                            new Figure(chessboard[path.getF().getY()][path.getF().getX()].getColor(),
                                    chessboard[path.getF().getY()][path.getF().getX()].getFigure());
                }
                else
                {
                    chessboard[path.getS().getY()][path.getS().getX()] =
                            new Figure(chessboard[path.getF().getY()][path.getF().getX()].getColor(),
                                    FigureType.QUEEN);
                }
                chessboard[path.getF().getY()][path.getF().getX()] = new Figure();
                break;
            case FORBIDDEN:
                break;
        }

        return ms;
    }

    GameStatus checkRules(Player player) {
        ArrayList<pos> temp = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j].getColor() == player.getColor()) {
                    temp.addAll(findAnyMoves(new pos(i,j), player));
                }
            }
        }

        for (pos p : temp) {
            if (chessboard[p.getY()][p.getX()].getFigure() == FigureType.KING) {
                return GameStatus.CHECK;
            }
        }

        return GameStatus.CONTINUE;
    }

    ArrayList<vec> getMoves(Player player) {
        // chess pieces to move
        ArrayList<pos> posList = new ArrayList<>();

        // player moves list
        ArrayList<vec> v = new ArrayList<>();

        // get pieces of player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j].getCol() == player.getColor()) {
                    posList.add(new pos(i, j));
                }
            }
        }

        for (pos p : posList) {
            switch (chessboard[p.getY()][p.getX()].getFigure()) {
                case PAWN:
                    int inc = 0;
                    if (player.getDirection()) {
                        inc++;
                    } else {
                        inc--;
                    }

                    // regular
                    if (p.getY() + inc >= 0 && p.getY() + inc <= 7)
                        if (chessboard[p.getY() + inc][p.getX()].getColor() == Color.NONE) {
                            v.add(new vec(p, new pos(p.getY() + inc, p.getX()))); // one cell

                            if (p.getY() == 1 && player.getDirection() || p.getY() == 6 && !player.getDirection()) {
                                if (chessboard[p.getY() + inc + inc][p.getX()].getColor() == Color.NONE) {
                                    if (this.checkMove(new vec(p, new pos(p.getY() + inc + inc, p.getX())), player) != MoveStatus.FORBIDDEN) {
                                        v.add(new vec(p, new pos(p.getY() + inc + inc, p.getX()))); // two cells
                                    }
                                }
                            }
                        }

                    // fight 1
                    if (p.getY() + inc >= 0 && p.getY() + inc <= 7 && p.getX() + 1 <= 7)
                        if (chessboard[p.getY() + inc][p.getX() + 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + inc][p.getX() + 1].getFigure() == FigureType.KING) {
                                if (this.checkMove(new vec(p, new pos(p.getY() + inc, p.getX() + 1)), player) != MoveStatus.FORBIDDEN) {
                                    //v.clear();
                                    v.add(new vec(p, new pos(p.getY() + inc, p.getX() + 1))); // fight 1 (right)
                                    return v;
                                }

                            } else if (chessboard[p.getY() + inc][p.getX() + 1].getFigure() != FigureType.NONE) {

                                if (this.checkMove(new vec(p, new pos(p.getY() + inc, p.getX() + 1)), player) != MoveStatus.FORBIDDEN) {
                                    //v.clear();
                                    v.add(new vec(p, new pos(p.getY() + inc, p.getX() + 1))); // fight 1
                                    return v;
                                }
                            }
                        }

                    // fight 2
                    if (p.getY() + inc >= 0 && p.getY() + inc <= 7 && p.getX() - 1 >= 0)
                        if (chessboard[p.getY() + inc][p.getX() - 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + inc][p.getX() - 1].getFigure() == FigureType.KING) {
                                if (this.checkMove(new vec(p, new pos(p.getY() + inc, p.getX() - 1)), player) != MoveStatus.FORBIDDEN) {
                                    //v.clear();
                                    v.add(new vec(p, new pos(p.getY() + inc, p.getX() - 1))); // fight 1 (left)
                                    return v;
                                }
                            } else if (chessboard[p.getY() + inc][p.getX() - 1].getFigure() != FigureType.NONE) {
                                if (this.checkMove(new vec(p, new pos(p.getY() + inc, p.getX() - 1)), player) != MoveStatus.FORBIDDEN) {
                                    //v.clear();
                                    v.add(new vec(p, new pos(p.getY() + inc, p.getX() - 1))); // fight 2 (left)
                                    return v;
                                }
                            }
                        }
                    break;
                case KNIGHT:
                    if (p.getY() + 2 <= 7 && p.getX() + 1 <= 7)
                        if (chessboard[p.getY() + 2][p.getX() + 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + 2][p.getX() + 1].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() + 2, p.getX() + 1))); // down right
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() + 2, p.getX() + 1))); // down right
                        }

                    if (p.getY() + 2 <= 7 && p.getX() - 1 >= 0)
                        if (chessboard[p.getY() + 2][p.getX() - 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + 2][p.getX() - 1].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() + 2, p.getX() - 1))); // down left
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() + 2, p.getX() - 1))); // down left
                        }

                    if (p.getY() + 1 <= 7 && p.getX() - 2 >= 0)
                        if (chessboard[p.getY() + 1][p.getX() - 2].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + 1][p.getX() - 2].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() + 1, p.getX() - 2))); // middle down left
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() + 1, p.getX() - 2))); // middle down left
                        }

                    if (p.getY() - 1 >= 0 && p.getX() - 2 >= 0)
                        if (chessboard[p.getY() - 1][p.getX() - 2].getColor() != player.getColor()) {
                            if (chessboard[p.getY() - 1][p.getX() - 2].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() - 1, p.getX() - 2))); // middle up left
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() - 1, p.getX() - 2))); // middle up left
                        }

                    if (p.getY() + 1 <= 7 && p.getX() + 2 <= 7)
                        if (chessboard[p.getY() + 1][p.getX() + 2].getColor() != player.getColor()) {
                            if (chessboard[p.getY() + 1][p.getX() + 2].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() + 1, p.getX() + 2))); // middle down right
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() + 1, p.getX() + 2))); // middle down right
                        }

                    if (p.getY() - 1 >= 0 && p.getX() + 2 <= 7)
                        if (chessboard[p.getY() - 1][p.getX() + 2].getColor() != player.getColor()) {
                            if (chessboard[p.getY() - 1][p.getX() + 2].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() - 1, p.getX() + 2))); // middle up right
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() - 1, p.getX() + 2))); // middle up right
                        }

                    if (p.getY() - 2 >= 0 && p.getX() + 1 <= 7)
                        if (chessboard[p.getY() - 2][p.getX() + 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() - 2][p.getX() + 1].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() - 2, p.getX() + 1))); // up right
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() - 2, p.getX() + 1))); // up right
                        }

                    if (p.getY() - 2 >= 0 && p.getX() - 1 >= 0)
                        if (chessboard[p.getY() - 2][p.getX() - 1].getColor() != player.getColor()) {
                            if (chessboard[p.getY() - 2][p.getX() - 1].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY() - 2, p.getX() - 1))); // up left
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY() - 2, p.getX() - 1))); // up left
                        }
                    break;
                case QUEEN:
                    for (int j = p.getX() + 1; j <= 7; j++) {
                        if (chessboard[p.getY()][j].getColor() != player.getColor()) {
                            if (chessboard[p.getY()][j].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY(), j))); // right
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY(), j))); // right
                            if (chessboard[p.getY()][j].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                    }

                    for (int j = p.getX() - 1; j >= 0; j--) {
                        if (chessboard[p.getY()][j].getColor() != player.getColor()) {
                            if (chessboard[p.getY()][j].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(p.getY(), j))); // left
                                return v;
                            }
                            v.add(new vec(p, new pos(p.getY(), j))); // left
                            if (chessboard[p.getY()][j].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                    }

                    for (int i = p.getY() + 1; i <= 7; i++) {
                        if (chessboard[i][p.getX()].getColor() != player.getColor()) {
                            if (chessboard[i][p.getX()].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(i, p.getX()))); // down
                                return v;
                            }
                            v.add(new vec(p, new pos(i, p.getX()))); // down
                            if (chessboard[i][p.getX()].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                    }

                    for (int i = p.getY() - 1; i >= 0; i--) {
                        if (chessboard[i][p.getX()].getColor() != player.getColor()) {
                            if (chessboard[i][p.getX()].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(i, p.getX()))); // up
                                return v;
                            }
                            v.add(new vec(p, new pos(i, p.getX()))); // up
                            if (chessboard[i][p.getX()].getFigure() != FigureType.NONE) {
                                break;
                            }
                        } else break;
                    }
                    break;
                case KING:
                    if (p.getY() + 1 <= 7)
                        if (chessboard[p.getY() + 1][p.getX()].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() + 1, p.getX())), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() + 1, p.getX()))); // down
                        }

                    if (p.getY() + 1 <= 7 && p.getX() + 1 <= 7)
                        if (chessboard[p.getY() + 1][p.getX() + 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() + 1, p.getX() + 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() + 1, p.getX() + 1))); // down right
                        }

                    if (p.getX() + 1 <= 7)
                        if (chessboard[p.getY()][p.getX() + 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY(), p.getX() + 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY(), p.getX() + 1))); // right
                        }

                    if (p.getY() - 1 >= 0 && p.getX() + 1 <= 7)
                        if (chessboard[p.getY() - 1][p.getX() + 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() - 1, p.getX() + 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() - 1, p.getX() + 1))); // up right
                        }

                    if (p.getY() - 1 >= 0)
                        if (chessboard[p.getY() - 1][p.getX()].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() - 1, p.getX())), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() - 1, p.getX()))); // up
                        }

                    if (p.getY() + 1 <= 7 && p.getX() - 1 >= 0)
                        if (chessboard[p.getY() + 1][p.getX() - 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() + 1, p.getX() - 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() + 1, p.getX() - 1))); // down left
                        }

                    if (p.getX() - 1 >= 0)
                        if (chessboard[p.getY()][p.getX() - 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY(), p.getX() - 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY(), p.getX() - 1))); // left
                        }

                    if (p.getX() - 1 >= 0 && p.getY() - 1 >= 0)
                        if (chessboard[p.getY() - 1][p.getX() - 1].getColor() != player.getColor()) {
                            if(checkMove(new vec(p, new pos(p.getY() - 1, p.getX() - 1)), player) != MoveStatus.FORBIDDEN)
                                v.add(new vec(p, new pos(p.getY() - 1, p.getX() - 1))); // up left
                        }
                    break;
                case BISHOP:
                    int row = p.getX() + 1;
                    int col = p.getY() + 1;

                    while (row <= 7 && col <= 7) {
                        if (chessboard[col][row].getColor() != player.getColor()) {
                            if (chessboard[col][row].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(col, row))); // down right
                                return v;
                            }
                            v.add(new vec(p, new pos(col, row))); // down right
                            if (chessboard[col][row].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                        row++;
                        col++;
                    }

                    row = p.getX() - 1;
                    col = p.getY() + 1;

                    while (col <= 7 && row >= 0) {
                        if (chessboard[col][row].getColor() != player.getColor()) {
                            if (chessboard[col][row].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(col, row))); // up right
                                return v;
                            }
                            v.add(new vec(p, new pos(col, row))); // up right
                            if (chessboard[col][row].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                        row--;
                        col++;
                    }

                    row = p.getX() + 1;
                    col = p.getY() - 1;

                    while (row <= 7 && col >= 0) {
                        if (chessboard[col][row].getColor() != player.getColor()) {
                            if (chessboard[col][row].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(col, row))); // up right
                                return v;
                            }
                            v.add(new vec(p, new pos(col, row))); // up right
                            if (chessboard[col][row].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                        row++;
                        col--;
                    }

                    row = p.getX() - 1;
                    col = p.getY() - 1;

                    while (col >= 0 && row >= 0) {
                        if (chessboard[col][row].getColor() != player.getColor()) {
                            if (chessboard[col][row].getFigure() == FigureType.KING) {
                                v.clear();
                                v.add(new vec(p, new pos(col, row))); // up left
                                return v;
                            }
                            v.add(new vec(p, new pos(col, row))); // up left
                            if (chessboard[col][row].getFigure() != FigureType.NONE) {
                                break;
                            }

                        } else break;
                        row--;
                        col--;
                    }
                    break;

            }
        }
        return v;
    }
}
