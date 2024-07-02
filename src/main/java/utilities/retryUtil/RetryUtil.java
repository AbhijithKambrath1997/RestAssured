package utilities.retryUtil;

import java.util.function.Supplier;

public class RetryUtil {

    public static <T> T retry(Supplier<T> action, int maxRetries, long waitTime, String errorMessage) {
        int retries = 0;
        while (true) {
            try {
                return action.get();
            } catch (Exception e) {
                retries++;
                if (retries >= maxRetries) {
                    throw new RuntimeException(errorMessage, e);
                }
                try {
                    synchronized (RetryUtil.class) {
                        RetryUtil.class.wait(waitTime);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Preserve the interrupt status
                    throw new RuntimeException("Interrupted while waiting for action", ex);
                }
            }
        }
    }

    public static <T> T retry(Supplier<T> action, int maxRetries, long waitTime) {
        return retry(action, maxRetries, waitTime, "");
    }

    public static <T> T retry(Supplier<T> action, int maxRetries) {
        return retry(action, maxRetries, 1000);
    }

    public static <T> T retry(Supplier<T> action) {
        return retry(action, 30);
    }
}
