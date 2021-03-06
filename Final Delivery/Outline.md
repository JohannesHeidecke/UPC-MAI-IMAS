IMAS final delivery 
Outline for report and presentation



## Outlook and Improvements

We have identified some specific areas of our implementation that have not been completed as described in our second delivery report, and which could be improved with more time. These are the last of our "project milestones", which we completed in order of task importance.

### Avoiding Collisions

##### Current implementation

Currently, whenever two vehicles cross each other’s paths, they make random steps, trying to step out of each other’s way, until the situation is resolved.

##### Problem

Whenever several vehicles collide in difficult locations this can lead to a deadlock. Usually, this only occurs on complicated maps with narrow dead ends and/or one-way streets, and approximately only every 50,000 steps.

##### Solution

As proposed in the second delivery, one solution would be to let the CoordinatorAgent avoid collisions through GPGP and a list of vehicle priorities. It receives partial plans of all vehicles’ paths and plans. When it detects an impending collision of two or more vehicles within n steps, the CoordinatorAgent decides which vehicle is able to continue its journey unchanged and which must wait in their place or back up to avoid the collision.


### Idle HarvesterAgents

##### Current implementation

After leaving garbage in a recycling center (or before any garbage assigned at the start), HarvesterAgents are idle (without new assignments) and only make random steps henceforth.

##### Problem

Idle HarvesterAgents tend to accumulate near recycling centers, which sometimes leads to collisions with other vehicles.

##### Solution

As proposed in the second delivery; ScoutAgents and idle HarvesterAgents could form coalitions. In these coalitions, idle HarvesterAgents would follow ScoutAgents in order to be as close as possible to newly detected garbage.


### Ranking garbage

##### Current implementation

New garbage is assigned to HarvesterAgents in the order in which it is discovered.

##### Problem

Garbage assignment order is random (as it is discovered). This can lead to HarvesterAgents spending time collecting garbage that earns few benefits, while valuable garbage is left to wait.

##### Solution

As proposed in the second delivery; we could implement a voting protocol to order pending garbage according to a rank, which is calculated using a performance measure. In the previous delivery we stated voting for the "most efficiently" harvested garbage but this should also include a measure of the most benefits that can be harvested. 


## Performance Measure

Balancing benefits and waiting time

### Comparing performance

to optimal solution

