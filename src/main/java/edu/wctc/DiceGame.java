package edu.wctc;

import edu.wctc.Die;
import edu.wctc.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiceGame {
    private	final List<Player> players = new ArrayList<>();
    private	final List<Die> dice = new ArrayList<>();
    private	final int maxRolls;
    private edu.wctc.Player currentPlayer;

    public DiceGame(int players, int dice, int maxRolls) throws IllegalArgumentException {
        if (players < 2) {
            throw new IllegalArgumentException();
        }

        for (int i = players; i > 0; i--){
            this.players.add(new Player());
        }
        for(int i = dice; i > 0; i--){
            this.dice.add(new Die(6));
        }
        this.maxRolls = maxRolls;
    }

    private boolean allDiceHeld(){
        return !dice.stream().map(Die::isBeingHeld).toList().contains(false);
    }

    public boolean autoHold(int faceValue){
//        for (Die die : dice) {
//            if (die.getFaceValue() == faceValue && die.isBeingHeld()){
//                return true;
//            }
//        }
//
//        for (Die die : dice) {
//            if (die.getFaceValue() == faceValue && !die.isBeingHeld()){
//                die.holdDie();
//                return true;
//            }
//        }
//
//        return false;

        if (dice.stream().anyMatch(die -> die.isBeingHeld() && die.getFaceValue() == faceValue)){
            return true;
        } else if (dice.stream().anyMatch(die -> die.getFaceValue() == faceValue)){
            dice.stream().filter(die -> die.getFaceValue() == faceValue).findFirst().ifPresent(Die::holdDie);
            return true;
        } else {
            return false;
        }
    }

    public boolean currentPlayerCanRoll(){
        if (currentPlayer.getRollsUsed() <= maxRolls) {
            return !allDiceHeld();
        } else {
            return false;
        }
    }

    public int getCurrentPlayerNumber(){
        if (currentPlayer == null){
            currentPlayer = players.get(0);
        }
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore(){
        return currentPlayer.getScore();
    }

    public String getDiceResults(){
        return dice.stream().map(Die::toString).collect(Collectors.joining("\n"));
    }

    public String getFinalWinner() {
//        List<Player> winners = new ArrayList<>();
//        winners.add(currentPlayer);
//        //I decided to make this the current player, since not initializing this would've meant a null,
//        //which is okay to deal with, but if I just set it to the current player, when the foreach loop tests the current
//        //player against itself, nothing will happen if the current player is already set to be the current winner
//
//        for (Player player : players) {
//            for (Player winner : winners) {
//                if (player.getWins() > winner.getWins()) {
//                    winners.clear();
//                    winners.add(player);
//                } else if (player.getWins() == winner.getWins()) {
//                    winners.add(player);
//                }
//            }
//        }
//
//        String winnersString = "";
//        if (winners.size() == 2){
//            return winners.get(0).toString() + " & " + winners.get(1).toString();
//        } else {
//            for (Player winner : winners) {
//                winnersString += winner.toString() + ",\n";
//            }
//            return winnersString.substring(0, winnersString.length() - 2);
//        }

        // I'm only leaving the code above in as a reminder to always use streams for stuff like this from now on haha
        // at first when I started writing this I was just running through and completely forgot about them!! although
        // the code above did count for ties if they were to happen
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getWins).reversed())
                .toList()
                .get(0)
                .toString();
    }

    public String getGameResults(){
        players.sort(Comparator.comparingInt(Player::getScore));
        List<Player> highestScoreList = new ArrayList<>();
        highestScoreList.add(currentPlayer);

        for (Player player : players) {
            for (Player player1 : players) {
                if (player.getScore() > player1.getScore()) {
                    highestScoreList.clear();
                    highestScoreList.add(player);
                } else if (player.getScore() == player1.getScore()) {
                    highestScoreList.add(player);
                }
            }
        }

        for (Player player: players) {
            if (highestScoreList.contains(player)){
                player.addWin();
            } else {
                player.addLoss();
            }
        }

        return players.stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .map(Player::toString)
                .collect(Collectors.joining("\n"));
    }

    private boolean isHoldingDie(int faceValue){
//        for (Die die : dice) {
//            if (die.getFaceValue() == faceValue && die.isBeingHeld()){
//                return true;
//            }
//        } return false;

        return dice.stream().anyMatch(die -> die.getFaceValue() == faceValue && die.isBeingHeld());
    }

    public boolean nextPlayer(){
        if (players.indexOf(currentPlayer) == players.size() - 1){
            return false;
        } else {
            currentPlayer = players.get(players.indexOf(currentPlayer) + 1);
            return true;
        }
    }

    public void playerHold(char dieNum){
//        for (Die die : dice) {
//            if (die.getDieNum() == dieNum){
//                die.holdDie();
//                return;
//            }
//        }

        if (dice.stream().anyMatch(die -> die.getDieNum() == dieNum)){
            dice.stream().filter(die -> die.getDieNum() == dieNum).findFirst().ifPresent(Die::holdDie);
        }
    }

    public void resetDice(){
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers(){
        for (Player player : players) {
            player.resetPlayer();
        }
    }

    public void rollDice(){
        scoreCurrentPlayer();
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        int score = 0;


        if (isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4)){
            int[] dieFaceValues = dice.stream().mapToInt(Die::getFaceValue).toArray();

            for (int face : dieFaceValues){
                score += face;
            }

            score -= 15; //Gets rid of ship captain & crew face values
        }

        currentPlayer.setScore(score);
    }

    public void startNewGame(){
        resetPlayers();
    }
}
