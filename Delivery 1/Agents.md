# Part 2 – Agent Architectures & Properties:


## SystemAgent

The SystemAgent has a complete representation of the environment. It is responsible for randomly adding new garbage in the city. It updates the city based on the performed actions.

#### Architecture

All actions performed by the SystemAgent can be performed without planning. A **reactive** architecture is sufficient. 

#### Properties

1. Flexibility — **No**. No flexible behaviour is required.
2. Reactivity — **Yes**. The agent has to react to recorded actions of other agents and received messages.
3. Proactiveness — **No**. The agent is not proactive.
4. Social Ability — **Minimal**. The agent communicates with other agents. No Coordination/Cooperation or Negotioation is performed.
5. Rationality — **Yes**. The agent only acts in ways that contribute to its goals.
6. Reasoning capabilites — **No**. There are no reasoning capabilities necessary.
7. Learning — **No**.
8. Autonomy — **Yes**. The agent is fully autonomous.
9. Temporal continuity — **Yes**. The agent is continuously active.
10. Mobility — **No**. The agent is always executed on the same machine.



## CoordinatorAgent

The CoordinatorAgent communicates between the SystemAgent and the other coordinators.

#### Architecture

All communication can be handled purely reactively, hence a **reactive** architecture is required.

#### Properties

1. Flexibility — **No**. The agent does not flexibly change its behavior based on environment changes.
2. Reactivity — **Yes**. The agent reacts to received information and passes it forward.
3. Proactiveness — **No**. The agent does not act proactively.
4. Social Ability — **Minimal**. The agent communicates with other agents but does not actually coordinate or cooperate with them in finding decisions.
5. Rationality — **Yes**. It never acts in a way to harm its goal of passing information.
6. Reasoning capabilites — No. There are no reasoning capabilities involved.
7. Learning — **No**.
8. Autonomy — **No**. 
9. Temporal continuity — **Yes**. The agent is continuously active.
10. Mobility — **No**. The agent is always executed on the same machine.



## ScoutCoordinator

The ScoutCoordinator makes an intelligent plan of distributing the available ScoutAgents in order to achieve the highest coverage of the city. It receives the information about newly detected garbage and passes it to the other coordinators. 

#### Architecture

The agent needs both a deliberative component to plan the optimal distribution and a reactive component to process communication between the other agents. Hence, it requires a **hybrid** architecture.

#### Properties

1. Flexibility — **No**. The agent does not have to change its plans based on changes in the environment's states.
2. Reactivity — **Yes**. The agent reacts to new information and passes it to other agents.
3. Proactiveness — **Yes**. The agent proactively plans the optimal way of achieving the highest coverage of the city.
4. Social Ability — **Yes**. The agent is responsible to pass important information to other agents. It coordinates the ScoutAgents.
5. Rationality — **Yes**. The agent always acts rationally.
6. Reasoning capabilites — Yes. Reasoning capabilities are required to plan the optimal routes for the ScoutAgents.
7. Learning — **No**. 
8. Autonomy — **Yes**. The agents acts autonomously.
9. Temporal continuity — **Yes**. The agent is continuously active.
10. Mobility — **No**. The agent is always executed on the same machine.



## HarvesterCoordinator

The HarvesterCoordinator keeps track of all HarvesterAgents. It will assign them with new garbage and recycling centers when they do not have a job. It will try to maximize the benefits (points received for recycling) and minimize the average time for collecting garbage. It will also provide all HarvesterAgents with information about the currently known state of the city that it obtains from the CoordinatorAgent. When HarvesterAgents recognize opportunities for picking up new garbage with small detours to their current planned routes, the HarvesterCoordinator will negotiate if those detours are permitted.

#### Architecture

The agent requires a **hybrid** architecture since there is both a lot of planning involved and reactive communication to other agents.

#### Properties

1. Flexibility — **Yes**. The agent has to flexibly react to HarvesterAgents asking for permissions to pick up additional garbage.
2. Reactivity — **Yes**. The agent reacts to new information about garbage distribution and to requests by the HarvesterAgents.
3. Proactiveness — **Yes**. The agent is proactive since it is looking for ways to maximize the "benefits" (points received for recycling) and minimize the average time for collecting garbage.
4. Social Ability — **Yes**. The agent is communicating with HarvesterAgents and other coordinators. It coordinates and negotiates with the HarvesterAgents.
5. Rationality — **Yes**. The agent will never act in a way that does not contribute to achieving its goals.
6. Reasoning capabilites — **Yes**. In order to optimally distribute the HarvesterAgents, it needs to reason.
7. Learning — **Potentially**. We will explore later, if there are some ways to improve performance by learning.
8. Autonomy — **Yes**. The agent acts autonomously.
9. Temporal continuity — **Yes**. The agent is continuously active.
10. Mobility — **No**. The agent is always executed on the same machine.



## ScoutAgent

ScoutAgents are responsible for moving through the city in order to detect garbage that has to be recycled. They will obtain a route from the ScoutCoordinators which they have to follow, and report the detected garbage back to the coordinator.

#### Architecture

The required architecture is **reactive**. The agent follows the given route step by step and reacts to orders from the coordinator by updating the route. Whenever garbage is detected, it reactively reports it to the coordinator.

#### Properties

1. Flexibility — **No**. The agent does not perform flexible actions.
2. Reactivity — **Yes**. The agent reacts to detected garbage.
3. Proactiveness — **No**. The agent only follows orders by other agents and does not act proactively.
4. Social Ability — **Yes**. The agent is coordinated by the ScoutCoordinator and follows its orders.
5. Rationality — **Yes**. The agent always follows its instructions from the ScoutCoordinator in order to maximise its goal of finding garbage.
6. Reasoning capabilites — **No**. The agent is only following orders without reasoning itself.
7. Learning — **No**. There is no learning involved.
8. Autonomy — **No**. The agent only follows orders.
9. Temporal continuity — **Yes**. The agent is continuously active.
10. Mobility — **No**. The agent is always executed on the same machine.



## HarvesterAgent

HarvesterAgents are responsible for picking up garbage from detected garbage locations and bringing it to recycling centers. They communicate their current plan to the HarvesterCoordinator and receive orders detailing where to pick up garbage next and where to recycle it. In addition, they can ask the HarvesterCoordinator for permission to pick up additional garbage on the way.

#### Architecture

HarvesterAgents have to plan the shortest path to the garbage and recycling centers. Since the agent should always react to potential additional garbage on its current path, a **hybrid** architecture with a reactive component is required.

#### Properties

1. Flexibility — **No**. The agent is following orders and needs the coordinator's permission to change its plan.
2. Reactivity — **Yes**. The agent maintains an ongoing interaction with its environment by picking up additional garbage on its way.
3. Proactiveness — **Yes.** The agent can act proactively by making changes to its current plan, to pick up additional garbage in order to maximize points obtained at the recycling center. 
4. Social Ability — **Yes**. It is both coordinated by the HarvesterCoordinator and negotiates possible detours. 
5. Rationality — **Yes**. The agent only acts in ways that contribute to achieving its goals. For example, it will not make unnecessary detours. 
6. Reasoning capabilites — **Yes**. It needs path-finding capabilities and needs to reason about when small detours are justified for picking up additional garbage. 
7. Learning — **No.** There is no capability to improve performance by learning.
8. Autonomy — **Yes**. The agent has autonomy of choosing paths and picking up additional garbage. The autonomy is limited, since it also has to follow orders by the coordinator. It can not decide which garbage to pick up and where to recycle it.
9. Temporal continuity — **Yes**. The agent is continuosly active.
10. Mobility — **No**. The agent is always executed on the same machine.

