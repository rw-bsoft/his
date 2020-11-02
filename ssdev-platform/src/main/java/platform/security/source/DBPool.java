package platform.security.source;


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ctd.util.AppContextHolder;

public class DBPool {
	private static DBPool dbPool;
//	private ComboPooledDataSource dataSource;
	private DataSource dataSource;
	public DBPool() {
		try {
			dataSource=(DataSource)AppContextHolder.get().getBean("dataSource4log");
		/*  String configpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			configpath=configpath.replace("classes/", "config/spring/db.properties");
			InputStream in = new FileInputStream(configpath);
			Properties p=new Properties();
			p.load(in);
			dataSource = new ComboPooledDataSource();
			dataSource.setUser(p.getProperty("db1.user"));
			dataSource.setPassword(p.getProperty("db1.password"));
			dataSource.setJdbcUrl(p.getProperty("db1.jdbcUrl"));
			dataSource.setDriverClass(p.getProperty("db1.driverClass"));
			dataSource.setInitialPoolSize(Integer.parseInt(p.getProperty("db1.pool.initialPoolSize")));
			dataSource.setMinPoolSize(Integer.parseInt(p.getProperty("db1.pool.minPoolSize")));
			dataSource.setMaxPoolSize(Integer.parseInt(p.getProperty("db1.pool.maxPoolSize")));
			dataSource.setMaxIdleTime(Integer.parseInt(p.getProperty("db1.pool.maxIdleTime")));*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static DBPool getInstance() {
		if (dbPool == null) {
			dbPool = new DBPool();
		}
		return dbPool;
	}

	public final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("无法从数据源获取连接 ", e);
		}
	}

	public static void main(String[] args) throws SQLException {
		Connection con = null;
		try {
			con = DBPool.getInstance().getConnection();
			System.out.println(con);
		} catch (Exception e) {
		} finally {
			if (con != null)
				con.close();
		}
	}
}