# Summary

Voting is a distributed deliberation process. Decisions are taken collectively. There are two different basic voting protocols. 

Simple voting is a group term for voting protocols, where each voter either chooses only their prefered option (Plurality), their least favourite option (Anti-plurality), both (Best-worst) or only the options they would accept (Approval).

In total order voting, each voter returns complete list of options sorted by their preference. There are different ways to count results and determine a winning option (Binary, Borda, Condorcet).



# Applicability

### Vehicle Coordination

Each vehicle (HarvesterAgent, ScoutAgent) puts a list of possible routes to their destination to a public vote. All other vehicles act as voters. Using the approval voting method the path with least conflicts can be determined. Each voter has to return a subset of paths that do not interfere with its own route. The option with the most votes wins.



# Advantages & Disadvantages

In voting the decision process is decentralized. Applied to our problem, this means the coordinating agents (HarvesterCoordinator, ScoutCoordinator) merely act as administrative entities. If they have a vote at all, it only counts as much as any other agent’s vote. 

This would work well in a scenario, where agents have individual objectives and therefore need more freedom. The problem of Harvesting and Scouting Coordination, though, require a centralized, hierarchic organisational structure. The use of a voting mechanism for both of these tasks, misses the actual purpose of that technique. Voting is used to collectively make a decision that affects all of the involved parties. As we want to split up tasks, though, there is no point in deciding on a plan for everyone together. There are more appropriate methods we can use.

Voting techniques, however, could well be applicable to the Vehicle Coordination task. Here the premise of individual objectives is given: each vehicle has its own destination. And the decision about which route to take potentially affects other agents. 

Two problems, however, remain. Many vehicles have to determine their route at the same time. It is therefore not clear if a proposed path by a vehicle might be conflictive with that of another one, which has not yet determined its route. We need to define an order in which the vehicles determine theirs paths. This solution also has an unnecessary high computational cost, as we need to calculate alternative routes that will never be used.











## Other Applications (that don’t make as much sense)

### Harvesting Coordination

The HarvesterCoordinator presents as ballot a list of garbages that have to be picked up. Each HarvesterAgent either casts a simple plurality vote or returns a totally ordered list of preferences. Each gargabe location that is a preference of at least one HarvesterAgent can be erased from the ballot and be assigned to the respective agent(s). A HarvesterAgent may not cast a vote, for example when it is in the process of pickup up garbage (moving to the location, wait one turn). It shall instead return an abstention message. The voting has to be done whenever a new garbage is detected or a HarvesterAgent reports as idle.

### Scouting Coordination

The ScoutCoordinator presents as ballot a list of cells ordered by the amount of turns it has not been seen by a ScoutAgent (descending). 









# Voting protocols 

Not necessary for report or slides



- Social choice 
- Properties of social choice rules 
- Simple voting protocols 
  - Plurality / Anti-plurality / Best-Worst / Approval 
  - Total order protocols: Binary / Borda / Condorcet 
- Complex voting mechanisms 
  - Linguistic votes 
  - Uncertain opinions 




## Simple

List of options

Each agent votes

One option is selected as winner



### Plurality voting

One vote per agent

Option with most votes wins

Problems:

- *Useful vote*, insincere voters
- Each agent can only give 1 vote, even if it considers 2-3-4 “good” alternatives
- May have strange outcome

Advantages:

- Simple


- Equality principle (1 agent = 1 vote)



### Anti-plurality voting

One vote (negative) per agent 

Option with least votes wins



### Best-worst voting

Each agent: 

- 1 positive vote to best option
- 1 negative vote to worst option

Each option:

- α > 0 points for each positive vote 
- -δ < 0 points for each negative vote 

Calculation for each option

​	A: 3α - 4δ

​	δ = 0 : plurality

​	α = 0 : anti-plurality

Option with most points wins



### Approval voting

Each agent selects a subset of options (the ones it would accept)

Option with most votes wins

k-approval: subset of k options

​	k = 1 : plurality

​	k = n-1 : anti-plurality



## Total order

Each agent orders list of options according to its preferences (best to worst)



### Binary protocol

Ordered list of options (o1, o2, …, oN)

Evaluation in pairs (oX vs oY)

Simple majority: option A wins over B iff number of voter that prefer A is bigger than number of voters that prefer B

Option that wins last evaluation is overall winner

​	win(o5, win (o4, win (o3, win(o2, o1))))

Problem: Order of options affect the outcome

- Last options have higher chances of winning (not neutral)





### Borda protocol

Each agent assigns points to each of N options

N points for preferred option, N-1 points for second, 1 point for least preferred

Points are summed up for each option

Option with most points wins

Problem: Borda paradox

- Eliminating (or adding) one irrelevant option may completely change the outcome
- Total order changes if options are removed one by one

Extension: weak orders

- Simple: each voter assigns to each option as many points as there are options considered worse by the voter
  Example:
  - 0 points if option is least preferred
  - 2 points if there are two option the voter prefers less





### Condorcet protocol

All options are compared to each other

Option wins if it wins all comparisons

Resolve ties with other methods (e.g. Borda count)

Problems:

- Circular ambiguities, no option wins comparison to all others

Resolved by:

- Keep option that wins most matches (Copeland)
- Consider relative strengths of defeats (Minimax, Ranked Pairs, Schulze)