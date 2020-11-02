package chis.source.task;

import chis.source.BaseDAO;

public class AbstractTaskListModelFactory {
	
	public static AbstractTaskListModelFactory factory = null;
	

	public static AbstractTaskListModelFactory getInstance() {
		if(factory == null){
			factory =  new AbstractTaskListModelFactory();
		}
		return factory;
	}
	
	public AbstractTaskListModel createTaskLiskModel(BaseDAO dao,String type){
		if(type.equals("1")){
			return new CommonTaskListModel(dao);
		}else if(type.equals("2")){
			return new MHCTaskListModel(dao);
		}else if(type.equals("3")){
			return new CDHTaskListModel(dao);
		}else{
			return null;
		}
		
	}
}
