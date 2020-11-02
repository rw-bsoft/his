<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.qcm.QCM" name="质量控制管理"  type="1"> 
	<catagory id="QCC" name="质量控制标准维护">
		<module id="QCC01" name="质控评分标准维护" script="chis.application.qcm.script.qcc.QualityControlCriterionModule">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QualityControlCriterion</p>  
				<p name="refQCCDM">chis.application.qcm.QCM/QCC/QCC_QCCDM</p>  
			</properties>
			<action id="QCCList" name="标准列表" ref="chis.application.qcm.QCM/QCC/QCC0101"/>  
			<action id="QCCDList" name="标准明细列表" ref="chis.application.qcm.QCM/QCC/QCC0102"/>  
		</module>
		<module id="QCC0101" name="质控评分标准列表" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionList">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QualityControlCriterion</p>  
				<p name="createCls">chis.application.qcm.script.qcc.QualityControlCriterionForm</p>  
				<p name="updateCls">chis.application.qcm.script.qcc.QualityControlCriterionForm</p>
			</properties>
			<action id="create" name="新建" iconCls="create"/>  
			<action id="update" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="QCC0102" name="质控评分标准明细列表" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionDetailList">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetails</p>
			</properties>
			<action id="createCriterion" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="QCC_QCCDM" name="质控评分标准明细" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionDetailModule">
			<properties>
			</properties>
			<action id="QCC_QCCDForm" name="基本信息" ref="chis.application.qcm.QCM/QCC/QCC_QCCDForm"/>
			<action id="QCC_QCCDList" name="基本信息" ref="chis.application.qcm.QCM/QCC/QCC_QCCDList"/>
		</module>
		<module id="QCC_QCCDForm" name="" type="1" script="chis.application.qcm.script.qcc.QCCDForm">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetails</p>
			</properties>
			<action id="create" name="新增" group="create"/> 
			<action id="save" name="确定" group="create||update"/> 
			<action id="cancel" name="关闭"  iconCls="common_cancel"/> 
		</module>
		<module id="QCC_QCCDList" name="" type="1" script="chis.application.qcm.script.qcc.QCCDList">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetailsRuleList</p>
			</properties>
		</module>
		<module id="QCC02" name="门诊一级质控配置" script="chis.application.qcm.script.qcc.QualityControlStairForm">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QualityControlStair</p>  
			</properties>
			<action id="save" name="保存" group="create||update"/>
		</module>
	</catagory>
	<catagory id="QCM" name="质量控制管理">
		<!-- ZBN add =================s========= -->
	
		<!-- ZBN add =================e========= -->
		<!-- YSQ=========s=====================-->
		<module id="QCM10" name="质控评分标准维护" script="chis.application.qcm.script.qcc.QualityControlCriterionModule">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QualityControlCriterion</p>  
				<p name="refQCCDM">chis.application.qcm.QCM/QCM/QCM_QCCDM</p>  
			</properties>
			<action id="QCCList" name="标准列表" ref="chis.application.qcm.QCM/QCM/QCM1001"/>  
			<action id="QCCDList" name="标准明细列表" ref="chis.application.qcm.QCM/QCM/QCM1002"/>  
		</module>
		<module id="QCM1001" name="质控评分标准列表" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionList">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QualityControlCriterion</p>  
				<p name="createCls">chis.application.qcm.script.qcc.QualityControlCriterionForm</p>  
				<p name="updateCls">chis.application.qcm.script.qcc.QualityControlCriterionForm</p>
			</properties>
		</module>
		<module id="QCM1002" name="质控评分标准明细列表" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionDetailList">
			<properties> 
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetails</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>  
		</module>
		<module id="QCM_QCCDM" name="质控评分标准明细" type="1" script="chis.application.qcm.script.qcc.QualityControlCriterionDetailModule">
			<properties>
			</properties>
			<action id="QCC_QCCDForm" name="基本信息" ref="chis.application.qcm.QCM/QCM/QCM_QCCDForm"/>
			<action id="QCC_QCCDList" name="基本信息" ref="chis.application.qcm.QCM/QCM/QCM_QCCDList"/>
		</module>
		<module id="QCM_QCCDForm" name="" type="1" script="chis.application.qcm.script.qcc.QCCDForm">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetails</p>
			</properties>
			<action id="cancel" name="关闭"  iconCls="common_cancel"/> 
		</module>
		<module id="QCM_QCCDList" name="" type="1" script="chis.application.qcm.script.qcc.QCCDList">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_QCCriterionDetailsRuleList</p>
			</properties>
		</module>
	
	
		<!-- YSQ=========e=====================-->
	
		<!-- CXR add =================s========= -->
		<module id="QCM20" name="查看质控报告（个人）" type="1" script="chis.application.qcm.script.qcv.PersonalQualityReportModule"/>
		<module id="QCM21" name="高血压质控查询" type="1" script="chis.application.qcm.script.qcv.QualityControlSelectListView">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_HypertensionQCSelect</p>
				<p name="criterionType">chis.application.hy.schemas.MDC_HypertensionVisit</p>
				<p name="qualityKind">1</p>
				<p name="listServiceId">chis.qualityControlViewService</p>
				<p name="listAction">loadQCSelect</p>
			</properties>
		</module>
		<module id="QCM22" name="糖尿病质控查询" type="1" script="chis.application.qcm.script.qcv.QualityControlSelectListView">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_DiabetesQCSelect</p>
				<p name="criterionType">chis.application.dbs.schemas.MDC_DiabetesVisit</p>
				<p name="qualityKind">2</p>
				<p name="listServiceId">chis.qualityControlViewService</p>
				<p name="listAction">loadQCSelect</p>
			</properties>
		</module>
		<module id="QCM23" name="T高危质控查询" type="1" script="chis.application.qcm.script.qcv.QualityControlSelectTHRListView">
			<properties>
				<p name="entryName">chis.application.qcm.schemas.QCM_TumourQCSelect</p>
				<p name="criterionType">chis.application.tr.schemas.MDC_TumourHighRiskVisit</p>
				<p name="qualityKind">3</p>
				<p name="listServiceId">chis.qualityControlViewService</p>
				<p name="listAction">loadQCSelect</p>
			</properties>
		</module>
		<module id="QCM24" name="质控报告" type="1" script="chis.application.qcm.script.qcv.QualityControlReportModule">
			<action id="print" name="打印"/>  
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!-- CXR add =================e========= -->	
	</catagory>
</application>