package interfaces;

//import org.openjdk.jol.info.ClassLayout;

// TODO Java Object Layout
public class Example7 {

    public static void main(String[] args) {
        Person person = new Person("Alex", 24);
        // header - 12 bytes
        // reference - 8 bytes
        // int - 4 bytes
        // padding - 0 bytes

//        ClassLayout layout = ClassLayout.parseInstance("123");
//        System.out.println(layout.toPrintable());

    }
}

class Person {

    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
