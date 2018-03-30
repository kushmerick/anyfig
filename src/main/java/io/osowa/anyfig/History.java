package io.osowa.anyfig;

import java.util.ArrayList;
import java.util.List;

public class History {

    private final List<Delta> deltas = new ArrayList<>();

    public void record(Delta delta) {
        deltas.add(delta);
    }

    public List<Delta> get() {
        return deltas;
    }

}
