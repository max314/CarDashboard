package ru.max314.cardashboard.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ru.max314.util.LogHelper;

/**
 * Задача класса логировать ковойто
 * Created by max on 17.12.2014.
 */
public abstract class EntityLogger<T> {
    private static LogHelper Log = new LogHelper(EntityLogger.class);
    private Class<T> type;

    /**
     * аккамулятор того что логируем
     */
    private List<T> entityList = new ArrayList<T>();
    private final Object lock = new Object();


    /**
     * Добавить что что мы собрались логировать
     * @param entity
     */
    public void addEntity(T entity){
        synchronized (lock){
            entityList.add(entity);
        }
    }

    /**
     * Выполнить логгирование
     */
    public void performLog(){
        synchronized (lock){
            // запомним текущий акамулятор
            final List<T> list = entityList;
            // пересоздаем аккамулятор
            entityList = new ArrayList<T>();
            // уводим сохранение в другой поток
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doPerformLog(list);
                }
            }).start();
        }
    }

    /**
     * Переопределить сохранение лога
     * @param list список того что лоuировать
     */
    protected void doPerformLog(List<T> list){
        if (list.size()==0)
            return;
        try {
            String fileName = getFileName();
            File file = new File(fileName);
            boolean printHeader = false;
            if (!file.exists())
                printHeader = true;
            IEntityLogFormater<T> formater = getFormater();
            PrintWriter output = new PrintWriter(new FileWriter(file,true));
            try
            {
                if (printHeader)
                    output.printf("%s\r\n", formater.getHeader());
                for(T item:list){
                    output.printf("%s\r\n",formater.getLine(item));
                }
                output.flush();
                output.close();
            }
            catch (Exception e) {
                Log.e("Error log for :" + type.getName(), e);
            }
        } catch (IOException e) {
            Log.e("Error log for :" + type.getName(), e);
        }
    }

    /**
     * получить имя файла для логгирования
     * @return имя файла
     */
    protected abstract String getFileName();

    /**
     * Получить форматер сущности
     * @return
     */
    protected abstract IEntityLogFormater<T> getFormater();
}
