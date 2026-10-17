package env;

import model.Action;
import model.Direction;

import java.util.Random;

public class Environment {
    public static final int EMPTY = 0;
    public static final int OBSTACLE = 1;

    private final int n;
    private final int[][] grid;
    private final boolean[][] objects;
    private int remainingObjects;

    private int agentR = 0;
    private int agentC = 0;
    private Direction agentDir = Direction.EAST;
    private int score = 0;

    public Environment(int n, int obstacleCount, int objectCount) {
        this.n = n;
        this.grid = new int[n][n];
        this.objects = new boolean[n][n];
        generateGrid(obstacleCount, objectCount);
    }

    private void generateGrid(int obstacleCount, int objectCount) {
        Random rand = new Random();

        int placedObs = 0;
        int attempts = 0;
        while (placedObs < obstacleCount && attempts < 500) {
            attempts++;
            int r = rand.nextInt(n);
            int c = rand.nextInt(n);

            if (r == 0 && c == 0) continue;

            if (isValidObstaclePlacement(r, c)) {
                grid[r][c] = OBSTACLE;
                placedObs++;
            }
        }

        int placedObj = 0;
        while (placedObj < objectCount) {
            int r = rand.nextInt(n);
            int c = rand.nextInt(n);

            if (grid[r][c] == EMPTY && !objects[r][c] && !(r == 0 && c == 0)) {
                objects[r][c] = true;
                placedObj++;
            }
        }
        this.remainingObjects = placedObj;
    }

    private boolean isValidObstaclePlacement(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr;
                int nc = c + dc;
                if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                    if (grid[nr][nc] == OBSTACLE) return false;
                }
            }
        }
        return true;
    }

    private boolean isBlocked(int r, int c) {
        return r < 0 || r >= n || c < 0 || c >= n || grid[r][c] == OBSTACLE;
    }

    public AgentView getViewState() {
        int frontR = agentR + agentDir.dr;
        int frontC = agentC + agentDir.dc;

        Direction rightDir = agentDir.turnRight();
        Direction leftDir = agentDir.turnLeft();

        return new AgentView(
                agentR, agentC,
                frontR, frontC,
                objects[agentR][agentC],
                isBlocked(frontR, frontC),
                isBlocked(agentR + rightDir.dr, agentC + rightDir.dc),
                isBlocked(agentR + leftDir.dr, agentC + leftDir.dc),
                remainingObjects == 0
        );
    }

    public void step(Action action) {
        score -= 1;
        
        switch (action) {
            case FORWARD:
                int nextR = agentR + agentDir.dr;
                int nextC = agentC + agentDir.dc;
                if (!isBlocked(nextR, nextC)) {
                    agentR = nextR;
                    agentC = nextC;
                }
                break;
            case TURN_LEFT:
                agentDir = agentDir.turnLeft();
                break;
            case TURN_RIGHT:
                agentDir = agentDir.turnRight();
                break;
            case PICK_OBJECT:
                if (objects[agentR][agentC]) {
                    objects[agentR][agentC] = false;
                    remainingObjects--;
                    score += 100;
                }
                break;
        }
    }

    public boolean isGoalReached() {
        return remainingObjects == 0 && agentR == 0 && agentC == 0;
    }

    public int getScore() { return score; }

    public void printState() {
        System.out.println("\n--- Grid State ---");
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (r == agentR && c == agentC) {
                    String dirIcon = switch (agentDir) {
                        case NORTH -> " ^ ";
                        case EAST  -> " > ";
                        case SOUTH -> " v ";
                        case WEST  -> " < ";
                    };
                    System.out.print(dirIcon);
                } else if (grid[r][c] == OBSTACLE) {
                    System.out.print(" O ");
                } else if (objects[r][c]) {
                    System.out.print(" X ");
                } else {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
    }
}
