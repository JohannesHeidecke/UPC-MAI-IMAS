# Part 1

Analyze the characteristics of the environment that the practical exercise presents:

#### Sources

[1]: https://www.doc.ic.ac.uk/project/examples/2005/163/g0516302/environments/environments.html "Imperial College London"
[An Introduction to MAS][1]

[2]: http://es.slideshare.net/EhsanNowrouzi/artificial-intelligence-chapter-two-agents "Artificial Intelligence Chapter two agents (Slides 15-17), Nowrouzi"
[Artificial Intelligence Chapter two agents][2]

Definitions come from the lecture slides (Lecture 2)

## Accessible or inaccessible.

### Definition

An accessible environment is one in which the agent can obtain complete, accurate, up-to-date information about the environment’s state. Most moderately complex environments (including, for example, the everyday physical world and the Web) are inaccessible.

### Examples

Inaccessible environment: physical world: information about any event on earth 

Accessible environment: empty room which state is defined by its temperature and agents can measure it. 

##### Sara

**Inaccessible**. As stated in the description of the task: “Each vehicle will have a visual range limited to the cell where it is located plus the 8 cells surrounding it. The vehicles will have to move, horizontally or vertically, through the street cells in order to explore the city. They will communicate to combine the information found so that the position of the garbage is known as soon as possible.” The agent can never obtain fully complete information, for instance if a scout moves throughout the city to discover trash, it is able to obtain up-to-date information wherever it is heading, however it is possible for trash to pop up in the area he is leaving behind. Therefore he will never be able to obtain complete information about the environment. 

##### Sebastian

Agreed, **inaccessible** (**partially observable** according to [Source 2][2]).

##### Johannes
I only agree partially. In my opinion it is **both accessible and inaccessible**, depending on which agent we are looking at. For the system agent, the environment is completely accessible. He has access to the simulation's configuration settings which "define the whole simulation properties". He controls the state of the environment and its changes. Thus the environment is accessible for this agent. For all other agents, the environment is inaccessible. ScoutAgents only have a vision range of 8 blocks. They are the only agents that can detect new trash (ignoring the omniscient SystemAgent from now on) and all other agents can only obtain new information about state changes via the information chain flowing from the ScoutAgents to ScoutCoordinator to CoordinatorAgent to HarvestCoordinator to HarvesterAgents. This means all other agents are indirectly limited by the limitations of the ScoutAgents and thus for them the environment is also inaccessible. They probably want us to focus on how the environment is for the non-system agents, but we should at least elaborate that the same environment can have different properties for different agents, like in this case.

## Deterministic or non-deterministic.

### Definition

A deterministic environment is one in which any action has a single guaranteed effect — there is no uncertainty about the state that will result from performing an action.

### Examples

Non-deterministic environment: physical world: Robot on Mars 

Deterministic environment: Tic Tac Toe game 

##### Sara

**Non-deterministic**. Because one move does not guarantee a certain outcome. The scout may move and discover trash, or it may not. This however is related to agents actions, does that apply to the question of analyzing the environment? System agent: decides if new garbage will appear based on a probability. Does this mean that the action “new garbage?” will result to a state with either new or no new garbage depending on this probability? Does one action have multiple outcomes? I think it does, so it is non-deterministic in the sense that an action has more than one single outcome, but the possible set of outcomes is limited. 

##### Sebastian

I think, this has to be analyzed from the agent’s point of view, as to: _I know the state of the environment and I know what effects my actions have. Therefore I can predict the environment’s state in the following cycle._ This does not apply merely because the System Agent randomly injects trash. In [Source 2][2] it is argued that an environment is **strategic** »if the environment is deterministic except for the actions of other agents«. Considering the only unpredictability comes from the System Agent, we could say, this situation is given.

On the other hand, since the environment is only partially observable we can in no state ever be completely sure about the state of the environment as there are areas that escape our perception. This would make the environment **non-deterministic** (or stochastic as it’s called in [Source 2][2]).

##### Johannes

The definition given in the slides requires "every action to have a single guaranteed effect" in order for an environment to be deterministic. In the practice description, three basic kinds of actions are declared:
* Garbage detection
* Garbage harvesting
* Garbage recycling

Garbage harvesting and recycling is purely deterministic in my opinion. There is no uncertainty about the outcomes of the actions. Sara argued that Garbage detection is non-deterministic since moving a field does not guarantee trash to be discovered. I think we can safely call garbage detection deterministic, if we take the state "detected all existing trash within 8 fields" as the expected outcome. Of course the agent can't predict the amount of trash before performing the detection, but it will always correctly know the current state of the neighboring fields after his action garbage detection. 

As to Sebastian's points: I don't think that a deterministic environment requires the environment to be accessible for the agent in the moment of the action. An agent can perform local changes with deterministic local outcome (e.g. unload all trash to a recycling center) and his action should still be called deterministic, even if another agent changes the environment somewhere else. We could assume that actions don't occur exactly at the same time (which is fair to assume since the actions are sequentially processed) and thus agents can exactly preditc the outcome of their actions without having to fear that at the same time new trash was randonmly inserted somewhere. It's probably a good idea to mention that the environment is strategic-deterministic, since the SystemAgent performs changes that can not be predicted by the other agents. 

Another argument for a strategic environment: If a HarvesterAgent gets the order to harvest some trash - there might be other agents that harvest this trash before the HarvestAgent reaches it - for example if we allow agents to reactively load trash of the same type without orders from the CoordinatorAgents. Hence this action would not be deterministic anymore, maybe after the action harvestTrash(3) there are not 3 new units of trash on the Harvester.

It is probably best to make our case for strategic (we have to find a good definition for that in a reputable source) and emphasize that with perfect information flow between the agents along the chain from Scouts to Harvesters, actions will be deterministic.

## Episodic or non-episodic.

### Definition

In an episodic environment, the performance of an agent is dependent on a number of discrete, independent episodes, with no link between the performance of an agent in different scenarios. Episodic environments are simpler from the agent developer’s perspective because the agent can decide which action to perform based only on the current episode — it does not need to reason about the interactions between different episodes

### Examples

Episodic environment: mail sorting system 

Non-episodic environment: chess game 

##### Sara

**Episodic**. The agent decides which action is best to take, it will only consider the task at hand and doesn’t have to consider the effect it may have on future tasks. In task description: “Each harvester can harvest one or more kinds of garbage but it can only carry one kind of garbage at the same time. Moreover, harvesters will have a maximum number of units of garbage that they can carry. When they have harvested garbage, they can go to harvest in another building if the maximum number of units has not been reached or they can go to recycle this garbage. To do this, a harvester has to be situated in a cell adjacent to a recycling center (horizontally, vertically or diagonally) that allows the kind of garbage it is carrying and remain there for some time (1 turn per garbage unit). Several harvesters can be harvesting garbage from the same building or disposing garbage in the same recycling center at the same time.” What is the decision of the Harvest agent based on regarding whether to go to harvest in another building if the maximum number of units has not been reached, or going to recycle this garbage? Is this based on the future or only on the current state?

##### Sebastian

My first question would be: In this scenario, what could be considered an episode? On single cycle? The task that a single agent is assigned (e.g. go to cell x,y and pick up trash)?

I tend to argue for a **non-episodic** environment, because I can’t think of a way to separate the different states and tasks w/o depending on the past or the future. 

Let’s analyze the examples: a mail sorting system is episodic, because with each new piece of mail it analyzes it has to go through a series of steps w/o taking into account the previous and next piece of mail. It does not matter whether the previous mail was national or international, all the required information for the system to decide on the current subject is contained within the current episode. In a chess game, of course, for the result of the game, each move it’s important where the pieces are located before and where they are going to end up afterwards.

##### Johannes

I tend to agree with Sebastian here. While the Harvester and Scouts simply do what they are told (you could count their orders as part of their current state and then say the environment is episodic for them), the CoordinatorAgents will have to do planning that involves more than one step. Since they have to take past states into account (e.g. where have Scouts already been recently) their environment is non-episodic.

## Static or dynamic.

### Definition

A static environment is one that can be assumed to remain unchanged except by the performance of actions by the agent

A dynamic environment is one that has other processes operating on it, and which hence changes in ways beyond the agent’s control

### Examples

Dynamic environment: physical world 

Static environment: empty office with no moving objects

##### Sara

I would lean towards **dynamic** as the environment is changing with trash popping up based on a probability. However, this is the result of the SystemAgent performing the action. If all changes in the environment are due to the actions of agents, then by definition it would be a static environment. 

##### Sebastian

[Source 2][2] suggests, a static environemnt remains »unchanged while an agent is deliberating«. I think, as you say, this must also apply to the System Agent. Nevertheless, we’re talking about a simulation and can very well imagine this application running in the real world, which the System Agent stands for. I also would lean towars **dynamic**.

##### Johannes
Well same as for (in)accessible - for the SystemAgent the environment is static, but his actions make the environment change and hence **dynamic** for all other agents. I agree with both of you.

## Discrete or continuous.

### Definition

An environment is discrete if there is a fixed, finite number of actions and percepts in it.

### Examples

Discrete environment: A game of chess or checkers where there are a set number of moves. 

Continuous environment: Taxi driving. There could be a route from to anywhere to anywhere else. 

##### Sara

**Discrete**. There are very clearly a limited numbers of actions that the agents are able to perform. Every agent in the environment is bound by its limited capabilities. 

##### Sebastian

Agreed, **discrete**.

##### Johannes

Yup, **discrete**. All properties of the environment are discrete and all actions change properties in a discrete way.

---

# Part 2

Analyze also the properties that each type of agents in the practical exercise must incorporate in order to fulfill all their objectives, based on their architecture and the environment characteristics

---

# Deliverables

- Written report detailing the study and analysis of:
- Characteristics of the environment.
- Best kind of architecture to apply to each type of agents.
- Properties that should be exhibited by each type of agents.
- Oral presentation