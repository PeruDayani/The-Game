package byog.Core;

import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Random;

public class Game implements java.io.Serializable {
    private static final int W = 80;
    private static final int H = 40;
    private static final int MAXFIGURES = 50;

    public static char[] deserializeArray() {
        char[] w1 = null;
        try {
            InputStream file = new FileInputStream("world.txt");
            ObjectInputStream input = new ObjectInputStream(file);
            w1 = (char[]) input.readObject();
            return w1;
        } catch (IOException | ClassNotFoundException e) {
            System.exit(0);
            return w1;
        }
    }

    //private static final String path =
    public void serializeArray(char[] w) {
        try {
            FileOutputStream fileOut = new FileOutputStream("world.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(w);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            System.out.print("IOException");
        }
    }

    private long getseed(char[] steps) {
        boolean found = false;
        long seed = 0;
        int i = 1;
        while (!found) {
            if (steps[i] != 's' && steps[i] != 'S') {
                seed = seed * 10 + Character.getNumericValue(steps[i]);
            } else {
                found = true;
            }
            i += 1;
        }
        return seed;
    }
    private int getsize(char[] steps) {
        int i = 0;
        for (; i < steps.length; i += 1) {
            if (steps[i] == 'q' || steps[i] == 'Q' ) {
                if(steps[i-1] == ':') {
                    return i-1;
                }
            }
        }
        return i;
    }

    public TETile[][] playWithInputString(String input) {
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        TETile[][] world2 = null;
        try {
            char[] steps = input.toCharArray();
            if (steps[0] == 'n' || steps[0] == 'N') {
                long seed = getseed(steps);
                int size = getsize(steps);
                long seedTemp = seed;
                int noOfDigits = 0;
                while (seedTemp > 0) {
                    seedTemp /= 10;
                    noOfDigits += 1;
                }
                char[] moves = new char[size - 2 - noOfDigits];
                System.arraycopy(steps, noOfDigits + 2, moves, 0, size - noOfDigits - 2);
                //for (int k = 0; k < moves.length; k += 1) {
                //    System.out.print(moves[k] + ":");
                //}
                char[] arrayToSerialize = new char[size];
                System.arraycopy(steps, 0, arrayToSerialize, 0, size);

                serializeArray(arrayToSerialize);
                Random r = new Random(seed);
                World world = generateRandomWorld(r);
                for (int j = 0; j < moves.length; j += 1) {
                    if (world.checkPlayerMove(moves[j])) {
                        world.changePlayerPosition(moves[j]);
                    }
                }
                return world.getFinalworld();
            } else {
                char[] oldmoves = deserializeArray();
                int size = 0;
                int k = 1;
                while (k < steps.length && steps[k] != 'q' && steps[k] != 'Q' && steps[k] != ':') {
                    //System.out.print(steps[k] + ":");
                    size += 1;
                    k += 1;
                }
                char[] moves = new char[oldmoves.length + size];
                System.arraycopy(oldmoves, 0, moves, 0, oldmoves.length);
                System.arraycopy(steps, 1, moves, oldmoves.length, size);
                String b = new String(moves);
                return playWithInputString(b);
            }
        } catch (NullPointerException e) {
            return world2;
        }
    }

    public void playWithKeyboard() {
        try {
            initializeStdDraw();
            char choice = drawMenu();
            if (choice == 'n' || choice == 'N') {
                newgame();
            }
            if (choice == 'l' || choice == 'L') {
                loadgame();
            }
            if (choice == 'Q' || choice == 'q') {
                System.exit(0);
            }

        } catch (NullPointerException e) {
            System.out.print("NullPointerException");
        }
        //serialize(finalworld);
        //System.out.println("Seed:"+ seed);
    }

    private void loadgame() {
        try {
            char[] steps = deserializeArray();
            //System.out.println("THE PREVIOUS KEYS: ");
            //for(int k=0;k<steps.length;k+=1){
            //    System.out.print(steps[k]+ ":");
            //}

            long seed = getseed(steps);
            int size = getsize(steps);

            //System.out.println("seed: " + seed);

            long seedTemp = seed;
            int noOfDigits = 0;
            while (seedTemp > 0) {
                seedTemp /= 10;
                noOfDigits += 1;
            }
            char[] moves = new char[size - 2 - noOfDigits];
            System.arraycopy(steps, noOfDigits + 2, moves, 0, size - noOfDigits - 2);


            char[] arrayToSerialize = new char[size];
            System.arraycopy(steps, 0, arrayToSerialize, 0, size);
            serializeArray(arrayToSerialize);

            //System.out.println("THE NEW KEYS: ");
            //for(int j=0;j<arrayToSerialize.length;j+=1) {
            //    System.out.print(arrayToSerialize[j]+":");
            //}
            //System.out.println();

            Random r = new Random(seed);
            World world = generateRandomWorld(r);
            for (int j = 0; j < moves.length; j += 1) {
                if (world.checkPlayerMove(moves[j])) {
                    world.changePlayerPosition(moves[j]);
                }
            }
            world.initializeRender();
            //world.renderWorld();
            //world.getRandom();

            //System.out.println();
            //System.out.println("PLAYING LOADED GAME");
            //System.out.println();

            char[] newmoves = playgame(world);

            char[] finalmoves = new char[steps.length + newmoves.length -1 ];
            int count =0;

            for(int j=0;j<steps.length;j+=1,count+=1){
                finalmoves[count]=steps[j];
            }

            for(int i=0;i<newmoves.length-1;i+=1,count+=1){
                finalmoves[count]=newmoves[i];
            }


            //System.out.println("Final keys");
            //for(int k=0;k<finalmoves.length;k+=1){
            //    System.out.print(finalmoves[k]);
            //}

            serializeArray(finalmoves);
            System.exit(0);

            //System.out.println("finished playing loaded game");
        } catch (NullPointerException e) {
            System.out.print("NullException");
        }
    }

    private void newgame() {
        try {
            long seed = seedMenu();
            //System.out.println("SEED: " + seed);
            Random r = new Random(seed);

            World world1 = generateRandomWorld(r);
            world1.initializeRender();
            char[] moves = playgame(world1);

            char[] numbers = ("" + seed).toCharArray();
            char[] finalmoves = new char[numbers.length + moves.length + 1];
            int count =1;
            finalmoves[0]='n';

            for(int j=0;j<numbers.length;j+=1,count+=1){
                finalmoves[count]=numbers[j];
            }

            finalmoves[count]='s';
            count+=1;

            for(int i=0;i<moves.length-1;i+=1,count+=1){
                finalmoves[count]=moves[i];
            }
            //System.out.println("Final keys");
            //for(int k=0;k<finalmoves.length;k+=1){
            //    System.out.print(finalmoves[k]);
            //}
            //System.out.println();

            serializeArray(finalmoves);
            System.exit(0);

        } catch (NullPointerException e) {
            System.out.print("NullPointerException");
        }
    }

    private char[] playgame(World world) {
        try {
            boolean quit = false;
            char move;
            char[] moves = new char[100000];
            int count =0;
            world.renderWorld();
            //System.out.print("Drawing top bar");
            drawTopBar(world);
            //System.out.print("Drawn top bar");
            while (!quit) {
                while (!StdDraw.hasNextKeyTyped()) {
                    StdDraw.pause(100);
                    //if(StdDraw.isMousePressed()){
                    //    drawTopBar(world);
                    //}
                    world.renderWorld();
                    drawTopBar(world);
                }
                move = StdDraw.nextKeyTyped();
                moves[count]=move;
                count+=1;

                if (move == 'q'|| move == 'Q') {
                    if(':' == moves[count - 2]){
                        quit = true;
                        break;
                    }
                }

                if (world.checkPlayerMove(move)) {
                    //System.out.println("Move is Valid");
                    world.changePlayerPosition(move);
                    //System.out.println("Change made");
                }

                world.renderWorld();
                //System.out.println("Rendered World");
                drawTopBar(world);

                if (world.check()) {
                    quit = true;
                    displayvictory(world.returnWinner());
                }
            }
            char[] finalmoves = new char[count];
            System.arraycopy(moves, 0, finalmoves, 0 , count);
            return finalmoves;
        } catch (NullPointerException e) {
            System.out.print("NullPointerException");
            return new char[1];
        }
    }

    private void displayvictory(int number) {
        Font font = new Font("Monaco", Font.BOLD, 40);
        Font font1 = new Font("Monaco", Font.BOLD, 55);
        StdDraw.clear();
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(W / 2, (3 * H) / 4, "CS61B: THE GAME");
        StdDraw.setFont(font1);
        StdDraw.text(W / 2, (H) / 2, "PLAYER " + number + " YOU WON!");
        StdDraw.show();
        StdDraw.pause(500);
    }

    private void initializeStdDraw() {
        StdDraw.setCanvasSize(W * 16, H * 16);
        StdDraw.setXscale(0, W);
        StdDraw.setYscale(0, H);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(StdDraw.WHITE);
    }

    private char drawMenu() {
        boolean inputCorrect = false;
        char choice = '!';
        Font font = new Font("Monaco", Font.BOLD, 30);
        Font font1 = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(font);
        StdDraw.text(W / 2, (3 * H) / 4, "CS61B: THE GAME");
        StdDraw.setFont(font1);
        StdDraw.text(W / 2, (H) / 3, "New Game (N)");
        StdDraw.text(W / 2, ((H) / 3) - 2, "Load Game (L)");
        StdDraw.text(W / 2, ((H) / 3) - 4, "Quit (Q)");
        StdDraw.show();
        StdDraw.pause(1000);
        while (!inputCorrect) {
            while (!StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(500);
            }
            choice = StdDraw.nextKeyTyped();
            if (choice == 'n' || choice == 'N' || choice == 'l'
                    || choice == 'L' || choice == 'q' || choice == 'Q') {
                inputCorrect = true;
            }
        }
        return choice;
    }

    private long seedMenu() {
        StdDraw.clear(StdDraw.BLACK);
        boolean input = false;
        boolean seedComplete = false;

        long seed = 0;
        char character = '!';
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(W / 2, (3 * H) / 4, "Please enter Seed");
        StdDraw.show();
        int i = 1;
        while (!seedComplete) {
            while (!StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(100);
            }
            character = StdDraw.nextKeyTyped();
            if (character == 's' || character == 'S') {
                seedComplete = true;
                continue;
            }
            displayTypedLetter(character, i);
            i += 1;
            int value = Character.getNumericValue(character);
            seed = seed * 10 + value;
        }
        return seed;
    }

    private void displayTypedLetter(char c, int i) {
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(W / 2 + i, ((3 * H) / 4) - 2, Character.toString(c));
        StdDraw.show();
    }
    private void drawTopBar(World world) {
        StdDraw.setPenColor(StdDraw.WHITE);
        double mouseXPos = StdDraw.mouseX();
        double mouseYPos = StdDraw.mouseY();
        String tile;
        try {
            tile = world.typeOfTile(mouseXPos, mouseYPos);
            //System.out.println(tile);
        }catch (IndexOutOfBoundsException e) {
            tile = "Mouse out of bounds";
            //System.out.println("out of bounds");
        }
        StdDraw.text(20,H + 4,tile);
        StdDraw.text(W / 2, H + 4, world.getposition());
        StdDraw.line(0, H + 3, W + 6, H + 3);
        StdDraw.show();
        //world.renderWorld();
    }
    /**
     * Method used for autograding and testing the game code. The input string
     * will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww".
     * The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should
     * be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing
     * with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l",
     * we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    /**
     **/

    public World generateRandomWorld(Random r) {
        World world = new World(r, W, H);
        world.createRoomsAndConnect(MAXFIGURES);
        world.makeFinalWorld();
        world.initializePlayer();
        world.initializeDiamonds();
        //world.renderWorld();
        return world;
    }
}
