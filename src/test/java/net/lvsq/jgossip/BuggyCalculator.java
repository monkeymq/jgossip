// src/main/java/com/example/BuggyCalculator.java
package net.lvsq.jgossip;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuggyCalculator {

    private static final Logger logger = Logger.getLogger(BuggyCalculator.class.getName());


    public double divide(int numerator, int denominator) {
        // 潜在 Bug 1: 除以零未处理
        // 潜在 Bug 2: 返回 double，但参数是 int，可能导致精度问题（如果结果需要精确）
        return numerator / denominator;
    }

    public int multiply(int a, int b) {
        // 潜在 Bug 3: 整数溢出未检查
        return a * b;
    }

    public String concatenateStrings(String s1, String s2) {
        // 潜在 Bug 4: 频繁创建 String 对象，性能差（应使用 StringBuilder/StringBuffer）
        String result = "";
        result = result + s1;
        result = result + s2;
        return result;
    }

    public void processData(List<String> data) {
        // 潜在 Bug 5: 迭代过程中修改集合（ConcurrentModificationException）
        for (String item : data) {
            if (item.equals("remove")) {
                data.remove(item);
            }
        }
    }

    public void writeToFile(String filename, String content) throws IOException {
        // 潜在 Bug 6: 资源未关闭（try-with-resources 缺失）
        // 潜在 Bug 7: 异常处理过于宽泛，直接抛出，没有日志记录或更细致处理
        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close(); // 即使写入失败，也应尝试关闭
            }
        }
    }

    public boolean checkPermission(String userRole) {
        // 潜在 Bug 8: 硬编码权限逻辑，不易维护和扩展
        if (userRole == "admin") { // 潜在 Bug 9: 字符串比较使用 == (应使用 .equals())
            return true;
        } else if (userRole.equals("guest")) {
            return false;
        } else {
            return false;
        }
    }

    // 潜在 Bug 10: 未使用的私有方法
    private void unusedPrivateMethod() {
        System.out.println("This method is never called.");
    }

    public static void main(String[] args) {
        BuggyCalculator calculator = new BuggyCalculator();

        // 测试除法
        System.out.println("Division: " + calculator.divide(10, 2));
        // System.out.println("Division by zero: " + calculator.divide(10, 0)); // 会导致运行时错误

        // 测试乘法
        System.out.println("Multiplication: " + calculator.multiply(Integer.MAX_VALUE, 2)); // 溢出

        // 测试字符串连接
        System.out.println("Concatenation: " + calculator.concatenateStrings("Hello", "World"));

        // 测试列表处理
        List<String> myData = new ArrayList<>();
        myData.add("item1");
        myData.add("remove");
        myData.add("item2");
        myData.add("remove");
        System.out.println("Original list: " + myData);
        calculator.processData(myData); // 可能会抛出 ConcurrentModificationException
        System.out.println("Processed list: " + myData);

        // 测试文件写入
        try {
            calculator.writeToFile("test.txt", "This is a test file content.");
            System.out.println("File written successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to file", e); // 简单的日志记录
        }

        // 测试权限检查
        System.out.println("Admin permission: " + calculator.checkPermission("admin"));
        System.out.println("Guest permission: " + calculator.checkPermission("guest"));
        System.out.println("Unknown role permission: " + calculator.checkPermission("developer"));
    }
}