package com.esause.chess;

class Player {
    private final boolean isHuman;
    private final boolean direction;
    private final Color color;
    private int score;

    /**
     * isHuman getter
     * @return isHuman value
     */
    boolean getIsHuman() {
        return isHuman;
    }

    /**
     * direction getter
     * @return player's move direction value
     */
    boolean getDirection() {
        return direction;
    }

    /**
     * color getter
     * @return player's color
     */
    Color getColor() {return color;}

    /**
     * score getter
     * @return player's score
     */
    int getScore() {return score;}

    void increaseScore() {
        score++;
    }

    /**
     * Set player's score
     * @param score is a new score
     */
    void setScore(int score) {
        this.score = score;
    }

    /**
     * Creates a new Player object
     * @param isHuman defines player type
     * @param direction defines player's direction on a chessboard
     * @param color defines color of player's figures
     */
    Player(boolean isHuman, boolean direction, Color color){
        this.isHuman = isHuman;
        this.direction = direction;
        this.color = color;
        score = 0;
    }
}
