package com.martianbank.loan;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class LoanApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
