package uk.co.inhealthcare.open.itk.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
 
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
 
public class AuditDBConnectionFactory {
 
    private static SqlSessionFactory sqlSessionFactory;
 
    static {
        try {
 
            String resource = "auditdb/SqlMapConfig.xml";
            Reader reader = Resources.getResourceAsReader(resource);
 
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
 
    public static SqlSessionFactory getSqlSessionFactory() {
 
        return sqlSessionFactory;
    }
}