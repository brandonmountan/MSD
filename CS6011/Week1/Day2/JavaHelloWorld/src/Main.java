//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello World \n");

        int[] nums = {33, 21, 2, 90, 35, 78, 42, 19, 38, 55};

        for (int i = 0; i < 10; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println(nums[i]);
        }

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter name");
        String name = myObj.nextLine();
        System.out.println("Enter age");
        int age = myObj.nextInt();

        if (age < 18) {
            System.out.println("Hello " + name + ". You are not old enough to vote. You are an iKid");
        } else if (age >= 18 && age <= 20) {
            System.out.println("Hello " + name + ". You are old enough to vote. You are an iKid.");
        } else if (age > 20 && age <= 40) {
            System.out.println("Hello " + name + ". You are old enough to vote. You are a Millenial.");
        } else if (age > 40 && age <= 60) {
            System.out.println("Hello " + name + ". You are old enough to vote. You are part of Generation X.");
        } else if (age > 60 && age <= 80) {
            System.out.println("Hello " + name + ". You are old enough to vote. You are a Baby Boomer.");
        } else if (age > 80) {
            System.out.println("Hello " + name + ". You are old enough to vote. You are part of the Greatest Generation.");
        }
    }
}