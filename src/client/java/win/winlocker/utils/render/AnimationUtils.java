package win.winlocker.utils.render;

/**
 * Утилиты для анимаций и easing функций
 */
public class AnimationUtils {

    /**
     * Интерфейс для easing функций
     */
    @FunctionalInterface
    public interface Easing {
        float apply(float progress);
    }

    // ========== Easing Functions ==========

    /**
     * Linear easing - без ускорения/замедления
     */
    public static final Easing LINEAR = progress -> progress;

    /**
     * Ease In Quad - медленное начало
     */
    public static final Easing EASE_IN_QUAD = progress -> progress * progress;

    /**
     * Ease Out Quad - медленный конец
     */
    public static final Easing EASE_OUT_QUAD = progress -> progress * (2 - progress);

    /**
     * Ease In Out Quad - медленное начало и конец
     */
    public static final Easing EASE_IN_OUT_QUAD = progress -> {
        if (progress < 0.5f) {
            return 2 * progress * progress;
        }
        return -1 + (4 - 2 * progress) * progress;
    };

    /**
     * Ease In Cubic - кубическое ускорение
     */
    public static final Easing EASE_IN_CUBIC = progress -> progress * progress * progress;

    /**
     * Ease Out Cubic - кубическое замедление
     */
    public static final Easing EASE_OUT_CUBIC = progress -> {
        float p = progress - 1;
        return 1 + p * p * p;
    };

    /**
     * Ease In Out Cubic - кубическое ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_CUBIC = progress -> {
        if (progress < 0.5f) {
            return 4 * progress * progress * progress;
        }
        float p = 2 * progress - 2;
        return 1 + (p * p * p) / 2;
    };

    /**
     * Ease In Quart - четвертичное ускорение
     */
    public static final Easing EASE_IN_QUART = progress -> progress * progress * progress * progress;

    /**
     * Ease Out Quart - четвертичное замедление
     */
    public static final Easing EASE_OUT_QUART = progress -> {
        float p = progress - 1;
        return 1 - p * p * p * p;
    };

    /**
     * Ease In Out Quart - четвертичное ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_QUART = progress -> {
        if (progress < 0.5f) {
            return 8 * progress * progress * progress * progress;
        }
        float p = 2 * progress - 2;
        return 1 - (p * p * p * p) / 2;
    };

    /**
     * Ease In Quint - пятеричное ускорение
     */
    public static final Easing EASE_IN_QUINT = progress -> progress * progress * progress * progress * progress;

    /**
     * Ease Out Quint - пятеричное замедление
     */
    public static final Easing EASE_OUT_QUINT = progress -> {
        float p = progress - 1;
        return 1 + p * p * p * p * p;
    };

    /**
     * Ease In Out Quint - пятеричное ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_QUINT = progress -> {
        if (progress < 0.5f) {
            return 16 * progress * progress * progress * progress * progress;
        }
        float p = 2 * progress - 2;
        return 1 + (p * p * p * p * p) / 2;
    };

    /**
     * Ease In Sine - синусоидальное ускорение
     */
    public static final Easing EASE_IN_SINE = progress -> (float) (1 - Math.cos((progress * Math.PI) / 2));

    /**
     * Ease Out Sine - синусоидальное замедление
     */
    public static final Easing EASE_OUT_SINE = progress -> (float) Math.sin((progress * Math.PI) / 2);

    /**
     * Ease In Out Sine - синусоидальное ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_SINE = progress -> (float) (-(Math.cos(Math.PI * progress) - 1) / 2);

    /**
     * Ease In Expo - экспоненциальное ускорение
     */
    public static final Easing EASE_IN_EXPO = progress -> {
        if (progress == 0) return 0;
        return (float) Math.pow(2, 10 * progress - 10);
    };

    /**
     * Ease Out Expo - экспоненциальное замедление
     */
    public static final Easing EASE_OUT_EXPO = progress -> {
        if (progress == 1) return 1;
        return 1 - (float) Math.pow(2, -10 * progress);
    };

    /**
     * Ease In Out Expo - экспоненциальное ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_EXPO = progress -> {
        if (progress == 0) return 0;
        if (progress == 1) return 1;
        if (progress < 0.5f) {
            return (float) Math.pow(2, 20 * progress - 10) / 2;
        }
        return (2 - (float) Math.pow(2, -20 * progress + 10)) / 2;
    };

    /**
     * Ease In Circ - круговое ускорение
     */
    public static final Easing EASE_IN_CIRC = progress -> (float) (1 - Math.sqrt(1 - progress * progress));

    /**
     * Ease Out Circ - круговое замедление
     */
    public static final Easing EASE_OUT_CIRC = progress -> (float) Math.sqrt(1 - (progress - 1) * (progress - 1));

    /**
     * Ease In Out Circ - круговое ускорение и замедление
     */
    public static final Easing EASE_IN_OUT_CIRC = progress -> {
        if (progress < 0.5f) {
            return (float) (1 - Math.sqrt(1 - 4 * progress * progress)) / 2;
        }
        return (float) (1 + Math.sqrt(1 - (2 * progress - 2) * (2 * progress - 2))) / 2;
    };

    /**
     * Ease In Back - ускорение с отскоком назад
     */
    public static final Easing EASE_IN_BACK = progress -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return c3 * progress * progress * progress - c1 * progress * progress;
    };

    /**
     * Ease Out Back - замедление с отскоком
     */
    public static final Easing EASE_OUT_BACK = progress -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float p = progress - 1;
        return 1 + c3 * p * p * p + c1 * p * p;
    };

    /**
     * Ease In Out Back - ускорение и замедление с отскоком
     */
    public static final Easing EASE_IN_OUT_BACK = progress -> {
        float c1 = 1.70158f;
        float c2 = c1 * 1.525f;
        if (progress < 0.5f) {
            return (float) ((Math.pow(2 * progress, 2) * ((c2 + 1) * 2 * progress - c2)) / 2);
        }
        float p = 2 * progress - 2;
        return (float) ((Math.pow(p, 2) * ((c2 + 1) * p + c2) + 2) / 2);
    };

    /**
     * Ease Out Bounce - замедление с эффектом прыжка
     */
    public static final Easing EASE_OUT_BOUNCE = progress -> {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (progress < 1 / d1) {
            return n1 * progress * progress;
        } else if (progress < 2 / d1) {
            float p = progress - 1.5f / d1;
            return n1 * p * p + 0.75f;
        } else if (progress < 2.5f / d1) {
            float p = progress - 2.25f / d1;
            return n1 * p * p + 0.9375f;
        } else {
            float p = progress - 2.625f / d1;
            return n1 * p * p + 0.984375f;
        }
    };

    /**
     * Ease In Bounce - ускорение с эффектом прыжка
     */
    public static final Easing EASE_IN_BOUNCE = progress -> 1 - EASE_OUT_BOUNCE.apply(1 - progress);

    /**
     * Ease In Out Bounce - ускорение и замедление с эффектом прыжка
     */
    public static final Easing EASE_IN_OUT_BOUNCE = progress -> {
        if (progress < 0.5f) {
            return (1 - EASE_OUT_BOUNCE.apply(1 - 2 * progress)) / 2;
        }
        return (1 + EASE_OUT_BOUNCE.apply(2 * progress - 1)) / 2;
    };

    /**
     * Ease In Elastic - ускорение с эластичным эффектом
     */
    public static final Easing EASE_IN_ELASTIC = progress -> {
        if (progress == 0) return 0;
        if (progress == 1) return 1;
        float c4 = (float) (2 * Math.PI) / 3;
        float p = progress - 1;
        return -(float) (Math.pow(2, 10 * p) * Math.sin(p * c4));
    };

    /**
     * Ease Out Elastic - замедление с эластичным эффектом
     */
    public static final Easing EASE_OUT_ELASTIC = progress -> {
        if (progress == 0) return 0;
        if (progress == 1) return 1;
        float c4 = (float) (2 * Math.PI) / 3;
        return (float) (Math.pow(2, -10 * progress) * Math.sin(progress * c4 - c4)) + 1;
    };

    /**
     * Ease In Out Elastic - ускорение и замедление с эластичным эффектом
     */
    public static final Easing EASE_IN_OUT_ELASTIC = progress -> {
        if (progress == 0) return 0;
        if (progress == 1) return 1;
        float c5 = (float) (2 * Math.PI) / 4.5f;
        float p = 2 * progress - 1;
        if (p < 0) {
            return -(float) (Math.pow(2, 10 * p) * Math.sin(p * c5)) / 2;
        }
        return (float) (Math.pow(2, -10 * p) * Math.sin(p * c5)) / 2 + 1;
    };

    // ========== Utility Methods ==========

    /**
     * Интерполировать значение с использованием easing функции
     */
    public static float applyEasing(float start, float end, float progress, Easing easing) {
        return start + (end - start) * easing.apply(progress);
    }

    /**
     * Ограничить значение в диапазоне
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Ограничить значение в диапазоне (int)
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Преобразовать значение из одного диапазона в другой
     */
    public static float map(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }

    /**
     * Плавное затухание к целевому значению
     */
    public static float approach(float current, float target, float speed) {
        if (current < target) {
            return Math.min(current + speed, target);
        }
        return Math.max(current - speed, target);
    }

    /**
     * Лерп с плавным затуханием
     */
    public static float lerpFactor(float current, float target, float factor) {
        return current + (target - current) * factor;
    }

    /**
     * Получить прогресс анимации (0-1) на основе elapsed времени
     */
    public static float getProgress(long elapsed, long duration) {
        return Math.min(1.0f, (float) elapsed / duration);
    }

    /**
     * Инвертировать прогресс (1 - progress)
     */
    public static float invertProgress(float progress) {
        return 1 - progress;
    }

    /**
     * Зациклить прогресс (для повторяющихся анимаций)
     */
    public static float loopProgress(float progress) {
        return progress - (float) Math.floor(progress);
    }

    /**
     * Прогресс с эффектом ping-pong (туда-обратно)
     */
    public static float pingPong(float progress) {
        float looped = loopProgress(progress);
        return looped < 0.5f ? looped * 2 : 2 - looped * 2;
    }
}
