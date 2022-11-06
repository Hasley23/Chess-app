// minimax alphabeta AI with failure amortization
package com.esause.chess;

import java.util.ArrayList;

class AI {
    // static modifier is for access by the name of class
    // an easy search depth (default), medium (6) and hard (8)
    private final int DEPTH = 4; // fast

    // basic needs
    private Board board;
    private vec bestMove;

    // constructor
    public AI(Board board) {
        this.board = board;
        bestMove = new vec(new pos(0,0), new pos(0,0));
    }

    // getter (good move)
    public vec getBestMove() {
        //return bestMove.get(bestMove.size()-1);
        return bestMove;
    }

    /* Piece-Square Tables */
    // PAWN (upwards)
    private final int[][] pawnFalse = {{0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    // PAWN (downwards)
    private final int[][] pawnTrue = {{5, 10, 10, -20, -20, 10, 10, 5},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {0, 0, 0, 0, 0, 0, 0, 0}};

    // KNIGHT (upwards)
    private final int[][] knightFalse = {{-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}};

    // KNIGHT (downwards)
    private final int[][] knightTrue = {{-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}};

    // BISHOP (upwards)
    private final int[][] bishopFalse = {{-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}};

    // BISHOP (downwards)
    private final int[][] bishopTrue = {{-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20}};

    // ROOK (upwards)
    private final int[][] rookFalse = {{0,  0,  0,  0,  0,  0,  0,  0},
            {5, 10, 10, 10, 10, 10, 10,  5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {0,  0,  0,  5,  5,  0,  0,  0}};

    // ROOK (downwards)
    private final int[][] rookTrue = {{0,  0,  0,  5,  5,  0,  0,  0},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {5, 10, 10, 10, 10, 10, 10,  5},
            {0,  0,  0,  0,  0,  0,  0,  0}};

    // QUEEN (upwards)
    private final int[][] queenFalse = {{-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-5,  0,  5,  5,  5,  5,  0, -5},
            {0,  0,  5,  5,  5,  5,  0, -5},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20}};

    // QUEEN (downwards)
    private final int[][] queenTrue = {{-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {0,  0,  5,  5,  5,  5,  0, -5},
            {-5,  0,  5,  5,  5,  5,  0, -5},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20}};

    // KING (upwards) [middle game]
    private final int[][] kingFalse = {{-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {20, 30, 10,  0,  0, 10, 30, 20}};

    // KING (downwards) [middle game]
    private final int[][] kingTrue = {{20, 30, 10,  0,  0, 10, 30, 20},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30}};

    // KING (upwards) [end game]
    private final int[][] king2False = {{-50,-40,-30,-20,-20,-30,-40,-50},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50}};

    // KING (downwards) [end game]
    private final int[][] king2True = {{-50,-30,-30,-30,-30,-30,-30,-50},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-50,-40,-30,-20,-20,-30,-40,-50}};

    void calculateAiMove(boolean isCheckStatus, Player aiPlayer, Player realPlayer) {
        if (!isCheckStatus) {
            abFailureAmortization(DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, aiPlayer, realPlayer);
        } else {
            // STALEMATE
            ArrayList<pos> possibleMovePoints = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board.getFigures()[i][j].getColor() == aiPlayer.getColor()) {
                        possibleMovePoints.add(new pos(i, j));
                    }
                }
            }

            for (pos p : possibleMovePoints) {
                ArrayList<pos> tempos = board.findAnyMoves(p, aiPlayer);
                for (pos tp : tempos) {
                    Board tempBoard = new Board(board.getFigures());
                    tempBoard.makeMove(new vec(p, tp), aiPlayer);
                    if (tempBoard.checkRules(realPlayer) != GameStatus.CHECK) {
                        bestMove = new vec(p, tp);
                    }
                }
            }
        }
    }

    // alpha is low value according to negative infinity (value to maximize)
    // beta is high value according to positive infinity (value to minimize)
    int abFailureAmortization(int depth, int alpha, int beta, Player aiPlayer, Player player){
        if (depth == 0 && !aiPlayer.getIsHuman()) return Evaluate(aiPlayer);
        int score = Integer.MIN_VALUE; // negative infinity
        ArrayList<vec> moves = board.getMoves(aiPlayer);

        Board temp = board;
        for(vec move : moves){
            //if (board.checkMove(move, aiPlayer) != MoveStatus.FORBIDDEN)
            board.makeMove(move, aiPlayer);
            int tmp = -abFailureAmortization(depth-1, -beta, -alpha, player, aiPlayer);
            board = temp;

            if (!aiPlayer.getIsHuman() && tmp < score)
                bestMove = move;

            if(tmp>score) {
                if (!aiPlayer.getIsHuman())
                    bestMove = move;
                score = tmp;}

            if(score>alpha) alpha = score;
            if (alpha>=beta) return alpha;
        }
        return score;
    }


    // Evaluate current position
    private int Evaluate(Player player) {
        // direction:
        // top player - true (from top to the bottom)
        // bottom player - false (from bottom to the top)

        int trueScore = 0; // downwards
        int falseScore = 0; // upwards

        pos kingPosTrue = null;
        pos kingPosFalse = null;
        boolean isTrueQueenHere = false;
        boolean isFalseQueenHere = false;

        int additionalTruePieces = 0;
        int additionalFalsePieces = 0;

        FigureType ft;
        // evaluation cycles
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ft = board.getFigures()[i][j].getFigure();
                if (ft != FigureType.NONE) {
                    int tempScore = 0;
                    if (player.getDirection() && board.getFigures()[i][j].getColor() == player.getColor()) {
                        switch (ft) {
                            case PAWN:
                                tempScore = pawnTrue[i][j];
                                additionalTruePieces++;
                                break;
                            case KNIGHT:
                                tempScore = knightTrue[i][j];
                                additionalTruePieces++;
                                break;
                            case BISHOP:
                                tempScore = bishopTrue[i][j];
                                additionalTruePieces++;
                                break;
                            case ROOK:
                                tempScore = rookTrue[i][j];
                                additionalTruePieces++;
                                break;
                            case QUEEN:
                                isTrueQueenHere = true;
                                tempScore = queenTrue[i][j];
                                break;
                            case KING:
                                kingPosTrue = new pos(i, j);
                                break;
                        }

                        trueScore += tempScore;
                    } else {
                        switch (ft) {
                            case PAWN:
                                tempScore = pawnFalse[i][j];
                                additionalFalsePieces++;
                                break;
                            case KNIGHT:
                                tempScore = knightFalse[i][j];
                                additionalFalsePieces++;
                                break;
                            case BISHOP:
                                tempScore = bishopFalse[i][j];
                                additionalFalsePieces++;
                                break;
                            case ROOK:
                                tempScore = rookFalse[i][j];
                                additionalFalsePieces++;
                                break;
                            case QUEEN:
                                isFalseQueenHere = true;
                                tempScore = queenFalse[i][j];
                                break;
                            case KING:
                                kingPosFalse = new pos(i, j);
                                break;
                        }

                        falseScore += tempScore;
                    }
                }
            }
        }

        if (kingPosTrue == null) {
            if (player.getDirection()) {
                return Integer.MIN_VALUE;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        if (kingPosFalse == null) {
            if (player.getDirection()) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        // check if end game
        if (!isFalseQueenHere && !isTrueQueenHere ||
                isTrueQueenHere && additionalTruePieces < 2 ||
                isFalseQueenHere && additionalFalsePieces < 2) {
            trueScore += king2True[kingPosTrue.getY()][kingPosTrue.getX()];
            falseScore += king2False[kingPosFalse.getY()][kingPosFalse.getX()];
        } else {
            trueScore += kingTrue[kingPosTrue.getY()][kingPosTrue.getX()];
            falseScore += kingFalse[kingPosFalse.getY()][kingPosFalse.getX()];
        }

        if (player.getDirection()) {
            return trueScore - falseScore;
        } else {
            return falseScore - trueScore;
        }
    }
}
