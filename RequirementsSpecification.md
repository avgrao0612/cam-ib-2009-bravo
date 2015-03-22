Part IB Group Project

Project Bravo - Magic Board Games

Requirements Specification

# Design Brief #

“Find a way to create a ‘magic’ board game in which a computer opponent moves the pieces invisibly, perhaps with magnets moving under the board. A significant amount of mechanical
assembly and experimentation is likely to be necessary. This will be achieved using stepper motors, rotary encoders, and construction using a simple prototyping toolset such as Lego Technic”

# Background Information #

The group decided to focus on the game of Draughts (or Checkers in American English). This offers a simple game for which both the mechanical and software components should be feasible. Draughts is played on an 8x8 chequered board. There are is a set of 12 pieces for each player, typically one black and one white. Pieces can also be crowned to make them a king. There are a number of variations of the rules, and therefore the version that we will be implementing is outlined below:

The game starts with the two sets of pieces at opposite sides of the board, occupying the black squares of the first three rows.

  * Pieces are only ever placed on black squares.
  * Normal pieces may only move in a forwards direction (i.e. towards the enemy’s side of the board) whereas King’s can move in both directions.
  * The player with the black pieces takes the first turn.
  * Each turn consists of either:
    * Moving a piece to an unoccupied diagonally adjacent square in a permitted direction.
    * If a diagonally adjacent square is occupied by an opponent’s piece and the square directly beyond that is unoccupied then you must capture the opponent’s piece by jumping to the unoccupied square and removing the opponent’s piece from the board.
    * If having captured an opponent’s piece, the piece that made the jump is now in a position to capture another of the opponent’s pieces, it must do so by doing a double- or multiple-capture.
  * The players take turns until one player wins by capturing all of their opponent’s pieces.
  * Once a piece reaches the opponent’s end of the board, they are crowned (made a King), which is usually indicated by placing an extra piece on top.

Experimentation with various materials has indicated that the best way for moving the pieces is using an electromagnet attached to a head, which can move in the x-y plane. The board will be made from either acrylic or plywood, as these are quite firm but also thin. The pieces will contain a small magnet, which as well as providing a means of being moved by the electromagnet, will also allow us to use reed switches to detect the presence of pieces.

Research on the online Lego shop (http://shop.lego.com) has revealed that Lego Technics components are only available as kits, and therefore would not be suitable for the sort of custom project we want to build. With the assistance of our demonstrator we are planning instead to use components direct from suppliers such as Farnell and RS and build a custom solution.

There are a few websites giving examples of projects and products similar to this:

  * http://www.ruschess.com/Store/Equipment/drboard.html is a commercial product which detects pieces but does not move them automatically. Although the information given is vague, and unfortunately does not give any details as to how the detection works, it appears that the intended use is for the human player to move the pieces in to the positions show on the computer screen.
  * http://www.vandeveen.nl/Research/Electronic%20Draughts%20Board.htm appears to be a similar product with more implementation details, but unfortunately the poor resolution image and foreign language prevent us from accessing this.
  * http://www.ce.rit.edu/research/projects/2007_fall/Mechanical_Checkers/. This project by Peter Frandina, Raymond Poudrier and Christopher Rouland of the Rochester Institute of Technology very closely resembles what we are trying to achieve. Unfortunately this was not found early enough in the process to influence our design decisions, but it appears that a similar solution to that planned by ourselves, particularly to the mechanical aspect of the problem, was also used by them. One major difference is the sensing technology, where they decided to use photo sensors. We considered this idea in an early project meeting but dismissed it in favour of magnetic switches, as it would require sufficient ambient light which could be blocked by a player leaning over the board, and embedding photosensitive components in every square could end up being expensive and impractical.

# FACILITIES REQUIRED #

As this project will involve a large amount of mechanical/electrical engineering, we will require workshop facilities and the assistance of our demonstrator. As this project is intended to be computer science-focussed, we feel that it should not be an issue if we receive extensive support in this area.

The software will be developed to run on Public Workstation Facility (PWF) machines, which are readily available in the Computer Laboratory and Altera DE2 teaching boards, which group members will still have from the ECAD & Arch labs.

# SPECIFICATION & ACCEPTANCE CRITERIA #

Having considered the design brief and researched the problem generally, we expect our project to be able to meet the following specification, which will act as the acceptance criteria for our project:

Hardware:
  * Pieces belonging to the computer player should move with no visible mechanism.
  * No other pieces should be disturbed as pieces are moved.
  * Once the movement of a piece has started, it should reach its final destination in no longer than 30 seconds.

Game play and AI:
  * The computer player should always follow the above rules of Draughts.
  * Pieces that are captured by the computer player should be automatically removed before
the next jump / turn.
  * There should be no longer than 10 seconds between the human player declaring the end of their move and the computer player starting to move their piece.
  * The system should automatically detect the end of a game, and indicate the winner.
  * The human player should be able to resign the game.
  * Human player’s moves should be sensed and calculated by the computer.

Interface:
  * There should be a facility for the human player to declare the end of their turn.
  * The computer player should indicate when it has finished its turn.

There are also additional features that could easily be incorporated if time permitted:
  * Pieces should be crowned automatically, possibly by exchanging with a pre-crowned piece placed on the edge of the board at the start of the game.
  * The maximum time permitted for the AI to calculate could be user-settable as a primitive difficulty setting.
  * The human player should be able to specify whether they wish to play as black or white, or whether it should be chosen at random.
  * At the end of the game, if the captured pieces were stored at the edge of the board, the computer could automatically reset them to their required positions.