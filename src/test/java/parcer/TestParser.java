package parcer;

import org.junit.jupiter.api.Test;
import parcer.exception.IncorrectArgumentCount;
import parcer.exception.IncorrectParse;
import parcer.exception.SuchElementConsist;


import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestParser {

    @Test
    public void testSimpleParse() {
        Parser par = new Parser();
        double result1 = 1 + 1;
        double result2 = 1 - 3;
        double result3 = 4 * 4.0;
        double result4 = 4 / 2d;
        double result5 = pow(2, 3);
        double result6 = 0;
        try {
            double count1 = par.parse("1+1");
            double count2 = par.parse("1-3");
            double count3 = par.parse("4*4.0");
            double count4 = par.parse("4/2");
            double count5 = par.parse("2^3");
            double count6 = par.parse("");
            assertAll(
                    () ->assertEquals(result1, count1),
                    () ->assertEquals(result2, count2),
                    () ->assertEquals(result3, count3),
                    () ->assertEquals(result4, count4),
                    () ->assertEquals(result5, count5),
                    () ->assertEquals(result6, count6)
                    );
        } catch (IncorrectArgumentCount | IncorrectParse  e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBracketsParse() {
        Parser par = new Parser();
        double result1 = (1);
        double result2 = (1 - 3);
        double result3 = (4+2)*(4-2);
        double result4 = ((2-3)-2);
        double result5 = pow((2*83), (2-2d)/2d);
        try {
            double count1 = par.parse("(1)");
            double count2 = par.parse("(1-3)");
            double count3 = par.parse("(4+2)*(4-2)");
            double count4 = par.parse("((2-3)-2)");
            double count5 = par.parse("(2*83)^(2-2)/2");
            assertAll(
                    () ->assertEquals(result1, count1),
                    () ->assertEquals(result2, count2),
                    () ->assertEquals(result3, count3),
                    () ->assertEquals(result4, count4),
                    () ->assertEquals(result5, count5)
            );
        } catch (IncorrectArgumentCount | IncorrectParse  e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVariables() {
        Parser par = new Parser();
        try {
            double x1 = 10;
            double AD1s = 9;
            double result2 = x1 * AD1s;
            par.addVariable("PI", Math.PI);
            par.addVariable("x1", x1);
            par.addVariable("AD1s", AD1s);
            double count1 = par.parse("PI");
            double count2 = par.parse("x1*AD1s");
            assertAll(
                    () ->assertEquals(Math.PI, count1),
                    () ->assertEquals(result2, count2)
            );
        } catch (IncorrectParse | SuchElementConsist | IncorrectArgumentCount incorrectParse) {
            incorrectParse.printStackTrace();
        }
    }
    
    @Test
    public void testBaseFunction() {
        Parser par = new Parser();
        try {
            double count1 = par.parse("sin(1)");
            double result1 = sin(1);
            double count2 = par.parse("cos(1)");
            double result2 = cos(1);
            double count3 = par.parse("tan(1)");
            double result3 = tan(1);
            double count4 = par.parse("asin(1)");
            double result4 = asin(1);
            double count5 = par.parse("acos(1)");
            double result5 = acos(1);
            double count6 = par.parse("exp(1)");
            double result6 = exp(1);
            double count7 = par.parse("log(1)");
            double result7 = log(1);
            double count8 = par.parse("log10(1)");
            double result8 = log10(1);
            double count9 = par.parse("sqrt(1)");
            double result9 = sqrt(1);
            double count10 = par.parse("abs(-1)");
            double result10 = abs(-1);
            double count11 = par.parse("pow(3, 1/2)");
            double result11 = pow(3, 1/2d);
            double count12 = par.parse("max(3, 1/2)");
            double result12 = max(3, 1/2d);
            double count13 = par.parse("min(3, 1/2)");
            double result13 = min(3, 1/2d);
            assertAll(
                    () ->assertEquals(result1, count1),
                    () ->assertEquals(result2, count2),
                    () ->assertEquals(result3, count3),
                    () ->assertEquals(result4, count4),
                    () ->assertEquals(result5, count5),
                    () ->assertEquals(result6, count6),
                    () ->assertEquals(result7, count7),
                    () ->assertEquals(result8, count8),
                    () ->assertEquals(result9, count9),
                    () ->assertEquals(result10, count10),
                    () ->assertEquals(result11, count11),
                    () ->assertEquals(result12, count12),
                    () ->assertEquals(result13, count13)
            );
        } catch (IncorrectArgumentCount | IncorrectParse e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUserFunction() {
        Parser par = new Parser();
        try {
            par.addFunction("f(x)=x");
            par.addFunction("g(x)=f(x)");
            par.addFunction("mt(x1, x2, x3, x4, x5) = x1 + x2 + x3 + x4 + x5");

            double count1 = par.parse("f(10)");
            double result1 = 10;
            double count2 = par.parse("g(2)");
            double result2 = 2;
            double count3 = par.parse("mt(1,2,3,4,5)");
            double result3 = 15;

            assertAll(
                    () ->assertEquals(result1, count1),
                    () ->assertEquals(result2, count2),
                    () ->assertEquals(result3, count3)
            );
        } catch (IncorrectArgumentCount | IncorrectParse | SuchElementConsist e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testException() {
        Parser par = new Parser();

        Exception ex1 = assertThrows(IncorrectParse.class, () ->par.addFunction("f()"));
        String ex1Answer = "Функция записана некорректно: f()";
        String ex1Message = ex1.getMessage();

        Exception ex2 = assertThrows(SuchElementConsist.class, () -> {
            par.addFunction("f(x)=x");
            par.addFunction("f(x)=x*x");
        });
        String ex2Answer = "Функция с таким именем существует: f";
        String ex2Message = ex2.getMessage();

        Exception ex3 = assertThrows(SuchElementConsist.class, () -> {
            par.addVariable("a", 10);
            par.addVariable("a", 12);
        });
        String ex3Answer = "Переменная с таким именен существует: a";
        String ex3Message = ex3.getMessage();

        Exception ex4 = assertThrows(IncorrectParse.class, () ->par.addVariable("aaaa#", 100));
        String ex4Answer = "Данный символ не может быть использован в названии: #";
        String ex4Message = ex4.getMessage();

        Exception ex5 = assertThrows(IncorrectParse.class, () ->par.parse(".100"));
        String ex5Answer = "Точка не может находится на первой позиции";
        String ex5Message = ex5.getMessage();

        Exception ex6 = assertThrows(IncorrectParse.class, () ->par.parse(")"));
        String ex6Answer = "Закрывающая скобка не может быть на первой позиции";
        String ex6Message = ex6.getMessage();

        Exception ex7 = assertThrows(IncorrectParse.class, () ->par.parse("( )f()"));
        String ex7Answer = "Неверный символ после скобкой: f";
        String ex7Message = ex7.getMessage();

        Exception ex8 = assertThrows(IncorrectParse.class, () ->par.parse("0.()"));
        String ex8Answer = "Неверный символ перед скобкой: .";
        String ex8Message = ex8.getMessage();

        Exception ex9 = assertThrows(IncorrectParse.class, () ->par.parse("(((((10)))"));
        String ex9Answer = "Некорректное количество открывающих и закрывающих скобок";
        String ex9Message = ex9.getMessage();

        Exception ex10 = assertThrows(IncorrectParse.class, () ->par.parse("+-"));
        String ex10Answer = "Два символа операции находятся рядом: +, -";
        String ex10Message = ex10.getMessage();

        Exception ex11 = assertThrows(IncorrectParse.class, () ->par.parse("0..10"));
        String ex11Answer = "Две точки не могуть находится рядом";
        String ex11Message = ex11.getMessage();

        Exception ex12 = assertThrows(IncorrectParse.class, () ->par.parse("a..10"));
        String ex12Answer = "Точка должна находится после числа";
        String ex12Message = ex12.getMessage();

        Exception ex13 = assertThrows(IncorrectParse.class, () ->par.parse("898#"));
        String ex13Answer = "Неизвестный символ: #";
        String ex13Message = ex13.getMessage();

        Exception ex14 = assertThrows(IncorrectArgumentCount.class, () -> {
            par.addFunction("g(x)=x");
            par.parse("g(10, 10)");
        });
        String ex14Answer = "Неверное количество аргументов функции: g. Необходимо: 1. Полученно: 2";
        String ex14Message = ex14.getMessage();

        Exception ex15 = assertThrows(IncorrectParse.class, () ->par.addVariable("898a", 10));
        String ex15Answer = "Первый символ в названии должен быть буквой";
        String ex15Message = ex15.getMessage();
        assertAll(
                () ->assertEquals(ex1Answer, ex1Message),
                () ->assertEquals(ex2Answer, ex2Message),
                () ->assertEquals(ex3Answer, ex3Message),
                () ->assertEquals(ex4Answer, ex4Message),
                () ->assertEquals(ex5Answer, ex5Message),
                () ->assertEquals(ex6Answer, ex6Message),
                () ->assertEquals(ex7Answer, ex7Message),
                () ->assertEquals(ex8Answer, ex8Message),
                () ->assertEquals(ex9Answer, ex9Message),
                () ->assertEquals(ex10Answer, ex10Message),
                () ->assertEquals(ex11Answer, ex11Message),
                () ->assertEquals(ex12Answer, ex12Message),
                () ->assertEquals(ex13Answer, ex13Message),
                () ->assertEquals(ex14Answer, ex14Message),
                () ->assertEquals(ex15Answer, ex15Message)
                );
    }
}
