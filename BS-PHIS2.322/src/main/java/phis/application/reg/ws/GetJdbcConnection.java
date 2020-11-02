package phis.application.reg.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class GetJdbcConnection {
	//1521为连接的端口号 
	private static String url="jdbc:oracle:thin:@32.33.1.77:1521:orcl";
	//oracle数据库的用户名 
	private static String user="phis77";
	//用户名system的密码 
	private static String password="bsoft";
	public static Connection conn;
	public static PreparedStatement ps;
	public static ResultSet rs;
	public static Statement st ;
	//连接数据库的方法  
	public static Connection getConnection(){
		Connection conn = null;  //创建用于连接数据库的Connection对象
		try {
			//初始化驱动包 
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//根据数据库连接字符，名称，密码给conn赋值 
			conn=DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			// TODO: handle exception 
			e.printStackTrace();
		}
		return conn; //返回所建立的数据库连接 
	}
	
	/* 插入数据记录，并输出插入的数据记录数*/
	public static int insert(String sql) {
		conn = getConnection(); // 首先要获取连接，即连接到数据库 
		try {
	//		String sql = "INSERT INTO staff(name, age, sex,address, depart, worklen,wage)"
	//		+ " VALUES ('Tom1', 32, 'M', 'china','Personnel','3','3000')";  // 插入数据的sql语句 
			st = (Statement) conn.createStatement();// 创建用于执行静态sql语句的Statement对象 
			int count = st.executeUpdate(sql);  // 执行插入操作的sql语句，并返回插入数据的个数 
			System.out.println("插入 " + count + " 条数据"); //输出插入操作的处理结果 
			conn.close();   //关闭数据库连接
			return count;
			
		} catch (SQLException e) {
			System.out.println("插入数据失败" + e.getMessage());
		}
		return 0;
	}
	
	/* 更新符合要求的记录，并返回更新的记录数目*/
	public static int update(String sql) {
		conn = getConnection(); //同样先要获取连接，即连接到数据库 
		try {
	//		String sql = "update staff set wage='2200' where name = 'lucy'";// 更新数据的sql语句 
			st = (Statement) conn.createStatement();//创建用于执行静态sql语句的Statement对象，st属局部变量 
			int count = st.executeUpdate(sql);// 执行更新操作的sql语句，返回更新数据的个数 
			System.out.println("更新 " + count + " 条数据");  //输出更新操作的处理结果 
			conn.close();   //关闭数据库连接
			return count; 
		} catch (SQLException e) {
			System.out.println("更新数据失败");
		}
		return 0;
	}
	
	/* 查询数据库，输出符合要求的记录的情况*/
	public static Map<String, Object> query(String sql) {
		conn = getConnection(); //同样先要获取连接，即连接到数据库 
		try {
	//		String sql = "select * from staff"; // 查询数据的sql语句 
			st = (Statement) conn.createStatement();//创建用于执行静态sql语句的Statement对象，st属局部变量 
			ResultSet rs = st.executeQuery(sql);//执行sql查询语句，返回查询数据的结果集
//			conn.close();//关闭数据库连接
			if(rs.next()){
				return getResultMap(rs);
			}
		} catch (SQLException e) {
			System.out.println("查询数据失败");
		}
		return null;
	}
	
	/* 删除符合要求的记录，输出情况*/
	public static int delete(String sql) {
		conn = getConnection(); //同样先要获取连接，即连接到数据库 
		try {
	//		String sql = "delete from staff  where name = 'lili'";// 删除数据的sql语句 
			st = (Statement) conn.createStatement();//创建用于执行静态sql语句的Statement对象，st属局部变量 
			int count = st.executeUpdate(sql);// 执行sql删除语句，返回删除数据的数量 
			System.out.println("删除 " + count + " 条数据\n");//输出删除操作的处理结果 
			conn.close();   //关闭数据库连接
			return count;
		} catch (SQLException e) {
		System.out.println("删除数据失败");
		}
		return 0;
	}
	
    @SuppressWarnings("unused")
	private static Map<String, Object> getResultMap(ResultSet rs)  
            throws SQLException {  
        Map<String, Object> hm = new HashMap<String, Object>();  
        ResultSetMetaData rsmd = rs.getMetaData();  
        int count = rsmd.getColumnCount();  
        for (int i = 1; i <= count; i++) {  
            String key = rsmd.getColumnLabel(i);  
            Object value = rs.getObject(i);  
            hm.put(key, value);  
        }  
        return hm;  
    }
}
