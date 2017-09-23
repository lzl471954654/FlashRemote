package com.lp.flashremote.beans;

public class MouseOpInfo {
    private float X;
    private float Y;
    private boolean click;
    private boolean singleClick;
    private boolean doubleClick;
    private boolean rightClick;



    public boolean isClick() {
        return click;
    }

    public void setClick(boolean click) {
        this.click = click;
    }


    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public boolean isSingleClick() {
        return singleClick;
    }

    public void setSingleClick(boolean singleClick) {
        this.singleClick = singleClick;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public boolean isRightClick() {
        return rightClick;
    }

    public void setRightClick(boolean rightClick) {
        this.rightClick = rightClick;
    }
}
