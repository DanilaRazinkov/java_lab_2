package parcer;

import parcer.exception.*;

import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.Math.*;

public class Parser {

    private final StorageVariable globalStorageVariables = new StorageVariable();
    
    private final StorageFunction functions = new StorageFunction();

    private final Pattern functionPattern = Pattern.compile("\\s*[a-zA-Z]\\s*[a-zA-Z0-9\\s]*\\([a-zA-Z,0-9\\s]+\\)\\s*=[a-zA-Z,.0-9()+-/*^\\s]*");

    /**
     * Добавляет пользовательскую функцию в храненине
     * @param fun имя функции
     * @throws IncorrectParse функция содержит ошибки в написании
     * @throws SuchElementConsist функция с таким именем существует
     */
    public void addFunction(String fun) throws IncorrectParse, SuchElementConsist {
        if(!functionPattern.matcher(fun).matches())
            throw new IncorrectParse("Функция записана некорректно: "  + fun);
        StringBuilder buf = new StringBuilder();
        int t = 0;
        while(fun.charAt(t) != '(') {
            buf.append(fun.charAt(t));
            t++;
        }
        String funName = buf.toString();
        if(functions.checkFunction(funName))
            throw new SuchElementConsist("Функция с таким именем существует: " + funName);
        addFunction(funName, fun.substring(t));
    }

    /**
     * Добавляет функцию по имени и ее определению
     * @param fun имя функции
     * @param var список аргументов и определение
     * @throws IncorrectParse имена переменных или определение содержит ошибки
     */
    private void addFunction(String fun, String var) throws IncorrectParse {
        int endArgs = 0;
        while(var.charAt(endArgs) != ')')
            endArgs++;
        int ePos = endArgs;
        while(var.charAt(ePos) != '=')
            ePos++;
        String funName = clearLine(fun);
        checkVariableName(funName);
        String function = clearLine(var.substring(ePos+1));
        String[] args = var.substring(1, endArgs).replaceAll("\\s+","").split(",");
        for(String str : args)
            checkVariableName(str);
        functions.addFunction(funName, new Function(function, args));
    }

    /**
     * Добавляет глобальную переменную на хранение
     * @param name имя перменной
     * @param var значение переменной
     * @throws IncorrectParse имя переменной некорректно
     * @throws SuchElementConsist такая переменная уже существует
     */
    public void addVariable(String name, double var) throws IncorrectParse, SuchElementConsist {
        checkVariableName(name);
        if(!globalStorageVariables.addVariables(name, var))
            throw new SuchElementConsist("Переменная с таким именен существует: " + name);
    }

    private void addAskUserVariable(String name, double var) throws IncorrectParse {
        checkVariableName(name);
        globalStorageVariables.addVariables(name, var);
    }

    /**
     * Проверяет название переменной на корректность
     * @param name имя переменной
     * @throws IncorrectParse имя переменной не соотвествует требованиям(начинается с символа или содержит неизвесный символ)
     */
    private void checkVariableName(String name) throws IncorrectParse {
        if (!checkCharIsCharacter(name.charAt(0)))
            throw new IncorrectParse("Первый символ в названии должен быть буквой");
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(checkCharIsCharacter(c) || checkCharIsNumber(c)))
                throw new IncorrectParse("Данный символ не может быть использован в названии: " + c);
        }
    }


    /**
     * Решает выражение
     * @param line выражение, котрое необходимо решить
     * @return результат решения выражения
     * @throws IncorrectArgumentCount функции в выражении передано некорректное количество парметров
     * @throws IncorrectParse выражение содержит ошибки в написании
     */
    public double parse(String line) throws IncorrectArgumentCount, IncorrectParse {
        return new LocaleParser().parse(clearLine(line));
    }

    /**
     * Проверяет строку на корректное написание и очищает от пробелов
     * @param line выражение для проверки
     * @return очищенную строку
     * @throws IncorrectParse строка содержит ошибки
     */
    private String clearLine(String line) throws IncorrectParse {
        StringBuilder buf = new StringBuilder();
        int countBrackets = 0;
        int start = 0;
        char now = '\0';
        char prev;

        while(buf.length() == 0 && start < line.length()) {
            now = line.charAt(start);

            if (now == '.')
                throw new IncorrectParse("Точка не может находится на первой позиции");

            if(now != ' ') {
                checkChar(now);
                buf.append(now);
                if (now == '(')
                    countBrackets++;

                if (now == ')')
                    throw new IncorrectParse("Закрывающая скобка не может быть на первой позиции");
            }
            start++;
        }
        if(now == '\0')
            return "";

        prev = now;
        for(int i = start; i < line.length() - 1; i++) {
            now = line.charAt(i);
            if (now != ' ') {
                checkCharInLine(prev , now);
                if(prev == ')')
                    if(!(now == ')' || checkCharOperation(now)))
                        throw new IncorrectParse("Неверный символ после скобкой: " + now);
                if(now == ')')
                    countBrackets--;
                if (now == '(') {
                    if (prev == '(' || checkCharOperation(prev) ||
                            checkCharIsCharacter(prev) || checkCharIsNumber(prev)) {
                        countBrackets++;
                    } else
                        throw new IncorrectParse("Неверный символ перед скобкой: " + prev);
                }
                buf.append(now);
                prev = now;
            }
        }
        if(line.length() > 1) {
            now = line.charAt(line.length() - 1);
            if (now != ' ') {
                checkCharInLine(prev, now);
                if (now == ')')
                    countBrackets--;
                buf.append(now);
            }
        }
        if(countBrackets != 0)
            throw new IncorrectParse("Некорректное количество открывающих и закрывающих скобок");
        return buf.toString();
    }

    private void checkCharInLine(char prev, char now) throws IncorrectParse {
        checkChar(now);

        if(checkCharOperation(now) && checkCharOperation(prev))
            throw new IncorrectParse("Два символа операции находятся рядом: " + prev + ", " + now);

        if(now == '.') {
            if(prev == '.')
                throw new IncorrectParse("Две точки не могуть находится рядом");
            if(!checkCharIsNumber(prev))
                throw new IncorrectParse("Точка должна находится после числа");
        }

    }

    /**
     * Проверяет символ на возможность использования
     * @param c проверяемый символ
     * @throws IncorrectParse данный символ нельзя использовать
     */
    private void checkChar(char c) throws IncorrectParse {
         if(!(c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^'
                || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c<= '9') || c == '.' || c == ','))
             throw new IncorrectParse("Неизвестный символ: " + c);
    }

    /**
     * Проверяет, что символ является операцией
     * @param c проверяемый символ
     * @return true - символ является операцией, иначе false
     */
    private boolean checkCharOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    /**
     * Проверяет, что символ является цифрой
     * @param c проверяемый символ
     * @return true - символ является цифрой, иначе false
     */
    private boolean checkCharIsNumber(char c) {
        return c >= '0' && c<= '9';
    }

    /**
     * Проверяет, что символ является буквой
     * @param c проверяемый символ
     * @return true - символ является буквой, иначе false
     */
    private boolean checkCharIsCharacter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    @Override
    public String toString() {
        return "Variable: " + globalStorageVariables + "\nFunctions: " + functions;
    }

    private class LocaleParser {
        private final StorageVariable localeStorageVariables;

        public LocaleParser() {
            localeStorageVariables = new StorageVariable();
        }

        public LocaleParser(StorageVariable locale) {
            localeStorageVariables = locale;
        }

        /**
         * Решает переданное выражение
         * @param line переданное выражение
         * @return результат вычислений
         * @throws IncorrectArgumentCount функции в выражении передано некорректное количество парметров
         * @throws IncorrectParse выражение содержит ошибки в написании
         */
        public double parse(String line) throws IncorrectArgumentCount, IncorrectParse {
            return parse(line, 0, line.length());
        }

        /**
         * Решает переданное выражение. Производит парсинг выражения
         * @param line переданное выражение
         * @param begin с какого символа начинать парсинг
         * @param end до какого символа проводить парсинг
         * @return результат определенного участка выражения
         * @throws IncorrectArgumentCount функции в выражении передано некорректное количество парметров
         * @throws IncorrectParse выражение содержит ошибки в написании
         */
        private double parse(String line, int begin, int end) throws IncorrectArgumentCount, IncorrectParse {
            int countBrackets = 0;
            int pos = -1;
            int posPower = -1;
            if(begin == end)
                return 0;
            int nBegin = line.charAt(begin) == '-' ? begin + 1: begin;
            for (int i = nBegin; i < end; i++) {
                switch (line.charAt(i)) {
                    case '(':
                        countBrackets++;
                        break;
                    case ')':
                        countBrackets--;
                        break;
                    default:
                        if (countBrackets == 0)
                            switch (line.charAt(i)) {
                                case '+':
                                    return parse(line, begin, i) + parse(line, i + 1, end);
                                case '-':
                                    return parse(line, begin, i) - parse(line, i + 1, end);
                                case '*':
                                case '/':
                                    pos = i;
                                    break;
                                case '^':
                                    posPower = i;
                                    break;
                            }
                }
            }
            if(posPower != -1)
                return pow(parse(line, begin, posPower), parse(line, posPower + 1, end));
            if (pos == -1) {
                if (line.charAt(end - 1) == ')')
                    if (line.charAt(begin) == '(')
                        return parse(line, begin + 1, end - 1);
                    else
                        return parseFunc(line.substring(begin, end));
                else
                    return getValue(line.substring(begin, end));
            } else
                return (line.charAt(pos) == '*' ? parse(line, begin, pos) * parse(line, pos + 1, end) :
                        parse(line, begin, pos) / parse(line, pos + 1, end));
        }

        /**
         * Получает значение перемнной по имени или преобразует строку в число
         * @param var строка для преобразования
         * @return числовое значение переменной или преобразованное число
         * @throws IncorrectParse добавленная пользователем перемнная содежит ошибку
         */
        private double getValue(String var) throws IncorrectParse {
            if (var.charAt(0) == '-' || var.charAt(0) >= '0' && var.charAt(0) <= '9')
                return Double.parseDouble(var);
            if (localeStorageVariables.checkVariable(var))
                return localeStorageVariables.getVariables(var);
            if (!globalStorageVariables.checkVariable(var))
                askUserVariable(var);
            return globalStorageVariables.getVariables(var);
        }

        /**
         * Производит парсинг функции
         * @param line фунция для парсинга
         * @return значение функции
         * @throws IncorrectArgumentCount функции передано некорректное количесво параметров или в функции содержится подобная ошибка
         * @throws IncorrectParse ошибка в написании
         */
        private double parseFunc(String line) throws IncorrectArgumentCount, IncorrectParse {
            int t = 0;
            StringBuilder buf = new StringBuilder();
            while (line.charAt(t) != '(') {
                buf.append(line.charAt(t));
                t++;
            }
            String[] funArgs = line.substring(t + 1, line.length() - 1).split(",");
            String nameFun = buf.toString();
            if (funArgs.length == 1)
                switch (nameFun) {
                    case "sin":
                        return sin(parse(funArgs[0]));
                    case "cos":
                        return cos(parse(funArgs[0]));
                    case "tan":
                        return tan(parse(funArgs[0]));
                    case "asin":
                        return asin(parse(funArgs[0]));
                    case "acos":
                        return acos(parse(funArgs[0]));
                    case "exp":
                        return exp(parse(funArgs[0]));
                    case "log":
                        return log(parse(funArgs[0]));
                    case "log10":
                        return log10(parse(funArgs[0]));
                    case "sqrt":
                        return sqrt(parse(funArgs[0]));
                    case "abs":
                        return abs(parse(funArgs[0]));
                }

            if (funArgs.length == 2)
                switch (nameFun) {
                    case "pow":
                        return pow(parse(funArgs[0]), parse(funArgs[1]));
                    case "max":
                        return max(parse(funArgs[0]), parse(funArgs[1]));
                    case "min":
                        return min(parse(funArgs[0]), parse(funArgs[1]));
                }

            Function function = getFunction(nameFun);
            if (function.getArgsSize() != funArgs.length)
                throw new IncorrectArgumentCount("Неверное количество аргументов функции: " + nameFun + ". Необходимо: " +
                        function.getArgsSize() + ". Полученно: " + funArgs.length);

            StorageVariable newLocaleVariable = new StorageVariable();
            String[] nameArgs = function.getArgs();
            for (int i = 0; i < function.getArgsSize(); i++) {
                newLocaleVariable.addVariables(nameArgs[i], parse(funArgs[i]));
            }
            return new LocaleParser(newLocaleVariable).parse(function.getFun());
        }

        /**
         * Возвращает функцию по имени
         * @param funName имя функции
         * @return ссылку на объект функции
         * @throws IncorrectParse добавленная пользователем функция содержит ошибку
         */
        private Function getFunction(String funName) throws IncorrectParse {
            if (!functions.checkFunction(funName))
                askUserFunction(funName);
            return functions.getFunction(funName);
        }

        /**
         * Запрашивает у пользователя функцию
         * @param funName имя функции
         * @throws IncorrectParse функция пользователя содержит ошибку в написании
         */
        private void askUserFunction(String funName) throws IncorrectParse {
            System.out.print("Неизвестная функция:" + funName + "\nВведите ее аргументы: ");
            Scanner in = new Scanner(System.in);
            String args = in.nextLine();
            System.out.print("Введите определение функции = ");
            addFunction(funName, "(" + args + ")=" + in.nextLine());
        }

        /**
         * * Запрашивает у пользователя перемнную
         * @param varName имя перемнной
         * @throws IncorrectParse переменная пользователя содержит ошибку в написании
         */
        private void askUserVariable(String varName) throws IncorrectParse {
            System.out.print("Неизвестная переменная:" + varName + "\nВведите ее значение: ");
            Scanner in = new Scanner(System.in);
            addAskUserVariable(varName, in.nextDouble());
        }
    }
}
