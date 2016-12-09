## Analysis of Cooperation Mechanisms

- Self-Interested Agents.md

We have assumed our agents are benevolent (cooperative) - our best interest is their best interest - this is a much simpler design and they cooperate with explicit communication. Next, we summarise and analyse possible cooperative coordination mechanisms. 


### Deliberative Coordination:
- PGP-GPGP.md
- Coalitions

###Negotiator Coordination:

- Contract Net
- Auctions
- Voting.md



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

3. Harvester Agent moving to garbage location (to harvest garbage)

4. Scout Agent 

5. Harvester Agent idle 

**Avoiding collision solution:**

`Max P (Vehicle 1, Vehicle 2) =>` Continue moving on current path.

`¬Max P (Vehicle 1, Vehicle 2) =>` Go back on previous path taken until no longer in current path of prioritised vehicle. Remain stationary until prioritised vehicle is out of path. Move again on original path. (Note: whilst moving back on path and waiting stationary to avoid collision, this vehicle has higher priority than any other vehicle).

Using the above solution the CA sends these updated paths for the vehicles involved to the SC and HC, who in turn pass these to each vehicle. Their paths are updated in their local plans to avoid the collision.  
