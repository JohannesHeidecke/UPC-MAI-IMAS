IMAS final delivery 
Outline for report and presentation



## Outlook and Improvements

What does not work perfectly.
We have solutions for this, but not yet implemented.



### Collisions

##### Current implementation

Make random steps until situation is resolved

##### Problem

Several vehicles collide in a difficult location and can not get out of deadlock.

This happens only on complicated maps with narrow dead ends and/or one-way streets, and approximately only every 50,000 steps.

##### Solution

As proposed in last delivery

Coalition of collided vehicles, with list of vehicle priorities



### Idle HarvesterAgents

##### Current implementation

After leaving garbage in a recycling center, HarvesterAgents become idle, without new assignments and only make random steps henceforth

##### Problem

Idle HarvesterAgents tend to accumulate near recycling centers, sometime causing collisions with other vehicles

##### Solution

As proposed in last delivery

Coalitions between HarvesterAgents and ScoutAgents
Idle HarvesterAgents follow ScoutAgents, in order to be as close as possible to newly detected garbage



### Garbage Assignment

Garbage announcement step in Contract Net

##### Current implementation

New garbage is assigned to HarvesterAgents in the order in which it is discovered

##### Problem

Garbage assignment order is random and HarvesterAgents may be busy collecting garbage that earns few benefits, while valuable garbage is left to wait.

##### Solution

As proposed in last delivery

Voting protocol to order pending garbage according to a rank which is calculated using the performance measure





## Performance Measure

Balancing benefits and waiting time

### Comparing performance

to optimal solution

