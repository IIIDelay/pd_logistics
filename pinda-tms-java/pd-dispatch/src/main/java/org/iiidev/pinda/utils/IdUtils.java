package org.iiidev.pinda.utils;

import org.iiidev.pinda.common.utils.IdWorker;

public class IdUtils {
    private static final IdWorker ID_WORKER = new IdWorker();

    public static String get() {
        return String.valueOf(ID_WORKER.nextId());
    }
}
