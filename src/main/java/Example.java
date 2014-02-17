
public class Example {

    public static void main(String[] args) throws Exception {
        for (int i = 0;i < 200;i++) {
            Thread.sleep(100);
            subMethod();
        }
    }

    private static void subMethod() {
        System.out.println("calling some code, lalala");
    }

}

