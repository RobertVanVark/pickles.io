package nl.devon;

/**
 * Created by harm on 20-12-2016.
 */
public class StubDVLoadHook {
    private static int nrTimesCalled;

    public static void reset() {
        nrTimesCalled = 0;
    }

    public static int getNrTimesCalled() {
        return nrTimesCalled;
    }
}
