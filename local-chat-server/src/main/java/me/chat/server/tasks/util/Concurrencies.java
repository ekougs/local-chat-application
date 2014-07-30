package me.chat.server.tasks.util;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * User: sennen
 * Date: 22/07/2014
 * Time: 13:30
 */
public class Concurrencies {
    private static final Runnable DO_NOTHING = () -> {
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
        private final InterruptableCallable<T> mainExecution;
        private Runnable whenInterruption = DO_NOTHING;
        private Runnable whenExecutionException = DO_NOTHING;

        private InterruptableCallableBuilder(InterruptableCallable<T> mainExecution) {
            this.mainExecution = mainExecution;
        }

        public InterruptableCallableBuilder<T> whenInterruption(Runnable whenInterruption) {
            this.whenInterruption = whenInterruption;
            return this;
        }

        public InterruptableCallableBuilder<T> whenExecutionException(Runnable whenExecutionException) {
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
                whenExecutionException.run();
            }
            return Optional.empty();
        }
    }

}
