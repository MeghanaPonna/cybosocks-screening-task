package com.cybosocks.runtime.model;

import java.util.Collections;
import java.util.List;

/**
 * Top-level program: an ordered list of blocks.
 */
public final class Program {

    private final List<Block> blocks;

    public Program(List<Block> blocks) {
        this.blocks = Collections.unmodifiableList(blocks);
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
