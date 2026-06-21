# Wordle Java Game

This project is a Java implementation of the popular Wordle game.
The game uses a Russian dictionary of five-letter nouns. The player has six attempts to guess the hidden word.

The project also includes a simple HTTP server for storing players' win statistics and showing the top players.

## Features

* Console-based Wordle game
* Russian five-letter word dictionary
* Word normalization:

  * lower case conversion
  * replacing `ё` with `е`
* Input validation:

  * word length check
  * Russian letters only
  * dictionary check
* Wordle hint system:

  * `+` means the letter is correct and in the correct position
  * `^` means the letter exists in the word but is in the wrong position
  * `-` means the letter is not in the hidden word
* Computer-generated hints
* Game history tracking
* Custom exceptions for game and system errors
* Logging to a file
* HTTP server for player statistics
* JSON-based saving and loading of statistics
* Unit tests for the main classes

## Project Structure

The main classes are:

* `Wordle` — main console application
* `WordleGame` — game logic and game state
* `WordleDictionary` — dictionary storage, filtering and normalization
* `WordleDictionaryLoader` — loading words from a file
* `WordleServer` — HTTP server for statistics
* `WordleServerStatisticLoader` — saving and loading statistics
* `WordleStatisticsClient` — client for sending results to the server
* `PlayerResult` — player result model
* `GuessAttempt` — stores one player attempt and its hint

The project also contains custom exceptions for invalid input, dictionary errors and game situations.

## How to Run the Game

1. Make sure Java is installed.
2. Open the project in IntelliJ IDEA.
3. Make sure the dictionary file is located in the project root.
4. Run the `Wordle` class.

The game will ask the player to enter a five-letter Russian word.

If the player enters an empty line, the game will suggest a possible word as a hint.

## How to Run the Statistics Server

Run the `WordleServer` class separately.

The server starts on port `8080`.

It supports two endpoints:

### POST `/statistics`

Saves a player's win result.

Example JSON:

```json
{
  "nickname": "player",
  "steps": 4,
  "usedHints": false
}
```

### GET `/statistics`

Returns the top players by number of wins.

Example response:

```json
[
  {
    "nickname": "player",
    "wins": 2
  }
]
```

## How the Game and Server Work Together

1. Start `WordleServer`.
2. Start `Wordle`.
3. Win the game.
4. Enter a nickname.
5. The game sends the result to the server.
6. The server saves the result to `statistics.json`.
7. The game requests and displays the top players.

## Tests

The project contains unit tests for the main functionality:

* dictionary normalization
* dictionary filtering
* input validation
* Wordle hint generation
* game finishing logic
* player result JSON conversion
* statistics saving and loading

Tests can be run from IntelliJ IDEA.

## Logging

System messages and errors are written to log files instead of being printed directly to the console.

Generated files such as logs and statistics are ignored by Git.

## Technologies

* Java
* JUnit 5
* Java HTTP Server
* Java HTTP Client
* JSON format for statistics storage
