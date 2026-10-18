import env.AgentView;
import env.Environment;
import model.Action;
import model.Direction;

import java.util.*;

public class CognitiveAgentSimulation {

    public static class CognitiveAgent {
        public record Point(int r, int c) {}

        private static final int VISITED = 2;

        private final int n;
        private final int[][] memoryGrid;
        private Direction currentDir = Direction.EAST;
        private int visitedCount = 0;

        public CognitiveAgent(int n) {
            this.n = n;
            this.memoryGrid = new int[n][n];
        }

        public int getMemorySize() {
            return visitedCount;
        }

        public Action decide(AgentView view) {
            if (view.hasObject) {
                return Action.PICK_OBJECT;
            }

            if (memoryGrid[view.r][view.c] != VISITED) {
                memoryGrid[view.r][view.c] = VISITED;
                visitedCount++;
            }

            Direction left = currentDir.turnLeft();
            Direction right = currentDir.turnRight();

            recordObstacle(view.frontR, view.frontC, view.isObstacleFront);
            recordObstacle(view.r + left.dr, view.c + left.dc, view.isObstacleLeft);
            recordObstacle(view.r + right.dr, view.c + right.dc, view.isObstacleRight);

            Direction targetDir = getNextStep(view.r, view.c, view.allObjectsCollected);

            if (currentDir == targetDir) {
                return Action.FORWARD;
            }

            currentDir = (left == targetDir) ? left : right;
            return (currentDir == left) ? Action.TURN_LEFT : Action.TURN_RIGHT;
        }

        private Direction getNextStep(int startR, int startC, boolean returnHome) {
            Queue<Point> queue = new ArrayDeque<>();
            Direction[][] firstDir = new Direction[n][n];

            Direction[] priorityDirs = {
                    currentDir,
                    currentDir.turnLeft(),
                    currentDir.turnRight(),
                    currentDir.turnLeft().turnLeft()
            };

            for (Direction d : priorityDirs) {
                int nr = startR + d.dr, nc = startC + d.dc;
                if (isValid(nr, nc) && memoryGrid[nr][nc] != Environment.OBSTACLE) {
                    firstDir[nr][nc] = d;
                    queue.add(new Point(nr, nc));
                }
            }

            while (!queue.isEmpty()) {
                Point curr = queue.poll();
                int r = curr.r(), c = curr.c();

                boolean isGoal = returnHome ? (r == 0 && c == 0) : (memoryGrid[r][c] == Environment.EMPTY);
                if (isGoal) {
                    return firstDir[r][c];
                }

                if (memoryGrid[r][c] == VISITED) {
                    for (Direction d : Direction.values()) {
                        int nr = r + d.dr, nc = c + d.dc;
                        if (isValid(nr, nc) && !(nr == startR && nc == startC)
                                && firstDir[nr][nc] == null && memoryGrid[nr][nc] != Environment.OBSTACLE) {
                            firstDir[nr][nc] = firstDir[r][c];
                            queue.add(new Point(nr, nc));
                        }
                    }
                }
            }
            return currentDir;
        }

        private void recordObstacle(int r, int c, boolean isBlocked) {
            if (isBlocked && isValid(r, c)) {
                memoryGrid[r][c] = Environment.OBSTACLE;
            }
        }

        private boolean isValid(int r, int c) {
            return r >= 0 && r < n && c >= 0 && c < n;
        }
    }

    public static void main(String[] args){
        int gridSize = 6;
        int obstacles = 3;
        int objects = 4;

        Environment env = new Environment(gridSize, obstacles, objects);
        CognitiveAgent agent = new CognitiveAgent(gridSize);

        int steps = 0;
        env.printState();

        while (!env.isGoalReached() && steps < 1000) {
            AgentView view = env.getViewState();
            Action action = agent.decide(view);

            env.step(action);
            int memoryUsed = agent.getMemorySize();
            env.applyMemoryPenalty(memoryUsed);
            steps++;

            env.printState();
            System.out.printf("Step: %d | Action: %s | Memory (ints): %d | Score: %.1f",
                    steps, action, memoryUsed, env.getScore());


        }

        System.out.printf("\nSimulation finished in %d steps. Final Score: %.1f", steps, env.getScore());
    }
}
