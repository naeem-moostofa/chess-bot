# chess-bot
[![Java CI](https://github.com/naeem-moostofa/chess-bot/actions/workflows/ci.yml/badge.svg)](https://github.com/naeem-moostofa/chess-bot/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/language-Java-blue)


A Chess Interface where you can play against a computer.

## Project Description:

#### User Interface:
The Java Swing library is used to create an interactive chess board where png images are used to display pieces, and players can select squares to highlight them and move pieces.

####  Chess Board:
The chess board is stored as a 2d array of Pieces, for each type of piece all possible moves are checked to determine if a move is legal.

#### Chess Bot:
In order to determine the best move in a position alpha beta pruning is used to navigate the tree of possible positions along with a static evaluation function to evaluate the final position reached in the tree where the player with the white pieces is trying to maximize the evaluation and the player with the black pieces is trying to minimize the evaluation. The static evaluation function uses the PeSTO piece square tables so that both material and piece positioning are considered: [https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function](url). Additonally a bonus is added for each square a piece can move to which encourages the program to make active moves in the opening. The apha beta search was extended in positions with checks or captures since the static evaluation function will be inaccurate if for example a queen in hanging. With a depth of 4 and a maximum depth of 6 for positions with checks and captures the program is able to acheive an estimated elo of 1500 when compared to bots on [chess.com](url), taking 2s-60s per move.

## Future Improvements:
Currently the board is stored as a 2d array of Pieces, however it would be faster to store the board as 12 64 bit integers, one integer for each colour and each piece. This would likely speed up move generation allowing an increase in depth and accuracy. 
