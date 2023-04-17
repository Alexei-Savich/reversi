# Reversi
At the beginning you are able to choose between 5 game options:
- 0 means real player
- 1 means AI with heuristics when the just difference between number of its pieces and opponent's pieces
- 2 means AI with the same heuristics above, but each position has its weight (**no** negative weights)
- 3 means AI with the same heuristics above, but each position has its weight (**with** negative weights)
- 4 means AI adapting the needed heuristics depending on the situation 
  - when 2 corners are occupied by player's pieces, heuristics 2 is used (aggressive strategy)
  - when more than 3 corners are occupied by player's pieces, strategy 1 is used (naive strategy)
  - when less than 2 corners are occupied by player's pieces, strategy 3 is used (the safest strategy)

When there are more than 1 move with the same evaluation score are available, the algorithms randomly chooses between them, so each game is different

Multithreading is used to increase computational performance

Weights for cells are used in the following way:

| | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| **1** | 120 | -20 | 20 | 5 | 5 | 20 | -20 | 120 |
| **2** | -20 | -40 | -5 | -5 | -5 | -5 | -40 | -20 |
| **3** | 20 | -5 | 15 | 3 | 3 | 15 | -5 | 20 |
| **4** | 5 | -5 | 3 | 3 | 3 | 3 | -5 | 5 |
| **5** | 5 | -5 | 3 | 3 | 3 | 3 | -5 | 5 | 
| **6** | 20 | -5 | 15 | 3 | 3 | 15 | -5 | 20 |
| **7** | -20 | -40 | -5 | -5 | -5 | -5 | -40 | -20 |
| **8** | 120 | -20 | 20 | 5 | 5 | 20 | -20 | 120 |