/*MIT License

Copyright (c) 2017 Apogee Research

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Side Effects are not Considered
public class Category6_vulnerable {
    private static final int port = 8000;
    private static final int maxLength = 10;
    private static final String password = "zzzzzzzzzz";
    //This is the worst case password, for shorter passwords there would be padding at the end.
    private static final int passwordLen = 10;
    private static ServerSocket server;

    private static void printError(PrintWriter out) {
        out.println("ERROR");
        out.flush();
    }

    private static void delay() {
        long start = System.nanoTime();
        while (System.nanoTime() - start < 1140000) {
        }
    }

    private static boolean checkChar(String candidate, int index) {
        return password.charAt(index) == candidate.charAt(index);
    }

    private static boolean verifyCredentials(String candidate, PrintWriter out) {
        boolean output;
        boolean printedError = false;
        boolean lengthOK = candidate.length() == passwordLen;
        for (int x = 0; x < candidate.length(); x++) {
            output = !(checkChar(candidate, x) || printedError);
            if (output) {
                printError(out);
                printedError = true;
            }
            delay();
        }
        if (!printedError && !lengthOK) {
            printError(out);
            return false;
        }
        return !printedError;
    }

    private static void startServer() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server Started Port: " + port);
            Socket client;
            PrintWriter out;
            BufferedReader in;
            String userInput;
            boolean correct;
            while (true) {
                client = server.accept();
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                userInput = in.readLine();
                if (userInput.length() <= maxLength && userInput.matches("[a-z]+")) {
                    correct = verifyCredentials(userInput, out);
                    delay();
                    if (correct) {
                        out.println("Correct");
                    } else {
                        out.println("Incorrect");
                    }
                } else {
                    out.println("Invalid Input");
                }
                client.shutdownOutput();
                client.shutdownInput();
                client.close();
            }
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}
