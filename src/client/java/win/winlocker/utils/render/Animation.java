package win.winlocker.utils.render;

import win.winlocker.utils.render.AnimationUtils.Easing;

/**
 * Класс для управления плавными анимациями значений
 */
public class Animation {
    private float currentValue;
    private float targetValue;
    private float startValue;
    private long startTime;
    private long duration;
    private Easing easing;
    private boolean isComplete;

    public Animation(float initialValue) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.startValue = initialValue;
        this.duration = 300;
        this.easing = AnimationUtils.EASE_OUT_QUAD;
        this.isComplete = true;
    }

    public Animation(float initialValue, long duration, Easing easing) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.startValue = initialValue;
        this.duration = duration;
        this.easing = easing;
        this.isComplete = true;
    }

    /**
     * Установить новое целевое значение для анимации
     */
    public void setTarget(float target) {
        if (this.targetValue != target) {
            this.startValue = this.currentValue;
            this.targetValue = target;
            this.startTime = System.currentTimeMillis();
            this.isComplete = false;
        }
    }

    /**
     * Установить новое целевое значение с кастомной длительностью
     */
    public void setTarget(float target, long duration) {
        this.duration = duration;
        setTarget(target);
    }

    /**
     * Установить новое целевое значение с кастомной длительностью и easing
     */
    public void setTarget(float target, long duration, Easing easing) {
        this.duration = duration;
        this.easing = easing;
        setTarget(target);
    }

    /**
     * Обновить текущее значение анимации
     */
    public void update() {
        if (isComplete || startValue == targetValue) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsed / duration);

        if (progress >= 1.0f) {
            this.currentValue = targetValue;
            this.isComplete = true;
        } else {
            float easedProgress = easing.apply(progress);
            this.currentValue = startValue + (targetValue - startValue) * easedProgress;
        }
    }

    /**
     * Мгновенно установить значение без анимации
     */
    public void setValue(float value) {
        this.currentValue = value;
        this.targetValue = value;
        this.startValue = value;
        this.isComplete = true;
    }

    /**
     * Получить текущее значение
     */
    public float getValue() {
        return currentValue;
    }

    /**
     * Получить целевое значение
     */
    public float getTarget() {
        return targetValue;
    }

    /**
     * Получить начальное значение
     */
    public float getStart() {
        return startValue;
    }

    /**
     * Проверить, завершена ли анимация
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Проверить, находится ли анимация в процессе выполнения
     */
    public boolean isRunning() {
        return !isComplete && startValue != targetValue;
    }

    /**
     * Получить длительность анимации
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Установить длительность анимации
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Получить текущий easing
     */
    public Easing getEasing() {
        return easing;
    }

    /**
     * Установить easing функцию
     */
    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    /**
     * Сбросить анимацию к начальному значению
     */
    public void reset() {
        this.currentValue = startValue;
        this.targetValue = startValue;
        this.isComplete = true;
    }

    /**
     * Интерполировать значение между двумя точками с использованием easing
     */
    public static float lerp(float start, float end, float progress, Easing easing) {
        float easedProgress = easing.apply(progress);
        return start + (end - start) * easedProgress;
    }

    /**
     * Интерполировать значение между двумя точками (линейно)
     */
    public static float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }
}
