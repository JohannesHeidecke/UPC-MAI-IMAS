# Part 1

Analyze the characteristics of the environment that the practical exercise presents:

Used as source: https://www.doc.ic.ac.uk/project/examples/2005/163/g0516302/environments/environments.html
Definitions come from the lecture slides (Lecture 2)

## Accessible or inaccessible.

### Definition

An accessible environment is one in which the agent can obtain complete, accurate, up-to-date information about the environment’s state. Most moderately complex environments (including, for example, the everyday physical world and the Web) are inaccessible.

### Examples

Inaccessible environment: physical world: information about any event on earth 
Accessible environment: empty room which state is defined by its temperature and agents can measure it. 

#### Sara

**Inaccessible**. As stated in the description of the task: “Each vehicle will have a visual range limited to the cell where it is located plus the 8 cells surrounding it. The vehicles will have to move, horizontally or vertically, through the street cells in order to explore the city. They will communicate to combine the information found so that the position of the garbage is known as soon as possible.” The agent can never obtain fully complete information, for instance if a scout moves throughout the city to discover trash, it is able to obtain up-to-date information wherever it is heading, however it is possible for trash to pop up in the area he is leaving behind. Therefore he will never be able to obtain complete information about the environment. 

## Deterministic or non-deterministic.

### Definition

A deterministic environment is one in which any action has a single guaranteed effect — there is no uncertainty about the state that will result from performing an action.

### Examples

Non-deterministic environment: physical world: Robot on Mars 
Deterministic environment: Tic Tac Toe game 

#### Sara

**Non-deterministic**. Because one move does not guarantee a certain outcome. The scout may move and discover trash, or it may not. This however is related to agents actions, does that apply to the question of analyzing the environment? System agent: decides if new garbage will appear based on a probability. Does this mean that the action “new garbage?” will result to a state with either new or no new garbage depending on this probability? Does one action have multiple outcomes? I think it does, so it is non-deterministic in the sense that an action has more than one single outcome, but the possible set of outcomes is limited. 

## Episodic or non-episodic.

### Definition

In an episodic environment, the performance of an agent is dependent on a number of discrete, independent episodes, with no link between the performance of an agent in different scenarios. Episodic environments are simpler from the agent developer’s perspective because the agent can decide which action to perform based only on the current episode — it does not need to reason about the interactions between different episodes

### Examples

Episodic environment: mail sorting system 
Non-episodic environment: chess game 

#### Sara

**Episodic**. The agent decides which action is best to take, it will only consider the task at hand and doesn’t have to consider the effect it may have on future tasks. In task description: “Each harvester can harvest one or more kinds of garbage but it can only carry one kind of garbage at the same time. Moreover, harvesters will have a maximum number of units of garbage that they can carry. When they have harvested garbage, they can go to harvest in another building if the maximum number of units has not been reached or they can go to recycle this garbage. To do this, a harvester has to be situated in a cell adjacent to a recycling center (horizontally, vertically or diagonally) that allows the kind of garbage it is carrying and remain there for some time (1 turn per garbage unit). Several harvesters can be harvesting garbage from the same building or disposing garbage in the same recycling center at the same time.” What is the decision of the Harvest agent based on regarding whether to go to harvest in another building if the maximum number of units has not been reached, or going to recycle this garbage? Is this based on the future or only on the current state?

## Static or dynamic.

### Definition

A static environment is one that can be assumed to remain unchanged except by the performance of actions by the agent
A dynamic environment is one that has other processes operating on it, and which hence changes in ways beyond the agent’s control

### Examples

Dynamic environment: physical world 
Static environment: empty office with no moving objects

#### Sara

I would lean towards **dynamic** as the environment is changing with trash popping up based on a probability. However, this is the result of the SystemAgent performing the action. If all changes in the environment are due to the actions of agents, then by definition it would be a static environment. 

## Discrete or continuous.

### Definition

An environment is discrete if there is a fixed, finite number of actions and percepts in it.

### Examples

Discrete environment: A game of chess or checkers where there are a set number of moves. 
Continuous environment: Taxi driving. There could be a route from to anywhere to anywhere else. 

#### Sara

**Discrete**. There are very clearly a limited numbers of actions that the agents are able to perform. Every agent in the environment is bound by its limited capabilities. 

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