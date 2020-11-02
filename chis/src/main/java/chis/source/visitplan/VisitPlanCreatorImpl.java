/*
 * @(#)VisitPlanCreatorImpl.java Created on 2012-3-1 上午9:40:51
 *
 * 版权：版权所有 B-Soft 保留所有权力。
 */
package chis.source.visitplan;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.visitplan.adapter.AdapterFactory;
import chis.source.visitplan.adapter.BusinessAdapter;
import chis.source.visitplan.precondition.Precondition;
import chis.source.visitplan.schedule.PlanSchedule;
import chis.source.visitplan.schedule.PlanScheduleArranger;
import chis.source.visitplan.schedule.PlanScheduleProcessor;

import ctd.controller.exception.ControllerException;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class VisitPlanCreatorImpl implements VisitPlanCreator {

	private static final Logger logger = LoggerFactory
			.getLogger(VisitPlanCreator.class);

	private AdapterFactory factory;
	private PlanScheduleArranger planScheduleArranger;
	private PlanScheduleProcessor planScheduleProcessor;
	private List<Precondition> preconditions;


	/**
	 * 各档案随访计划生成需要传的字段：<br/>
	 * empiId,recordId(各档案的编号，如孕妇档案的pregnantId，体弱儿童档案的recordId等），businessType（BusinessType字典类中各代码）<br/>
	 * 以上字段是必须要传的。<br/>
	 * 1、高血压：fixGroupDate(字符串),groupCode,visitResult,lastVisitDate(可选,字符串),lastPlanDate(可选,字符串)<br/>
	 * 2、高血压询问：visitDate(日期),nextPlanDate(可选，日期)，nextPlanId(可选)<br/>
	 * 3、糖尿病：fixGroupDate(字符串),visitResult,groupCode,lastVisitDate(可选,字符串),lastPlanDate(可选,字符串)<br/> 
	 * 4、老年人：birthday(字符串) <br/> 
	 * 5、儿童（体格检查和询问）：birthday(字符串) <br/>
	 * 6、体弱儿童：debilityReason,birthday,visitDate(可选,字符串) <br/>
	 * 7、孕妇产检：lastMenstrualPeriod(字符串),createDate(日期型),isCreateRecord(布尔值) <br/> 
	 * 8、孕妇高危：lastMenstrualPeriod(字符串),highRisknesses(高危因素，List<Map<String, Object>>类型),createDate(日期型),isCreateRecord(布尔值)<br/> 
	 * 9、精神病：planBeginDate(字符串),lastVisitDate(日期),visitType,referral,isReferral
	 * 15、肿瘤高危人群：createDate(建档日期)，highRiskType(高危类别)、managerGroup(当前管理组别)、oldManagerGroup(原管理组别)、lastVisitDate(可选,字符串),lastPlanDate(可选,字符串)<br/>
	 * 16、肿瘤患者随访：createDate(建档日期)，group(当前管理组别)、oldGroup(原管理组别)、lastVisitDate(可选,字符串),lastPlanDate(可选,字符串)<br/>
	 * 当使用按下次预约方式生成时，必须要传以下参数：<br/>
	 * reserveDate(预约的日期，日期) <br/>
	 * 孕妇模块还需要传：lastMenstrualPeriod(字符串) <br/> 
	 * @throws ServiceException 
	 * @throws ControllerException 
	 * @see chis.source.visitplan.VisitPlanCreator#create(java.util.Map, ctd.util.context.Context)
	 */
	public PlanType create(Map<String, Object> params, Context ctx)
			throws CreateVisitPlanException, ServiceException, ControllerException {
		CreatationParams cp = new CreatationParams();
		cp.setValues(params);
		String businessType = cp.getBusinessType();
		BusinessAdapter adapter = factory.getAdapter(businessType);
		if (preconditions != null) {
			for (Precondition pcon : preconditions) {
				if (!pcon.satisfied(cp, adapter, ctx)) {
					logger.warn("Cannot create visit plan, some precondition was not satisfied.");
					return null;
				}
			}
		}
		PlanSchedule schedule = planScheduleArranger.arrange(cp, adapter, ctx);
		planScheduleProcessor.process(cp, adapter, schedule, ctx);

		return (PlanType) cp.getObjectValue("$planType");
	}

	/**
	 * @return
	 */
	public AdapterFactory getAdapterFactory() {
		return factory;
	}

	public void setAdapterFactory(AdapterFactory factory) {
		this.factory = factory;
	}

	public PlanScheduleArranger getPlanScheduleArranger() {
		return planScheduleArranger;
	}

	public void setPlanScheduleArranger(
			PlanScheduleArranger planScheduleArranger) {
		this.planScheduleArranger = planScheduleArranger;
	}

	public PlanScheduleProcessor getPlanScheduleProcessor() {
		return planScheduleProcessor;
	}

	public void setPlanScheduleProcessor(
			PlanScheduleProcessor planScheduleProcessor) {
		this.planScheduleProcessor = planScheduleProcessor;
	}

	public List<Precondition> getPreconditions() {
		return preconditions;
	}

	public void setPreconditions(List<Precondition> preconditions) {
		this.preconditions = preconditions;
	}
}
