package com.esause.chess;

/**
 * Contains all chess figures
 */
enum FigureType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING, NONE
}

/**
 * Contains figure colors
 */
enum Color {
    WHITE, BLACK, NONE
}

/**
 * Contains all possible move results
 */
enum MoveStatus {
    REGULAR, ELIMINATION, FORBIDDEN
}

/**
 * Contains all possible rule check results
 */
enum GameStatus {
    CONTINUE, NORMAL, DRAW, CHECK
}