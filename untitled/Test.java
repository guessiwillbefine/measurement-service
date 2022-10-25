import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {

//        System.out.println(check("{[]}"));
//        System.out.println(check("[]"));
        System.out.println(check("{[()]}"));
//        System.out.println(check("{[]"));
//        System.out.println(check("()[]{}"));
//        System.out.println(check("(){}[]"));
        System.out.println(check("()"));
        System.out.println(check("(){}"));
        System.out.println(check("({})[({})]"));
    }

    public static boolean check(String s) {
        Pattern pattern = Pattern.compile("(\\[]||\\{}||\\(\\))+?");

        if (s.matches(pattern.pattern())) return true;
        Stack<String> stack = new Stack<>();

        String[] braces = s.split(pattern.pattern());

        Arrays.stream(braces).forEach(System.out::print);

        if (Arrays.stream(braces).reduce((a,b) -> a+b).get().matches(pattern.pattern())) return true;

        for (int i = 0; i < braces.length / 2; i++) {
            stack.push(braces[i]);
        }

        Queue<String> reversedStack = new LinkedList<>(Arrays.asList(braces).subList(braces.length / 2, braces.length));

        while (!stack.empty()) {
            String opposite = reversedStack.poll();
            String brace = stack.pop();
            String mustBe;
            switch (brace) {
                case "(" -> mustBe = ")";
                case "[" -> mustBe = "]";
                default -> mustBe = "}";
            }
            if (!Objects.equals(opposite, mustBe)) return false;
        }
        return true;
    }
}
