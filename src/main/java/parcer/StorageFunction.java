package parcer;

import java.util.HashMap;
import java.util.Objects;

public class StorageFunction {

    private final HashMap<String, Function> function = new HashMap<>();

    /**
     * Добавляет значение функции на хранение
     * @param name имя переменной
     * @param val значение переменной
     * @return true - если добавлено, false - данная переменная уже имеется
     */
    public boolean addFunction(String name, Function val) {
        if(function.containsKey(name))
            return false;
        function.put(name, val);
        return true;
    }

    /**
     * Возвращает значение функции по имени
     * @param name имя функции
     * @return значение функции
     * @throws NullPointerException если данная функция не существует
     */
    public Function getFunction(String name) {
        return Objects.requireNonNull(function.get(name),"Данная функция не существует: " + name);
    }

    /**
     * Проверяет наличие функции в хранилище
     * @param funName имя функции
     * @return true - функция есть, false - функция отсутсвует
     */
    public boolean checkFunction(String funName) {
        return function.containsKey(funName);
    }


    @Override
    public String toString() {
        return function.toString();
    }
}
