package chis.source.dr.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ctd.dao.exception.DataAccessException;


public abstract interface IDAO
{
  public abstract Map<String, Object> load(String paramString, Serializable paramSerializable)
    throws DataAccessException;
  
  public abstract int queryForCount(String paramString1, String paramString2, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract int queryForCount(String paramString1, String paramString2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract Map<String, Object> queryForMap(String paramString1, String paramString2, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract Map<String, Object> queryForMap(String paramString1, String paramString2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract Map<String, Object> queryForMap(String paramString, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract Map<String, Object> queryForMap(String paramString, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForList(String paramString1, String paramString2, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForList(String paramString1, String paramString2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForList(String paramString, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForList(String paramString, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForPage(String paramString1, String paramString2, int paramInt1, int paramInt2, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForPage(String paramString1, String paramString2, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForPage(String paramString, int paramInt1, int paramInt2, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract List<Map<String, Object>> queryForPage(String paramString, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract void update(String paramString, Map<String, Object> paramMap)
    throws DataAccessException;
  
  public abstract int update(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract int delete(String paramString1, String paramString2, String paramString3)
    throws DataAccessException;
  
  public abstract int delete(String paramString1, String paramString2, Object[] paramArrayOfObject)
    throws DataAccessException;
  
  public abstract void save(String paramString, Map<String, Object> paramMap)
    throws DataAccessException;
}
