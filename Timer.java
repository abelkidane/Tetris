/**
 * Created by Abel on 9/30/17.
 */

import java.util.concurrent.Semaphore;

public class Timer extends Thread {

    private Semaphore semBoard;
    private Semaphore semPause;
    private Semaphore semLevel;
    private Semaphore semLines;
    private Semaphore semScore;
    private TetrisCanvas tetris;
    private double timeFactor;
    private int initTime;
    private int time;
    private int waitTime;

    public Timer(Semaphore sb, Semaphore sp, Semaphore slev, Semaphore slin, Semaphore score, double tf, TetrisCanvas t) {

        semBoard = sb;
        semPause = sp;
        semLevel = slev;
        semLines = slin;
        semScore = score;
        tetris = t;
        timeFactor = tf;
        if (timeFactor != 1) {
            timeFactor = 1- timeFactor;
        }
        else {
            timeFactor = 0.1;
        }
        initTime = 10000;
        time = 500;
        waitTime = (int)timeFactor;


    }

    public void run() {
        int waitTime = 0;

        while (!tetris.gameStarted) {
            //System.out.println("Still waiting for the game to start.");
            timeDelay(50);
        }
        boolean paused = false;
        while (true) {
            try {

                semPause.acquire();
                paused = tetris.pause;


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semPause.release();
            if (paused) {
                timeDelay(50);
                continue;
            }
            try {
//                try {
//                    semLevel.acquire();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                waitTime = (int)(waitTime * (1 - () )//(1.0 / Math.pow(tetris.level, 0.5)));
//                semLevel.release();
               // System.out.println(time);
                timeDelay(time);

//                System.out.println("Hello!");
                semBoard.acquire();
                //tetris.printBoard(tetris.board);
                tetris.removePiece();
                tetris.moveDown();
                tetris.setPiece();
                boolean valid = tetris.validateState();
                if (!valid) {
//                    System.out.println("Not Valid");
                    if (tetris.doesFullRowExist()) {
                     //   System.out.println("full row");
                        int lines = tetris.removeFullRows();

//                        semLines.acquire();
//                        int tempLines = tetris.linesCleared;
//                        semLines.release();
                        semLevel.acquire();
                        int tempLevel = tetris.level;

                        tetris.level = (tetris.linesCleared/ tetris.rowsPerLevel) + 1;
                        //double timeCalc = 1000/((double)(20) / initTime + (1 + tetris.level-1 * timeFactor));
                        //System.out.println("Time = " + timeCalc);
                        //System.out.println("Level = " + tetris.level);
                       // time = (int)(timeCalc);
                        //time = (int)((double)(initTime) / (1.0 + (double)(tetris.level-1) * timeFactor));
                        //time = (int) (1000.0/ (double) ((double)(initTime) * (1.0 + (double)(tempLevel) * timeFactor )));
                        if (tetris.level > tempLevel) {
                            time = (int)((double)(time) * timeFactor) ;
                        }
                        semLevel.release();
                        semScore.acquire();
                        tetris.score = tetris.score + lines * tempLevel * tetris.scoreFactor;
                        semScore.release();
                    }
                    tetris.nextPiece();
                    if (!tetris.isValidMove()) {
                        tetris.endGame();
                        semBoard.release();
                        return;
                    }
                } else {
                    tetris.setPiece();
                    tetris.repaint();
//                    System.out.println("Valid");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semBoard.release();
        }

    }

    public void timeDelay(long t) {
        try {
            Thread.sleep(t);

        } catch (InterruptedException e) {
        }
    }


}
