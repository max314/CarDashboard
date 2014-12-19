package ru.max314.cardashboard.model;

/** Интерфейс логиррования сущности
 * Created by max on 17.12.2014.
 */
public interface IEntityLogFormater<T> {

    /**
     * Заголовок  файла
     * @return
     */
    public String getHeader();

    /**
     *  Одна строка сущности
     * @return
     */
    public String getLine(T entity);

}
