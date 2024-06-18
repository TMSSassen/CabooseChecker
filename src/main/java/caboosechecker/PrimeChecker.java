package caboosechecker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tom
 */
public class PrimeChecker {

    private final Map<Long, Boolean> checkedPrimes = Collections.synchronizedMap(new HashMap());

    public PrimeChecker() {
        checkedPrimes.put(2l, true);
        checkedPrimes.put(3l, true);
    }

    public boolean isPrime(long n) {
        Boolean cached = checkedPrimes.get(n);
        if (cached != null) {
            return cached;
        }
        boolean isPrime = checkPrime(n);
        checkedPrimes.put(n, isPrime);
        return isPrime;
    }
    
    /**
     * Prime check gotten from https://www.geeksforgeeks.org/java-prime-number-program/
     */

    private boolean checkPrime(long n) {

        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }

        for (int i = 5; i <= Math.sqrt(n); i = i + 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

}
