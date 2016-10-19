# Part 2 – Agents



## SystemAgent

- Does have a complete representation of the environment
  (which elements currently exist and their specific status)
- Decides if garbage is appearing
- Keeps state of the city
- Executes orders to update the state of the world

Architecture: Deliberative



## CoordinatorAgent

- Mainly communicative and reactive behaviour


- Receives current status from the SystemAgent
- Centralizes orders to be executed in each turn
- Forwards to the SystemAgent orders to update the state

Architecture: Reactive



## ScoutCoordinator

- Reports to CoordinatorAgent
- Receives position of newly discovered garbage by the ScoutAgents
- Have a map of the explored area
- Make decisions

Architecture: Deliberative



## HarvesterCoordinator

- Have a map of the explored area

- Assign harvester to building to with garbage 
  (depending on the kind and amount of garbage, the capacity of the harvester and their location)

Architecture: Deliberative



## ScoutAgent

- Limited vision (8 adjacent cells)

Architecture: Reactive



## HarvesterAgent

- Limited vision (8 adjacent cells)

Architecture: Deliberative, but maybe hybrid, in case we want that »a HarvesterAgent with a stated plan can change it to collect some other garbage before continuing its own path«

