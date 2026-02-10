package com.vinicius.notificacao_api.infrastructure.excecptions;

public class EmailException extends RuntimeException{
    public EmailException(String message){super(message);}
    public EmailException(String message, Throwable throwable){super(message,throwable);}
}
