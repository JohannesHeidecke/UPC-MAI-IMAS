# Part 1 – Environment 

For the discussion of the environment properties we used the definitions given in "Artificial Intelligence - A Modern Approach", Russel & Norvig, 2010.

## Accessible or Inaccessible

The environment is fully **accessible to the system agent**. The state of the system is known at all times. There are no noisy or inaccurate sensors.

However, the environment is not fully accessible to the other agents. 

The CoordinatorAgent is provided with dynamic information about the current state of the city by the SystemAgent. This information, however, does not include the location of garbage to be recycled. 

None of the non-system agents has the ability to observe the locations of all garbage in the city (assuming a city size > 9 cells). ScoutAgents can scan their neighborhood for garbage. Their sensors can only detect garbage within a range of the adjacent 8 cells. All other non-system agents can only obtain knowledge about garbage distribution by directly or indirectly communicating with ScoutAgents. Thus, the distribution of garbage is only **partially accessible to non-system agents**. 



## Deterministic or Non-deterministic

There are three basic actions that can be performed in the environment: garbage detection, garbage harvesting, and garbage recycling. In addition, the system agent randomly adds new garbage to the city. 

In the definition for deterministic environment we use, we ignore  uncertainty that arises purely from the actions of other agents. The given environment is entirely determined by the current states and the actions executed by the agents, thus it is **deterministic**.  



## Episodic or Non-episodic

Since the efficient recycling of the garbage requires planning of more than one step ahead, decisions about actions are dependent on more than one episode. The environment is **non-episodic**. 

An example of non-episodic planning is the ScoutCoordinator sending ScoutAgents to places that have not been scanned for a long time. This decision includes information about previous episodes and can not be done episodically.



## Static or Dynamic

The environment is **static**, since the enviroment doesn't change while the agents are deciding what to do next. According to the project description, the SystemAgent only proceeds with the next simulation step after all coordinaton tasks have been completed. The CoordinatorAgents "need not keep looking at the world while [they are] deciding on an action, nor need [they] worry about the passage of time" (compare Russel, Norvig, p.44). 

## Discrete or Continuous

The environment is clearly **discrete** since there is a finite number of possible states for the city, finite number of perceptions and actions, and time is handled in a discrete, turn-based way. 

