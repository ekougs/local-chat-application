package me.chat.server.tasks.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * User: sennen
 * Date: 22/07/2014
 * Time: 13:30
 */
public class Concurrencies {
    private static final Runnable DO_NOTHING = () -> {
    };
    private static final Consumer<Exception> DO_NOTHING_CONS = (e) -> {
    };

    public static InterruptableRunnableBuilder buildInterruptionReadyRun(InterruptableRunnable runnable) {
        return new InterruptableRunnableBuilder(runnable);
    }

    public static <T> InterruptableCallableBuilder<T> buildInterruptionReadyCall(InterruptableCallable<T> callable) {
        return new InterruptableCallableBuilder<>(callable);
    }

    public static class InterruptableRunnableBuilder {
        private final InterruptableRunnable mainExecution;
        private Runnable whenInterruption = DO_NOTHING;

        private InterruptableRunnableBuilder(InterruptableRunnable mainExecution) {
            this.mainExecution = mainExecution;
        }

        public InterruptableRunnableBuilder whenInterruption(Runnable whenInterruption) {
            this.whenInterruption = whenInterruption;
            return this;
        }

        public void run() {
            try {
                mainExecution.run();
            } catch (InterruptedException e) {
                whenInterruption.run();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class InterruptableCallableBuilder<T> {
        private static final Logger LOGGER = LoggerFactory.getLogger(InterruptableCallableBuilder.class);

        private final InterruptableCallable<T> mainExecution;
        private Runnable whenInterruption = DO_NOTHING;
        private Consumer<Exception> whenExecutionException = DO_NOTHING_CONS;

        private InterruptableCallableBuilder(InterruptableCallable<T> mainExecution) {
            this.mainExecution = mainExecution;
        }

        public InterruptableCallableBuilder<T> whenInterruption(Runnable whenInterruption) {
            this.whenInterruption = whenInterruption;
            return this;
        }

        public InterruptableCallableBuilder<T> whenExecutionException(Consumer<Exception> whenExecutionException) {
            this.whenExecutionException = whenExecutionException;
            return this;
        }

        public Optional<T> call() {
            try {
                return Optional.of(mainExecution.call());
            } catch (InterruptedException e) {
                whenInterruption.run();
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                Throwable actualException = e.getCause();
                if (actualException instanceof Exception) {
                    whenExecutionException.accept((Exception) actualException);
                } else {
                    LOGGER.error("Error during call.", e);
                    throw new RuntimeException(actualException);
                }
            }
            return Optional.empty();
        }
    }

}
