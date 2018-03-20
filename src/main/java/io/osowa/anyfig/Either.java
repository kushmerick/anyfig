package io.osowa.anyfig;

public class Either<Left,Right> {

    public final Possible<Left> left;
    public final Possible<Right> right;

    public Either(Possible<Left> left, Possible<Right> right) {
        if (left.present() == right.present()) {
            throw new IllegalArgumentException("Exactly one argument must be present");
        }
        this.left = left;
        this.right = right;
    }

    public static <Left,Right> Either<Left,Right> or(Possible<Left> left, Possible<Right> right) {
        return new Either<Left,Right>(left, right);
    }

}
