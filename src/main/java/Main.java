import parcer.exception.*;
import parcer.Parser;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        Scanner in = new Scanner(System.in);
        char mot;
        do {
            System.out.print("Выберите действие: \n1 - Решить выражение\n2 - Добавить переменную\n" +
                    "3 - Добавить функцию\n4 - Просмотреть парсер\n5 - Помощь\ne - выход\n>>");
            mot = in.next().charAt(0);
            switch (mot) {
                case '1':
                    System.out.print("Введите выражение: ");
                    try {
                        System.out.println("Результат: " + parser.parse(in.next()));
                    } catch (IncorrectArgumentCount |IncorrectParse e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case '2':
                    String varName = null;
                    double var = 0;
                    System.out.print("Введите значение переменной: ");
                    boolean check = true;
                    while(check) {
                        try {
                            var = Double.parseDouble(in.next());
                            check = false;
                        } catch (NumberFormatException e) {
                            System.out.print("Ввод должен быть числом. Повторите ввод: ");
                        }
                    }
                    while(varName == null) {
                        System.out.print("Введите имя переменной: ");
                        varName = in.next();
                        try {
                            parser.addVariable(varName, var);
                        } catch (IncorrectParse | SuchElementConsist e) {
                            System.out.println(e.getMessage());
                            varName = null;
                        }
                    }
                    break;
                case '3':
                    boolean check1 = true;
                    while(check1) {
                        System.out.print("Введите функцию: ");
                        try {
                            parser.addFunction(in.next());
                            check1 = false;
                        } catch (IncorrectParse | SuchElementConsist e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case '4':
                    System.out.println(parser);
                    break;
                case '5':
                    System.out.println("Данный парсер позволяет работать с переменными и пользовательскими функция.\n" +
                            "Содержит поддержку большинства стандартных функций из модуля Math.\n" +
                            "Создание переменных и функций с одинаковыми именами не допускается.\n" +
                            "Примечание: создание функции с именем стандартной функции не переопределяет стандартную функцию.\n" +
                            "Поддерживаемые символы: a-z, A-Z, 0-9, (, ), +, -, /, *, ^  и запятая, точка, пробел.\n" +
                            "Для добавления переменной: \n" +
                            "1. Необходимо выбрать пункт 2.\n" +
                            "2. Ввести значение переменной, а после ее название.\n" +
                            "3. Если название переменной не подходит, то измените название переменной в соответствии требований и повторите ввод.\n" +
                            "Для добавления функции:\n" +
                            "1. Необходимо выбрать пункт 3.\n" +
                            "2. Ввести функцию(Примеры: f(x)=x, g(x, y)=x + y)\n" +
                            "3. Если функция не подходит, то измените ввод в соответствии требований и попробуйте снова.\n");
                    break;
            }
        } while(mot != 'e');
    }
}
