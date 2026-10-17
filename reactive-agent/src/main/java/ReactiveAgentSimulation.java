import java.util.*;

import env.AgentView;
import env.Environment;
import model.Action;


public class ReactiveAgentSimulation {

    public static class ReactiveAgent {

        private final Random rand = new Random();

        public Action decideAction(AgentView p) {
            if (p.hasObject) {
                return Action.PICK_OBJECT;
            }

            if (p.allObjectsCollected) {

                boolean movesCloserToHome = (p.frontR + p.frontC) < (p.r + p.c);

                if (!p.isObstacleFront && movesCloserToHome) {
                    return Action.FORWARD;
                }

                if (!p.isObstacleFront && rand.nextDouble() < 0.30) {
                    return Action.FORWARD;
                }
                return turnAwayFromObstacle(p);
            }

            if (!p.isObstacleFront) {
                return Action.FORWARD;
            }

            return turnAwayFromObstacle(p);
        }

        private Action turnAwayFromObstacle(AgentView p) {
            if (!p.isObstacleLeft && p.isObstacleRight) {
                return Action.TURN_LEFT;
            }
            if (!p.isObstacleRight && p.isObstacleLeft) {
                return Action.TURN_RIGHT;
            }
            return rand.nextBoolean() ? Action.TURN_LEFT : Action.TURN_RIGHT;
        }
    }

    public static void main(String[] args) {
        int gridSize = 6;
        int obstacles = 3;
        int objects = 4;

        Environment env = new Environment(gridSize, obstacles, objects);
        ReactiveAgent agent = new ReactiveAgent();


        int steps = 0;
        int maxSteps = 1000;

        while (steps < maxSteps) {
            AgentView viewState = env.getViewState();
            Action action = agent.decideAction(viewState);
            env.printState();

            if ((viewState.r == 0 && viewState.c == 0) && env.isGoalReached()) {
                System.out.println("\nGoal Reached");
                break;
            }

            env.step(action);
            steps++;
            System.out.println("Score: " + env.getScore());
        }

        env.printState();
        System.out.println("Final Score: " + env.getScore());
        System.out.println("Steps Performed: " + steps);
    }
}