## Classes ##

**`Draughts`** - controls the flow of the game.
  * The main method will receive signals indicating which players should be `Human` or `AI`, construct that game, and call its `play()` method.
  * While `Board` still has valid moves to be made, it will wait for the current `Player` to return from `doTurn()`. If it returns `false`, this indicates a resignation. Otherwise, it will pass control to `Board`, calling its `applyBoardState()` method.
  * When there are no more valid moves or a player has resigned, the winner will be declared.

**`Board`** - controls the state of the board.
  * When `applyBoardState()` is called, `Board` will retrieve the state of the physical board, attempt to detect the `Turn` taken by the `Player`, and work out how to complete it. This involves a mixture of virtual `Move`s (modifying its own internal data structures) and physical `Move`s.
  * When these are finalised, they will be put into a `PendingChanges` object, whose `execute()` method will then be called. After this is complete, `Board` should then be in a fully correct and consistent state ready for the next turn, and control will be returned back to `Draughts`.

**`Board$PendingChanges`** - represents the changes pending to the board state.
  * Upon `execute()`, its virtual `Move`s will be enacted by `Board`, and its physical `Move`s will be passed to `Pathing`.

**`Player`** - represents a player.
  * This must define a `doTurn()` method that returns a boolean, signalling resignation.
  * The `doTurn()` method must also result in the piece on the `src` square being **physically moved** to the `dst` square. This will later be detected by `Board`, which will then process it as described above.

**`HumanPlayer`** - `doTurn()` returns when it has received a signal from the hardware indcating that the player has completed their turn, or resigned.<br />
**`AIPlayer`** - `doTurn()` returns after it has physically executed the `src`-`dst` part of a `Turn` returned by the `AI`.<br />

**`Piece`** - a class containing immutable `id`, `side`, `king` fields, and one mutable field `pos`.<br />
**`Turn`** - a class containing immutable `src`, `dst` fields, and an immutable `capt[]` array.<br />
**`Move`** - a class containing immutable `src`, `dst` fields.<br />

**`Pathing`** - work out the path that needs to be taken by a `Move`, and tell `HWInterface` to execute this.

**`HWInterface`** - interface between HW/SW
  * retrieve what type of game to start (black:`Human` vs white:`AI`, or whatever).
  * retrieve the current state of the board as a `boolean[256]`
  * retrieve whether a human player has finished their turn, or resigned.
  * signal that the board is in a state of error
  * signal a move to be made by the magnets and motors

## Internal Board representation ##

```
+---+---+---+---+---+---+---+---+---+---+
|152 144 145 146 147 148 149 150 151 153| 9
+   +---+---+---+---+---+---+---+---+   +
|120|112|113|114|115|116|117|118|119|121| 7
+   +---+---+---+---+---+---+---+---+   +
|104|96 |97 |98 |99 |100|101|102|103|105| 6
+   +---+---+---+---+---+---+---+---+   +
|88 |80 |81 |82 |83 |84 |85 |86 |87 |89 | 5
+   +---+---+---+---+---+---+---+---+   +
|72 |64 |65 |66 |67 |68 |69 |70 |71 |73 | 4
+   +---+---+---+---+---+---+---+---+   +
|56 |48 |49 |50 |51 |52 |53 |54 |55 |57 | 3
+   +---+---+---+---+---+---+---+---+   +
|40 |32 |33 |34 |35 |36 |37 |38 |39 |41 | 2
+   +---+---+---+---+---+---+---+---+   +
|24 |16 |17 |18 |19 |20 |21 |22 |23 |25 | 1
+   +---+---+---+---+---+---+---+---+   +
| 8 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 9 | 0
+   +---+---+---+---+---+---+---+---+   +
|136 128 129 130 131 132 133 134 135 137| 8
+---+---+---+---+---+---+---+---+---+---+
  8   0   1   2   3   4   5   6   7   9 x\y

pos = |yyyy|xxxx|
    = (y<<4) | x
```

This is arranged more logically than the clockwise-round-the-outside method, since the representation of every cell, including reserve cells, can be easily split into its x, y co-ordinates. This also means that we can do simple bitwise arithmetic to test for various properties of a given cell, eg:

```
	public static boolean inPlay(byte pos) {
		return (pos & 0x88) == 0;
	}
```

Further tests can be seen in source code of the `Board` class.

As a point of interest, if we were to extend the board, the most natural extension would be:

```
  |14 |12 |10 | 8 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 9 |11 |13 |15 |
```

since then the sideOf method is just:

```
	public static boolean sideOf(byte pos) {
		// black: 00?????? or 1??0????; white: 01?????? or 1??1????
		return (pos & 0xC0) == 0x40 || (pos & 0x90) == 0x90;
	}
```