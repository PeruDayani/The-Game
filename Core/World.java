package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class World implements java.io.Serializable {

    private TERenderer ter = new TERenderer();
    private TETile[][] world;
    private TETile[][] finalworld;
    private Random r;
    private int width;
    private int height;
    private int playerXPos;
    private int playerYPos;
    private int pXpos;
    private int pYpos;
    private int[][] diamonds;
    private int collected1;
    private int collected2;
    private int winner;

    public World(TETile[][] currentworld) {
        for (int i = 0; i < width + 2; i += 1) {
            for (int j = 0; j < height + 2; j += 1) {
                finalworld[i][j] = currentworld[i][j];
            }
        }
    }

    public World(Random rN, int W, int H) {
        width = W;
        height = H;
        world = new TETile[W][H];
        finalworld = new TETile[W + 2][H + 2];
        r = rN;
        playerXPos = 0;
        playerYPos = 0;
        pXpos = 0;
        pYpos = 0;
        collected1 = 0;
        collected2 = 0;
        diamonds = new int[10][2];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.WATER;
            }
        }
        for (int x = 0; x < width + 2; x += 1) {
            for (int y = 0; y < height + 2; y += 1) {
                finalworld[x][y] = Tileset.WATER;
            }
        }
    }

    public String getposition() {
        return "Player 1: " + collected1 + " | Players 2: " + collected2;
    }

    public void createRoomsAndConnect(int maxfigures) {
        ArrayDeque<Room> rooms = new ArrayDeque<>();
        for (int i = 0; i < maxfigures; i++) {
            Room room = new Room(r, width - 1, height - 1, i);
            //System.out.println("Creating Room: " + room.getIdentity());
            rooms.addLast(room);
            addfigure(room.width(), room.height(), room.xPos(), room.yPos(), Tileset.SAND);
        }
        ArrayDeque<Room>[] arrayOfConnectedFigures = (ArrayDeque<Room>[]) connections(rooms);
        connectfigures(arrayOfConnectedFigures);
    }

    private void addfigure(int w, int h, int xpos, int ypos, TETile t) {
        //System.out.println("Adding figure");
        for (int i = 0; i < h; i += 1) {
            for (int j = 0; j < w; j += 1) {
                if (xpos + j < width - 1 && ypos + i < height - 1) {
                    world[xpos + j][ypos + i] = t;
                    addaround(xpos + j, ypos + i);
                }
            }
        }
    }

    private void addaround(int posx, int posy) {
        if (world[posx][posy] == Tileset.SAND) {
            for (int i = -1; i < 2; i += 1) {
                for (int j = -1; j < 2; j += 1) {
                    int x = posx + i;
                    int y = posy + j;
                    if (x < width && y < height && x >= 0 && y >= 0) {
                        if (world[x][y] == Tileset.WATER) {
                            world[x][y] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

    private void addaroundfinal(int posx, int posy) {
        if (finalworld[posx][posy] == Tileset.SAND) {
            for (int i = -1; i < 2; i += 1) {
                for (int j = -1; j < 2; j += 1) {
                    int x = posx + i;
                    int y = posy + j;
                    if (x < width && y < height && x >= 0 && y >= 0) {
                        if (finalworld[x][y] == Tileset.WATER) {
                            finalworld[x][y] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

    private void connectclosest(ArrayDeque<Room> A, ArrayDeque<Room> B) {
        //System.out.println("connectclosest");
        Room closestA = A.get(0);
        Room closestB = B.get(0);
        double mindist;
        mindist = distance(closestA.xPos(), closestA.yPos(), closestB.xPos(), closestB.yPos());
        //System.out.println("Min_ddist: "+ mindist);
        for (int i = 0; i < A.size(); i += 1) {
            for (int j = 0; j < B.size(); j += 1) {
                Room currA = A.get(i);
                Room currB = B.get(j);
                int xA = currA.xPos();
                int yA = currA.yPos();
                int xB = currB.xPos();
                int yB = currB.yPos();
                double currdist = distance(xA, yA, xB, yB);
                //    System.out.println("Distance b/w " + currA.getIdentity() +
                //     " and " + currB.getIdentity() + " is: " + currdist);
                if (currdist < mindist) {
                    closestA = currA;
                    closestB = currB;
                    mindist = currdist;
                }
            }
        }
        makeconnection(closestA, closestB);

    }

    private double distance(int xA, int yA, int xB, int yB) {
        double A = (Math.pow((double) (xA - xB), (double) 2));
        double B = (Math.pow((double) (yA - yB), (double) 2));
        return Math.sqrt(A + B);

    }

    private void connectfigures(ArrayDeque<Room>[] array) {
        //System.out.println("connectfigures");
        int groups = array.length;
        for (int i = 1; i < groups; i += 1) {
            int randomgroup = r.nextInt(i);
            ArrayDeque<Room> first = array[randomgroup];
            ArrayDeque<Room> second = array[i];
            connectclosest(first, second);
        }
    }

    private void makeconnection(Room a, Room b) {
        //System.out.println("makeconnections");
        int xA = a.xPos();
        int yA = a.yPos();
        int xB = b.xPos();
        int yB = b.yPos();
        //addfigure(int width, int height, int x_pos, int ypos, TETile[][] world, TETile temp)
        //System.out.println("Connecting Rooms" + a.getIdentity() + " & " + b.getIdentity());
        if (xB > xA) {
            addfigure(xB - xA, 1, xA, yA, Tileset.SAND);
            if (yA > yB) {
                addfigure(1, yA - yB, xB, yB, Tileset.SAND);
            }
            if (yB > yA) {
                addfigure(1, yB - yA, xB, yA, Tileset.SAND);
            }
        } else if (xA > xB) {
            addfigure(xA - xB, 1, xB, yB, Tileset.SAND);
            if (yB > yA) {
                addfigure(1, yB - yA, xA, yA, Tileset.SAND);
            }
            if (yA > yB) {
                addfigure(1, yA - yB, xA, yB, Tileset.SAND);
            }
        } else {
            if (yA > yB) {
                addfigure(1, yA - yB, xA, yB, Tileset.SAND);
            }
            if (yB > yA) {
                addfigure(1, yB - yA, xA, yA, Tileset.SAND);
            }
        }
    }

    private boolean isConnected(Room A, Room B) {
        boolean result = connected(A.xPos(), A.yPos(), B.xPos(), B.yPos());
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                if (world[x][y] == Tileset.MOUNTAIN) {
                    world[x][y] = Tileset.SAND;
                }
            }
        }
        return result;
    }

    private boolean connected(int ax, int ay, int bx, int by) {
        if (ax == bx && ay == by) {
            return true;
        }
        boolean p = false;
        boolean q = false;
        boolean t = false;
        boolean s = false;
        int x = ax;
        int y = ay + 1;
        if (x < width && y < height && x > 0 && y > 0) {
            if (world[x][y] == Tileset.SAND) {
                world[x][y] = Tileset.MOUNTAIN;
                p = connected(x, y, bx, by);
            }
        }
        x = ax + 1;
        y = ay;
        if (x < width && y < height && x > 0 && y > 0) {
            if (world[x][y] == Tileset.SAND) {
                world[x][y] = Tileset.MOUNTAIN;
                q = connected(x, y, bx, by);
            }
        }
        x = ax - 1;
        y = ay;
        if (x < width && y < height && x > 0 && y > 0) {
            if (world[x][y] == Tileset.SAND) {
                world[x][y] = Tileset.MOUNTAIN;
                t = connected(x, y, bx, by);
            }
        }
        x = ax;
        y = ay - 1;
        if (x < width && y < height && x > 0 && y > 0) {
            if (world[x][y] == Tileset.SAND) {
                world[x][y] = Tileset.MOUNTAIN;
                s = connected(x, y, bx, by);
            }
        }
        return p || q || t || s;
    }

    private Object[] connections(ArrayDeque<Room> l) {
        int numberOfRooms = l.size();
        ArrayDeque<Room>[] tempArrayOfCF = (ArrayDeque<Room>[]) new ArrayDeque[numberOfRooms];
        int arrayCounter = 0;
        int actualSize = 0;
        while (!l.isEmpty()) {
            ArrayDeque<Room> connectedFigures = new ArrayDeque<>();
            Room first = l.removeFirst();
            connectedFigures.addFirst(first);
            int lSize = l.size();
            int[] tempIndexesToBeRemoved = new int[lSize];
            int actualNumber = 0;
            int iTBRCounter = 0;
            for (int i = 0; i < lSize; i++) {
                boolean result = isConnected(first, l.get(i));
                if (result) {
                    tempIndexesToBeRemoved[iTBRCounter] = i;
                    iTBRCounter += 1;
                    actualNumber += 1;
                }
            }
            if (actualNumber > 0) {
                for (int j = 0; j < actualNumber; j++) {
                    connectedFigures.addLast(l.get(tempIndexesToBeRemoved[j]));

                }
                int[] indexesToBeRemoved = new int[actualNumber];
                System.arraycopy(tempIndexesToBeRemoved, 0, indexesToBeRemoved, 0, actualNumber);
                l.removeIndex(indexesToBeRemoved);
            }
            tempArrayOfCF[arrayCounter] = connectedFigures;
            arrayCounter += 1;
            actualSize += 1;
        }
        ArrayDeque<Room>[] arrayOfCF = (ArrayDeque<Room>[]) new ArrayDeque[actualSize];
        System.arraycopy(tempArrayOfCF, 0, arrayOfCF, 0, actualSize);
        return arrayOfCF;
    }

    public void makeFinalWorld() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                finalworld[x + 1][y + 1] = world[x][y];
                addaroundfinal(x + 1, y + 1);
            }
        }
    }

    public void initializeRender(){
        ter.initialize(width + 5, height + 5);
    }

    public void renderWorld() {
        TETile[][] temp = new TETile[width + 2][height + 2];
        //System.out.println("Width: " + width + " height: " + height);
        //System.out.println("Collected 1: " + collected1 + " Collected 2" + collected2);
        //System.out.println("Player 1 x: " + pXpos + " Player 1 y" + pYpos);
        //System.out.println("Player 2 x: " + playerXPos + " Player 2 y" + playerYPos);
        //System.out.println("TER: " + ter);

        int range1 = 10 - collected1;
        int range2 = 10 - collected2;
        int x2min = pXpos - range2;
        int x2max = pXpos + range2;
        int y2min = pYpos - range2;
        int y2max = pYpos + range2;
        int x1min = playerXPos - range1;
        int x1max = playerXPos + range1;
        int y1min = playerYPos - range1;
        int y1max = playerYPos + range1;
        for (int i = 0; i < width + 2; i += 1) {
            for (int j = 0; j < height + 2; j += 1) {
                if ((i >= x1min && i <= x1max && j >= y1min && j <= y1max)
                        || (i >= x2min && i <= x2max && j >= y2min && j <= y2max)) {
                    temp[i][j] = finalworld[i][j];
                } else {
                    temp[i][j] = Tileset.NOTHING;
                }
            }
        }

        ter.renderFrame(temp);
    }

    public void initializeDiamonds() {
        for (int i = 0; i < diamonds.length; i += 1) {
            int x = r.nextInt(width);
            int y = r.nextInt(height);
            while (finalworld[x][y] != Tileset.SAND) {
                x = r.nextInt(width);
                y = r.nextInt(height);
            }
            finalworld[x][y] = Tileset.TREE;
        }
    }

    public void initializePlayer() {
        do {
            playerXPos = r.nextInt(width);
            playerYPos = r.nextInt(height);
        } while (finalworld[playerXPos][playerYPos] != Tileset.SAND);
        finalworld[playerXPos][playerYPos] = Tileset.FLOWER;

        do {
            pXpos = r.nextInt(width);
            pYpos = r.nextInt(height);
        } while (finalworld[pXpos][pYpos] != Tileset.SAND);
        finalworld[pXpos][pYpos] = Tileset.FLOWER;
    }

    public boolean checkPlayerMove(char c) {
        //System.out.println("checking move");
        //System.out.println("player 1 x : " + playerXPos);
        //System.out.println("player 1 y : " + playerYPos);
        //System.out.println("player 2 x : " + pXpos);
        //System.out.println("player 2 y : " + pYpos);

        //System.out.println("final world tile one : " + typeOfTile(pXpos,pYpos));
        //System.out.println("final world tile two: " + typeOfTile(playerXPos,playerYPos));


        boolean allowed = false;
        if (c == 'w' || c == 'W') {
            if (finalworld[playerXPos][playerYPos + 1] == Tileset.SAND
                    || finalworld[playerXPos][playerYPos + 1] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'a' || c == 'A') {
            if (finalworld[playerXPos - 1][playerYPos] == Tileset.SAND
                    || finalworld[playerXPos - 1][playerYPos] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'd' || c == 'D') {
            if (finalworld[playerXPos + 1][playerYPos] == Tileset.SAND
                    || finalworld[playerXPos + 1][playerYPos] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 's' || c == 'S') {
            if (finalworld[playerXPos][playerYPos - 1] == Tileset.SAND
                    || finalworld[playerXPos][playerYPos - 1] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'i' || c == 'I') {
            if (finalworld[pXpos][pYpos + 1] == Tileset.SAND
                    || finalworld[pXpos][pYpos + 1] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'j' || c == 'J') {
            if (finalworld[pXpos - 1][pYpos] == Tileset.SAND
                    || finalworld[pXpos - 1][pYpos] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'l' || c == 'L') {
            if (finalworld[pXpos + 1][pYpos] == Tileset.SAND
                    || finalworld[pXpos + 1][pYpos] == Tileset.TREE) {
                allowed = true;
            }
        }
        if (c == 'k' || c == 'K') {
            if (finalworld[pXpos][pYpos - 1] == Tileset.SAND
                    || finalworld[pXpos][pYpos - 1] == Tileset.TREE) {
                allowed = true;
            }
        }
        //System.out.println("Move made: " + allowed);

        return allowed;
    }

    public void changePlayerPosition(char c) {
        int move = 0;
        if (c == 'w' || c == 'W') {
            finalworld[playerXPos][playerYPos] = Tileset.SAND;
            playerYPos += 1;

        }
        if (c == 'a' || c == 'A') {
            finalworld[playerXPos][playerYPos] = Tileset.SAND;
            playerXPos -= 1;

        }
        if (c == 'd' || c == 'D') {
            finalworld[playerXPos][playerYPos] = Tileset.SAND;
            playerXPos += 1;
        }
        if (c == 's' || c == 'S') {
            finalworld[playerXPos][playerYPos] = Tileset.SAND;
            playerYPos -= 1;
        }
        if (c == 'i' || c == 'I') {
            finalworld[pXpos][pYpos] = Tileset.SAND;
            pYpos += 1;
            move = 1;
        }
        if (c == 'j' || c == 'J') {
            finalworld[pXpos][pYpos] = Tileset.SAND;
            pXpos -= 1;
            move = 1;

        }
        if (c == 'l' || c == 'L') {
            finalworld[pXpos][pYpos] = Tileset.SAND;
            pXpos += 1;
            move = 1;

        }
        if (c == 'k' || c == 'K') {
            finalworld[pXpos][pYpos] = Tileset.SAND;
            pYpos -= 1;
            move = 1;

        }

        if (move == 0) {
            if (finalworld[playerXPos][playerYPos] == Tileset.SAND) {
                finalworld[playerXPos][playerYPos] = Tileset.FLOWER;
            } else {
                collected1 += 1;
                finalworld[playerXPos][playerYPos] = Tileset.FLOWER;
            }
        } else {
            if (finalworld[pXpos][pYpos] == Tileset.SAND) {
                finalworld[pXpos][pYpos] = Tileset.FLOWER;
            } else {
                collected2 += 1;
                finalworld[pXpos][pYpos] = Tileset.FLOWER;
            }
        }
    }

    public String typeOfTile(double x, double y) {
        String type = "";
        int xP = (int) Math.round(x);
        int yP = (int) Math.round(y);
        //System.out.print("xP: "  + xP + " yp:" + yP);
        if (finalworld[xP][yP] == Tileset.TREE) {
            type = "Tree.";
        } else if (xP==playerXPos && yP==playerYPos){
            type = "Player One";
        } else if (xP==pXpos && yP==pYpos){
            type = "Player Two";
        } else if (finalworld[xP][yP] == Tileset.SAND) {
            type = "Sand.";
        } else if (finalworld[xP][yP] == Tileset.WALL) {
            type = "Wall.";
        } else {
            type = "Water.";
        }
        return type;
    }

    public boolean check() {
        return (collected1 == 5 || collected2 == 5);
    }

    public int returnWinner() {
        if (collected2 > collected1) {
            return 2;
        } else {
            return 1;
        }
    }

    public TETile[][] getFinalworld() {
        return finalworld;
    }
}
