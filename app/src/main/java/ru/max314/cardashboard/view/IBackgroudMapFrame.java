package ru.max314.cardashboard.view;

/**
 * фоновый фрейм с картой
 * Created by max on 22.12.2014.
 */
public interface IBackgroudMapFrame  extends IBackgroudFrame{

    /**
     * Приблизить карту
     */
    public void ZoomIn();

    /**
     * Отдалить карту
     */
    public void ZoomOut();
}
