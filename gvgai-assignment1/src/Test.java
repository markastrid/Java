import java.lang.annotation.Repeatable;
import java.util.Random;
import core.ArcadeMachine;
import core.competition.CompetitionParameters;
public class Test
{
    public static void main(String[] args)
    {
      ArcadeMachine.playOneGame( "examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", null, new Random().nextInt());
        //String depthfirstController = "controllers.depthfirst.Agent";
        //core.competition.CompetitionParameters.ACTION_TIME = 10000; // set to the time that allow you to do the depth first search
     //   ArcadeMachine.runOneGame("examples/gridphysics/bait.txt", "examples/gridphysics/bait_lvl0.txt", true, depthfirstController, null, new Random().nextInt(), false);
    }
}
