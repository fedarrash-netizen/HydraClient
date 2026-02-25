package win.winlocker.utils.misc;

/**
 * Утилита для работы с таймерами
 */
public class StopWatch {
    private long startTime;
    private long delay;
    private boolean running;

    public StopWatch() {
        this.startTime = System.currentTimeMillis();
        this.delay = 0;
        this.running = true;
    }

    public StopWatch(long delay) {
        this.startTime = System.currentTimeMillis();
        this.delay = delay;
        this.running = true;
    }

    /**
     * Проверить, прошло ли указанное время
     */
    public static boolean finished(long delay) {
        return System.currentTimeMillis() - getInstance().startTime >= delay;
    }

    /**
     * Проверить, прошло ли указанное время, и сбросить таймер
     */
    public static boolean reset(long delay) {
        StopWatch instance = getInstance();
        if (System.currentTimeMillis() - instance.startTime >= delay) {
            instance.startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Получить время, прошедшее с запуска
     */
    public static long getTime() {
        return System.currentTimeMillis() - getInstance().startTime;
    }

    /**
     * Сбросить таймер
     */
    public static void reset() {
        getInstance().startTime = System.currentTimeMillis();
    }

    /**
     * Установить задержку
     */
    public static void setDelay(long delay) {
        getInstance().delay = delay;
    }

    /**
     * Проверить, прошёл ли таймер
     */
    public boolean hasReached(long delay) {
        return System.currentTimeMillis() - startTime >= delay;
    }

    /**
     * Проверить, прошёл ли таймер с установленной задержкой
     */
    public boolean hasReached() {
        return System.currentTimeMillis() - startTime >= delay;
    }

    /**
     * Получить оставшееся время
     */
    public long getRemainingTime(long delay) {
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, delay - elapsed);
    }

    /**
     * Получить прогресс (0-1)
     */
    public float getProgress(long delay) {
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(1.0f, (float) elapsed / delay);
    }

    /**
     * Запустить таймер
     */
    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    /**
     * Остановить таймер
     */
    public void stop() {
        running = false;
    }

    /**
     * Проверить, запущен ли таймер
     */
    public boolean isRunning() {
        return running;
    }

    // Singleton
    private static StopWatch instance;

    public static StopWatch getInstance() {
        if (instance == null) {
            instance = new StopWatch();
        }
        return instance;
    }
}
