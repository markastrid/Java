package controllers.Astar;
import core.game.StateObservation;
import ontology.Types;
import java.util.ArrayList;
public class Node {
    public Node(StateObservation stateObs, double scores, ArrayList<Types.ACTIONS> REaction, ArrayList<StateObservation> REstate, boolean Key) {
        this.stateObs = stateObs.copy();
        this.scores = scores;
        this.REaction = (ArrayList<Types.ACTIONS>) REaction.clone();
        this.REstate = (ArrayList<StateObservation>) REstate.clone();
        this.Key = Key;
    }               //初始化
    StateObservation stateObs;  //当前节点的状态
    double scores;   //当前节点的评分
    ArrayList<Types.ACTIONS> REaction; //当前节点的已走过的路径
    ArrayList<StateObservation> REstate;   //当前节点的已走过的状态
    boolean Key;   //当前节点是否已经拥有钥匙
    public Node parent;
}