package com.tuling.jta;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
 
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Author Fox
 */
public class AtomikosDemo {
 
   private static AtomikosDataSourceBean createAtomikosDataSourceBean(String dbName) {
      // 连接池基本属性
      Properties p = new Properties();
      p.setProperty("url", "jdbc:mysql://192.168.190.210:3306/" + dbName);
      p.setProperty("user", "root");
      p.setProperty("password", "123456");
 
      // 使用AtomikosDataSourceBean封装com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
      AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
      //设置resourceName 唯一
      ds.setUniqueResourceName(dbName);
      //mysql8 驱动
      ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
      //mysql 5.7 驱动
//      ds.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
      ds.setXaProperties(p);
      return ds;
   }
 
   public static void main(String[] args) {
 
      AtomikosDataSourceBean ds1 = createAtomikosDataSourceBean("db_user");
      AtomikosDataSourceBean ds2 = createAtomikosDataSourceBean("db_account");
 
      Connection conn1 = null;
      Connection conn2 = null;
      PreparedStatement ps1 = null;
      PreparedStatement ps2 = null;
 
      UserTransaction userTransaction = new UserTransactionImp();
      try {
         // 开启事务
         userTransaction.begin();
 
         // 执行db1上的sql
         conn1 = ds1.getConnection();
         ps1 = conn1.prepareStatement("INSERT into user(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
         ps1.setString(1, "Fox");
         ps1.executeUpdate();
         ResultSet generatedKeys = ps1.getGeneratedKeys();
         int userId = -1;
         while (generatedKeys.next()) {
            // 获得自动生成的userId
            userId = generatedKeys.getInt(1);
         }
 
         // 模拟异常 ，直接进入catch代码块，2个都不会提交
        //int i=1/0;
 
         // 执行db2上的sql
         conn2 = ds2.getConnection();
         ps2 = conn2.prepareStatement("INSERT into account(user_id,money) VALUES (?,?)");
         ps2.setInt(1, userId);
         ps2.setDouble(2, 10000000);
         ps2.executeUpdate();
 
         // 两阶段提交
         userTransaction.commit();
      } catch (Exception e) {
         try {
            e.printStackTrace();
            userTransaction.rollback();
         } catch (SystemException e1) {
            e1.printStackTrace();
         }
      } finally {
         try {
            ps1.close();
            ps2.close();
            conn1.close();
            conn2.close();
            ds1.close();
            ds2.close();
         } catch (Exception ignore) {
         }
      }
   }
}