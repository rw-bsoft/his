package phis.application.ccl.source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class QybrDaoImpl{
	private JDBCUtilSingle  jdbc=JDBCUtilSingle.getInitJDBCUtil();

	
	public List<Map<String, Object>> findQyMap(String sql){
		List<Map<String, Object>> list =new ArrayList<Map<String,Object>>();
		ResultSetMetaData ns;
		ResultSet res;
		try{
			Connection con = null;
			////������ʱ��pdjh_ghmx  
			try {
				con=jdbc.getConnection();
				res=con.createStatement().executeQuery(sql);
				ns =res.getMetaData();
				while (res.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for(int i=1;i<=ns.getColumnCount();i++){
						String key =ns.getColumnName(i);
						int type = ns .getColumnType(i); 
						//System.out.println(ns.getColumnName(i)+"==="+ns.getColumnType(i)+"==="+ns.getColumnTypeName(i));
						// else if (Types.DOUBLE == type) {
				    	  //map.put(key,res.getDouble(key));
				      //}
					    if (Types.VARCHAR == type) {
					    	 map.put(key,res.getString(key));
					      }else if (Types.INTEGER == type) {
					    	  map.put(key,res.getInt(key)); 
					      }else if (Types.DATE == type) {
					    	  map.put(key,res.getDate(key)); 
					      }else if(Types.LONGVARCHAR==type){
					    	  map.put(key, res.getString(key));
					      }
					}
					list.add(map);
				}
				
				
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/***
	 * by yzg
	 * @param sql
	 * @return ��ȡ������
	 */
	public int getTotalNumber (String sql){
		int total=0;
		Connection con = null;
		Statement smt =null;
		ResultSet res =null;
		try {
			con=jdbc.getConnection();
			 smt = con.createStatement();
			 res =smt.executeQuery(sql);
			while (res.next()) {
				total = res.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeResultSet(res);
			closeStatement(smt);
			closeCon(con);
		}
		return total;
	}
	/**
	 * by yzg
	 * @param con
	 * ���
	 */
	public void closeCon(Connection con){
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public void closeResultSet(ResultSet res){
		if (res != null) {
			try {
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void closeStatement(Statement smt){
		if (smt != null) {
			try {
				smt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
