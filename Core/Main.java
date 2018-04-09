package byog.Core;

/**
 * This is the main entry point for the program. This class simply parses
 * the command line inputs, and lets the byog.Core.Game class take over
 * in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        //TERenderer ter = new TERenderer();
        //ter.initialize();
        //Game game = new Game();
        //game.playWithKeyboard();
        //TETile[][] world = game.playWithInputString("ld");
        //TETile[][] world = game.playWithInputString("LWq");
        if (args.length > 1) {
            System.exit(0);
        } else if (args.length == 1) {
            Game game = new Game();
            game.playWithInputString(args[0]);
            System.out.println(game.toString());
        } else {
            Game game = new Game();
            game.playWithKeyboard();
        }
    }
}
