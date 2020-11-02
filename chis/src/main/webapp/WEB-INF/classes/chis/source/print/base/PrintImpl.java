package chis.source.print.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.hibernate.SessionFactory;
import chis.source.Constants;
import ctd.controller.exception.ControllerException;
import ctd.dictionary.DictionaryController;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

public abstract class PrintImpl implements IHandler{
	
	protected void dicKeyToText(Map<String, Object> r, String dic, String field) throws PrintException{
		if(r.get(field) == null){
			return;
		}
		String key = r.get(field).toString();
		String text =  null;
		try {
			text = DictionaryController.instance().get(dic).getText(key);
		} catch (ControllerException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		r.put(field, text);
	}
	
	protected String dicKeyToText(String dic, String key) throws PrintException{
		String text = null;
		try {
			text = DictionaryController.instance().get(dic).getText(key);
		} catch (ControllerException e) {
			throw new PrintException(Constants.CODE_EXP_ERROR,e.getMessage());
		}
		return text;
	}

	@SuppressWarnings({ "deprecation" })
	protected BasicDynaClass autoCreateBean(JasperDesign design){
		JRBand detialBand = design.getDetailSection().getBands()[0];
		List<JRChild> els = detialBand.getChildren();
		Iterator<JRChild> it = els.iterator();
		List<DynaProperty> list = new ArrayList<DynaProperty>();
		while(it.hasNext()){
			JRChild el = it.next();
			if(el instanceof JRDesignTextField){
				JRDesignTextField f = (JRDesignTextField) el;
				if(f.getExpression() == null || f.getExpression().getText().length()<5){
					continue;
				}
				String pName = getExpressionValue(f.getExpression().getText());
				DynaProperty p = new DynaProperty(pName, f.getExpression().getValueClass());
				list.add(p);
			}
		}
		DynaProperty[] ps = (DynaProperty[]) list.toArray();
		BasicDynaClass dynaClass = new BasicDynaClass("PrintBean",BasicDynaBean.class, ps);
		return dynaClass;
	}
	
	protected DynaBean fillDataIntoBean(BasicDynaClass dynaClass,Map<String, Object> data) throws IllegalAccessException, InstantiationException{
		DynaBean bean = dynaClass.newInstance();
		for(Iterator<String> it=data.keySet().iterator();it.hasNext();){
			String key = it.next();
			Object value = data.get(key);
			bean.set(key, value);
		}
		return bean;
	}
	
	protected SessionFactory getSessionFactory(Context ctx){
		return (SessionFactory) AppContextHolder.get()
				.getBean("mySessionFactory");
	}
	
	private String getExpressionValue(String str){
		return str.substring(3, str.length()-1);
	}
	
}
