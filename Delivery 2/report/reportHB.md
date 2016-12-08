## Analysis of Cooperation Mechanisms

###Self-Interested Agents:

These agents work independently to further their own interests, sometimes at the expense of others.

*Discrete MAS* have agents working independently with no relation or cooperation to one another. *MAS with emergent behaviour* have agents working independently that can sometimes cooperate without intentionally meaning to. 

**Application to our problem:**
The scout agents could reactively search for garbage. Their only goal is to find the garbage. This would be a suboptimal mechanism for the scout agents as it may lead to multiple scout agents searching in one place and leave areas of the map not searched. 

***
	
We have assumed our agents are benevolent (cooperative) - our best interest is their best interest - this is a much simpler design and they cooperate with explicit communication. Next, we summarise and analyse possible cooperative coordination mechanisms. 


### Deliberative Coordination:
####(1) PGP/GPGP

**PGP summary:**

(i) Each agent creates a local plan: its own goals and most important actions to solve problem (abstract plan), and steps to achieve next step in abstract plan. This is updated as plan is executed.

(ii) Agents exchange local plans and generate PGP by combining local partial plans. Each agent knows organisation structure and knows which agents to send plans to.

(iii) Optimise PGP: analyse received information e.g. detect if several agents working on same activity. Alter local plans to better coordinate activities. 

**Advantages:**

Flexible - so beneficial for changing environment i.e. new garbage added, plan can be updated to make optimal.
Efficiency - no two agents will work on same subproblem e.g. collecting same garbage

**Disadvantages:**

Complex

**Applicability to our problem:**

*Scout Coordination Task:* The scout agents (SA) could be responsible for deciding their paths to discover garbage. They will put their proposed route information into their local plans and then send this onto the scout coordinator (SC). The SC then builds a PGP with the local plans from all agents. The SC detects if SAs plans are conflicting e.g. they plan to search the same area of map, they modify the PGP to avoid this and send this back to the scouts. 

*Harvester Coordination Task:* Similarly, when harvester agents (HA) find out about garbage location they can generate their own plan (route) to harvest this located garbage. They add their suggested route to the garbage and onto appropriate recycling centre to their local plan, and send this onto the harvester coordinator (HC). The HC builds a PGP with the local plans from all HAs, and decides which HA route is the most beneficial for collecting which garbage. The HC modifies the PGP with its decision for each garbage collection and sends this back to the HAs so they have their updated individual plans. 

*Vehicle Coordination Task:* The SC decides the routes that the SAs will take and this information goes into its local plan (it knows the current location and planned future location of all agents). Similarly, the HC is aware at all times of the location of all HAs, which are idle and which are en route to harvest garbage. This information is put into their local plan. The SC and HC send their local plans to the coordinator agent (CA). The CA builds a PGP and is able to see the locations and planned routes of all SAs and HAs. Using this information it can resolve conflicts e.g.two agents on one cell at one time, by deciding (based on a predetermined hierarchy) which agent must allow for another to pass by before continuing on its path. It will update the PGP with these decisions and send back to the SC and HC to update the SAs and HAs of any path modifications.

####GPGP summary:

GPGP is a domain-independent extension of PGP. Furthermore, GPGP mechanisms communicate scheduling for “particular tasks” [Decker & Lesser, 1995] rather than a complete schedule as in PGP. In this way agents can detect and coordinate task interactions. 

**Coordination Mechanisms:**

(i) Agents update non-local viewpoints by sending information about a particular task to all other agents that can also solve it.

(ii) Deals with ‘Hard’ coordination relationships e.g. by committing prerequisites of certain tasks to be completed by a certain deadline and rescheduling agent actions. 

(iii) Deals with ‘Soft’ coordination relationships e.g. where possible, committing for certain tasks to be completed as soon as possible if they benefit other tasks. 

(iv) Avoiding redundancy:  when it appears that more than one agent will be completing the same task, one agent is chosen either by random choice or by other means e.g. calculating the best solution. All other agents are updated with this information denoting another agent is completing the task. 

**Applicability to our problem:**

If two harvesters commit to collect the same garbage, this information will be shared in their non-local viewpoints. The harvester coordinator would detect the redundancy in this task and will choose the agent that offers the best solution e.g. closest to garbage.  This agent will be notified to collect the garbage and the other will be updated with instructions that another agent is completing the task. 

####(2) Coalitions

###Negotiator Coordination:
####(3) Contract Net

####(4) Auctions

####(5) Voting

##Chosen Cooperation Mechanisms

###Scouting Coordination

We have chosen to employ a combination of cooperation mechanisms in order to best fit the scouting coordination task. We will apply patrolling/TSP … **(Johannes)** and also GPGP to restore equidistance between scout agents (SAs). This could occur if a scout agent has to amend its path to avoid a collision with another vehicle. 

**(TSP Johannes)**

The SAs keep their local viewpoints updated with their current goals and actions - this will always be to follow their given path and to find garbage. If a situation arises in which a SA has to change its path to avoid a collision, (this coordination is described in Vehicle Coordination Mechanism), then this change of path will be updated to the SA’s local viewpoint and then passed to the scout coordinator (SC) who will keep a partial plan of all SA local plans. The SC will calculate how best to keep the SAs equidistant following this change of path, e.g. by keeping all SAs stationary until the collision-avoiding SA is back to its original given path, and then it will pass these instructions back to the individual SAs. The SAs will now have updated local plans and will follow these new actions. In this way the SAs will continue, ensuring they are at equidistant points on their path to maintain our optimal map search. 


###Harvesting Coordination

###Vehicle Coordination

We will use **GPGP** for vehicle coordination. Each SA and HA includes their current goals and actions in their local plan. These are passed to their respective coordinators and from there passed to the coordinator agent (CA) who has a partial plan of all SA and HA current paths/actions. If a situation arises in which the paths of 2 vehicles are going to collide in n steps, the CA detects this and sets out to resolve it using the following solution:

**We define a hierarchy of vehicle priorities, *P*, as follows:**

1. Vehicle already moving to avoid collision

2. Harvester Agent moving to recycling centre (to recycle garbage)

3. Harvester Agent moving to garbage location (to harvester garbage)

4. Scout Agent 

5. Harvester Agent idle 

**Avoiding collision solution:**

`Max P (Vehicle 1, Vehicle 2) =>` Continue moving on current path.

`¬Max P (Vehicle 1, Vehicle 2) =>` Go back on previous path taken until no longer in current path of prioritised vehicle. Remain stationary until prioritised vehicle is out of path. Move again on original path. (Note: whilst moving back on path and waiting stationary to avoid collision, this vehicle has higher priority than any other vehicle).

Using the above solution the CA sends these updated paths for the vehicles involved to the SC and HC, who in turn pass these to each vehicle. Their paths are updated in their local plans to avoid the collision.  
