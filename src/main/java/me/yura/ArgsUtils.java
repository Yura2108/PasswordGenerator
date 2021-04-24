package me.yura;

public class ArgsUtils {

    public static int getPasswordLength(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-length")) {
                if (i == args.length - 1) {
                    System.err.println("Значение длины не указано!");
                    return 8;
                }
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Указано неверное значение длины пароля!");
                    return 8;
                }
            }
        }

        return 0;
    }

    public static String getFileURI(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-file")) {
                if (i == args.length - 1) {
                    return "";
                }
                return args[i + 1];
            }
        }

        return "";
    }

    public static String getPasswd(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-passwd")) {
                if (i == args.length - 1) {
                    return "";
                }
                return args[i + 1];
            }
        }

        return "";
    }


    public static boolean containsArg(String[] args, String toFind) {
        for (String str : args) {
            if (str.equals(toFind)) return true;
        }

        return false;
    }

}
