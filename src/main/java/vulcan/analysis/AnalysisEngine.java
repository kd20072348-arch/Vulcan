package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;
import vulcan.parser.ProgressListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs every Analyzer in parallel on a thread pool.
 * (Review 2 - Threads: ExecutorService, CountDownLatch, AtomicInteger, ConcurrentHashMap)
 */
public class AnalysisEngine {
    private final List<Analyzer> analyzers;

    public AnalysisEngine(List<Analyzer> analyzers) {
        this.analyzers = List.copyOf(analyzers);
    }

    /** Blocks until all analyzers finish, so call it from a background thread - never from the Swing thread. */
    public List<AnalysisResult> runAll(Dataset data, ProgressListener listener) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(analyzers.size());
        Map<String, AnalysisResult> results = new ConcurrentHashMap<>();   // written by many threads
        AtomicInteger finished = new AtomicInteger();                      // thread-safe counter
        CountDownLatch latch = new CountDownLatch(analyzers.size());

        for (Analyzer a : analyzers) {
            pool.submit(() -> {
                try {
                    results.put(a.name(), a.analyze(data));
                } catch (RuntimeException ex) {
                    results.put(a.name(), new AnalysisResult(a.name(), "Failed: " + ex.getMessage(), 0));
                } finally {
                    int done = finished.incrementAndGet();
                    listener.onProgress(done * 100 / analyzers.size(),
                            a.name() + " finished (" + done + "/" + analyzers.size() + ") on "
                                    + Thread.currentThread().getName());
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } finally {
            pool.shutdown();
        }

        List<AnalysisResult> ordered = new ArrayList<>();                  // keep the original analyzer order
        for (Analyzer a : analyzers) ordered.add(results.get(a.name()));
        return ordered;
    }
}
