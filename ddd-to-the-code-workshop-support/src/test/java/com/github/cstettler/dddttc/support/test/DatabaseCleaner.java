package com.github.cstettler.dddttc.support.test;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.springframework.jdbc.support.JdbcUtils.extractDatabaseMetaData;
import static org.springframework.test.context.junit.jupiter.SpringExtension.getApplicationContext;

public class DatabaseCleaner implements AfterEachCallback {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        JdbcTemplate jdbcTemplate = getApplicationContext(context).getBean(JdbcTemplate.class);
        DataSource dataSource = jdbcTemplate.getDataSource();

        extractDatabaseMetaData(dataSource, (databaseMetaData) -> {
            truncateTables(jdbcTemplate, databaseMetaData);

            return null;
        });
    }

    @SuppressWarnings("SqlResolve")
    private static void truncateTables(JdbcTemplate jdbcTemplate, DatabaseMetaData databaseMetaData) throws SQLException {
        ResultSet resultSet = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"});

        while (resultSet.next()) {
            String tableName = resultSet.getString(3);

            jdbcTemplate.execute("DELETE FROM \"" + tableName + "\"");
        }
    }

}
