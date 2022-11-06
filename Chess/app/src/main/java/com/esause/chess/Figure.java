package com.esause.chess;

/**
 * Implements a chess figure
 */
class Figure {
    private Color color;
    private FigureType figure;

    Color getColor() { return color; }
    FigureType getFigure() { return figure; }

    Figure() {
        color = Color.NONE;
        figure = FigureType.NONE;
    }

    Figure(Color color, FigureType figure) {
        this.color = color;
        this.figure = figure;
    }

    public Color getCol() { return color; }
}
