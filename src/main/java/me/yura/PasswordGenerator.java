package me.yura;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.yura.ArgsUtils.*;

public class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%&*()_+-=[]|,./?><";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java -jar PasswordGenerator-1.0.jar -generate -length {ДлинаПароля} [-applyUpper, -applySpecial, -applyDigits, -applyAll]");
            System.out.println("Использование: java -jar PasswordGenerator-1.0.jar -check [-file, -passwd] {Объект} [-applyUpper, -applySpecial, -applyDigits, -applyAll]");
            return;
        }

        long time = System.nanoTime();


        //Генерация пароля, используя аргументы
        if (containsArg(args, "-generate")) {

            int length = getPasswordLength(args);
            if (length <= 0) {
                System.err.println("Обнаружено недопустимое значение длины пароля, используем стандартное!");
                length = 8;
            }


            if (containsArg(args, "-applyAll")) {
                System.out.format("Пароль: %s", generatePassword(length, true, true, true));
            } else {
                System.out.format("Пароль: %s", generatePassword(length, containsArg(args, "-applyUpper"), containsArg(args, "-applySpecial"), containsArg(args, "-applyDigits")));
            }
            System.out.println();

            float elapsed = ((System.nanoTime() - time) / 1000000000F);
            System.out.format("Времени затрачено %f секунд", elapsed);
        } else if (containsArg(args, "-check")) {
            //Проверяем пароль используя данный объект: Файл или стринг пароля
            if (containsArg(args, "-file")) {
                String file = getFileURI(args);
                if (file.isEmpty()) {
                    System.err.println("Ссылка на файл указана неверно");
                    return;
                }

                File f0 = new File(file);

                if (!f0.exists()) {
                    System.err.println("Файл не существует!");
                    return;
                }
                final boolean upper = containsArg(args, "-applyAll") || containsArg(args, "-applyUpper");
                final boolean special = containsArg(args, "-applyAll") || containsArg(args, "-applySpecial");
                final boolean digits = containsArg(args, "-applyAll") || containsArg(args, "-applyDigits");

                new Thread(() -> {
                    try {

                        FileReader fr = new FileReader(file);
                        BufferedReader reader = new BufferedReader(fr);

                        String line = reader.readLine();

                        while (line != null) {
                            if(checkPassword(line, upper, special, digits)){
                                System.out.println("Пароль " + line + " подходит по критериям");
                            }else{
                                System.out.println("Пароль " + line + " не подходит по критериям");
                            }

                            //Считываем следующую строку
                            line = reader.readLine();
                        }

                        float elapsed = ((System.nanoTime() - time) / 1000000000F);
                        System.out.format("Времени затрачено %f секунд", elapsed);

                    } catch (Exception e) {
                        System.err.println("Непредвиденная ошибка");
                        return;
                    }
                }).start();


            } else if (containsArg(args, "-passwd")) {

                String passwd = getPasswd(args);
                if (passwd.isEmpty()) {
                    System.err.println("Пароль указан неверно!");
                    return;
                }

                boolean match;

                if (containsArg(args, "-applyAll")) {
                    match = checkPassword(passwd, true, true, true);
                } else {
                    match = checkPassword(passwd, containsArg(args, "-applyUpper"), containsArg(args, "-applySpecial"), containsArg(args, "-applyDigits"));
                }

                System.out.println(match ? "Пароль подходит по критериям" : "Пароль не подходит по критериям");

                float elapsed = ((System.nanoTime() - time) / 1000000000F);
                System.out.format("Времени затрачено %f секунд", elapsed);


            } else {
                System.err.println("Не указан тип для проверки (Файл или пароль)");
                return;
            }


        } else {
            System.err.println("Не указан тип работы: генерация или проверка");
            return;
        }


    }


    private static boolean checkPassword(String input, boolean upper, boolean special, boolean digits) {
        boolean hasUppercase = !input.equals(input.toLowerCase());
        boolean hasSpecial = !input.matches("[A-Za-z0-9 ]*");//Checks at least one char is not alpha numeric

        if (hasUppercase && !upper) {
            return false;
        }

        if (input.matches(".*\\d.*") && !digits) {
            return false;
        }

        if (hasSpecial && !special) {
            return false;
        }

        return true;

    }


    private static String generatePassword(int length, boolean upper, boolean special, boolean digits) {
        List<String> allCharacters = new ArrayList<>(4);

        //Рандомный генератор
        StringBuilder passwd = new StringBuilder();
        Random random = new Random(System.nanoTime());

        //Изначально пароль состоит только из символов нижнего регистра
        allCharacters.add(LOWER);

        if (upper) allCharacters.add(UPPER);
        if (special) allCharacters.add(SPECIAL);
        if (digits) allCharacters.add(DIGITS);

        for (int i = 0; i < length; i++) {
            String charCategory = allCharacters.get(random.nextInt(allCharacters.size()));
            int position = random.nextInt(charCategory.length());
            passwd.append(charCategory.charAt(position));
        }

        return passwd.toString();
    }

}

