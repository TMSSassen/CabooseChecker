/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package caboosechecker;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Tom
 */
public class CabooseChecker implements Runnable {

    private final PrimeChecker cache;
    private final int size;
    private final int mod;
    private final long start;

    public CabooseChecker(PrimeChecker cache, int size,int mod) {
        this.cache = cache;
        this.size = size;
        this.mod = mod;
        start= Instant.now().getEpochSecond();
    }

    public static void main(String[] args) {
        PrimeChecker c = new PrimeChecker();
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 1; i <= threads; i++) {
            pool.submit(new CabooseChecker(c, threads,i));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(CabooseChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void print(String s){
        synchronized(System.out){
            System.out.println(s);
        }
    }

    @Override
    public void run() {
        int iterations=-1;
        NEXT_NUMBER:while(true){
            iterations++;
            int number=iterations * size + mod;
            if(number % 100_000 == 0){
                double diffInHour = (Instant.now().getEpochSecond() - start) / 60.0 / 60.0;
                print("Reached "+number+", speed "+(number/diffInHour)+" numbers checked / hour.");
            }
            for(long i=1;i<number;i++){
                long calc = i*i -i + number;
                if(!cache.isPrime(calc)){
                    continue NEXT_NUMBER;
                }
            }
            print(number+" is a caboose number!");
        }
    }
}
