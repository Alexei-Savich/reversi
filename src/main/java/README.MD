# Reversi
At the beginning you are able to choose between 5 game options:
- 0 means real player
- 1 means AI with heuristics when the just difference between number of its pieces and opponent's pieces
- 2 means AI with the same heuristics above, but each position has its weight (**no** negative weights)
- 3 means AI with the same heuristics above, but each position has its weight (**with** negative weights)
- 4 means AI adapting the needed heuristics depending on the situation (when 2 corners are occupied by player's pieces, heuristics 2 is used; when more than 3 corners are occupied, strategy 1 is used; when less than 2 corners are occupied - strategy 3 is used)

When there are more than 1 move with the same evaluation score are available, the algorithms randomly chooses between them, so each game is different