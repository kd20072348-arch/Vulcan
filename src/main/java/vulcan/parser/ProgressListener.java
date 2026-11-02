package vulcan.parser;

/**
 * Interface #1: callback used by long-running work (parsing, analysis) to report progress.
 * It is called from background threads, so implementers must hand UI work to the Swing thread.
 */
public interface ProgressListener {
    void onProgress(int percent, String message);

    /** A listener that ignores everything (handy for tests). */
    ProgressListener NONE = (percent, message) -> { };
}
