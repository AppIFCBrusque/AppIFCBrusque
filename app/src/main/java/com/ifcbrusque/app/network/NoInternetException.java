package com.ifcbrusque.app.network;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException() {
        super("Internet não disponível");
    }
}
