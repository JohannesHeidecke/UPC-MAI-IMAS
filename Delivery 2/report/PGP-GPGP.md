# PGP

### Summary

(i) Each agent creates a local plan: its own goals and most important actions to solve problem (abstract plan), and steps to achieve next step in abstract plan. This is updated as plan is executed.

(ii) Agents exchange local plans and generate PGP by combining local partial plans. Each agent knows organisation structure and knows which agents to send plans to.

(iii) Optimise PGP: analyse received information e.g. detect if several agents working on same activity. Alter local plans to better coordinate activities. 

### Advantages

Flexible - so beneficial for changing environment i.e. new garbage added, plan can be updated to make optimal.
Efficiency - no two agents will work on same subproblem e.g. collecting same garbage

### Disadvantages

Complex



## Applicability to our problem

### Scout Coordination Task

The scout agents (SA) could be responsible for deciding their paths to discover garbage. They will put their proposed route information into their local plans and then send this onto the scout coordinator (SC). The SC then builds a PGP with the local plans from all agents. The SC detects if SAs plans are conflicting e.g. they plan to search the same area of map, they modify the PGP to avoid this and send this back to the scouts. 

### Harvester Coordination Task

Similarly, when harvester agents (HA) find out about garbage location they can generate their own plan (route) to harvest this located garbage. They add their suggested route to the garbage and onto appropriate recycling centre to their local plan, and send this onto the harvester coordinator (HC). The HC builds a PGP with the local plans from all HAs, and decides which HA route is the most beneficial for collecting which garbage. The HC modifies the PGP with its decision for each garbage collection and sends this back to the HAs so they have their updated individual plans. 

### Vehicle Coordination Task

The SC decides the routes that the SAs will take and this information goes into its local plan (it knows the current location and planned future location of all agents). Similarly, the HC is aware at all times of the location of all HAs, which are idle and which are en route to harvest garbage. This information is put into their local plan. The SC and HC send their local plans to the coordinator agent (CA). The CA builds a PGP and is able to see the locations and planned routes of all SAs and HAs. Using this information it can resolve conflicts e.g.two agents on one cell at one time, by deciding (based on a predetermined hierarchy) which agent must allow for another to pass by before continuing on its path. It will update the PGP with these decisions and send back to the SC and HC to update the SAs and HAs of any path modifications.



# GPGP

### Summary

GPGP is a domain-independent extension of PGP. Furthermore, GPGP mechanisms communicate scheduling for “particular tasks” [Decker & Lesser, 1995] rather than a complete schedule as in PGP. In this way agents can detect and coordinate task interactions. 

### Coordination Mechanisms

(i) Agents update non-local viewpoints by sending information about a particular task to all other agents that can also solve it.

(ii) Deals with ‘Hard’ coordination relationships e.g. by committing prerequisites of certain tasks to be completed by a certain deadline and rescheduling agent actions. 

(iii) Deals with ‘Soft’ coordination relationships e.g. where possible, committing for certain tasks to be completed as soon as possible if they benefit other tasks. 

(iv) Avoiding redundancy:  when it appears that more than one agent will be completing the same task, one agent is chosen either by random choice or by other means e.g. calculating the best solution. All other agents are updated with this information denoting another agent is completing the task. 



## Applicability to our problem

If two harvesters commit to collect the same garbage, this information will be shared in their non-local viewpoints. The harvester coordinator would detect the redundancy in this task and will choose the agent that offers the best solution e.g. closest to garbage.  This agent will be notified to collect the garbage and the other will be updated with instructions that another agent is completing the task. 