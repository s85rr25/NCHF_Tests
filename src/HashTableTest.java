import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

public class HashTableTest {
    private static FNV1aHashTable<Object> fnvHashTable;
    private static MurmurHashTable<Object> murmurHashTable;
    private Stack<String> englishWords;
    private Stack<String> passwords;
    private Stack<String> news;

    private void printFNVCollisions(){
        int collisions = fnvHashTable.getCollision();
        int numBuckets = fnvHashTable.getSize();
        System.out.println("Number of collisions: " + collisions);
        System.out.println("Number of buckets: " + numBuckets);
        System.out.println("Collision Rate: " + collisions*1.0/numBuckets);
    }

    private void printMurmurCollisions(){
        int collisions = murmurHashTable.getCollision();
        int numBuckets = murmurHashTable.getSize();
        System.out.println("Number of collisions: " + collisions);
        System.out.println("Number of buckets: " + numBuckets);
        System.out.println("Collision Rate: " + collisions*1.0/numBuckets);
    }

    @TestFactory
    static void reset(int size){
        fnvHashTable = new FNV1aHashTable<>(size);
        murmurHashTable = new MurmurHashTable<>(size);
    }

    @TestFactory
    void resetDictionary(){
        try (BufferedReader reader = new BufferedReader(new FileReader("src/words.txt"))) {
            englishWords = new Stack<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!englishWords.contains(line)){
                    englishWords.push(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset(englishWords.size());
    }

    @TestFactory
    void resetPasswords(){
        try (BufferedReader reader = new BufferedReader(new FileReader("src/passwords.txt"))) {
            passwords = new Stack<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!passwords.contains(line)){
                    passwords.push(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset(passwords.size());
    }

    @TestFactory
    void resetNews(){
        try (BufferedReader reader = new BufferedReader(new FileReader("src/bbc_news.csv"))) {
            news = new Stack<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if(!news.contains(line)){
                    news.push(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reset(news.size());
    }

    @TestFactory
    static String createRandomString(int size){
        return new Random().ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(size)
                .mapToObj(i -> String.valueOf((char)i))
                .collect(Collectors.joining());
    }

    @TestFactory
    void collisionResistanceDistributionFile(String dataset, int[] distribution){
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("distributionOf" + dataset + ".txt");
            for (int i: distribution) {
                myWriter.write("" + i + "\n");
            }
            for (int i = 0; i < 100; i++){
                myWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void collisionResistanceDictionaryTest(){
        resetDictionary();
        System.out.println("FNV-1a: ");
        while (!englishWords.isEmpty()) {
            fnvHashTable.add(englishWords.pop());
        }
        printFNVCollisions();
        collisionResistanceDistributionFile("DictionaryFNV", fnvHashTable.getDistribution());

        resetDictionary();
        System.out.println("MurmurHash3: ");
        while (!englishWords.isEmpty()) {
            murmurHashTable.add(englishWords.pop());
        }
        printMurmurCollisions();
        collisionResistanceDistributionFile("DictionaryMurmur", murmurHashTable.getDistribution());
    }

    @Test
    void collisionResistancePasswordsTest(){
        resetPasswords();
        System.out.println("FNV-1a: ");
        while (!passwords.isEmpty()) {
            fnvHashTable.add(passwords.pop());
        }
        printFNVCollisions();
        collisionResistanceDistributionFile("PasswordsFNV", fnvHashTable.getDistribution());

        resetPasswords();
        System.out.println("MurmurHash3: ");
        while (!passwords.isEmpty()) {
            murmurHashTable.add(passwords.pop());
        }
        printMurmurCollisions();
        collisionResistanceDistributionFile("PasswordsMurmur", murmurHashTable.getDistribution());
    }

    @Test
    void collisionResistanceBBCNewsTest(){
        resetNews();
        System.out.println("FNV-1a: ");
        while (!news.isEmpty()) {
            fnvHashTable.add(news.pop());
        }
        printFNVCollisions();
        collisionResistanceDistributionFile("BBCNewsFNV", fnvHashTable.getDistribution());

        resetNews();
        System.out.println("MurmurHash3: ");
        while (!news.isEmpty()) {
            murmurHashTable.add(news.pop());
        }
        printMurmurCollisions();
        collisionResistanceDistributionFile("BBCNewsMurmur", murmurHashTable.getDistribution());
    }

    @Test
    void hashingTimeComplexityTest() {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("hashingTimeComplexity.txt");
            for(int i = 0; i < 6; i++){
                for (int j = 100000000; j < 400000000; j += 30000000) {
                    String randomString = createRandomString(j);
                    long startTime = System.nanoTime();
                    FNV1a.fnv1aHash(randomString);
                    long endTime = System.nanoTime();
                    myWriter.write(j + ": \n" + (endTime - startTime) + " nanoseconds\n");

                    startTime = System.nanoTime();
                    MurmurHash3.murmurHash(randomString);
                    endTime = System.nanoTime();
                    myWriter.write((endTime - startTime) + " nanoseconds\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertTimeComplexityTest() {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("insertionTimeComplexity.txt");
            for(int i = 0; i < 6; i++){
                for (int j = 100000000; j < 400000000; j += 30000000) {
                    reset(10);
                    String randomString = createRandomString(j);
                    long startTime = System.nanoTime();
                    fnvHashTable.add(randomString);
                    long endTime = System.nanoTime();
                    myWriter.write(j + ": \n" + (endTime - startTime) + " nanoseconds\n");

                    startTime = System.nanoTime();
                    murmurHashTable.add(randomString);
                    endTime = System.nanoTime();
                    myWriter.write((endTime - startTime) + " nanoseconds\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void containsTimeComplexityTest() {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("containsTimeComplexity.txt");
            for(int i = 0; i < 6; i++){
                for (int j = 100000000; j < 400000000; j += 30000000) {
                    reset(10);
                    String randomString = createRandomString(j);
                    long startTime = System.nanoTime();
                    fnvHashTable.contains(randomString);
                    long endTime = System.nanoTime();
                    myWriter.write(j + ": \n" + (endTime - startTime) + " nanoseconds\n");

                    startTime = System.nanoTime();
                    murmurHashTable.contains(randomString);
                    endTime = System.nanoTime();
                    myWriter.write((endTime - startTime) + " nanoseconds\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeTimeComplexityTest() {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter("removeTimeComplexity.txt");
            for(int i = 0; i < 6; i++){
                for (int j = 100000000; j < 400000000; j += 30000000) {
                    reset(10);
                    String randomString = createRandomString(j);
                    fnvHashTable.add(randomString);
                    murmurHashTable.add(randomString);

                    long startTime = System.nanoTime();
                    fnvHashTable.remove(randomString);
                    long endTime = System.nanoTime();
                    myWriter.write(j + ": \n" + (endTime - startTime) + " nanoseconds\n");

                    startTime = System.nanoTime();
                    murmurHashTable.remove(randomString);
                    endTime = System.nanoTime();
                    myWriter.write((endTime - startTime) + " nanoseconds\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void countDistributions(){
        String[] datasets = {
                "DictionaryFNV", "DictionaryMurmur",
                "PasswordsFNV", "PasswordsMurmur",
                "BBCNewsFNV", "BBCNewsMurmur"
        };
        for (String dataset: datasets) {
            int[] count = new int[10];
            try (Scanner scanner = new Scanner(new File("distributionOf" + dataset + ".txt"))) {
                while (scanner.hasNextLine()) {
                    String num = scanner.nextLine();
                    count[Integer.parseInt(num)]++;
                }
                System.out.println(dataset + ":");
                System.out.println(Arrays.toString(count));
            } catch (FileNotFoundException e) {
                System.out.println("File not found: ");
                e.printStackTrace();
            }
        }
    }

}
