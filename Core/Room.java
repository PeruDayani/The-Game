package byog.Core;

import java.util.Random;

public class Room {
    private char identity;
    private int width;
    private int height;
    private int xPos;
    private int yPos;
    //private TETile image;

    public Room(Random r, int w, int h, int i) {
        //image = Tileset.FLOOR;
        identity = (char) (65 + i);
        width = r.nextInt(10);
        if (width == 0) {
            width += 1;
        }
        height = r.nextInt(10);
        if (height == 0) {
            height += 1;
        }
        xPos = r.nextInt(w);
        yPos = r.nextInt(h);
        //System.out.println("Room "+
        // identity+" X: "+xPos+" Y:
        // "+yPos + "||  length:
        // "+width+" height: "+ height);
    }

    /**
     * public Room(int w, int h, int xP, int yP, TETile temp) {
     * width = w;
     * height = h;
     * xPos = xP;
     * yPos = yP;
     * image = temp;
     * }
     **/

    public char getIdentity() {
        return identity;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int xPos() {
        return xPos;
    }

    public int yPos() {
        return yPos;
    }
    /**
     public void change_image(TETile temp) {
     image = temp;
     }
     **/
}
