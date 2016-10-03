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

#### Sara

**Inaccessible**. As stated in the description of the task: “Each vehicle will have a visual range limited to the cell where it is located plus the 8 cells surrounding it. The vehicles will have to move, horizontally or vertically, through the street cells in order to explore the city. They will communicate to combine the information found so that the position of the garbage is known as soon as possible.” The agent can never obtain fully complete information, for instance if a scout moves throughout the city to discover trash, it is able to obtain up-to-date information wherever it is heading, however it is possible for trash to pop up in the area he is leaving behind. Therefore he will never be able to obtain complete information about the environment. 

#### Sebastian

Agreed, **inaccessible** (**partially observable** according to [Source 2][2]).

## Deterministic or non-deterministic.

### Definition

A deterministic environment is one in which any action has a single guaranteed effect — there is no uncertainty about the state that will result from performing an action.

### Examples

Non-deterministic environment: physical world: Robot on Mars 

Deterministic environment: Tic Tac Toe game 

#### Sara

**Non-deterministic**. Because one move does not guarantee a certain outcome. The scout may move and discover trash, or it may not. This however is related to agents actions, does that apply to the question of analyzing the environment? System agent: decides if new garbage will appear based on a probability. Does this mean that the action “new garbage?” will result to a state with either new or no new garbage depending on this probability? Does one action have multiple outcomes? I think it does, so it is non-deterministic in the sense that an action has more than one single outcome, but the possible set of outcomes is limited. 

#### Sebastian

I think, this has to be analyzed from the agent’s point of view, as to: _I know the state of the environment and I know what effects my actions have. Therefore I can predict the environment’s state in the following cycle._ This does not apply merely because the System Agent randomly injects trash. In [Source 2][2] it is argued that an environment is **strategic** »if the environment is deterministic except for the actions of other agents«. Considering the only unpredictability comes from the System Agent, we could say, this situation is given.

On the other hand, since the environment is only partially observable we can in no state ever be completely sure about the state of the environment as there are areas that escape our perception. This would make the environment **non-deterministic** (or stochastic as it’s called in [Source 2][2]).

## Episodic or non-episodic.

### Definition

In an episodic environment, the performance of an agent is dependent on a number of discrete, independent episodes, with no link between the performance of an agent in different scenarios. Episodic environments are simpler from the agent developer’s perspective because the agent can decide which action to perform based only on the current episode — it does not need to reason about the interactions between different episodes

### Examples

Episodic environment: mail sorting system 

Non-episodic environment: chess game 

#### Sara

**Episodic**. The agent decides which action is best to take, it will only consider the task at hand and doesn’t have to consider the effect it may have on future tasks. In task description: “Each harvester can harvest one or more kinds of garbage but it can only carry one kind of garbage at the same time. Moreover, harvesters will have a maximum number of units of garbage that they can carry. When they have harvested garbage, they can go to harvest in another building if the maximum number of units has not been reached or they can go to recycle this garbage. To do this, a harvester has to be situated in a cell adjacent to a recycling center (horizontally, vertically or diagonally) that allows the kind of garbage it is carrying and remain there for some time (1 turn per garbage unit). Several harvesters can be harvesting garbage from the same building or disposing garbage in the same recycling center at the same time.” What is the decision of the Harvest agent based on regarding whether to go to harvest in another building if the maximum number of units has not been reached, or going to recycle this garbage? Is this based on the future or only on the current state?

#### Sebastian

My first question would be: In this scenario, what could be considered an episode? On single cycle? The task that a single agent is assigned (e.g. go to cell x,y and pick up trash)?

I tend to argue for a **non-episodic** environment, because I can’t think of a way to separate the different states and tasks w/o depending on the past or the future. 

Let’s analyze the examples: a mail sorting system is episodic, because with each new piece of mail it analyzes it has to go through a series of steps w/o taking into account the previous and next piece of mail. It does not matter whether the previous mail was national or international, all the required information for the system to decide on the current subject is contained within the current episode. In a chess game, of course, for the result of the game, each move it’s important where the pieces are located before and where they are going to end up afterwards.

## Static or dynamic.

### Definition

A static environment is one that can be assumed to remain unchanged except by the performance of actions by the agent

A dynamic environment is one that has other processes operating on it, and which hence changes in ways beyond the agent’s control

### Examples

Dynamic environment: physical world 

Static environment: empty office with no moving objects

#### Sara

I would lean towards **dynamic** as the environment is changing with trash popping up based on a probability. However, this is the result of the SystemAgent performing the action. If all changes in the environment are due to the actions of agents, then by definition it would be a static environment. 

#### Sebastian

[Source 2][2] suggests, a static environemnt remains »unchanged while an agent is deliberating«. I think, as you say, this must also apply to the System Agent. Nevertheless, we’re talking about a simulation and can very well imagine this application running in the real world, which the System Agent stands for. I also would lean towars **dynamic**.

## Discrete or continuous.

### Definition

An environment is discrete if there is a fixed, finite number of actions and percepts in it.

### Examples

Discrete environment: A game of chess or checkers where there are a set number of moves. 

Continuous environment: Taxi driving. There could be a route from to anywhere to anywhere else. 

#### Sara

**Discrete**. There are very clearly a limited numbers of actions that the agents are able to perform. Every agent in the environment is bound by its limited capabilities. 

#### Sebastian

Agreed, **discrete**.

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