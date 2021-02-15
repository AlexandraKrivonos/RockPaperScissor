package com.company;

public class Main {
   public static void main(String[] args) {
       RockPaperScissors newGame;
       try {
           newGame = new RockPaperScissors(args);
           newGame.startGame();
       } catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
       }
   }
}
