package com.ifcbrusque.app.util.network;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException() {
        super("Internet não disponível");
    }
}
