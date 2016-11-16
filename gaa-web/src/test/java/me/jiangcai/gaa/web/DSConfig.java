package me.jiangcai.gaa.web;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CJ
 */
class DSConfig {

    private Connection tempDatabaseConnection() throws ClassNotFoundException, SQLException {

        Class.forName("org.h2.Driver");

        Connection connection = DriverManager.getConnection("jdbc:h2:mem:init;DB_CLOSE_DELAY=-1", "", "");
//        connection.setAutoCommit(true);

        return connection;
    }

    @Bean
    public DataSource dataSource() throws IOException {
        //
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:target/gaa");
        dataSource.setUrl("jdbc:h2:mem:gaa;DB_CLOSE_DELAY=-1");
        return dataSource;
    }
}
