package ru.max314.music;

/**
 * Created by max on 11.11.2014.
 * Интерфейс для управления музыкальными плеерами
 */
public interface IMusicOpertion {
    /**
     * Играть
     */
    public void play();
    /**
     * Pause
     */
    public void pause();
    /**
     * переключатель играть пауза
     */
    public void playPause();

    /**
     * Следующий трек
     */
    public void nextTrack();

    /**
     * Предыдущий трек
     */
    public void prevTrack();

}
