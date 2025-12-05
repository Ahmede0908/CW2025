package com.comp2042.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    @Override
    public Brick getBrick() {
        // Ensure we always have at least 2 pieces in the queue
        while (nextBricks.size() < 2) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        Brick brick = nextBricks.poll();
        // After polling, ensure we still have at least 2 pieces
        while (nextBricks.size() < 2) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return brick;
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Returns a list of the next N bricks without removing them from the queue.
     * The bricks are returned in order (first will be used next).
     * 
     * @param count the number of next bricks to retrieve
     * @return a list of the next N bricks (may be smaller if queue is smaller)
     */
    public List<Brick> getNextBricks(int count) {
        List<Brick> result = new ArrayList<>();
        // Ensure we have enough bricks in the queue
        while (nextBricks.size() < count) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        
        // Peek at the bricks without removing them
        int index = 0;
        for (Brick brick : nextBricks) {
            if (index >= count) break;
            result.add(brick);
            index++;
        }
        
        return result;
    }
}

