package lambda.part1.example;

public class Example12 {

    private static void execute(Runnable task) {
        // 1
        task.run();
    }

    private static int staticField = 50;

    private static Runnable createTask() {
        StringBuilder builder = new StringBuilder("1");


//        Runnable task = () -> {
//            System.out.println(builder);
//            builder = new StringBuilder("2");
//        };

//        task.run();
//        System.out.println(builder);
        return null;
    }

    public static void main(String[] args) {
        // 0
        int value = 42;
        int value2 = 42;
        int value3 = 42;

        // 2
        execute(createTask());
    }
}
