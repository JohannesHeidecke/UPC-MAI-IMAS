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

Coalition of collided vehicles, with list of priorities

### Idle HarvesterAgents

##### Current implementation

After recycling, idle HarvesterAgents make random steps

##### Problem

Idle HarvesterAgents tend to accumulate near recycling centers, sometime causing collisions

##### Solution

As proposed in last delivery

Coalitions between HarvesterAgents and ScoutAgents
HarvesterAgents follow ScoutAgents

### Garbage

Garbage announcement step





## Performance Measure

Balancing benefits and waiting time

### Comparing performance

to optimal solution

