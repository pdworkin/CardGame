# CardGame
This is a weekend hack that started from sketching up a data structure for a deck of cards.  It ended up as a game among N computer players with a simple AI and with the player agents able to connect in remotely via sockets. Also a Swing GUI that monitors the play and keeps track of score.

The code is not pretty.  It should be refactored into a MVC model.

## To Run
Precompiled jar files are included in the repository so you can just do:
```
java -jar target/server.jar & java -jar target/clients.jar
```

If you want to remake from source do:
```
mvn clean; mvn package
```

After quitting, you will have to ^C to kill the client process whose agents are still waiting for another game.

## Operation
When the GUI appears, you may select the score needed to win, the number of players and the number of cards for each player.  Then press the Resume button to play the game.  At he end of the game, it will pause again so you can adjust your selections.
rr
Play consists of each player playing a card in turn and the trick is taken by whichever card is highest in rank.  That player then leads the next card.

When hands are exhausted, the player with the most tricks is the winner of that round and gains one point. The lead moves to the next player and cards are shuffled and re-dealt. A player wins the game when they have reached winngingScore.

The current AI is very simple: If a player can beat the highest card already played, then play their highest card.  Otherwise play their lowest. The AI is in <code>clients/ClientTask.selectCard()</code>.
