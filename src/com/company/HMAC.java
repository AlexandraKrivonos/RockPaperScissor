package com.company;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

class HMAC {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int j = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[j >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[j & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}

class RockPaperScissors {
    private final ArrayList<String> moves;
    private final Mac mac;
    private final String secretKey;
    public RockPaperScissors(String[] moves) throws NoSuchAlgorithmException, InvalidKeyException {
        if (moves == null) {
            throw new IllegalArgumentException("The argument mustn't be null");
        }

        if (moves.length % 2 == 0) {
            throw new IllegalArgumentException("The argument must be of odd length");
        }

        if (hasDuplicates(Arrays.asList(moves))) {
            throw new IllegalArgumentException("The argument mustn't contain duplicates");
        }
        this.moves = new ArrayList<>(Arrays.asList(moves));
        mac = Mac.getInstance("HmacSHA256");
        byte[] key = generateSecretKey();
        secretKey = HMAC.bytesToHex(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
    }

    private boolean hasDuplicates(List<String> moves) {
        HashSet<String> movesSet = new HashSet<>(moves);
        return movesSet.size() < moves.size();
    }

    private byte[] generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        return key;
    }

    public void startGame() {
        showMovesMenu();
        int computerMove = makeComputerMove();
        System.out.println("HMAC: " + getMoveHMAC(moves.get(makeComputerMove())));
        int userMove = makeUserMove();
        System.out.println("Your move: " + moves.get(userMove));
        System.out.println("Computer move: " + moves.get(computerMove));
        printResult(userMove, computerMove);
        System.out.println("HMAC Key: " + secretKey);
    }



    private int makeUserMove() {
        System.out.println("Enter your move: ");
        Scanner scanner = new Scanner(System.in);
        int move = scanner.nextInt();
        move--;
        if (move > moves.size() - 1 || move < 0) {
            throw new IndexOutOfBoundsException("Illegal move!");
        }
        return move;
    }

    private int makeComputerMove() {
        Random random = new Random();
        int move = random.nextInt(moves.size());
      //  System.out.println("HMAC: " + getMoveHMAC(moves.get(move)));
        return move;
    }

    private String getMoveHMAC(String move) {
        return HMAC.bytesToHex(mac.doFinal(move.getBytes()));
    }
    private void showMovesMenu() {
        System.out.println("Available moves: ");

        for (int i = 0; i < moves.size(); i++) {
            System.out.println((i+1) + " - " + moves.get(i));
        }
    }

    private void printResult(int p, int c) {
        int diff = c - p;

        if (diff == 0) {
            System.out.println("It's a draw!");
            return;
        }

        if ((diff > 0 && diff % 2 == 0) ||
                (diff < 0) && diff % 2 != 0) {
            System.out.println("You win!");
        } else {
            System.out.println("Computer wins!");
        }
    }
}