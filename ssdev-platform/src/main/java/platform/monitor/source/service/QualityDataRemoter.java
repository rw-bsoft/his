package platform.monitor.source.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.AppContextHolder;
import ctd.util.S;
import ctd.util.annotation.RpcService;

@SuppressWarnings("unchecked")
public class QualityDataRemoter implements QualityDataUpload, Callable<Boolean>{
	private static String QualityData_Main = "QualityData_Main";
	private static String QualityData_Error = "QualityData_Error";
	private static String QualityData_ErrorNumb = "QualityData_ErrorNumb";
	private static final Logger LOGGER = LoggerFactory.getLogger(QualityDataRemoter.class);
	private final Lock lock = new ReentrantLock();
	private final Lock lock1 = new ReentrantLock();
	private static ExecutorService es = Executors.newFixedThreadPool(10);
	private int batchSize = 100;
	private int op;
	private List<Map<String, Object>> list;
	
	public QualityDataRemoter() {
		
	}
	
	public QualityDataRemoter(int op, List<Map<String, Object>> list) {
		this.op = op;
		this.list = list;
	}
	
	public static void main(String[] args) {
		int batchSize = 100;
		List<String> list = new ArrayList<String>();
		for(int i=0;i<100;i++){
			list.add(""+(i+1));
		}
		int total = list.size();
		int size = list.size()/batchSize;
		int cursor = 0;
		for(int i=0;i<size;i++){
			List<String> ls = list.subList(i*batchSize, (i+1)*batchSize);
			System.out.println(ls);
			cursor = (i+1)*batchSize;
		}
		if(cursor < total){
			List<String> ls = list.subList(cursor, total);
			System.out.println(ls);
		}
	}
	
	@RpcService
	@Override
	public Boolean uploadData(List<Map<String, Object>> list) {
		lock.lock();
		try {
			int total = list.size();
			LOGGER.info("qua data begin upload.. total size is {}", total);
			int size = total/batchSize;
			int cursor = 0;
			for(int i=0;i<size;i++){
				Future<Boolean> f = es.submit(new QualityDataRemoter(1, list.subList(i*batchSize, (i+1)*batchSize)));
				cursor = (i+1)*batchSize;
				Boolean b = f.get();
				if(b == false){
					throw new Exception((i*batchSize+1)+"-"+(i+1)*batchSize+" upload error.");
				}else{
					LOGGER.info("upload qua data {} - {} finished.",i*batchSize+1,(i+1)*batchSize);
				}
			}
			if(cursor < total){
				Future<Boolean> f = es.submit(new QualityDataRemoter(1, list.subList(cursor, total)));
				Boolean b = f.get();
				if(b == false){
					throw new Exception((cursor+1)+"-"+total+" upload error.");
				}else{
					LOGGER.info("upload qua data {} - {} finished.",cursor+1,total);
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			lock.unlock();
		}
	}

	@RpcService
	@Override
	public Boolean uploadUnFinishData(List<Map<String, Object>> list) {
		lock1.lock();
		try {
			int total = list.size();
			LOGGER.info("qua data begin upload.. total size is {}", total);
			int size = total/batchSize;
			int cursor = 0;
			for(int i=0;i<size;i++){
				Future<Boolean> f = es.submit(new QualityDataRemoter(2, list.subList(i*batchSize, (i+1)*batchSize)));
				cursor = (i+1)*batchSize;
				Boolean b = f.get();
				if(b == false){
					throw new Exception((i*batchSize+1)+"-"+(i+1)*batchSize+" upload error.");
				}else{
					LOGGER.info("upload qua data {} - {} finished.",i*batchSize+1,(i+1)*batchSize);
				}
			}
			if(cursor < total){
				Future<Boolean> f = es.submit(new QualityDataRemoter(2, list.subList(cursor, total)));
				Boolean b = f.get();
				if(b == false){
					throw new Exception((cursor+1)+"-"+total+" upload error.");
				}else{
					LOGGER.info("upload qua data {} - {} finished.",cursor+1,total);
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			lock1.unlock();
		}
	}
	
	private void ifNotNullThenPutInMap(Object v, String k, Map<String, Object> m){
		if(v != null){
			m.put(k, v);
		}
	}
	
	
	@SuppressWarnings("unused")
	private void ifNotNullThenPlus(Integer num, String k, Map<String, Object> m){
		if(num != null){
			Integer original = (Integer) m.get(k);
			if(original == null){
				original = 0;
			}
			m.put(k, original+num);
		}
	}
	
	private void uploadErrors(String StageType,Map<String,Object> stageFailDetail,
			String RecordClassifying,String Authororganization,Date StatDate,Date UploadTime,Session ss){
		if(stageFailDetail == null){
			return;
		}
		Query dq = ss.createQuery("delete "+QualityData_Error+" a where a.RecordClassifying=? and a.Authororganization=? and a.StatDate=? and a.StageType=?");
		dq.setString(0, RecordClassifying);
		dq.setString(1, Authororganization);
		dq.setDate(2, StatDate);
		dq.setString(3, StageType);
		dq.executeUpdate();
		for(String ErrorType:stageFailDetail.keySet()){
			Integer ErrorCount = (Integer) stageFailDetail.get(ErrorType);
			Map<String,Object> error=new HashMap<String, Object>();
			error.put("RecordClassifying", RecordClassifying);
			error.put("Authororganization", Authororganization);
			error.put("StatDate", StatDate);
			error.put("StageType", StageType);
			error.put("ErrorType", ErrorType);
			error.put("ErrorCount", ErrorCount);
			if(UploadTime != null){
				error.put("UploadTime", UploadTime);
			}
			ss.save(QualityData_Error,error);
		}
	}
	
	private void uploadErrorsByNum(String StageType,Map<String,Object> stageFailDetailByNum,
			String RecordClassifying,String Authororganization,Date StatDate,Date UploadTime,Session ss){
		if(stageFailDetailByNum == null){
			return;
		}
		Query dq = ss.createQuery("delete "+QualityData_ErrorNumb+" a where a.RecordClassifying=? and a.Authororganization=? and a.StatDate=? and a.StageType=?");
		dq.setString(0, RecordClassifying);
		dq.setString(1, Authororganization);
		dq.setDate(2, StatDate);
		dq.setString(3, StageType);
		dq.executeUpdate();
		for(String ErrorCategory:stageFailDetailByNum.keySet()){
			Integer ErrorCount = (Integer) stageFailDetailByNum.get(ErrorCategory);
			Map<String,Object> error=new HashMap<String, Object>();
			error.put("RecordClassifying", RecordClassifying);
			error.put("Authororganization", Authororganization);
			error.put("StatDate", StatDate);
			error.put("StageType", StageType);
			error.put("ErrorCategory", ErrorCategory);
			error.put("ErrorCount", ErrorCount);
			ss.save(QualityData_ErrorNumb,error);
		}
	}
	
	public Boolean uploadDataBatch(List<Map<String, Object>> list) {
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		Transaction tr = null;
		try {
			ss = sf.openSession();
			tr = ss.beginTransaction();
			for(Map<String, Object> map:list){
				String RecordClassifying = (String) map.get("RecordClassifying");
				String Authororganization = (String) map.get("Authororganization");
				Date StatDate = (Date) map.get("StatDate");
				Map<String,Object> data=(Map<String,Object>) map.get("Data");
				if(S.isEmpty(RecordClassifying) || S.isEmpty(Authororganization) || StatDate == null || data == null){
					throw new Exception("uploadData missing RecordClassifying,Authororganization or StatDate");
				}
				Integer Stage1Success = (Integer) data.get("Stage1Success");
				Integer Stage1Fail = (Integer) data.get("Stage1Fail");
				Integer Stage2Success = (Integer) data.get("Stage2Success");
				Integer Stage2Fail = (Integer) data.get("Stage2Fail");
				Integer Stage3Success = (Integer) data.get("Stage3Success");
				Integer Stage3Fail = (Integer) data.get("Stage3Fail");
				Date Stage1UploadTime = (Date) data.get("Stage1UploadTime");
				Date Stage2UploadTime = (Date) data.get("Stage2UploadTime");
				Date Stage3UploadTime = (Date) data.get("Stage3UploadTime");
				Map<String, Object> Stage1FailDetail = (Map<String, Object>) data.get("Stage1FailDetail");
				Map<String, Object> Stage2FailDetail = (Map<String, Object>) data.get("Stage2FailDetail");
				Map<String, Object> Stage3FailDetail = (Map<String, Object>) data.get("Stage3FailDetail");
				Map<String, Object> Stage1FailDetailByNum = (Map<String, Object>) data.get("Stage1FailDetailByNum");
				Map<String, Object> Stage2FailDetailByNum = (Map<String, Object>) data.get("Stage2FailDetailByNum");
				Map<String, Object> Stage3FailDetailByNum = (Map<String, Object>) data.get("Stage3FailDetailByNum");
				
				Query q = ss.createQuery("from "+QualityData_Main+" a where a.RecordClassifying=? and a.Authororganization=? and StatDate=?");
				q.setString(0, RecordClassifying);
				q.setString(1, Authororganization);
				q.setDate(2, StatDate);
				Map<String, Object> result = (Map<String, Object>) q.uniqueResult();
				
				if(result == null){
					Map<String, Object> rec = new HashMap<String, Object>();
					rec.put("RecordClassifying", RecordClassifying);
					rec.put("Authororganization", Authororganization);
					rec.put("StatDate", StatDate);
					ifNotNullThenPutInMap(Stage1Success, "Stage1Success", rec);
					ifNotNullThenPutInMap(Stage1Fail, "Stage1Fail", rec);
					ifNotNullThenPutInMap(Stage2Success, "Stage2Success", rec);
					ifNotNullThenPutInMap(Stage2Fail, "Stage2Fail", rec);
					ifNotNullThenPutInMap(Stage3Success, "Stage3Success", rec);
					ifNotNullThenPutInMap(Stage3Fail, "Stage3Fail", rec);
					ifNotNullThenPutInMap(Stage1UploadTime, "Stage1UploadTime", rec);
					ifNotNullThenPutInMap(Stage2UploadTime, "Stage2UploadTime", rec);
					ifNotNullThenPutInMap(Stage3UploadTime, "Stage3UploadTime", rec);
					ss.save(QualityData_Main, rec);
				}else{
					ifNotNullThenPutInMap(Stage1Success, "Stage1Success", result);
					ifNotNullThenPutInMap(Stage1Fail, "Stage1Fail", result);
					ifNotNullThenPutInMap(Stage2Success, "Stage2Success", result);
					ifNotNullThenPutInMap(Stage2Fail, "Stage2Fail", result);
					ifNotNullThenPutInMap(Stage3Success, "Stage3Success", result);
					ifNotNullThenPutInMap(Stage3Fail, "Stage3Fail", result);
					ifNotNullThenPutInMap(Stage1UploadTime, "Stage1UploadTime", result);
					ifNotNullThenPutInMap(Stage2UploadTime, "Stage2UploadTime", result);
					ifNotNullThenPutInMap(Stage3UploadTime, "Stage3UploadTime", result);
					ss.update(result);
				}
				uploadErrors("1", Stage1FailDetail, RecordClassifying, Authororganization, StatDate, Stage1UploadTime, ss);
				uploadErrors("2", Stage2FailDetail, RecordClassifying, Authororganization, StatDate, Stage2UploadTime, ss);
				uploadErrors("3", Stage3FailDetail, RecordClassifying, Authororganization, StatDate, Stage3UploadTime, ss);
				uploadErrorsByNum("1", Stage1FailDetailByNum, RecordClassifying, Authororganization, StatDate, Stage1UploadTime, ss);
				uploadErrorsByNum("2", Stage2FailDetailByNum, RecordClassifying, Authororganization, StatDate, Stage2UploadTime, ss);
				uploadErrorsByNum("3", Stage3FailDetailByNum, RecordClassifying, Authororganization, StatDate, Stage3UploadTime, ss);
			}
			tr.commit();
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			if(tr != null && tr.isActive()){
				tr.rollback();
			}
			return false;
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
	}
	
	public Boolean uploadUnFinishDataBatch(List<Map<String, Object>> list) {
		SessionFactory sf = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY, SessionFactory.class);
		Session ss = null;
		Transaction tr = null;
		try {
			ss = sf.openSession();
			tr = ss.beginTransaction();
			for(Map<String, Object> map:list){
				String RecordClassifying = (String) map.get("RecordClassifying");
				String Authororganization = (String) map.get("Authororganization");
				Date StatDate = (Date) map.get("StatDate");
				Integer UnFinishCount = (Integer) map.get("UnFinishCount");
				if(S.isEmpty(RecordClassifying) || S.isEmpty(Authororganization) || StatDate == null || UnFinishCount == null){
					throw new Exception("uploadUnFinishData missing RecordClassifying,Authororganization,StatDate or UnFinishCount.");
				}
				
				Query q = ss.createQuery("from "+QualityData_Main+" a where a.RecordClassifying=? and a.Authororganization=? and StatDate=?");
				q.setString(0, RecordClassifying);
				q.setString(1, Authororganization);
				q.setDate(2, StatDate);
				Map<String, Object> result = (Map<String, Object>) q.uniqueResult();
				if(result == null){
					Map<String, Object> rec = new HashMap<String, Object>();
					rec.put("RecordClassifying", RecordClassifying);
					rec.put("Authororganization", Authororganization);
					rec.put("StatDate", StatDate);
					rec.put("UnFinishCount", UnFinishCount);
					ss.save(QualityData_Main, rec);
				}else{
					result.put("UnFinishCount", UnFinishCount);
					ss.update(result);
				}
			}
			tr.commit();
			return true;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			if(tr != null && tr.isActive()){
				tr.rollback();
			}
			return false;
		} finally {
			if(ss != null && ss.isOpen()){
				ss.close();
			}
		}
	}

	@Override
	public Boolean call() throws Exception {
		if(1 == op){
			return uploadDataBatch(list);
		}
		if(2 == op){
			return uploadUnFinishDataBatch(list);
		}
		return false;
	}
	
}
