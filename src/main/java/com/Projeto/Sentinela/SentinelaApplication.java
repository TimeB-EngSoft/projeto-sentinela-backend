package com.projeto.sentinela;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class SentinelaApplication implements CommandLineRunner {

    private final DataSource dataSource;

    public SentinelaApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(SentinelaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 Testando conexão com o banco de dados...");

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                System.out.println("✅ Conexão com o banco de dados estabelecida com sucesso!");
                System.out.println("URL: " + connection.getMetaData().getURL());
                System.out.println("Usuário: " + connection.getMetaData().getUserName());
            } else {
                System.out.println("❌ Falha ao validar a conexão com o banco de dados.");
            }
        } catch (Exception e) {
            System.err.println("🚨 Erro ao conectar ao banco de dados:");
            e.printStackTrace();
        }
    }
}
