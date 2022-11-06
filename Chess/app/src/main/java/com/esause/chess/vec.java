package com.esause.chess;

class vec {
    private pos f;
    private pos s;

    /**
     * Creates a new move
     * @param f start position
     * @param s end position
     */
    vec(pos f, pos s){
        this.f = f;
        this.s = s;
    }

    /**
     * @return start position
     */
    final pos getF(){
        return f;
    }

    /**
     * @return end position
     */
    final pos getS(){
        return s;
    }
}
