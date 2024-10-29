package br.com.alura.screenMatch.service;

public interface iConverteDados {
    <T> T obterDados(String json, Class<T> tClass);
}
