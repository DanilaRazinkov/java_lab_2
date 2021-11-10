package parcer;

import java.util.HashMap;
import java.util.Objects;

public class StorageVariable {

    private final HashMap<String, Double> variables = new HashMap<>();

    /**
     * Добавляет значение переменной на хранение
     * @param name имя переменной
     * @param val значение переменной
     * @return true - если добавлено, false - данная переменная уже имеется
     */
    public boolean addVariables(String name, double val) {
        if(variables.containsKey(name))
            return false;
        variables.put(name, val);
        return true;
    }

    /**
     * Возвращает значение переменной по имени
     * @param name имя переменной
     * @return значение переменной
     * @throws NullPointerException если данная переменная не существует
     */
    public double getVariables(String name) {
        return Objects.requireNonNull(variables.get(name), "Данная переменная не существует: " + name);
    }

    /**
     * Проверяет перемнной функции в хранилище
     * @param varName имя переменной
     * @return true - переменная есть, false - переменная отсутсвует
     */
    public boolean checkVariable(String varName) {
        return variables.containsKey(varName);
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}
