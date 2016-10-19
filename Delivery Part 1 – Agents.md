# Part 2 – Agents



##### Architectures

- Reactive
- Deliberative
- Hybrid



##### Properties

1. Flexibility
2. Reactivity
3. Proactiveness
4. Social Ability
5. Rationality
6. Reasoning capabilites
7. Learning
8. Autonomy
9. Temporal continuity
10. Mobility



## SystemAgent

- Does have a complete representation of the environment
  (which elements currently exist and their specific status)
- Decides if garbage is appearing, what type, where and how much
- Keeps state of the city
- Executes orders to update the state of the world

#### Architecture

Has to be discussed: it does have an overview of the world, but not any specific goal and basically receives order to update its state.

What does »decide« mean?

Is is autonomous?

#### Properties

1. Flexibility — NO
2. Reactivity — YES
3. Proactiveness — NO
4. Social Ability — YES
5. Rationality — NO
6. Reasoning capabilites
7. Learning — NO
8. Autonomy
9. Temporal continuity — YES
10. Mobility — NO



## CoordinatorAgent

- Mainly communicative and reactive behaviour


- Receives current status from the SystemAgent
- Centralizes orders to be executed in each turn
- Forwards to the SystemAgent orders to update the state

Architecture: Reactive

#### Properties

1. Flexibility — NO
2. Reactivity — YES
3. Proactiveness — NO
4. Social Ability — YES
5. Rationality — NO
6. Reasoning capabilites — NO
7. Learning — NO
8. Autonomy — NO
9. Temporal continuity — YES
10. Mobility — NO



## ScoutCoordinator

- Reports to CoordinatorAgent
- Receives position of newly discovered garbage by the ScoutAgents
- Have a map of the explored area
- Make decisions

#### Architecture

Deliberative

#### Properties

1. Flexibility — YES
2. Reactivity — YES
3. Proactiveness — YES
4. Social Ability — YES
5. Rationality — YES
6. Reasoning capabilites — YES
7. Learning — Would be interesting
8. Autonomy — YES
9. Temporal continuity — YES
10. Mobility — NO



## HarvesterCoordinator

- Have a map of the explored area

- Assign harvester to building to with garbage 
  (depending on the kind and amount of garbage, the capacity of the harvester and their location)

#### Architecture

Deliberative

#### Properties

1. Flexibility — YES
2. Reactivity — YES
3. Proactiveness — YES
4. Social Ability — YES
5. Rationality — YES
6. Reasoning capabilites — YES
7. Learning — Would be interesting
8. Autonomy — YES
9. Temporal continuity — YES
10. Mobility — NO



## ScoutAgent

- Limited vision (8 adjacent cells)

#### Architecture

Reactive

#### Properties

1. Flexibility — YES
2. Reactivity — YES
3. Proactiveness — YES
4. Social Ability — YES
5. Rationality — NO
6. Reasoning capabilites — NO
7. Learning — NO
8. Autonomy — YES
9. Temporal continuity — YES
10. Mobility — NO



## HarvesterAgent

- Limited vision (8 adjacent cells)

#### Architecture

Deliberative, but maybe hybrid, in case we want that »a HarvesterAgent with a stated plan can change it to collect some other garbage before continuing its own path«

#### Properties

1. Flexibility — YES
2. Reactivity — YES
3. Proactiveness — YES
4. Social Ability — YES
5. Rationality — YES
6. Reasoning capabilites — YES
7. Learning — Would be interesting (to remember routes that it has done before)
8. Autonomy — NO
9. Temporal continuity — YES
10. Mobility — NO

