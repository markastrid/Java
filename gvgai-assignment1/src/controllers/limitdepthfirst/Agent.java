package controllers.limitdepthfirst;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import java.util.ArrayList;
import tools.Vector2d;
import tools.ElapsedCpuTimer;
//上面是一些引用
public class Agent extends controllers.sampleRandom.Agent{
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer){
        super(so,elapsedTimer);//调用父类并且进行初始化
        return;
    }
    ArrayList<StateObservation> REState = new ArrayList<StateObservation>();  //状态记忆
    ArrayList<Types.ACTIONS> REAction = new ArrayList<Types.ACTIONS>();  //动作记忆
    ArrayList<Types.ACTIONS> BestAction = new ArrayList<Types.ACTIONS>();      //存储当前的最优动作
    int now_index = 0;   //当前的动作
    boolean action_flag = false;  //作为对路径的标识
    Vector2d key_position;   //钥匙位置
    Vector2d Target;  //目标位置
    protected double distance; //距离
    protected  double SCORE_BEST;    //已走过的路径的最优评分（值越小越好）,因为评分是根据距离来计算的
    protected boolean KEY ;  //拿了没
    protected int Depth_limit=4; //搜索深度,试探了一个可以用的最低深度
    protected int Depth ;   //搜索深度
    //初始化在后面进行这里只
    double distance(StateObservation stateObs){  //这个是计算距离所用
        Vector2d pos = stateObs.getAvatarPosition();          //精灵的位置
        if(KEY){
            return Math.abs(Target.x - pos.x) + Math.abs(Target.y - pos.y);
        }
        else{
            for(StateObservation so : REState){ //否则先搜索路径
                if(so.getAvatarPosition().equals(key_position)){//到过钥匙的位置，说明找到钥匙了，直接返回距离
                    return Math.abs(Target.x - pos.x) + Math.abs(Target.y - pos.y);
                }
            }
            //要是已经拿到过钥匙了，那么其实出口的距离其实就是分数
            return Math.abs(pos.y - key_position.y) + distance+Math.abs(pos.x - key_position.x) ;//否则返回距离之和
        }
    }
    boolean Have_gone(StateObservation state){                  //判断当前状态之前是否到达过,这个函数在这里仍然适用
        for(StateObservation so : REState){
            if(state.equalPosition(so)){
                return true;
            }
        }
        return false;
    }
    void LimitDepthFirst(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        REState.add(stateObs);
        Depth++;   //深度加一
        for(Types.ACTIONS action : stateObs.getAvailableActions()){ //继续进行遍历
            StateObservation stCopy = stateObs.copy(); //创建一个拷贝作为模拟
            stCopy.advance(action);    //施加动作
            REAction.add(action);      //加入已走过的动作
            if(Depth == Depth_limit){     //到达深度
                double scores = distance(stateObs);   //算分
                if(scores <SCORE_BEST){    //评分小于之前的最优，则更新
                    BestAction = (ArrayList<Types.ACTIONS>) REAction.clone();
                    SCORE_BEST = scores;
                }
            }
            else if(stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS) { //还没到受限的搜索深度就已经胜利，计算分数
                double scores = (Depth_limit - Depth);
                ///////////////////////////////////////////////////////////////////////
                if(scores <=SCORE_BEST) {                                     //如果评分小于之前的最优评分，则更新最优解
                    BestAction = (ArrayList<Types.ACTIONS>) REAction.clone();
                    SCORE_BEST = scores;
                }
                REAction.remove(REAction.size() - 1);             //因为执行该动作后已胜利，故当前局面的其他动作已无搜索的必要，可以直接返回
                Depth--;
                REState.remove(REState.size() - 1);
                return;
            }
            else if(Have_gone(stCopy) || stCopy.isGameOver()){         //如果如果动作施加后的状态之前已经到达过或者游戏失败，不进行操作

            }
            else{                                                      //动作施加后的是一个新的状态
                LimitDepthFirst(stCopy,elapsedTimer);        //递归进行深度优先搜索
            }
            REAction.remove(REAction.size() - 1);                 //移除当前施加的动作，尝试另外的动作
        }
        Depth--;                                           //当前状态的搜索结束，返回上一层
        REState.remove(REState.size() - 1);
        return;
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
        if (stateObs.getAvatarPosition().equals(key_position)) {
            //如果当前下精灵在钥匙处，则算拿到了钥匙，状态更新
            KEY = true;
        }
        if (Depth == 0 && !KEY) {
            //初始化目标位置、钥匙位置和钥匙与目标之间的曼哈顿距离
            Target = stateObs.getImmovablePositions()[1].get(0).position;
            key_position = stateObs.getMovablePositions()[0].get(0).position;
            distance = Math.abs(Target.x - key_position.x) + Math.abs(Target.y - key_position.y);
        }
        Depth = 0;        //初始化
        SCORE_BEST = 5000;
        REAction = new ArrayList<Types.ACTIONS>();
        BestAction = new ArrayList<Types.ACTIONS>();
        REState.add(stateObs);   //将当前状态加入已走过的状态中
        LimitDepthFirst(stateObs, elapsedTimer);    //进行深度优先搜索，
        return BestAction.get(0); //执行最佳动作集的第一步
    }}