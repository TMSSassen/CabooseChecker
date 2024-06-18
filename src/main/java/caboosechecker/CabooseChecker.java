
package caboosechecker;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final BigInteger size;
    private final BigInteger mod;
    private final long start;

    private CabooseChecker(PrimeChecker cache, int size, int mod) {
        this.cache = cache;
        this.size = BigInteger.valueOf(size);
        this.mod = BigInteger.valueOf(mod);
        start = Instant.now().getEpochSecond();
    }

    public static void main(String[] args) {
        final PrimeChecker c = new PrimeChecker();
        final int threads = 10;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 1; i <= threads; i++) {
            pool.submit(new CabooseChecker(c, threads, i));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(CabooseChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void print(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }
    private final BigInteger zero = BigInteger.valueOf(0);
    private final BigInteger one = BigInteger.valueOf(1);
    private final BigInteger period = BigInteger.valueOf(100_000);
    private final BigInteger secondsPerHour=BigInteger.valueOf(3600);

    @Override
    public void run() {
        BigInteger iterations = BigInteger.valueOf(-1);
        CHECK_NEXT_NUMBER_LOOP: while (true) {
            iterations = iterations.add(one);
            final BigInteger number = iterations.multiply(size).add(mod);
            if (number.mod(period).equals(zero)) {
                BigInteger diffInSeconds = BigInteger.valueOf(Instant.now().getEpochSecond() - start);
                if(!diffInSeconds.equals(zero)){
                    BigInteger speedInHour = number.divide(diffInSeconds).multiply( secondsPerHour );
                    print("Reached " + number + ", speed " + speedInHour + " numbers checked / h.");
                }
            }
            BigInteger i = one;
            while (!i.equals(number)) {
                final BigInteger calc = i.multiply(i).subtract(i).add(number);
                if (!cache.isPrime(calc)) {
                    continue CHECK_NEXT_NUMBER_LOOP;
                }
                i = i.add(one);
            }
            print(number + " is a caboose number!");
        }
    }
}

class PrimeChecker {

    private final Map<BigInteger, Boolean> checkedPrimes = Collections.synchronizedMap(new HashMap());
    private final BigInteger zero = BigInteger.valueOf(0);
    private final BigInteger two = BigInteger.valueOf(2);
    private final BigInteger three = BigInteger.valueOf(3);
    private final BigInteger five = BigInteger.valueOf(5);
    private final BigInteger six = BigInteger.valueOf(6);

    public PrimeChecker() {
        checkedPrimes.put(two, true);
        checkedPrimes.put(three, true);
    }

    public boolean isPrime(BigInteger n) {
        final Boolean cached = checkedPrimes.get(n);
        if (cached != null) {
            return cached;
        }
        final boolean isPrime = checkPrime(n);
        checkedPrimes.put(n, isPrime);
        return isPrime;
    }

    /**
     * Prime check gotten from
     * https://www.geeksforgeeks.org/java-prime-number-program/
     */
    private boolean checkPrime(BigInteger n) {

        if (n.mod(two).equals(zero) || n.mod(three).equals(zero)) {
            return false;
        }
        BigInteger i = five;
        final BigInteger end = n.sqrt();
        while (i.compareTo(end) < 0) {
            if (n.mod(i).equals(zero) || n.mod(i.add(two)).equals(zero)) {
                return false;
            }
            i = i.add(six);
        }

        return true;
    }

}
