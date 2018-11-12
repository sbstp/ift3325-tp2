package tp2;

public class Util {

    public static int ceil(int num, int div) {
        int x = num / div;
        return num % div != 0 ? x + 1 : x;
    }

}
