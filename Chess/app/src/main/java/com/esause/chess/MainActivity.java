package com.esause.chess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // App context
    private Context context;
    // Board cells
    private final ImageView[][] ivCell = new ImageView[8][8];
    // Board markup (horizontal)
    private final TextView[] tvMarker = new TextView[8];
    // Board markup (vertical)
    private final TextView[] tvMarkerVert = new TextView[8];

    // Drawable resources
    private final Drawable[] drawResources = new Drawable[18];

    // TextView
    private TextView tvTurn;
    private TextView tvWhiteScore;
    private TextView tvBlackScore;
    private TextView tvStatus;
    private TextView tvMovesToDraw;

    // Game
    private Core core;
    private boolean isGameRunning = false;
    private int xMove, yMove, xMove2, yMove2;   // path coordinates
    private boolean whiteOnTop = false;   // are white pieces on top of the board?
    private boolean playWithAI = false;

    // Players
    private Player player1;
    private Player player2;
    private Player gamePlayer;
    // alpha-beta with failure amortization
    AI abSeeker;

    // Miscellaneous
    private boolean isClicked = false; //tracking player's click
    private boolean isMarked = false; //tracking marked cells
    private ArrayList<pos> marks;
    private ArrayList<pos> possibleMovePoints;
    private boolean status_check = false;
    private vec aiMove;
    boolean alreadyReturned = false;
    // moves counter
    private int moveCounter;
    // moves counter value to return
    private int moveCounterReturn;
    // emergency board
    private Figure[][] returnsBoard;

    // button for return board states
    Button btnReturnMove;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Use this to prepare the game */

        // main layout
        setContentView(R.layout.activity_main);

        // set app context
        context = this;

        // expand layout to status bar
        findViewById(R.id.content_main).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Background animation
        AnimationDrawable animationDrawable = (AnimationDrawable) findViewById(
                R.id.content_main).getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        // Prepare game
        loadResources();
        setListen();
        startBoardGame();
    }

    /**
     * Set listeners and connect xml elements
     */
    @SuppressLint("NewApi")
    private void setListen() {
        // Button block
        Button btnNewGame = findViewById(R.id.btnPlay);
        btnReturnMove = findViewById(R.id.btnBack);

        // TextView block
        tvTurn = findViewById(R.id.tvTurn);
        tvBlackScore = findViewById(R.id.tvBlackScore);
        tvWhiteScore = findViewById(R.id.tvWhiteScore);
        tvStatus = findViewById(R.id.tvStatus);
        tvMovesToDraw = findViewById(R.id.tvMovesToDraw);

        moveCounter = 50;

        // Setters
        btnNewGame.setOnClickListener(v -> {
            // Show dialog to choose game params
            showRadioButtonDialog();
        });

        btnReturnMove.setOnClickListener(v -> {
            // Return the move back
            returnMove();
        });
    }

    /**
     * Load board images
     */
    private void loadResources() {
        /* Use this to load cell drawables */
        // Get resources from context
        Resources res = context.getResources();

        Drawable redInset = res.getDrawable(R.drawable.white);
        redInset.setLevel(1);

        //cell with states
        drawResources[0] = res.getDrawable(R.drawable.white);
        //cell without states
        drawResources[1] = res.getDrawable(R.drawable.blue_square);
        //red inset
        drawResources[2] = redInset;
        //pink inset
        drawResources[3] = res.getDrawable(R.drawable.white_pink_border);
        //yellow inset
        drawResources[4] = res.getDrawable(R.drawable.white_yellow_border);
        //fix
        drawResources[5] = res.getDrawable(R.drawable.transparent_piece);

        // White figures
        // Pawn
        drawResources[6] = getPng("pawn");
        // Rook
        drawResources[7] = getPng("rook");
        // Knight
        drawResources[8] = getPng("knight");
        // Bishop
        drawResources[9] = getPng("bishop");
        // King
        drawResources[10] = getPng("king");
        // Queen
        drawResources[11] = getPng("queen");

        // Black figures
        // Pawn
        drawResources[12] = getPng("pawn_b");
        // Rook
        drawResources[13] = getPng("rook_b");
        // Knight
        drawResources[14] = getPng("knight_b");
        // Bishop
        drawResources[15] = getPng("bishop_b");
        // King
        drawResources[16] = getPng("king_b");
        // Queen
        drawResources[17] = getPng("queen_b");
    }

    /**
     * Init game with chosen params
     */
    @SuppressLint("SetTextI18n")
    private void init_game() {
        // Init vars
        isClicked = false;//tracking player's click
        isMarked = false;//tracking marked cells
        status_check = false;

        xMove = 0;
        yMove = 0;
        xMove2 = 0;
        yMove2 = 0;

        if (playWithAI) whiteOnTop = false;
        // Up
        player1 = new Player(!playWithAI, true, whiteOnTop ? Color.WHITE : Color.BLACK);
        // Down
        player2 = new Player(true, false, whiteOnTop ? Color.BLACK : Color.WHITE);

        // Init core
        core = new Core(whiteOnTop, player1, player2);

        if (player1.getColor() == Color.WHITE) {
            gamePlayer = player1;
        }
        else gamePlayer = player2;

        // Output
        updateGraphics();
        tvTurn.setText(R.string.player_white);
        tvStatus.setText("");

        // update moves counter
        moveCounter = 50;
        tvMovesToDraw.setText(Integer.toString(moveCounter));



        if (!whiteOnTop) {
            for (int i=0; i<8; i++){
                // Markup vertical
                if (i==0)
                    tvMarkerVert[i].setText("8");
                else if (i==1)
                    tvMarkerVert[i].setText("7");
                else if (i==2)
                    tvMarkerVert[i].setText("6");
                else if (i==3)
                    tvMarkerVert[i].setText("5");
                else if (i==4)
                    tvMarkerVert[i].setText("4");
                else if (i==5)
                    tvMarkerVert[i].setText("3");
                else if (i==6)
                    tvMarkerVert[i].setText("2");
                else
                    tvMarkerVert[i].setText("1");
            }
        }
        else {
            for (int i=0; i<8; i++){
                // Markup vertical
                if (i==0)
                    tvMarkerVert[i].setText("1");
                else if (i==1)
                    tvMarkerVert[i].setText("2");
                else if (i==2)
                    tvMarkerVert[i].setText("3");
                else if (i==3)
                    tvMarkerVert[i].setText("4");
                else if (i==4)
                    tvMarkerVert[i].setText("5");
                else if (i==5)
                    tvMarkerVert[i].setText("6");
                else if (i==6)
                    tvMarkerVert[i].setText("7");
                else
                    tvMarkerVert[i].setText("8");
            }
        }

        for (int i = 0; i < 8; i++) {
            // Markup horizontal
            if (!whiteOnTop) {
                if (i == 0)
                    tvMarker[i].setText("A");
                else if (i == 1)
                    tvMarker[i].setText("B");
                else if (i == 2)
                    tvMarker[i].setText("C");
                else if (i == 3)
                    tvMarker[i].setText("D");
                else if (i == 4)
                    tvMarker[i].setText("E");
                else if (i == 5)
                    tvMarker[i].setText("F");
                else if (i == 6)
                    tvMarker[i].setText("G");
                else
                    tvMarker[i].setText("H");
            } else {
                if (i == 0)
                    tvMarker[i].setText("H");
                else if (i == 1)
                    tvMarker[i].setText("G");
                else if (i == 2)
                    tvMarker[i].setText("F");
                else if (i == 3)
                    tvMarker[i].setText("E");
                else if (i == 4)
                    tvMarker[i].setText("D");
                else if (i == 5)
                    tvMarker[i].setText("C");
                else if (i == 6)
                    tvMarker[i].setText("B");
                else
                    tvMarker[i].setText("A");
            }
        }
    }

    /**
     * Shows new game dialog
     */
    @SuppressLint("NewApi")
    private void showRadioButtonDialog() {
        /* Use this to get params and start the game */
        // declare dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.radiobutton_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // declare RadioGroup
        RadioGroup rg = dialog.findViewById(R.id.radio_group);

        // access to dialog content view
        View lt = dialog.getWindow().getDecorView();

        // Button block
        Button btnPlayerVsPlayer = lt.findViewById(R.id.btnPlayWithoutAI);
        Button btnPlayerVsAI = lt.findViewById(R.id.btnPlayWithAI);

        // check necessary radio button
        if (!whiteOnTop) {
            rg.check(R.id.radio0);
        }
        else {
            rg.check(R.id.radio1);
        }

        // Setters
        rg.setOnCheckedChangeListener((group, checkedId) -> whiteOnTop = group.getCheckedRadioButtonId() == R.id.radio1);

        btnPlayerVsPlayer.setOnClickListener(v -> {
            isGameRunning = true;
            playWithAI = false;
            init_game();
            dialog.dismiss();
        });

        btnPlayerVsAI.setOnClickListener(v -> {
            isGameRunning = true;
            playWithAI = true;
            init_game();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * return board state
     */
    @SuppressLint("SetTextI18n")
    private void returnMove(){
        if (core != null && !alreadyReturned && returnsBoard != null && isGameRunning) {
            if (!playWithAI) {
                // player change
                if (gamePlayer == player1)
                    gamePlayer = player2;
                else gamePlayer = player1;

                if (gamePlayer.getColor() == Color.WHITE) {
                    tvTurn.setText(R.string.player_white);
                } else {
                    tvTurn.setText(R.string.player_black);
                }
            }

            // move counter return
            moveCounter = moveCounterReturn;
            tvMovesToDraw.setText(Integer.toString(moveCounter));

            // return last board state
            core.board = new Board(returnsBoard);

            // invert
            alreadyReturned = !alreadyReturned;
            if (status_check)
                status_check = false;

            // if check
            if (tvStatus.getText() != "")
            {
                core.setGameStatus(GameStatus.CONTINUE);
                tvStatus.setText("");
            }

            // clear history entry
            core.history.remove(core.history.get(core.history.size() - 1));

            // update screen
            updateGraphics();
        }
    }

    /**
     * Get PNG drawable
     * @param name of png file
     * @return png drawable
     */
    private Drawable getPng(String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);
    }

    /**
     * Main loop
     */
    @SuppressLint({"NewApi", "SetTextI18n"})
    private void startBoardGame() {
        /* Use this to start a game session */

        // layout works (board)
        final int sizeOfCell = Math.round(boardViewWidth() / 9);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(
                sizeOfCell * 8 + sizeOfCell/2, sizeOfCell);
        final LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(
                sizeOfCell, sizeOfCell);
        final LinearLayout linBoard = findViewById(R.id.linBoard);
        //// layout
        // layout works (board frame, horizontal)
        final LinearLayout.LayoutParams lpFrame = new LinearLayout.LayoutParams(
                sizeOfCell, sizeOfCell/2);
        final LinearLayout tvRow = new LinearLayout(context);
        // layout works (board frame, vertical)
        //final LinearLayout.LayoutParams lpFrame2 = new LinearLayout.LayoutParams(sizeOfCell/2, sizeOfCell);

        for (int i = 0; i < 8; i++){
            tvMarker[i] = new TextView(context);
            //tvMarker2[i] = new TextView(context);

            // Markup horizontal
            if (!whiteOnTop) {
                if (i==0)
                    tvMarker[i].setText("A");
                else if (i==1)
                    tvMarker[i].setText("B");
                else if (i==2)
                    tvMarker[i].setText("C");
                else if (i==3)
                    tvMarker[i].setText("D");
                else if (i==4)
                    tvMarker[i].setText("E");
                else if (i==5)
                    tvMarker[i].setText("F");
                else if (i==6)
                    tvMarker[i].setText("G");
                else
                    tvMarker[i].setText("H");
            } else {
                if (i==0)
                    tvMarker[i].setText("H");
                else if (i==1)
                    tvMarker[i].setText("G");
                else if (i==2)
                    tvMarker[i].setText("F");
                else if (i==3)
                    tvMarker[i].setText("E");
                else if (i==4)
                    tvMarker[i].setText("D");
                else if (i==5)
                    tvMarker[i].setText("C");
                else if (i==6)
                    tvMarker[i].setText("B");
                else
                    tvMarker[i].setText("A");
            }



            // horizontal
            tvMarker[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvMarker[i].setTextColor(this.getResources().getColor(R.color.colorWhite));
            tvMarker[i].setPadding(0,-10,0,10);
            tvMarker[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);



            // add to main view
            tvRow.addView(tvMarker[i], lpFrame);
        }

        linBoard.addView(tvRow);

        //tvMarker8
        for (int i=0; i<8; i++){
            tvMarkerVert[i] = new TextView(context);
            tvMarkerVert[i].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvMarkerVert[i].setTextColor(this.getResources().getColor(R.color.colorWhite));
            tvMarkerVert[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            // Markup vertical
            if (i==0)
                tvMarkerVert[i].setText("8");
            else if (i==1)
                tvMarkerVert[i].setText("7");
            else if (i==2)
                tvMarkerVert[i].setText("6");
            else if (i==3)
                tvMarkerVert[i].setText("5");
            else if (i==4)
                tvMarkerVert[i].setText("4");
            else if (i==5)
                tvMarkerVert[i].setText("3");
            else if (i==6)
                tvMarkerVert[i].setText("2");
            else
                tvMarkerVert[i].setText("1");
        }

        final LinearLayout.LayoutParams lpTextVertical = new LinearLayout.LayoutParams(
                sizeOfCell/2, sizeOfCell);
        LinearLayout lin8 = new LinearLayout(context);
        LinearLayout lin7 = new LinearLayout(context);
        LinearLayout lin6 = new LinearLayout(context);
        LinearLayout lin5 = new LinearLayout(context);
        LinearLayout lin4 = new LinearLayout(context);
        LinearLayout lin3 = new LinearLayout(context);
        LinearLayout lin2 = new LinearLayout(context);
        LinearLayout lin1 = new LinearLayout(context);

        lin8.setPadding(20,25,0,0);
        lin7.setPadding(20,25,0,0);
        lin6.setPadding(20,25,0,0);
        lin5.setPadding(20,25,0,0);
        lin4.setPadding(20,25,0,0);
        lin3.setPadding(20,25,0,0);
        lin2.setPadding(20,25,0,0);
        lin1.setPadding(20,25,0,0);

        lin8.addView(tvMarkerVert[0], lpTextVertical);
        lin7.addView(tvMarkerVert[1], lpTextVertical);
        lin6.addView(tvMarkerVert[2], lpTextVertical);
        lin5.addView(tvMarkerVert[3], lpTextVertical);
        lin4.addView(tvMarkerVert[4], lpTextVertical);
        lin3.addView(tvMarkerVert[5], lpTextVertical);
        lin2.addView(tvMarkerVert[6], lpTextVertical);
        lin1.addView(tvMarkerVert[7], lpTextVertical);


        ////

        // Main loop
        for(int i = 0; i < 8; i++) {
            LinearLayout linRow = new LinearLayout(context);
            //make a row
            for(int j=0; j < 8; j++) {
                ivCell[i][j] = new ImageView(context);
                // Fill
                ivCell[i][j].setBackground(drawResources[1]);
                ivCell[i][j].setScaleType(ImageView.ScaleType.FIT_CENTER);
                final int x = j;
                final int y = i;
                // main listener
                ivCell[i][j].setOnClickListener(v -> {
                    // !isGameRunning --> board untouchable
                    if (isGameRunning) {
                        // first click
                        if (!isClicked) {
                            // set BLANK spaces untouchable
                            if (core.getBoard()[y][x].getColor() == gamePlayer.getColor()) {
                                isClicked = true;
                                xMove = x;
                                yMove = y;

                                // make inset
                                ivCell[yMove][xMove].setBackground(drawResources[2]);
                                marks = core.getInsets(new pos(yMove, xMove), gamePlayer);
                                for (pos position : marks) {
                                    ivCell[position.getY()][position.getX()].setBackground(drawResources[4]);
                                }

                            }
                        } else {
                            // second click
                            xMove2 = x;
                            yMove2 = y;
                            isClicked = false;

                            // undo inset

                            for (pos position : marks) {
                                if ((position.getX() % 2 == 0 && position.getY() % 2 != 0) || (position.getX() % 2 != 0 && position.getY() % 2 == 0))
                                    ivCell[position.getY()][position.getX()].setBackground(drawResources[0]);
                                else
                                    ivCell[position.getY()][position.getX()].setBackground(drawResources[1]);
                            }

                            if (!isMarked) {
                                if ((xMove % 2 == 0 && yMove % 2 != 0) || (xMove % 2 != 0 && yMove % 2 == 0))
                                    ivCell[yMove][xMove].setBackground(drawResources[0]);
                                else
                                    ivCell[yMove][xMove].setBackground(drawResources[1]);
                            } else {
                                ivCell[yMove][xMove].setBackground(drawResources[4]);
                                isMarked = false;
                            }

                            // Figures on start and finish
                            FigureType fg0 = core.board.getFigures()[yMove][xMove].getFigure();
                            FigureType fg1 = core.board.getFigures()[yMove2][xMove2].getFigure();
                            // last board state
                            emergencyBoard();

                            // Call game core for a move
                            boolean res = core.mainAction(
                                    new vec(new pos(yMove,xMove), new pos(yMove2,xMove2)),
                                    gamePlayer, gamePlayer == player1 ? player2 : player1,
                                    status_check);


                            // cut off failed moves
                            if (res) {
                                // for returns
                                moveCounterReturn = moveCounter;
                                alreadyReturned = false;

                                // 50 moves rule
                                if (fg0 != FigureType.PAWN && fg1 == FigureType.NONE){
                                    moveCounter--;
                                } else {
                                    moveCounter = 50;
                                }
                                tvMovesToDraw.setText(Integer.toString(moveCounter));

                                visualizeMoves(lpCell);

                                // play sound
                                AssetFileDescriptor afd = null;
                                try {
                                    afd = getAssets().openFd("move.mp3");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                MediaPlayer player = new MediaPlayer();
                                try {
                                    assert afd != null;
                                    player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    player.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.start();

                                status_check = false;

                                // FIN
                                if (moveCounter == 0) {
                                    isGameRunning = false;
                                    core.setGameStatus(GameStatus.DRAW);
                                    finalDialog();
                                    return;
                                }

                                if (core.getGameStatus() != GameStatus.CONTINUE &&
                                core.getGameStatus() != GameStatus.CHECK) {
                                    isGameRunning = false;
                                    tvStatus.setText(R.string.status_win);
                                    if (core.getGameStatus() == GameStatus.DRAW)
                                        tvStatus.setText(R.string.draft);
                                    finalDialog();
                                    return;
                                }

                                if (gamePlayer == player1)
                                    gamePlayer = player2;
                                else gamePlayer = player1;

                                if (gamePlayer.getColor() == Color.WHITE) {
                                    tvTurn.setText(R.string.player_white);
                                } else {
                                    tvTurn.setText(R.string.player_black);
                                }

                                if (!gamePlayer.getIsHuman()) {
                                    tvTurn.setText(R.string.thinking);
                                }

                                // STALEMATE
                                possibleMovePoints = new ArrayList<>();
                                Figure[][] fg = core.getBoard();
                                for (int i1 = 0; i1 < 8; i1++) {
                                    for (int j1 = 0; j1 < 8; j1++) {
                                        if (fg[i1][j1].getColor() == (gamePlayer == player1 ? player1 : player2).getColor()) {
                                            possibleMovePoints.add(new pos(i1, j1));
                                        }
                                    }
                                }
                                int score1 = player1.getScore();
                                int score2 = player2.getScore();
                                boolean temp1 = true;
                                ArrayList<pos> tempos;
                                for (pos p : possibleMovePoints) {
                                    tempos = core.getInsets(p, gamePlayer == player1 ? player1 : player2);
                                    for (pos tp : tempos) {
                                        Board tempBoard = new Board(fg);
                                        tempBoard.makeMove(new vec(p, tp), gamePlayer == player1 ? player1 : player2);
                                        if (tempBoard.checkRules(gamePlayer == player1 ? player2 : player1) != GameStatus.CHECK) {
                                            temp1 = false;
                                        }
                                    }
                                }

                                player1.setScore(score1);
                                player2.setScore(score2);

                                // CHECK AND FIN
                                GameStatus gs = core.checkGame(gamePlayer == player1 ? player2 : player1, true);
                                if (gs == GameStatus.CHECK) {


                                    if (temp1) {
                                        isGameRunning = false;
                                        core.setGameStatus(GameStatus.NORMAL);
                                        tvStatus.setText(R.string.status_win);
                                        gamePlayer = gamePlayer == player1 ? player2 : player1;
                                        finalDialog();
                                        return;
                                    }

                                    tvStatus.setText(R.string.status_check);
                                    status_check = true;
                                } else if (gs == GameStatus.CONTINUE) {
                                    tvStatus.setText("");
                                    if (temp1) {
                                        tvStatus.setText(R.string.stalemate);
                                        isGameRunning = false;
                                        core.setGameStatus(GameStatus.DRAW);
                                        finalDialog();
                                    }
                                }
                            }
                        }
                    }
                });

                if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0))
                {
                    ivCell[i][j].setBackground(drawResources[0]);
                }

                linRow.addView(ivCell[i][j], lpCell);
                if (i == 0 && j == 7)
                    linRow.addView(lin8, lpTextVertical);
                else if (i == 1 && j == 7)
                    linRow.addView(lin7, lpTextVertical);
                else if (i == 2 && j == 7)
                    linRow.addView(lin6, lpTextVertical);
                else if (i == 3 && j == 7)
                    linRow.addView(lin5, lpTextVertical);
                else if (i == 4 && j == 7)
                    linRow.addView(lin4, lpTextVertical);
                else if (i == 5 && j == 7)
                    linRow.addView(lin3, lpTextVertical);
                else if (i == 6 && j == 7)
                    linRow.addView(lin2, lpTextVertical);
                else if (i == 7 && j == 7)
                    linRow.addView(lin1, lpTextVertical);
            }
            linBoard.addView(linRow, lpRow);

        }
    }

    /**
     * Show ending
     */
    @SuppressLint("NewApi")
    private void finalDialog() {
        /* Show this when the game is over */
        // declare dialog
        isGameRunning = false;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.game_completed);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // access to dialog content view
        View lt = dialog.getWindow().getDecorView();

        // TextView block
        TextView winner = lt.findViewById(R.id.tvWinner);
        TextView conqueror = lt.findViewById(R.id.conqueror);

        // Button block
        Button Restart = lt.findViewById(R.id.btnRestart);
        Button Quit = lt.findViewById(R.id.btnQuit);

        conqueror.setText(R.string.dialog2_message);

        if (core.getGameStatus() == GameStatus.NORMAL) {
            winner.setText(gamePlayer.getColor() == Color.WHITE ? R.string.player_white : R.string.player_black);
        } else if (core.getGameStatus() == GameStatus.DRAW) {
            conqueror.setText(R.string.final_game_state);
            winner.setText(R.string.draft);
        }

        Restart.setOnClickListener(v -> {
            dialog.cancel();
            showRadioButtonDialog();
        });

        Quit.setOnClickListener(v -> {
            dialog.cancel();
            finish();
        });

        dialog.show();
    }

    /**
     * AI move after animation (if with AI)
     */
    @SuppressLint("SetTextI18n")
    private void afterAnim(LinearLayout.LayoutParams lpCell){
        if (playWithAI) {

            Board board = new Board(core.board.getFigures());
            abSeeker = new AI(board);

            // alpha-beta process
            abSeeker.calculateAiMove(status_check, player1, player2);
            aiMove = abSeeker.getBestMove();

            FigureType fg0 = core.board.getFigures()[aiMove.getF().getY()][aiMove.getF().getX()].getFigure();
            FigureType fg1 = core.board.getFigures()[aiMove.getS().getY()][aiMove.getS().getX()].getFigure();

            // 50 moves rule
            if (fg0 != FigureType.PAWN && fg1 == FigureType.NONE){
                moveCounter--;
            } else {
                moveCounter = 50;
            }
            tvMovesToDraw.setText(Integer.toString(moveCounter));

            if (aiMove == null)
                status_check = true;

            if (status_check) {
                if (aiMove == null) {
                    isGameRunning = false;
                    core.setGameStatus(GameStatus.NORMAL);
                    tvStatus.setText(R.string.status_win);
                    gamePlayer = gamePlayer == player1 ? player2 : player1;
                    if (gamePlayer.getColor() == Color.WHITE) {
                        tvTurn.setText(R.string.player_white);
                    } else {
                        tvTurn.setText(R.string.player_black);
                    }
                    finalDialog();
                    return;
                }
            }

            //tvStatus.setText(null);
            core.mainAction(
                    aiMove,
                    gamePlayer, gamePlayer == player1 ? player2 : player1,
                    false);

            // ai move check
            int ctr = 0;
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                {
                    if (board.getFigures()[i][j].getFigure() == core.board.getFigures()[i][j].getFigure())
                        ctr++;
                }
            if (ctr == 64)
            {
                ArrayList<vec> moves = board.getMoves(player1);
                boolean boardChanged = false;
                for (vec move : moves){
                    if (core.board.checkMove(move, player1) != MoveStatus.FORBIDDEN) {
                        core.board.makeMove(move, player1);
                        boardChanged = true;
                        break;
                    }
                }

                if (!boardChanged)
                {
                    isGameRunning = false;
                    core.setGameStatus(GameStatus.NORMAL);
                    tvStatus.setText(R.string.status_win);
                    gamePlayer = gamePlayer == player1 ? player2 : player1;
                    if (gamePlayer.getColor() == Color.WHITE) {
                        tvTurn.setText(R.string.player_white);
                    } else {
                        tvTurn.setText(R.string.player_black);
                    }
                    finalDialog();
                    return;
                }
            }

            // play sound
            AssetFileDescriptor afd = null;
            try {
                afd = getAssets().openFd("move.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaPlayer player = new MediaPlayer();
            try {
                assert afd != null;
                player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.start();

            status_check = false;

            // FIN
            if (moveCounter == 0) {
                isGameRunning = false;
                core.setGameStatus(GameStatus.DRAW);
                finalDialog();
                return;
            }

            if (core.getGameStatus() != GameStatus.CONTINUE &&
                    core.getGameStatus() != GameStatus.CHECK) {
                isGameRunning = false;
                tvStatus.setText(R.string.status_win);
                if (core.getGameStatus() == GameStatus.DRAW)
                    tvStatus.setText(R.string.draft);
                finalDialog();
                return;
            }

            if (gamePlayer == player1)
                gamePlayer = player2;
            else gamePlayer = player1;

            if (gamePlayer.getColor() == Color.WHITE) {
                tvTurn.setText(R.string.player_white);
            } else {
                tvTurn.setText(R.string.player_black);
            }

            if (!gamePlayer.getIsHuman()) {
                tvTurn.setText(R.string.thinking);
            }

            // STALEMATE
            possibleMovePoints = new ArrayList<>();
            Figure[][] fg = core.getBoard();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (fg[i][j].getColor() == (gamePlayer == player1 ? player1 : player2).getColor()) {
                        possibleMovePoints.add(new pos(i, j));
                    }
                }
            }

            int score1 = player1.getScore();
            int score2 = player2.getScore();
            boolean temp = true;

            for (pos p : possibleMovePoints) {
                ArrayList<pos> tempos = core.getInsets(p, gamePlayer == player1 ? player1 : player2);
                for (pos tp : tempos) {
                    Board tempBoard = new Board(fg);
                    tempBoard.makeMove(new vec(p, tp), gamePlayer == player1 ? player1 : player2);
                    if (tempBoard.checkRules(gamePlayer == player1 ? player2 : player1) != GameStatus.CHECK) {
                        temp = false;
                    }
                }
            }

            player1.setScore(score1);
            player2.setScore(score2);

            GameStatus gs = core.checkGame(gamePlayer == player1 ? player2 : player1, true);

            if (gs == GameStatus.CHECK) {


                if (temp) {
                    isGameRunning = false;
                    core.setGameStatus(GameStatus.NORMAL);
                    tvStatus.setText(R.string.status_win);
                    gamePlayer = gamePlayer == player1 ? player2 : player1;
                    finalDialog();
                    return;
                }

                tvStatus.setText(R.string.status_check);
                status_check = true;
            } else if (gs == GameStatus.CONTINUE) {
                tvStatus.setText("");
                if (temp) {
                    tvStatus.setText(R.string.stalemate);
                    isGameRunning = false;
                    core.setGameStatus(GameStatus.DRAW);
                    finalDialog();
                    return;
                }
            }
            visualizeAI(lpCell);
        }
    }

    private void emergencyBoard(){
        if (core == null) {return;}

        returnsBoard = new Figure[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                returnsBoard[i][j] = new Figure(core.board.getFigures()[i][j].getColor(),
                        core.board.getFigures()[i][j].getFigure());
            }
        }

    }

    /**
     * Play animation and make AI move
     * @param lpCell board cell params
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void visualizeMoves(final LinearLayout.LayoutParams lpCell){
        // declare animation path
        final ArrayList<vec> anim = new ArrayList<>();
        // add player path
        anim.add(new vec(new pos(yMove,xMove),
                new pos(yMove2,xMove2)));
        // get animation
        final AnimatorSet set = translationAnim(anim, lpCell);

        // listen for the end of animation
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(
                    Animator animation) {
                findViewById(R.id.pieceAnim).
                        setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // hide animation view
                findViewById(R.id.pieceAnim).setVisibility(
                        View.GONE);
                updateGraphics();

                if (core.getGameStatus() == GameStatus.CONTINUE ||
                        core.getGameStatus() == GameStatus.CHECK) {
                    isGameRunning = true;
                    // delay in milliseconds (200)
                    new Handler().postDelayed(() -> afterAnim(lpCell), 200);
                }
            }
        });
        set.start();
    }

    /**
     * Play animation and make AI move
     * @param lpCell board cell params
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void visualizeAI(final LinearLayout.LayoutParams lpCell){
        // declare animation path
        final ArrayList<vec> anim = new ArrayList<>();
        // add player path
        anim.add(aiMove);
        // get animation
        final AnimatorSet set = translationAnim(anim, lpCell);

        // listen for the end of animation
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(
                    Animator animation) {
                findViewById(R.id.pieceAnim).
                        setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // hide animation view
                findViewById(R.id.pieceAnim).setVisibility(
                        View.GONE);
                updateGraphics();
                if (core.getGameStatus() == GameStatus.CONTINUE ||
                        core.getGameStatus() == GameStatus.CHECK)
                    isGameRunning = true;
            }
        });
        set.start();
    }

    /**
     * Animation stuff
     * @param path move path
     * @param lpCell cell params
     * @return ready to play animation
     */
    @SuppressLint("NewApi")
    private AnimatorSet translationAnim(final ArrayList<vec> path,
                                        final LinearLayout.LayoutParams lpCell){
        /* Use this for translation animation */

        // lock the board
        isGameRunning = false;

        // find animation view
        LinearLayout animSpace = findViewById(R.id.animator);
        ImageView piece = findViewById(R.id.pieceAnim);

        final int[] location = new int[2];

        ivCell[path.get(0).getF().getY()][path.get(0).getF().getX()].
                getLocationOnScreen(location);

        if (path.size() != 0) {
            // prepare board and animation view
            piece.setImageDrawable(
                    ivCell[path.get(0).getF().getY()][path.get(0).getF().getX()].
                            getDrawable());
            animSpace.updateViewLayout(piece, lpCell);
            ivCell[path.get(0).getF().getY()][path.get(0).getF().getX()].
                    setImageDrawable(null);
        }

        // set animation path
        Path animPath = new Path();
        animPath.moveTo(location[0], location[1]);

        for (vec v : path) {
            ivCell[v.getS().getY()][v.getS().getX()].getLocationOnScreen(location);
            animPath.lineTo(location[0], location[1]);
        }

        //piece.setVisibility(View.VISIBLE);
        // add path to animator
        ValueAnimator pathAnimator = ObjectAnimator.ofFloat(
                piece, "x", "y", animPath);
        pathAnimator.setDuration(path.size() * 500);

        // add animator to animator set
        AnimatorSet set = new AnimatorSet();
        set.play(pathAnimator);
        return set;
    }

    /**
     * Updates board and score
     */
    @SuppressLint({"SetTextI18n", "NewApi"})
    private void updateGraphics() {
        /* Use this to draw a board */
        // get states array
        Figure[][] cellsState = core.getBoard();

        // parse states array and draw pieces
        for(int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cellsState[i][j].getFigure() == FigureType.PAWN
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[6]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.ROOK
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[7]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.KNIGHT
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[8]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.BISHOP
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[9]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.KING
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[10]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.QUEEN
                        && cellsState[i][j].getColor() == Color.WHITE){
                    ivCell[i][j].setImageDrawable(drawResources[11]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.PAWN
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[12]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.ROOK
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[13]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.KNIGHT
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[14]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.BISHOP
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[15]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.KING
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[16]);
                }
                else if (cellsState[i][j].getFigure() == FigureType.QUEEN
                        && cellsState[i][j].getColor() == Color.BLACK){
                    ivCell[i][j].setImageDrawable(drawResources[17]);
                }
                else {
                    ivCell[i][j].setImageDrawable(null);
                }
                if (ivCell[i][j].getBackground() == drawResources[3]
                        || ivCell[i][j].getBackground() == drawResources[4]){
                    if ((j % 2 == 0 && i % 2 != 0) || (j % 2 != 0 && i % 2 == 0))
                        ivCell[i][j].setBackground(drawResources[0]);
                    else
                        ivCell[i][j].setBackground(drawResources[1]);
                }
            }
        }

        int whites = 0;
        int blacks = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++){
                if (cellsState[i][j].getCol() == Color.WHITE)
                    whites++;
                else if (cellsState[i][j].getCol() == Color.BLACK)
                    blacks++;
            }
        // draw score
        tvWhiteScore.setText(Integer.toString(16 - blacks));
        tvBlackScore.setText(Integer.toString(16 - whites));

        /* OLD
        if (player1.getColor() == Color.WHITE) {

            //tvWhiteScore.setText(Integer.toString(player1.getScore()));
            //tvBlackScore.setText(Integer.toString(player2.getScore()));

        } else {
            //tvWhiteScore.setText(Integer.toString(player2.getScore()));
            //tvBlackScore.setText(Integer.toString(player1.getScore()));
        }*/
    }

    /**
     * @return board size in pixels
     */
    private float boardViewWidth() {
        /* Use this to take the size of the board */
        // get display metrics
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        // get offset (dp --> int)
        int OFFSET_CONSTANT = 25; // board offset on screen (was 44)
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OFFSET_CONSTANT,
                context.getResources().getDisplayMetrics());

        // return view width
        return (float) (dm.widthPixels - px);
    }
}

