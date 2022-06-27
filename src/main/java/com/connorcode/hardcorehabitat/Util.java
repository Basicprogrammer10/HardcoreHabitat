package com.connorcode.hardcorehabitat;

public class Util {
    public static String genLiveCountText(int lives) {
        return String.format("%sYou have %s %s left", colorForLives(lives), lives == 0 ? "no" : lives, lives == 1 ?
                "life" : "lives");
    }

    public static String colorForLives(int lives) {
        String color = "d";
        if (inRange(lives, 7, 5)) color = "a";
        if (inRange(lives, 4, 3)) color = "e";
        if (inRange(lives, 2, 0)) color = "c";

        return "§" + color;
    }

    public static boolean inRange(int x, int a, int b) {
        return x >= Math.min(a, b) && x <= Math.max(a, b);
    }
}
