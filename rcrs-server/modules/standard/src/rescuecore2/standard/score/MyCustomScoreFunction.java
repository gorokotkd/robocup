package rescuecore2.standard.score;

import rescuecore2.score.CumulativeScoreFunction;
import rescuecore2.score.ScoreFunction;
import rescuecore2.score.UnaryOperatorScoreFunction;
import rescuecore2.score.WeightedScoreFunction;

import java.util.ArrayList;
import java.util.List;

public class MyCustomScoreFunction extends CumulativeScoreFunction {

    List<String> teamNamesList = new ArrayList<>(List.of(
            new String[]{"team-a", "team-b"}
    ));

    public MyCustomScoreFunction() {
        super("Scoring by Teams");

        //WeightedScoreFunction f = new WeightedScoreFunction("Civilians Score");
        CumulativeScoreFunction f = new CumulativeScoreFunction("Civilians Score");
        f.addChildFunction(new CiviliansAliveScoreFunction());
        addChildFunctions(f);

        teamNamesList.stream().forEach(teamName -> {
            CumulativeScoreFunction civs = new CumulativeScoreFunction("Team Score For :" + teamName);
            civs.addChildFunction(new DistanceTravelledByTeamScoreFunction(teamName));
            addChildFunction(civs);
        });
    }
}
