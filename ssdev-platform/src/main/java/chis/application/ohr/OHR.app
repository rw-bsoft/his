<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.ohr.OHR" name="老年人健康管理"  type="1">
	<catagory id="OHR"   name="老年人健康管理"> 
		<module id="B4"  name="老年人档案管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleRecord</p>  
				<p name="manageUnitField">a.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.ohr.OHR/OHR/B41"/> 
		</module>  
		<module id="B41"  name="老年人列表" script="chis.application.ohr.script.OldPeopleList" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleRecord</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">a.manageUnitId</p> 
				<p name="listServiceId">chis.publicService</p>
				<p name="listAction">queryRecordList</p>
			</properties>  
			<action id="createDoc" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
			<action id="print" name="打印" />
		</module>  
		<module id="B19" type="1" name="老年人档案档案表单" script="chis.application.ohr.script.OldPeopleRecordForm"> 
			<properties> 
				<p name="saveAction">saveOldPeopleRecord</p>  
				<p name="saveServiceId">chis.oldPeopleRecordService</p>  
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleRecord</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="check" name="健康检查" iconCls="update"/>
		</module>  
		<module id="B14" type="1" name="老年人管理" script="chis.application.ohr.script.OldPeopleManageModule" icon="default"> 
			<properties> 
				<p name="saveServiceId">chis.oldPeopleRecordService</p>  
				<p name="serviceAction">saveOldPeopleVisitRecord</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="OldPeopleManageList" name="计划列表" ref="chis.application.ohr.OHR/OHR/B14_04"/>  
			<action id="BaseRecord" name="随访信息" ref="chis.application.ohr.OHR/OHR/B14_01" type="tab"/>  
			<action id="CheckRecord" name="体格检查" ref="chis.application.ohr.OHR/OHR/B14_02" type="tab"/>  
			<action id="Description" name="中医辩体描述" ref="chis.application.ohr.OHR/OHR/B14_03" type="tab"/> 
		</module>  
		<module id="B14_01" type="1" name="老年人随访信息表单" script="chis.application.ohr.script.OldPeopleForm" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleVisit</p>  
				<p name="saveServiceId">chis.oldPeopleRecordService</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" iconCls="save" group="create||update"/> 
			<action id="importJzInfo" name="导入就诊记录" group="create,update" iconCls="add"/>	
		</module>  
		<module id="B14_02" type="1" name="老年人体检信息管理" script="chis.application.ohr.script.OldPeopleCheckupModule" icon="default"> 
			<properties> 
				<p name="refModule">chis.application.ohr.OHR/OHR/B14_02_01</p> 
			</properties> 
		</module>  
		<module id="B14_02_01" type="1" name="老年人体检列表" script="chis.application.ohr.script.OldPeopleCheckupList" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleCheckup</p> 
			</properties>  
			<action id="save" name="确定" iconCls="save" group="create||update"/> 
		</module>  
		<module id="B14_03" name="中医辩体描述" type="1" script="chis.application.ohr.script.OldPeopleDescriptionForm"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPelpleDescription</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>  
		<module id="B14_04" name="老年人随访计划列表" type="1" script="chis.application.ohr.script.OldPeopleVisitPlanList"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>  
				<p name="saveServiceId">chis.oldPeopleRecordService</p>  
				<p name="loadAction">loadPlanVisitRecords</p> 
			</properties> 
		</module>  
		<module id="B5" name="老年人随访记录" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleVisit</p>  
				<p name="manageUnitField">a.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.ohr.OHR/OHR/B15"/> 
		</module>  
		<module id="B15"  name="老年人随访列表" script="chis.application.ohr.script.OldPeopleVisitList" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleVisit</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">a.manageUnitId</p> 
			</properties>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="print" name="打印"/> 
		</module>
		
		<!--2019-05-28 增加老年人自理能力评估查询 -->
		<module id="B201" name="老年人自理评估" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleSelfCareListView</p>  
				<p name="manageUnitField">d.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.ohr.OHR/OHR/B202"/> 
		</module>  
		<module id="B202"  name="老年人自理评估列表" script="chis.application.ohr.script.OldPeopleSelfCareListView" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleSelfCareListView</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">d.manageUnitId</p> 
			</properties>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="print" name="打印"/> 
		</module>
		<module id="B20" name="老年人自理评估" script="chis.application.ohr.script.OldPeopleSelfCareModule" type="1"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleSelfCare</p> 
				<p name="saveServiceId">chis.oldPeopleRecordService</p>  
			</properties>  
			<action id="OPSCList" name="老年人自理评估列表" ref="chis.application.ohr.OHR/OHR/B20-1"/>  
			<action id="OPSCFrom" name="老年人自理评估表单" ref="chis.application.ohr.OHR/OHR/B20-2"/> 
		</module>  
		<module id="B20-1" name="老年人自理评估列表" script="chis.application.ohr.script.OldPeopleSelfCareList" type="1"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleSelfCare</p> 
			</properties> 
		</module>  
		<module id="B20-2" name="老年人自理评估表单" script="chis.application.ohr.script.OldPeopleSelfCareFrom" type="1"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_OldPeopleSelfCare</p> 
			</properties>  
			<action id="save" name="确定" group="create||update"/>  
			<action id="add" name="新增" group="create||update"/> 
		</module>
		<module id="B6" name="中医药健康管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManageListView</p>  
				<p name="manageUnitField">c.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.ohr.OHR/OHR/B61"/> 
		</module>  
		<module id="B61" name="中医药健康管理列表" script="chis.application.ohr.script.MedicineManageList" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManageListView</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">c.manageUnitId</p> 
			</properties>
			<action id="createDoc" name="新建" iconCls="create" group="create"/>  
			<action id="modify" name="查看" iconCls="update" group="update"/> 
			<action id="writeOff" name="注销" iconCls="common_writeOff" group="update"/>   
			<action id="print" name="打印"/>  
		</module>
		<module id="B62" name="中医药健康管理" script="chis.application.ohr.script.MedicineManageModule" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManageList</p>
				<p name="isAutoScroll">true</p> 
			</properties>
			<action id="CMMList" name="中医药健康管理列表" ref="chis.application.ohr.OHR/OHR/B62_1"/>  
			<action id="CMMFromQ" name="体质辨识问卷" ref="chis.application.ohr.OHR/OHR/B62_2" type="tab"/> 
			<action id="CMMFromA" name="中医药保健指导" ref="chis.application.ohr.OHR/OHR/B62_3" type="tab"/> 
		</module>
		<module id="B62_1" name="中医药健康管理列表" script="chis.application.ohr.script.MedicineManageQueryList" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManageList</p>  
			</properties> 
		</module>
		<module id="B62_2" name="体质辨识问卷" script="chis.application.ohr.script.MedicineManageFormQ" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManage</p>
				<p name="isAutoScroll">true</p>   
			</properties>
			<action id="save" name="保存" group="create||update"/>  
			<action id="create" name="新增" group="create"/>  
		</module>
		<module id="B62_3" name="中医药保健指导" script="chis.application.ohr.script.MedicineManageFormA" type="1" icon="default"> 
			<properties> 
				<p name="entryName">chis.application.ohr.schemas.MDC_ChineseMedicineManage</p> 
				<p name="isAutoScroll">true</p>  
			</properties>
			<action id="save" name="保存" group="create||update"/>
			<action id="printManage" name="打印" iconCls="print"/>  
		</module>
	</catagory>  
</application>