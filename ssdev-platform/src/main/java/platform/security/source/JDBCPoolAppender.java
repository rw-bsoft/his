package platform.security.source;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.jdbc.JDBCAppender;

public class JDBCPoolAppender extends JDBCAppender {
	private DBPool poll=null;
	private Connection con=null;
	public JDBCPoolAppender() {
		super();
	}

	/**
	 * 重载JDBCAppender的getConnection()方法
	 */
	@Override
	protected Connection getConnection() throws SQLException {
		poll=DBPool.getInstance();
		con=poll.getConnection();
		return con;
	}

	/**
	 * 重载getLogStatement()方法， 在SQL字符串最后添加用户ID等信息
	 */
	/*protected String getLogStatement(LoggingEvent event) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(layout.format(event));

		if (event.getMessage() instanceof ParameterizedMessage) {

			// 检测SQL的最后一个字符是不是逗号，如果不是，则在这里补上
			if (stringBuffer.charAt(stringBuffer.length() - 1) != ',')
				stringBuffer.append(",");

			Object[] params = ((ParameterizedMessage) event.getMessage())
					.getParameters();
			for (int i = 1; i < params.length; i++) {
				stringBuffer.append(params[i]);
				stringBuffer.append(",");
			}

			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
			stringBuffer.append(")");
		}
		return stringBuffer.toString();
	}*/

	/**
	 * 重载JDBCAppender的方法，取消与数据库的连接
	 */
	
	@Override
	protected void closeConnection(Connection con) {
		try {
			if (con != null && !con.isClosed())
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
