/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import devlang.lexer.Lexer;
import devlang.parser.Parser;
import devlang.parser.nodes.Node;
import java.io.ByteArrayInputStream;
import devlang.compiler.Compiler;
import devlang.compiler.DevObjectInteger;
import devlang.lexer.tokens.Token;
import devlang.lexer.tokens.TokenType;
import devlang.vm.VM;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Scanner;

/*
a = 5

if (a == 5) {
    print(a)
} else {
    print("a is not 5")
}

for (i = 0; i < 10; ++i) {
    print(i)
}

*/

/*
/ a = 5
LOAD_CONST        0 (5)
STORE_VAR         0 (a)

// if (a == 5) {
LOAD_VAR          0 (a)
LOAD_CONST        0 (5)
COMPARE           0 (==)
POP_JUMP_IF_FALSE
LOAD_VAR          0 (a)
CALL

*/

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            File f = new File(args[0]);
            FileInputStream fis = new FileInputStream(f);
            byte[] b = new byte[8128 * 10];
            fis.read(b);
            Lexer lex = new Lexer(new ByteArrayInputStream(b));
            Parser parser = new Parser(lex);
            Compiler compiler = new Compiler(parser);
            VM vm = new VM(compiler.compile());
            vm.run();
            return;
        }

        Scanner s = new Scanner(System.in);
        //String fn = s.nextLine();



        String[] tests = new String[] {
            // Test integer math
            "print(5+5)",
            "print(5/2)",
            "print(5*5)",
            "print(5-5)",
            "print(5+5)",

            // Test float math
            "print(5.0+8.2)",
            "print(5.0/2.5)",
            "print(5.2*5.8)",
            "print(5.0-3.5)",

            // Test integer/float math
            "print(5+3.2)",
            "print(5/2.5)",
            "print(5*1.5)",
            "print(5-1.2)",
            "print(5+0.5)",

            // Test float/integer math
            "print(3.2+5)",
            "print(2.5/5)",
            "print(1.5*5)",
            "print(1.2-5)",

            // Test string math
            "print('asd'+'dsa')",
            "print(true)",
            "print(1.5*5)",
            "print(1.2-5)",

            // Some difficult stuff for VM
            "a = 5 if (a == 5) {print('a is five')} else {print('a is not five')}",
            "a = 5 while (a > 0) {print(a) a = a-1} print('end')",
            "for (i = 0; i < 5; for (j = 0; j < 5; j = j+1) {print('j')} i=i+1) {print('i')}",
            "for (i = 0; i < 5; i = 5 ) {print('i')}",
            "function p(a) {return a} print(p('a'))",
            "function test(a,b) { print(a) print(b) } for (i = 0; i < 10; i++) { test(i, i-10) }",
            //"function gcd(a,b) {if (b == 0) { return a } else { return gcd(b, a%b)} } for (l = 0; l < 10; l++){ for (i = 0; i < 1000; i++) {for (j = 0; j < 1000; j++) {a = gcd(10,3)}}}",
            //"for (l = 0; l < 10; l++) {for (i = 0; i < 1000; i++) {for (j = 0; j < 1000; j++) {a = 10 b = 3 while (b != 0) { t = b b=a%b a=t }}}}",
            "if (1 == 0) {print('ok')}",
            "function a(b) { print(b) } a(5)",
            "a = 5 a = 5 b = a a a a 65 6 6 false true",
            "print('asd' + 5) print(5 + 'asd')",
            "for (i = 0; i < 5; for (j = 0; j < 5; j++) {print('j')} i++) {print('i')}"
        };

        for (String test : tests) {
            System.out.println(test);
            System.out.println();


            Lexer lex = new Lexer(new ByteArrayInputStream(test.getBytes()));
            Parser parser = new Parser(lex);
            Compiler compiler = new Compiler(parser);
            VM vm = new VM(compiler.compile());

            int run = 1000;
            long sum = 0;

            //for (int i = 0; i < run; i++) {
            boolean a = true;
            //while (a) {
                long start = System.currentTimeMillis();
                vm.dis();
                vm.run();
                long end = System.currentTimeMillis();
                System.out.println(end-start);
            //}
            //}

            //System.out.println(DevObjectInteger.n);
            System.out.println("--------------");
        }

        /*
        String input = "if (1 != 0) {print('a')}";
        Lexer lex = new Lexer(new ByteArrayInputStream(input.getBytes()));

        //Parser parser = new Parser(lex);
        //inorder(parser.parse());
        Token tok = null;
        do {
            tok = lex.next();
            System.out.println(tok);
        } while (tok.type != TokenType.END);
        */

        /*lex = new Lexer(new ByteArrayInputStream(input.getBytes()));

        Parser parser = new Parser(lex);
        Compiler compiler = new Compiler(parser);
        VM vm = new VM(compiler.compile());
        vm.run();*/

        //ystem.out.println(inorder(parser.parse()));
        //System.out.println(inorder(parser.parse()));
        //new Main();
    }

    public Main() {
        int run = 1000;
        int a,b,t;
        long start = System.currentTimeMillis();
        for (int j = 0; j < 10; ++j) {
            for (int i = 0; i < 1000; ++i) {
                for (int k = 0; k < 1000; ++k) {
                    /*a = 10;
                    b = 3;
                    while (b != 0) {
                        t = b;
                        b = a%b;
                        a = t;
                    }*/
                    gcd(10, 3);
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    public int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a%b);
        }
    }

    public static int inorder(Node node) {
        if (node == null) {
            return 0;
        }

        inorder(node.left);
        System.out.println(node);
        inorder(node.right);

        /*
        if (node.type == NodeType.NUMBER) {
            return ((NodeNumber)node).value;
        }

        if (node.type == NodeType.OPERATOR) {
            String op = ((NodeOperator)node).value;
            if (op == "+") {
                return inorder(node.left) + inorder(node.right);
            }

            if (op == "*") {
                return inorder(node.left) * inorder(node.right);
            }

            if (op == "-") {
                return inorder(node.left) - inorder(node.right);
            }

            if (op == "/") {
                return inorder(node.left) / inorder(node.right);
            }

            if (op == "=") {
                NodeIdentifier nid = (NodeIdentifier)node.left;

                System.out.println(node.left+" "+node.right);
                inorder(node.right);
            }
        }
        */

        return 0;
    }

}
