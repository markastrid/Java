package controllers.Astar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
public class Agent extends controllers.sampleRandom.Agent{
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so,elapsedTimer);//调用父类并且进行初始化
        return;
    }
    ArrayList<StateObservation> REState = new ArrayList<StateObservation>();  //存储所有的走过的状态
    ArrayList<StateObservation> Now_State = new ArrayList<StateObservation>();                //存储当前节点的走过的状态
    Comparator<Node> OrderDistance = Comparator.comparingDouble(o -> o.scores);
    PriorityQueue<Node> openNodeState = new PriorityQueue<Node>(OrderDistance);             //定义一个按局面评分比较的优先队列，用来存储还未展开的节点
    ArrayList<Types.ACTIONS> REaction = new ArrayList<Types.ACTIONS>();   //存储走过的动作
    Vector2d goalpos;      //目标的位置
    Vector2d keypos;   //钥匙的位置
    int now_index = -1;          //act函数中输出动作的数组下标
    double goaltokey;       //钥匙与目标之间的曼哈顿距离
    boolean Key = false;                 //是否找到钥匙
    int limitDepth = 32;    //限制A*算法的搜索深度,防止超限

    double distance(StateObservation stateObs, boolean hasKey){   //启发式函数还是上次那个经过一些改动
        Vector2d poaition_p = stateObs.getAvatarPosition();     //精灵的位置
        if(hasKey){
            return Math.abs(goalpos.x - poaition_p.x) + Math.abs(goalpos.y - poaition_p.y) + (REaction.size()*50 );
            //拿到钥匙就返回其和出口的距离加上消耗函数
        }
        return Math.abs(poaition_p.x - keypos.x) + Math.abs(poaition_p.y - keypos.y) + goaltokey + (REaction.size()*50);
        //没拿到就要加上钥匙和目标的距离，再加上消耗（这也是A*的不同之处）
    }

    Node isEqual(StateObservation state){   //如果当前在节点中就展开，否则就返回空
        for(Node node : openNodeState){
            if(state.equalPosition(node.stateObs)){
                return node;
            }
        }
        return null;
    }
    boolean Have_gone(StateObservation state){          //判断当前状态之前是否到达过
        for(StateObservation so : REState){
            if(state.equalPosition(so)){
                return true;//来过就返回true
            }
        }
        return false;//否则返回false
    }
    void Astar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        openNodeState = new PriorityQueue<Node>(OrderDistance);    //初始化待展开节点
        REState = (ArrayList<StateObservation>) Now_State.clone();   //初始化所有已走过状态为上轮搜索后实际走过的状态
        REaction = new ArrayList<Types.ACTIONS>();    //初始化已走过的动作
        Node NewNode = new Node(stateObs,distance(stateObs, Key), REaction, Now_State, Key);  //新建初始节点并加入待展开节点的序列中去
        openNodeState.add(NewNode);//加入这个新节点
        while(!openNodeState.isEmpty()) {   //还有待展开节点
            Node bestnode = openNodeState.poll();    //评分最优的节点
            REaction = (ArrayList<Types.ACTIONS>) bestnode.REaction.clone();   //将Actions初始化为temp节点储存的已走过动作集
            Now_State = (ArrayList<StateObservation>) bestnode.REstate.clone();   //将targetPastState初始化为temp节点储存的已走过状态
            REState.add(bestnode.stateObs); //将temp节点的状态加入所有已走过的状态
            Now_State.add(bestnode.stateObs);    //将temp节点的状态加入当前节点的走过的状态
            if(REaction.size() >= limitDepth){     //如果达到搜索深度，则返回，按该最优节点存储的Actions执行动作
                return;
            }
            Key = bestnode.Key; //初始化hasKey为当前节点的hasKey
            if(!Key){ //如果没有钥匙，则判断精灵位置是否与钥匙位置相同，如果是则有钥匙了
                if(bestnode.stateObs.getAvatarPosition().equals(keypos)){
                    Key = true;
                }
            }
            for(Types.ACTIONS action : bestnode.stateObs.getAvailableActions()){
                StateObservation stCopy = bestnode.stateObs.copy();
                stCopy.advance(action);
                REaction.add(action);
                if(stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                    return;
                }
                if(stCopy.isGameOver() ) {
                    REaction.remove(REaction.size() - 1);
                    continue;
                }
                if( Have_gone(stCopy)){
                    REaction.remove(REaction.size() - 1);
                    continue;
                }
                Node equalNode = isEqual(stCopy);
                    if(equalNode != null){
                        if(distance(stCopy, Key) < equalNode.scores){
                        openNodeState.remove(equalNode);
                        openNodeState.add(new Node(stCopy,distance(stCopy, Key), REaction, Now_State, Key));
                    }
                    REaction.remove(REaction.size() - 1);
                }
                else{
                    openNodeState.add(new Node(stCopy,distance(stCopy, Key), REaction, Now_State, Key));
                    REaction.remove(REaction.size() - 1);
                }
            }
        }
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        //游戏程序会不断调用控制器的act函数，stateObs对象表示当前游戏局面，elapsedTimer是计算一次动作可用的时间
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions();
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        ArrayList<Observation>[] resourcesPositions = stateObs.getResourcesPositions();
        ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions();
        grid = stateObs.getObservationGrid();
        //从statesObs中读取当前局面上的物体
        if(REaction.size() == 0){
            keypos = stateObs.getMovablePositions()[0].get(0).position;
            goalpos = stateObs.getImmovablePositions()[1].get(0).position;
            goaltokey = Math.abs(goalpos.x - keypos.x) + Math.abs(goalpos.y - keypos.y);
        }
        now_index++;
        if(now_index == REaction.size()){
            Astar(stateObs,elapsedTimer);
            now_index = 0;
        }
        return REaction.get(now_index);     //返回动作
    }}