package env;

public class AgentView {
    public final int r, c;
    public final int frontR, frontC;
    public final boolean hasObject;
    public final boolean isObstacleFront;
    public final boolean isObstacleRight;
    public final boolean isObstacleLeft;
    public final boolean allObjectsCollected;

    public AgentView(int r, int c, int frontR, int frontC, boolean hasObject, boolean isObstacleFront, boolean isObstacleRight, boolean isObstacleLeft, boolean allObjectsCollected) {
        this.r = r;
        this.c = c;
        this.frontR = frontR;
        this.frontC = frontC;
        this.hasObject = hasObject;
        this.isObstacleFront = isObstacleFront;
        this.isObstacleRight = isObstacleRight;
        this.isObstacleLeft = isObstacleLeft;
        this.allObjectsCollected = allObjectsCollected;
    }
}
