package controllers.depthfirst;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;

import tools.ElapsedCpuTimer;
//上面是一些引用
public class Agent extends controllers.sampleRandom.Agent{
   public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer){
       super(so,elapsedTimer);//调用父类并且进行初始化
       return;
   }
    ArrayList<StateObservation> REState = new ArrayList<StateObservation>();  //状态记忆
    ArrayList<Types.ACTIONS> REAction = new ArrayList<Types.ACTIONS>();  //动作记忆
    int now_index = 0;   //当前的动作
    boolean action_flag = false;  //作为对路径的标识
    boolean Have_gone(StateObservation state){                  //判断当前状态之前是否到达过
        for(StateObservation so : REState){
            if(state.equalPosition(so)){
                return true;
            }
        }
        return false;
    }
    boolean DepthFirst(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        REState.add(stateObs);//把当前状态放进跑过的状态
        for(Types.ACTIONS action : stateObs.getAvailableActions()){
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);
            REAction.add(action);
            if(stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS){
                return true;
            }
            if(stCopy.isGameOver()){
                REAction.remove(REAction.size() - 1);
            }
            else if(Have_gone(stCopy) ){
                REAction.remove(REAction.size() - 1);
            }
            else{
                if(DepthFirst(stCopy,elapsedTimer)){
                    return true;
                }
                else {
                    REAction.remove(REAction.size() - 1);
                }
            }
        }
        REState.remove(REState.size() - 1); //如果到最后都没有办法了就减去当前局面然后返回
        return false;
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
        /*printDebug(npcPositions,"npc");
        printDebug(fixedPositions,"fix");
        printDebug(movingPositions,"mov");
        printDebug(resourcesPositions,"res");
        printDebug(portalPositions,"por");
        System.out.println();               */
      /*  Types.ACTIONS action = null;
        StateObservation stCopy = stateObs.copy();//局部对象的拷贝：仿真器；用来预见一系列过程的执行结果，不会影响结果
        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        int numIters = 0;
        int remainingLimit = 5;
        //循环到快要超时
            while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();//计时
            ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();//获得当前可用的动作集合
            int index = randomGenerator.nextInt(actions.size());
            action = actions.get(index);//随机选择一个动作
            stCopy.advance(action);
            if(stCopy.isGameOver())
            {//在stCopy中执行该动作
                stCopy = stateObs.copy();//这里面游戏结束就返回
            }
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
            avgTimeTaken  = acumTimeTaken/numIters;
            remaining = elapsedTimer.remainingTimeMillis();
        }*/
        if(!action_flag){
            action_flag = DepthFirst(stateObs, elapsedTimer);
        }//如果没有搜索过就进行搜索
        if(now_index < REAction.size()){
            now_index++;//有动作之后就不断执行到结束
            return REAction.get(now_index - 1);
        }
        return null;//最后返回一个空
    }
 }