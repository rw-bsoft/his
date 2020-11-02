package phis.application.ccl.source;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
/**
 * 
 * @author 罗盛力 JDBC辅助类 用于构建数据库连接（采用单例模式）
 */
public final class JDBCUtilSingle {
    // 该url为缺省方式（省略主机跟端口）
    // bhis  dg
	 static String url = "jdbc:sqlserver://10.85.3.31:1433;DatabaseName=XCQHZK";//RADINFOHB
	 static String name = "RADINFO";
	 static String password = "PACS";
   /* static String url = "jdbc:oracle:thin:@10.85.3.10:1521/oracle";
    static String name = "RADINFO";
    static String password = "PACS";*/
   /* static String url = "jdbc:oracle:thin:@localhost:1521/MYORCL";
    static String name = "phis";
    static String password = "phis";*/
    static Connection conn = null;
    private static JDBCUtilSingle jdbcUtilSingle = null;
 
    public static JDBCUtilSingle getInitJDBCUtil() {
        if (jdbcUtilSingle == null) {
            // 给类加锁 防止线程并发
            synchronized (JDBCUtilSingle.class) {
                if (jdbcUtilSingle == null) {
                    jdbcUtilSingle = new JDBCUtilSingle();
                }
            }
        }
        return jdbcUtilSingle;
    }
 
    private JDBCUtilSingle() {
    }
 
    // 通过静态代码块注册数据库驱动，保证注册只执行一次
    static {
        try {
            // 注册驱动有如下方式：
            // 1.通过驱动管理器注册驱动，但会注册两次，并且会对类产生依赖。如果该类不存在那就报错了。
            // DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            // 2.与3类似
            // System.setProperty("jdbc.drivers","com.mysql.jdbc.Driver");
            Class.forName("oracle.jdbc.driver.OracleDriver");// 推荐使用方式
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
 
    // 获得连接
    public Connection getConnection() {
        try {
            conn = DriverManager.getConnection(url,name,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
 
    }
 
    // 关闭连接
    public void closeConnection(ResultSet rs, Statement statement, Connection con) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
 
}