package bookmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class Dbutil {
    private static void initializeDatabase(Connection conn) throws SQLException, IOException {

        String schemaFilePath = "schema.sql";

        try(InputStream is = ClassLoader.getSystemResourceAsStream( schemaFilePath)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
            if(is == null){
                throw new IOException("스키마 파일 없음 + " + schemaFilePath);
            }

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if(line.trim().isEmpty() || line.trim().startsWith("--")){
                    continue;
                }
                sqlBuilder.append(line);

                if(line.trim().endsWith(";")){
                    String sqlStatement = sqlBuilder.toString().trim();
                    if(!sqlStatement.isEmpty()){
                        try(Statement stmt = conn.createStatement()){
                            stmt.executeUpdate(sqlStatement);
                            System.out.println("실행됨 "+ sqlStatement);
                        }
                    }
                    sqlBuilder.setLength(0);
                }
            }
            String remainingSql = sqlBuilder.toString().trim();
            if(remainingSql.isEmpty()){
                try(Statement stmt = conn.createStatement()){
                    stmt.executeUpdate(remainingSql);
                    System.out.println("실행됨 "+ remainingSql);
                }
            }

        } catch (IOException e) {
            System.err.println("스키마 파일 읽기 오류: " + e.getMessage());
            throw new SQLException("스키마 파일 읽기 실패", e);
        }
    }

}
