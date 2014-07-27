
public class Example {

    public static void main(String[] args) throws Exception {
        for (int i = 0;i < 20000;i++) {
            Thread.sleep(1);
            subMethod();
        }
    }

    private static void subMethod() {
        System.out.println("calling some code, lalala");
    }

}

