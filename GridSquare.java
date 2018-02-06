/**
 * Created by Abel on 9/26/17.
 */

import java.awt.*;

public class GridSquare {
    private int count;
    private Color color;
    private boolean isBorder;
    private boolean isDrawn;

    GridSquare(int type) {

        color = null;
        if (type == 0) {
            isBorder = true;
            isDrawn = false;
            count = 1;
        } else if (type == 1) {
            isBorder = false;
            isDrawn = false;
            count = 0;
        } else if (type == 2) {
            isBorder = false;
            isDrawn = true;
            count = 0;
        }
    }

    GridSquare(GridSquare gs) {
        count = gs.getCount();
        isBorder = gs.isBorder();
        isDrawn = gs.getIsDrawn();
        if (gs.getColor() == null)
            color = null;
        else
            color = new Color(gs.getColor().getRGB());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int c) {
        count = c;
    }

    public void countUp() {
        count++;
    }

    public void countDown() {
        if (count > 0)
            count--;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean getIsDrawn() {
        return isDrawn;
    }

    public boolean isBorder() {
        return isBorder;
    }

    public boolean isExtra() {
        return !isDrawn && !isBorder;
    }

    public boolean isOnBoard() {
        return isDrawn && !isBorder;
    }


}
