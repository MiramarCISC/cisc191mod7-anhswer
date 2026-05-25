package edu.sdccd.cisc191.server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks server-wide match statistics shared by many gRPC request threads.
 */
public class MatchStatistics {

    private final AtomicInteger joinedMatchCount = new AtomicInteger(0);
    private final AtomicInteger completedMatchCount = new AtomicInteger(0);

    public void recordJoin() {
        joinedMatchCount.incrementAndGet();
    }

    public void recordCompletion() {
        completedMatchCount.incrementAndGet();
    }

    public int getJoinedMatchCount() {
        return joinedMatchCount.get();
    }

    public int getCompletedMatchCount() {
        return completedMatchCount.get();
    }

    /**
     * Return a readable, thread-safe statistics summary.
     * Expected format:
     * Server stats: 3 joined, 2 completed
     */
    public String buildStatusLine() {
        return String.format(
                "Server stats: %d joined, %d completed",
                joinedMatchCount.get(),
                completedMatchCount.get()
        );
    }
}