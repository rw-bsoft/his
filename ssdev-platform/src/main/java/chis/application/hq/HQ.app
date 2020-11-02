<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hq.HQ" name="高危筛查" type="0">
	<catagory id="HQ" name="高危筛查表一">
		<module id="HQ01" name="高危人群筛查" script="chis.application.hq.script.HealthQueryMoudle">
			<properties>
				<p name="entryName">chis.application.hq.schemas.HC_HealthCheck</p>
				<p name="healthStatusModifyForm">chis.application.hq.HQ/HQ/HQ0101</p>
			</properties>
			<action id="print" name="导出"/>
			<action id="modifyStatus" name="修改状态" iconCls="update"/>
		</module>
		<module id="HQ02" name="高危人群登记" script="chis.application.hq.script.HealthQueryNextMoudle">
			<properties>
				<p name="entryName">chis.application.hq.schemas.HC_HealthCheck_NEXT</p>
				<p name="refApplyModule">chis.application.hq.HQ/HQ/HQ0201</p>
				<p name="healthStatusModifyForm">chis.application.hq.HQ/HQ/HQ0101</p>
			</properties>
			<action id="management" name="管理" iconCls="create" />
			<action id="print" name="导出"/>
			<action id="modifyStatus" name="修改状态" iconCls="update"/>
		</module>
		<module id="HQ0201" name="高危人群登记表单" type="1" script="chis.application.hq.script.HighRiskManageForm">
			<properties>
				<p name="entryName">chis.application.hq.schemas.MDC_HighRiskRecord</p>
			</properties>
			<action id="savedata" name="确定" iconCls="save" group="update"/>
		</module>
		<module id="HQ03" name="高危人群管理" script="chis.application.hq.script.HighRiskRecordList">
			<properties>
				<p name="entryName">chis.application.hq.schemas.MDC_HighRiskRecord_NEXT</p>
				<p name="healthStatusModifyForm">chis.application.hq.HQ/HQ/HQ0101</p>
			</properties>
			<action id="visit" name="随访" iconCls="hypertension_visit" />
			<action id="print" name="导出"/>
			<action id="modifyStatus" name="修改状态" iconCls="update"/>
		</module>
		<module id="HQ0301" name="高危人群随访" script="chis.application.hq.script.HighRiskVisitModule">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="VisitPlanList" name="随访计划列表" ref="chis.application.hq.HQ/HQ/HQ030101" />
			<action id="VisitBaseForm" name="随访基本信息" ref="chis.application.hq.HQ/HQ/HQ030102" type="tab" />
		</module>
		<module id="HQ030101" name="随访计划列表" type="1"
			script="chis.application.hq.script.HighRiskVisitPlanList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
				<p name="serviceId">chis.hqQueryService</p>
			</properties>
		</module>
		<module id="HQ030102" name="随访基本信息" type="1"
			script="chis.application.hq.script.HighRiskVisitForm">
			<properties>
				<p name="entryName">chis.application.hq.schemas.MDC_HighRiskVisit</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="savedata" name="确定" iconCls="save" />
			<action id="addplan" name="增加计划" iconCls="add" />
			<action id="deleteplan" name="删除计划" iconCls="remove" />
		</module>	
		<module id="HQ0101" name="目前状态修改" script="chis.application.hq.script.HealthStatusModifyForm" type="1">
			<properties>
				<p name="entryName">chis.application.hq.schemas.HC_HealthCheck_STATUS</p>
				<p name="serviceId">chis.hqQueryService</p>
			</properties>
		</module>	
		<!--2019-08-01 增加高危人群随访列表查询 -->
		<module id="HQ04" name="高危人群随访" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.hq.schemas.MDC_HighRiskVisit</p>
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hq.HQ/HQ/HQ0401"/> 
		</module>  
		<module id="HQ0401"  name="高危人群随访列表" script="chis.application.hq.script.HighRiskVisitList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.hq.schemas.MDC_HighriskVisitPlan</p> 
			</properties>  
			<action id="visit" name="查看" iconCls="update"/>  
			<action id="print" name="导出"/>
		</module>
	</catagory>
</application>