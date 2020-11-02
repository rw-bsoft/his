<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.rvc.RVC" name="离休干部档案"  type="1">
	<catagory id="RVC" name="离休干部管理">
		<module id="R1"  name="离休干部档案管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord</p>  
				<p name="manageUnitField">a.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.rvc.RVC/RVC/R1_1"/> 
		</module> 
		<module id="R1_1"  name="离休干部档案管理" type="1" script="chis.application.rvc.script.RetiredVeteranCadresRecordList"> 
			<properties> 
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">a.manageUnitId</p>
			</properties>  
			<action id="createDoc" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="writeOff" name="注销" iconCls="common_writeOff"/>  
			<action id="visit" name="随访" iconCls="hypertension_visit"/>
		</module>
		<module id="R11" type="1" name="离休干部档案表单" script="chis.application.rvc.script.RetiredVeteranCadresRecordForm"> 
			<properties> 
				<p name="saveAction">saveRetiredVeteranCadresRecord</p>  
				<p name="saveServiceId">chis.retiredVeteranCadresService</p>  
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<module id="R12" name="离休干部随访管理" type="1" script="chis.application.rvc.script.RVCVisitModule" icon="default"> 
			<properties> 
				<p name="saveServiceId">chis.retiredVeteranCadresService</p>  
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="visitList" name="随访计划列表" ref="chis.application.rvc.RVC/RVC/R12_1"/>  
			<action id="visitForm" name="随访表单" ref="chis.application.rvc.RVC/RVC/R12_2"/>  
		</module> 
		<module id="R12_1" type="1" name="离休干部随访列表" script="chis.application.rvc.script.RVCVisitList"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_VisitPlan</p>
				<p name="saveServiceId">chis.retiredVeteranCadresService</p>  
				<p name="loadAction">loadPlanVisitRecords</p> 
			</properties>  
		</module>
		<module id="R12_2" type="1" name="离休干部随访表单" script="chis.application.rvc.script.RVCVisitForm"> 
			<properties> 
				<p name="saveAction">saveRVCVisitRecord</p>  
				<p name="saveServiceId">chis.retiredVeteranCadresService</p>  
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="create||update"/> 
		</module>
		<module id="R2"  name="离休干部随访管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit_list</p>  
				<p name="manageUnitField">a.manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.rvc.RVC/RVC/R2_1"/> 
		</module> 
		<module id="R2_1"  name="离休干部随访管理" type="1" script="chis.application.rvc.script.RVCVisitListView"> 
			<properties> 
				<p name="entryName">chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit_list</p>  
				<p name="navField">c.regionCode</p>  
				<p name="navDic">a.manageUnitId</p>
			</properties>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="print" name="打印"/>
		</module>
	</catagory>
	
</application>