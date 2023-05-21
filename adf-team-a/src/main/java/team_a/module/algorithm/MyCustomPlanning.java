package team_a.module.algorithm;

import adf.core.agent.communication.MessageManager;
import adf.core.agent.develop.DevelopData;
import adf.core.agent.info.AgentInfo;
import adf.core.agent.info.ScenarioInfo;
import adf.core.agent.info.WorldInfo;
import adf.core.agent.module.ModuleManager;
import adf.core.agent.precompute.PrecomputeData;
import adf.core.component.module.algorithm.PathPlanning;
import rescuecore2.misc.collections.LazyMap;
import rescuecore2.standard.entities.*;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;

import java.util.*;
import java.util.stream.Collectors;

public class MyCustomPlanning extends PathPlanning {

  private Map<EntityID, Set<EntityID>> graph;

  //La Casilla actual
  private EntityID from;
  //Lista de casillas objetivo
  private Collection<EntityID> targets;

  private List<EntityID> result;

  public MyCustomPlanning(AgentInfo ai, WorldInfo wi, ScenarioInfo si, ModuleManager moduleManager, DevelopData developData) {
    super(ai, wi, si, moduleManager, developData);
    this.init();

  }


  private void init() {
    Map<EntityID,
        Set<EntityID>> neighbours = new LazyMap<EntityID, Set<EntityID>>() {

          @Override
          public Set<EntityID> createValue() {
            return new HashSet<>();
          }
        };
    for (Entity next : this.worldInfo) {
      if (next instanceof Area) {
        Collection<EntityID> areaNeighbours = ((Area) next).getNeighbours();
        neighbours.get(next.getID()).addAll(areaNeighbours);
      }
    }
    this.graph = neighbours;
  }


  @Override
  public List<EntityID> getResult() {
    return this.result;
  }


  @Override
  public PathPlanning setFrom(EntityID id) {
    this.from = id;
    return this;
  }


  @Override
  public PathPlanning setDestination(Collection<EntityID> targets) {
    this.targets = targets;
    return this;
  }


  @Override
  public PathPlanning updateInfo(MessageManager messageManager) {
    super.updateInfo(messageManager);
    return this;
  }


  @Override
  public PathPlanning precompute(PrecomputeData precomputeData) {
    super.precompute(precomputeData);
    return this;
  }


  @Override
  public PathPlanning resume(PrecomputeData precomputeData) {
    super.resume(precomputeData);
    return this;
  }


  @Override
  public PathPlanning preparate() {
    super.preparate();
    return this;
  }

  @Override
  public PathPlanning calc(){
    StandardEntity entity = agentInfo.me();
    if(entity instanceof AmbulanceTeam)
      return calcForAmbulanceTeam();
    else if (entity instanceof PoliceForce)
      return calcForPoliceForce();
    else
      return this;
  }

  /**
   * Calcula el camino a seguir comprobando que haya un agente de tipo policia en una casilla cercana.
   * Si existe dicha asilla prioriza su eleccion.
   * Si no hubuiese nadie cerca escoje un camino al azar.
   * @return
   */
  private PathPlanning calcForPoliceForce(){
    StandardEntity entity = agentInfo.me();
    PoliceForce policeForce = ((PoliceForce) entity);
    List<EntityID> path = new ArrayList<>();
    List<StandardEntity> policeForces = worldInfo.getEntitiesOfType(StandardEntityURN.POLICE_FORCE).stream()
            .filter(k -> !k.getID().equals(agentInfo.getID()))
            .filter(k -> ((PoliceForce)k).getTeam().equals(policeForce.isTeamDefined() ? policeForce.getTeam() : ""))
            .collect(Collectors.toList());

    path.add(from);


    while (!isGoal(path.get(path.size() - 1), targets)){
      Set<EntityID> neighbours = graph.get(path.get(path.size() - 1));
      Boolean isPoliceClose = false;
      for(StandardEntity police : policeForces){
        EntityID position = ((PoliceForce)police).getPosition();
        if(neighbours.contains(position) && !path.contains(position)){
          path.add(position);
          isPoliceClose = true;
          break;
        }
      }

      if(!isPoliceClose){
        path.add(getRandom(neighbours));
      }

    }

    this.result = path;
    return this;
  }

  /**
   * Random path calculation
   * @return
   */
  private PathPlanning calcForAmbulanceTeam(){
    List<EntityID> path = new ArrayList<>();
    path.add(from);
    while (!isGoal(path.get(path.size() - 1), targets)){
      Collection<EntityID> neighbours = graph.get(path.get(path.size() - 1));
      EntityID next = getRandom(neighbours);
      path.add(next);
    }
    this.result = path;
    return this;
  }

  public static <T> T getRandom(Collection<T> coll) {
    int num = (int) (Math.random() * coll.size());
    for(T t: coll) if (--num < 0) return t;
    throw new AssertionError();
  }

  private boolean isGoal(EntityID e, Collection<EntityID> test) {
    return test.contains(e);
  }
}