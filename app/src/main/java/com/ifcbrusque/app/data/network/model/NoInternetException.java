package com.ifcbrusque.app.data.network.model;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException() {
        super("Internet não disponível");
    }
}
