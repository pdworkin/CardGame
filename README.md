# CardGame
This is a weekend hack that started from sketching up a data structure for a deck of cards.  It ended up as a game among n computer players with a simple AI and with the agents able to connect in remotely via sockets. Also a Swing GUI that monitors the play and keeps track of score.

## To Run
Precompiled jar files are included in the repository so you can just do:<code>
java -jar target/server.jar & java -jar target/clients.jar</code>

If you want to remake from source do:<code>
mvn clean; mvn package</code>

After quitting, you will have to ^C to kill the client process still running.

## Operation
When the GUI appears, you may select the score needed to win, the number of players and the number of cards for each player.  Then press the Start button to play the game.  At he end of the game, it will pause again so you can adjust your selections.

Play consists of each player playing a card in turn and the trick is taken by whichever card is highest in rank.  That player then leads the next card.

When hands are exhausted, the player with the most tricks is the winner of that round and gains one point. The lead moves to the next player and cards are shuffled and re-dealt. A player wins the game when they have reached winngingScore.

The current AI is very simple: If a player can beat the highest card already played, then play their highest card.  Otherwise play their lowest. The AI is in clients/ClientTask.selectCard().
