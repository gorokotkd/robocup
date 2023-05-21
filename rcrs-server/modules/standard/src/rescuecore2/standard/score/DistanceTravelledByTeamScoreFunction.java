package rescuecore2.standard.score;

import rescuecore2.Timestep;
import rescuecore2.score.AbstractScoreFunction;
import rescuecore2.standard.entities.*;
import rescuecore2.standard.misc.AgentPath;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.WorldModel;

/**
   A score function that measures the distance travelled by all agents.
*/
public class DistanceTravelledByTeamScoreFunction extends AbstractScoreFunction {

    private String team;

    /**
       Construct a DistanceTravelledScoreFunction.
    */
    public DistanceTravelledByTeamScoreFunction(String team) {
        super("Distance travelled by Team: " + team);
        this.team = team;
    }

    @Override
    public double score(WorldModel<? extends Entity> world, Timestep timestep) {
        StandardWorldModel model = StandardWorldModel.createStandardWorldModel(world);
        // Find out how far each agent moved
        double sum = 0;
        for (Entity next : model) {
            if (next instanceof FireBrigade
                || next instanceof PoliceForce
                || next instanceof AmbulanceTeam) {
                if(((Human) next).getTeam().equals(this.team)){
                    Human h = (Human)next;
                    AgentPath path = AgentPath.computePath(h, model);
                    if (path != null) {

                        sum += Math.round(path.getLength() / 1000);
                    }
                }
            }
        }
        //System.out.println("Total distance travelled: " + sum);
        return sum;
    }


}
