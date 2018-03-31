package io.osowa.anyfig.utils;

public class Pair<Left,Right> {

    public Left left;
    public Right right;

    public static <Left,Right> Pair of(Left left, Right right) {
        Pair<Left,Right> pair = new Pair<>();
        pair.left = left;
        pair.right = right;
        return pair;
    }

}
