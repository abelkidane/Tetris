/**
 * Created by Abel on 9/26/17.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Semaphore;
import javax.swing.SwingUtilities;

public class Tetris extends Frame {
    public static void main(String[] args) {
        new Tetris();
    }

    Tetris() {
        super("Tetris");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setSize(600, 400);
        TetrisCanvas t = new TetrisCanvas();
        add("Center", t);
        //t.start();
        show();
    }
}

class TetrisCanvas extends Canvas {

    TetrisCanvas() {
        score = 0;
        zoomUp = true;
        scoreUp = scoreDown = levelUp = levelDown = speedUp = speedDown = sizeUp = sizeDown = zoomDown = false;
        scoreFactor = 1;
        rowsPerLevel = 4;
        speedFactor = 1;
        extraCols = 0;
        extraRows = 2 * extraCols;
        rows = 25 + extraRows;
        cols = 14 + extraCols;
        startRow = 3;
        startCol = cols/2;
        linesCleared = 0;
        level = 1;
        gameOver = false;
        gameStarted = false;
        largeMode = true;
        semBoard = new Semaphore(1);
        semPause = new Semaphore(1);
        semLevel = new Semaphore(1);
        semLines = new Semaphore(1);
        semScore = new Semaphore(1);
//        printPieceCount();
        //System.out.println();
        curPiece = null;
        nextPiece = null;
        curX = startCol;
        curY = startRow;
        xOff = 3;
        yOff = 2;

//        initializeBoards();

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int input = e.getKeyCode();

                switch (input) {
                    case 'w':
                    case 'W':
                    case KeyEvent.VK_UP: {
                        boolean tempPause = false;
                        if (gameOver)
                            return;
                        try {
                            semPause.acquire();
                            tempPause = pause;

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        semPause.release();
                        if (tempPause) {
                            return;
                        }
                        if (!gameStarted) {
                            return;
                        }
                        try {
                            semBoard.acquire();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        tryRotatingRight();
                        semBoard.release();
                        break;
                    }
                    case 's':
                    case 'S':
                    case KeyEvent.VK_DOWN: {
                        boolean tempPause = false;
                        if (gameOver)
                            return;
                        try {
                            semPause.acquire();
                            tempPause = pause;

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        semPause.release();
                        if (tempPause) {
                            return;
                        }
                        if (!gameStarted) {
                            return;
                        }
                        try {
                            semBoard.acquire();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        tryRotatingLeft();
                        semBoard.release();
                        break;
                    }
                    case 'a':
                    case 'A':
                    case KeyEvent.VK_LEFT: {
                        boolean tempPause = false;
                        if (gameOver)
                            return;
                        try {
                            semPause.acquire();
                            tempPause = pause;

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        semPause.release();
                        if (tempPause) {
                            return;
                        }
                        if (!gameStarted) {
                            return;
                        }
                        try {
                            semBoard.acquire();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        tryMovingLeft();

                        semBoard.release();
                        break;
                    }
                    case 'd':
                    case 'D':
                    case KeyEvent.VK_RIGHT: {
                        boolean tempPause = false;
                        if (gameOver)
                            return;
                        try {
                            semPause.acquire();
                            tempPause = pause;

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        semPause.release();
                        if (tempPause) {
                            return;
                        }
                        if (!gameStarted) {
                            return;
                        }
                        try {
                            semBoard.acquire();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        tryMovingRight();

                        semBoard.release();
                        break;
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        addMouseWheelListener(new MouseAdapter() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                boolean tempPause = false;
                if (gameOver)
                    return;
                try {
                    semPause.acquire();
                    tempPause = pause;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                semPause.release();
                if (tempPause) {
                    return;
                }
                try {
                    semBoard.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // If the wheel rotation is negative, then it was rotated up
                // Rotate the piece right
                if (evt.getWheelRotation() < 0) {
                    tryRotatingRight();
                }
                // Else it was rotated down. Rotate the piece left.
                else {
                    tryRotatingLeft();
                }
                semBoard.release();

            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                pause = mouseOnMainArea(evt);

                if (pause && gameStarted) {
                    swapPieces();
                }
                if (mouseOnQuit(evt)) {
                    System.exit(0);
                }
                if (mouseOnStart(evt)) {
                    //System.out.println("Game is starting!");
                    boolean alreadyStarted = gameStarted;
                    gameStarted = true;
                    if(!alreadyStarted) {
                        start();
                    }
                }
                if (!gameStarted) {
                    if (scoreDown) {
                        if (scoreFactor > 1) {
                            scoreFactor--;
                        }
                    } else if (scoreUp) {
                        if (scoreFactor < 10) {
                            scoreFactor++;
                        }
                    } else if (levelDown) {
                        if (rowsPerLevel > 4) {
                            rowsPerLevel--;
                        }
                    } else if (levelUp) {
                        if (rowsPerLevel < 50) {
                            rowsPerLevel++;
                        }
                    } else if (speedDown) {
                        if (speedFactor > 1) {
                            speedFactor--;
                        }
                    } else if (speedUp) {
                        if (speedFactor < 10) {
                            speedFactor++;
                        }
                    } else if (sizeDown) {
                        if (extraCols > 0) {
                            extraCols--;
                            cols = 14 + extraCols;
                            rows = 25 + extraCols * 2;
                            startCol = cols / 2;
                        }
                    } else if (sizeUp) {
                        if (extraCols < 50) {
                            extraCols++;
                            cols = 14 + extraCols;
                            rows = 25 + extraCols * 2;
                            startCol = cols / 2;
                        }
                    }
                }
               if (zoomDown) {
                    largeMode = false;
                } else if (zoomUp) {
                    largeMode = true;
                }
                repaint();
                boolean tempPause = false;
                if (gameOver)
                    return;
                try {
                    semPause.acquire();
                    tempPause = pause;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                semPause.release();
                if (tempPause) {
                    return;
                }
                if (!gameStarted) {
                    return;
                }
                try {
                    semBoard.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    tryMovingLeft();
                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    tryMovingRight();
                }
                semBoard.release();

            }


        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent evt) {
                onQuitButton = mouseOnQuit(evt);
                onStartButton = mouseOnStart(evt);

                pause = mouseOnMainArea(evt);



                scoreDown = mouseOnRectangle(evt, 3.5f, 4.0f, 3.5f, 4.0f);
                scoreUp = mouseOnRectangle(evt, 4.0f, 4.5f, 3.5f, 4.0f);
                levelDown = mouseOnRectangle(evt, 3.5f, 4.0f, 2.5f, 3.0f);
                levelUp = mouseOnRectangle(evt, 4.0f, 4.5f, 2.5f, 3.0f);
                speedDown = mouseOnRectangle(evt, 3.5f, 4.0f, 1.5f, 2.0f);
                speedUp = mouseOnRectangle(evt, 4.0f, 4.5f, 1.5f, 2.0f);
                sizeDown = mouseOnRectangle(evt, 3.5f, 4.0f, 0.5f, 1.0f);
                sizeUp = mouseOnRectangle(evt, 4.0f, 4.5f, 0.5f, 1.0f);
                zoomDown = mouseOnRectangle(evt, 3.5f, 4.0f, -0.5f, 0.0f);
                zoomUp = mouseOnRectangle(evt, 4.0f, 4.5f, -0.5f, 0.0f);


                repaint();
            }
        });
        pause = false;
        onQuitButton = false;
        onStartButton = false;

//        testPieces();
//        timer.start();
    }

    Semaphore semBoard, semPause, semLevel, semLines, semScore;
    Timer timer;
    TetrisPiece curPiece, lastPiece, nextPiece;
    int scoreFactor, rowsPerLevel, score;
    int rows, cols, extraRows, extraCols;
    boolean gameOver, gameStarted, largeMode;
    boolean scoreUp, scoreDown, levelUp, levelDown, speedUp, speedDown, sizeUp, sizeDown, zoomUp, zoomDown;
    int speedFactor;
    int linesCleared, level;
    int lastX, lastY;
    int curX, curY;
    int startRow, startCol;
    final int border = 0, extra = 1, inner = 2;
    final int J = 0, L = 1, T = 2, Z = 3, S = 4, I = 5, O = 6;
    int xOff, yOff;
    int maxX, maxY, minMaxXY, xCenter, yCenter;
    float pixelSize, rWidth, rHeight, pieceSize;
    boolean pause, onQuitButton, onStartButton, needToSwapPieces;
    GridSquare[][] board;
    GridSquare[][] prevBoard;
    final int[][][] pieces = {
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // J piece
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}}, // L piece
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // T piece
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // Z piece
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // S piece
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // I piece
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}} // O Piece
    };
    final Color[] colors = {Color.blue, Color.red, Color.orange, Color.magenta, Color.yellow, Color.cyan, Color.green};


    void initgr() {
        Dimension d = getSize();
        maxX = d.width - 1;
        maxY = d.height - 1;
        rWidth = 10.0f;
        rHeight = 10.0f;
        pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
        xCenter = maxX / 2;
        yCenter = maxY / 2;
        pieceSize = 0.4f * 20/(rows-5);
        if (!largeMode)
            pieceSize = pieceSize * .75f;
    }

    int IX(float x) {
        return Math.round(xCenter + (x / pixelSize));
    }

    int iX(float x) {
        return Math.round(xCenter + (x / pixelSize));
    }

    int iY(float y) {
        return Math.round(yCenter - (y / pixelSize));
    }

    int iL(float width) {
        //return Math.round(width / pixelSize) - 1;
        return (int) (width / pixelSize);
    }

    float fx(int x) {
        return (x - xCenter) * pixelSize;
    }

    float fy(int y) {
        return (yCenter - y) * pixelSize;
    }

    public void paint(Graphics g) {

        int fontSize;
        int minusFontSize = (int) (0.08f * (float) (Math.min(maxX, maxY)));
        int plusFontSize = (int) (0.06f * (float) (Math.min(maxX, maxY)));
        int tempLevel = 0, tempLines = 0, tempScore = 0;
        long startTime = System.nanoTime();
        initgr();
        float xStart = -3.5f;
        float yStart = 4.0f;
        float xNext = 2.0f;
        float yNext = 3.5f;

        float dimSize = rWidth / 2.0f;

        // Draw a border around the window.

        drawLine(g, -4.9f, 4.9f, 4.9f, 4.9f);
        drawLine(g, 4.9f, 4.9f, 4.9f, -4.9f);
        drawLine(g, 4.9f, -4.9f, -4.9f, -4.9f);
        drawLine(g, -4.9f, -4.9f, -4.9f, 4.9f);

        // Draw board border
        if(largeMode) {
            drawLine(g, -3.5f, 4, 0.5f, 4);
            drawLine(g, 0.5f, 4, 0.5f, -4);
            drawLine(g, 0.5f, -4, -3.5f, -4);
            drawLine(g, -3.5f, -4, -3.5f, 4);
        }
        else {
            drawLine(g, -3.5f, 4, -0.5f, 4);
            drawLine(g, -0.5f, 4, -0.5f, -2);
            drawLine(g, -0.5f, -2, -3.5f, -2);
            drawLine(g, -3.5f, -2, -3.5f, 4);

        }
        // Draw next piece border
        drawLine(g, 3.3f, 4.0f, 3.3f, 2.2f);
        drawLine(g, 3.3f, 2.2f, 1.0f, 2.2f);
        drawLine(g, 1.0f, 2.2f, 1.0f, 4.0f);
        drawLine(g, 1.0f, 4.0f, 3.3f, 4.0f);

        // Draw start button border
        drawLine(g, 1.5f, -1.5f, 3.0f, -1.5f);
        drawLine(g, 3.0f, -1.5f, 3.0f, -2.5f);
        drawLine(g, 3.0f, -2.5f, 1.5f, -2.5f);
        drawLine(g, 1.5f, -2.5f, 1.5f, -1.5f);

        // Draw quit button border
        drawLine(g, 1.5f, -3, 3.0f, -3);
        drawLine(g, 3.0f, -3, 3.0f, -4);
        drawLine(g, 3.0f, -4, 1.5f, -4);
        drawLine(g, 1.5f, -4, 1.5f, -3);

        if (onQuitButton) {
            g.drawRect(iX(1.5f), iY(-3.0f), iL(1.5f), iL(1.0f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(1.5f), iY(-3.0f), iL(1.5f), iL(1.0f));
            g.setColor(Color.BLACK);
        }

        if (onStartButton) {
            g.drawRect(iX(1.5f), iY(-1.5f), iL(1.5f), iL(1.0f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(1.5f), iY(-1.5f), iL(1.5f), iL(1.0f));
            g.setColor(Color.BLACK);
        }

        if (scoreDown) {
            g.drawRect(iX(3.5f), iY(4.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(3.5f), iY(4.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (scoreUp) {
            g.drawRect(iX(4.0f), iY(4.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(4.0f), iY(4.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (levelDown) {
            g.drawRect(iX(3.5f), iY(3.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(3.5f), iY(3.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (levelUp) {
            g.drawRect(iX(4.0f), iY(3.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(4.0f), iY(3.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (speedDown) {
            g.drawRect(iX(3.5f), iY(2.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(3.5f), iY(2.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (speedUp) {
            g.drawRect(iX(4.0f), iY(2.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(4.0f), iY(2.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (sizeDown) {
            g.drawRect(iX(3.5f), iY(1.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(3.5f), iY(1.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        else if (sizeUp) {
            g.drawRect(iX(4.0f), iY(1.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(4.0f), iY(1.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        if (zoomDown || !largeMode) {
            g.drawRect(iX(3.5f), iY(0.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(3.5f), iY(0.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }
        if (zoomUp || largeMode) {
            g.drawRect(iX(4.0f), iY(0.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(iX(4.0f), iY(0.0f), iL(0.5f), iL(0.5f));
            g.setColor(Color.BLACK);
        }


        // Draw control buttons

        // Draw Score Control
        drawLine(g, 3.5f, 4.0f, 4.0f, 4.0f);
        drawLine(g, 4.0f, 4.0f, 4.0f, 3.5f);
        drawLine(g, 4.0f, 3.5f, 3.5f, 3.5f);
        drawLine(g, 3.5f, 3.5f, 3.5f, 4.0f);


        // Draw the string "-"
        g.setFont(new Font("Arial", Font.PLAIN, minusFontSize));
        g.drawString("-", iX(3.63f), iY(3.55f));

        drawLine(g, 4.0f, 4.0f, 4.5f, 4.0f);
        drawLine(g, 4.5f, 4.0f, 4.5f, 3.5f);
        drawLine(g, 4.5f, 3.5f, 4.0f, 3.5f);
        drawLine(g, 4.0f, 3.5f, 4.0f, 4.0f);

        g.setFont(new Font("Arial", Font.PLAIN, plusFontSize));
        g.drawString("+", iX(4.09f), iY(3.55f));

        fontSize = (int) (0.03f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Score: " + scoreFactor, iX(3.5f), iY(4.1f));


        // Draw Level Controls

        drawLine(g, 3.5f, 3.0f, 4.0f, 3.0f);
        drawLine(g, 4.0f, 3.0f, 4.0f, 2.5f);
        drawLine(g, 4.0f, 2.5f, 3.5f, 2.5f);
        drawLine(g, 3.5f, 2.5f, 3.5f, 3.0f);


        // Draw the string "-"
        g.setFont(new Font("Arial", Font.PLAIN, minusFontSize));
        g.drawString("-", iX(3.63f), iY(2.55f));

        drawLine(g, 4.0f, 3.0f, 4.5f, 3.0f);
        drawLine(g, 4.5f, 3.0f, 4.5f, 2.5f);
        drawLine(g, 4.5f, 2.5f, 4.0f, 2.5f);
        drawLine(g, 4.0f, 2.5f, 4.0f, 3.0f);

        g.setFont(new Font("Arial", Font.PLAIN, plusFontSize));
        g.drawString("+", iX(4.09f), iY(2.55f));

        fontSize = (int) (0.03f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Level: " + rowsPerLevel, iX(3.5f), iY(3.1f));


        // Draw Speed Controls

        drawLine(g, 3.5f, 2.0f, 4.0f, 2.0f);
        drawLine(g, 4.0f, 2.0f, 4.0f, 1.5f);
        drawLine(g, 4.0f, 1.5f, 3.5f, 1.5f);
        drawLine(g, 3.5f, 1.5f, 3.5f, 2.0f);


        // Draw the string "-"
        g.setFont(new Font("Arial", Font.PLAIN, minusFontSize));
        g.drawString("-", iX(3.63f), iY(1.55f));

        drawLine(g, 4.0f, 2.0f, 4.5f, 2.0f);
        drawLine(g, 4.5f, 2.0f, 4.5f, 1.5f);
        drawLine(g, 4.5f, 1.5f, 4.0f, 1.5f);
        drawLine(g, 4.0f, 1.5f, 4.0f, 2.0f);

        g.setFont(new Font("Arial", Font.PLAIN, plusFontSize));
        g.drawString("+", iX(4.09f), iY(1.55f));

        fontSize = (int) (0.025f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Speed: " + speedFactor + "/10", iX(3.43f), iY(2.1f));


        // Draw Size Controls

        drawLine(g, 3.5f, 1.0f, 4.0f, 1.0f);
        drawLine(g, 4.0f, 1.0f, 4.0f, 0.5f);
        drawLine(g, 4.0f, 0.5f, 3.5f, 0.5f);
        drawLine(g, 3.5f, 0.5f, 3.5f, 1.0f);


        // Draw the string "-"
        g.setFont(new Font("Arial", Font.PLAIN, minusFontSize));
        g.drawString("-", iX(3.63f), iY(0.55f));

        drawLine(g, 4.0f, 1.0f, 4.5f, 1.0f);
        drawLine(g, 4.5f, 1.0f, 4.5f, 0.5f);
        drawLine(g, 4.5f, 0.5f, 4.0f, 0.5f);
        drawLine(g, 4.0f, 0.5f, 4.0f, 1.0f);

        g.setFont(new Font("Arial", Font.PLAIN, plusFontSize));
        g.drawString("+", iX(4.09f), iY(0.55f));

        fontSize = (int) (0.03f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Size: " + (extraCols+10), iX(3.5f), iY(1.1f));


        // Draw Zoom Controls
        drawLine(g, 3.5f, 0.0f, 4.0f, 0.0f);
        drawLine(g, 4.0f, 0.0f, 4.0f, -0.5f);
        drawLine(g, 4.0f, -0.5f, 3.5f, -0.5f);
        drawLine(g, 3.5f, -0.5f, 3.5f, 0.0f);


        // Draw the string "-"
        fontSize = (int) (0.04f * (float) (Math.min(maxX, maxY)));

        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("N", iX(3.63f), iY(-0.4f));

        drawLine(g, 4.0f, 0.0f, 4.5f, 0.0f);
        drawLine(g, 4.5f, 0.0f, 4.5f, -0.5f);
        drawLine(g, 4.5f, -0.5f, 4.0f, -0.5f);
        drawLine(g, 4.0f, -0.5f, 4.0f, 0.0f);

        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Y", iX(4.09f), iY(-0.4f));

        fontSize = (int) (0.03f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("Zoom? ", iX(3.5f), iY(0.1f));


//        for (int i = 3; i < 23; i++) {
//            for (int j = 2; j < 12; j++) {
//                if (!board[i][j].isEmpty()) {
//                    drawTetrisSquare(g, board[i][j].getColor(), xStart + ((j - 2) * pieceSize), yStart - ((i - 3) * pieceSize));
//                }
//            }
//        }
        if (gameStarted) {
            for (int i = 3; i < rows - 2; i++) {
                for (int j = 2; j < cols - 2; j++) {
                    if (!board[i][j].isEmpty()) {
                        drawTetrisSquare(g, board[i][j].getColor(), xStart + ((j - 2) * pieceSize), yStart - ((i - 3) * pieceSize));
                    }
                }
            }
        }

        if (gameStarted && nextPiece != null) {
            int[][] npArray = nextPiece.getPiece();
            for (int i = 0; i < npArray.length; i++) {
                int xOff = npArray[i][0];
                int yOff = npArray[i][1];
                drawTetrisSquare(g, nextPiece.getColor(), xNext - (xOff * pieceSize), yNext - (yOff * pieceSize));
            }
        }


        fontSize = (int) (0.0675f * (float) (Math.min(maxX, maxY)));

        if (pause && !gameOver) {
            // Draw the PAUSE Rectangle
            g.setColor(Color.BLACK);
            g.drawRect(iX(-2.75f), iY(0.5f), iL(2.5f), iL(1.0f));

            // Draw the string "PAUSE"
            g.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g.drawString("PAUSE", iX(-2.65f), iY(-0.22f));
        }

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.drawRect(iX(-2.75f), iY(3.2f), iL(2.5f), iL(1.0f));

            fontSize = (int) (0.0375f * (float) (Math.min(maxX, maxY)));

            // Draw the string "PAUSE"
            g.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g.drawString("GAME OVER", iX(-2.65f), iY(2.5f));

        }

        // Draw the string "START"
        g.setColor(Color.BLACK);
        fontSize = (int) (0.04f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("START", iX(1.6f), iY(-2.2f));

        // Draw the string "QUIT"
        g.setColor(Color.BLACK);
        fontSize = (int) (0.05f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString("QUIT", iX(1.7f), iY(-3.7f));

        try {
            semLevel.acquire();
            tempLevel = level;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semLevel.release();

        try {
            semLines.acquire();
            tempLines = linesCleared;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semLines.release();



        // Draw the string "Level:   1"
        fontSize = (int) (0.04f * (float) (Math.min(maxX, maxY)));
        g.setFont(new Font("Courier", Font.PLAIN, fontSize));
        g.drawString("Level: " + tempLevel, iX(1.0f), iY(1.0f));
        g.drawString("Lines: " + tempLines, iX(1.0f), iY(0.0f));
        g.drawString("Score: " + score, iX(1.0f), iY(-1.0f));

        long endTime = System.nanoTime();
        //System.out.println(startTime + "   " + endTime);

        double difference = (double)(endTime - startTime) / 1000000000;

        //System.out.println(difference);
        double fps = 1.0 / difference;

        //System.out.println("FPS = " + fps);

    }

    private void drawLine(Graphics g, float x1, float y1, float x2, float y2) {
        g.drawLine(iX(x1), iY(y1), iX(x2), iY(y2));
    }

    private void drawTetrisSquare(Graphics g, Color color, float x, float y) {

        g.drawRect(iX(x), iY(y), iL(pieceSize), iL(pieceSize));
        g.setColor(color);
        g.fillRect(iX(x), iY(y), iL(pieceSize), iL(pieceSize));
        g.setColor(Color.BLACK);
    }

    private boolean mouseOnQuit(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        return (iX(1.5f) <= x && x <= iX(3.0f) && iY(-4.0f) >= y && y >= iY(-3.0f));
    }
    private boolean mouseOnStart(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        return (iX(1.5f) <= x && x <= iX(3.0f) && iY(-2.5f) >= y && y >= iY(-1.5f));
    }

    private boolean mouseOnMainArea(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        return (iX(-3.5f) <= x && x <= iX(0.5f) && iY(-4.0f) >= y && y >= iY(4.0f));
    }
    private boolean mouseOnRectangle(MouseEvent evt, float minX, float maxX, float minY, float maxY) {
        int x = evt.getX();
        int y = evt.getY();
        return (iX(minX) <= x && x <= iX(maxX) && iY(minY) >= y && y >= iY(maxY));
    }



    public void printBoard(GridSquare[][] b) {
        char block = 0x25A0;
        System.out.print("  ");
        for (int i = 0; i < cols; i++) {
            System.out.print(i % 10 + " ");
        }
        System.out.println();
        for (int i = 0; i < b.length; i++) {
            System.out.print(i % 10 + " ");
            for (int j = 0; j < b[i].length; j++) {

                GridSquare gs = b[i][j];
                if (gs == null) {
                    System.out.print("! ");
                } else if (gs.isBorder()) {
                    System.out.print("B ");
                } else if (gs.isExtra()) {
                    System.out.print("E ");
                } else if (gs.getCount() > 0) {
                    System.out.print(block + " ");
                } else if (gs.isOnBoard()) {
                    System.out.print("  ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.print(i % 10);
            System.out.println();
        }
        System.out.print("  ");
        for (int i = 0; i < cols; i++) {
            System.out.print(i % 10 + " ");
        }
        System.out.println();
//
        //printPieceCount();
        System.out.println();
    }

    public void setPiece() {
        final int[][] piece = curPiece.getPiece();
        int rowOffset;
        int colOffset;
        for (int i = 0; i < piece.length; i++) {
            rowOffset = piece[i][0];
            colOffset = piece[i][1];
            board[curY + rowOffset][curX + colOffset].setColor(curPiece.getColor());
//            System.out.println("Count = " + board[curY + rowOffset][curX + colOffset].getCount());
            board[curY + rowOffset][curX + colOffset].countUp();
//            System.out.println("Count now = " + board[curY + rowOffset][curX + colOffset].getCount());
        }
//        lastX = curX;
//        lastY = curY;
    }

    public void removePiece() {
        final int[][] piece = curPiece.getPiece();
        int rowOffset;
        int colOffset;
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                rowOffset = piece[i][0];
                colOffset = piece[i][1];
                board[curY + rowOffset][curX + colOffset].setColor(null);
                board[curY + rowOffset][curX + colOffset].countDown();
            }
        }

    }

    private void removePiece(int[][] piece, int row, int col) {
        int rowOffset;
        int colOffset;
        for (int i = 0; i < piece.length; i++) {
            rowOffset = piece[i][0];
            colOffset = piece[i][1];
            board[row + rowOffset][col + colOffset].setColor(null);
            board[row + rowOffset][col + colOffset].countDown();
        }
    }


    public void moveDown() {
        curY++;
    }

    private void moveLeft() {
        curX--;
    }

    private void moveRight() {
        curX++;
    }

    public void timeDelay(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
        }
    }

    public boolean isValidMove() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getCount() > 1) {
//                    System.out.println("i = " + i + ". j = " + j);
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeBoards() {

        board = new GridSquare[rows][cols];
        prevBoard = new GridSquare[rows][cols];
        // Create top and bottom borders
        for (int i = 0; i < board[0].length; i++) {
            board[0][i] = new GridSquare(border);
            board[rows-2][i] = new GridSquare(border);
            board[rows-1][i] = new GridSquare(border);
            prevBoard[0][i] = new GridSquare(border);
            prevBoard[rows-2][i] = new GridSquare(border);
            prevBoard[rows-1][i] = new GridSquare(border);
        }
        // Create left and right borders
        for (int i = 1; i < board.length - 2; i++) {
            board[i][0] = new GridSquare(border);
            board[i][1] = new GridSquare(border);
            board[i][cols-2] = new GridSquare(border);
            board[i][cols-1] = new GridSquare(border);
            prevBoard[i][0] = new GridSquare(border);
            prevBoard[i][1] = new GridSquare(border);
            prevBoard[i][cols-2] = new GridSquare(border);
            prevBoard[i][cols-1] = new GridSquare(border);
        }
        // Create extra spaces that aren't drawn at the top of the board
        for (int i = 2; i < board[0].length - 2; i++) {
            board[1][i] = new GridSquare(extra);
            board[2][i] = new GridSquare(extra);
            prevBoard[1][i] = new GridSquare(extra);
            prevBoard[2][i] = new GridSquare(extra);
        }
        // Create actual board
        for (int i = 3; i < board.length - 2; i++) {
            for (int j = 2; j < board[i].length - 2; j++) {
                board[i][j] = new GridSquare(inner);
                prevBoard[i][j] = new GridSquare(inner);

            }
        }
    }

    private void testPieces() {
//        printPieceCount();
//        while(!gameStarted) {
//            repaint();
//            //timeDelay(50);
//        }
        curPiece = new TetrisPiece();
        lastPiece = new TetrisPiece(curPiece.getPieceNum());
        nextPiece = new TetrisPiece();
        //moveDown();
        setPiece();

    }

    public void updatePrevBoard() {
        prevBoard = new GridSquare[rows][cols];
        for (int i = 0; i < prevBoard.length; i++) {
            for (int j = 0; j < prevBoard[i].length; j++) {
                prevBoard[i][j] = new GridSquare(board[i][j]);
            }
        }
        lastX = curX;
        lastY = curY;
        lastPiece.setPiece(curPiece.getPiece());

    }

    private void rollback() {

        removePiece();
        board = new GridSquare[rows][cols];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new GridSquare(prevBoard[i][j]);
            }
        }
        curX = lastX;
        curY = lastY;
//        System.out.println("curX = " + curX + ", curY = " + curY);
//        System.out.println("lastX = " + lastX + ", lastY = " + lastY);
        curPiece.setPiece(lastPiece.getPiece());

    }

    public void printPieceCount() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j].getCount());
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean validateState() {
        if (isValidMove()) {
            updatePrevBoard();
            //  printBoard(board);
            return true;
        } else {
            rollback();
            return false;
        }
    }

    public void nextPiece() {
        //setPiece();

        curPiece = new TetrisPiece(nextPiece.getPieceNum());
        lastPiece = new TetrisPiece(curPiece.getPieceNum());
        nextPiece = new TetrisPiece();
        curX = startCol;
        curY = startRow;
        lastX = curX;
        lastY = curY;
        setPiece();
    }

    private void tryMovingLeft() {
        removePiece();
        moveLeft();
        setPiece();
        if (validateState()) {
            repaint();
        }
    }

    private void tryMovingRight() {
        removePiece();
        moveRight();
        setPiece();
        if (validateState()) {
            repaint();
        }
    }

    private void tryRotatingRight() {
        removePiece();
        curPiece.rotateRight();
        setPiece();
        if (validateState()) {
            repaint();
        }

    }

    private void tryRotatingLeft() {
        removePiece();
        curPiece.rotateLeft();
        setPiece();
        if (validateState()) {
            repaint();
        }

    }

    public boolean doesFullRowExist() {

        for (int i = xOff; i < board.length - 2; i++) {
            boolean full = true;
            for (int j = yOff; j < board[i].length; j++) {
                //System.out.println("i = " + i + ", j = " + j);
                if (board[i][j].getCount() == 0) {
                    full = false;
                }
            }
            if (full) {
                return true;
            }
        }
        return false;

    }
    public boolean doesFullRowExist(int row) {
        for (int i = 0; i < board[row].length; i++) {
            if (board[row][i].getCount() == 0) {
                return false;
            }
        }
        return true;
    }

    public void endGame() {

        gameOver = true;
        repaint();
        //System.out.println("Game Over!");
    }

    public int removeFullRows () throws InterruptedException {
        int count = 0;
        for (int i = board.length - 3; i > xOff; i--) {
            if (doesFullRowExist(i)) {
                count++;
                for (int l = i; l > xOff; l--) {
                    for (int k = 0; k < board[i].length; k++) {
                        board[l][k] = new GridSquare(board[l - 1][k]);
                    }
                }
                i++;
                semLines.acquire();
                linesCleared++;
                semLines.release();
            }

        }
        return count;
    }

    public void swapPieces () {
        removePiece();
        TetrisPiece temp = null;
        temp = curPiece;
        curPiece = nextPiece;
        nextPiece = temp;
        curX = startCol;
        curY = startRow;
        lastX = curX;
        lastY = curY;
        setPiece();
    }

    public void start() {

        startCol = cols / 2;
        curX = startCol;
        curY = startRow;
        initializeBoards();
        testPieces();
        timer = new Timer(semBoard, semPause, semLevel, semLines, semScore, (double)(speedFactor)/10.0, this);
        timer.start();
    }
}
