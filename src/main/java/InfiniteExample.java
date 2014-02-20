public class InfiniteExample {

    public static void main(String[] args) throws Exception {
        while (true) {
            Thread.sleep(100);
            subMethod();
        }
    }

    private static void subMethod() {
        System.out.println("calling some code, lalala");
    }

}
