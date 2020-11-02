<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.her.HER" name="健康教育"  type="1">
	<catagory id="HER" name="健康教育">
		<module id="HE01" name="计划制定" script="chis.application.her.script.PlanSetRecord">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanSet</p>
				<p name="navField">planUnit</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="rootVisible">true</p>
				<p name="navParentKey">%user.manageUnit.id</p>
				<p name="removeServiceId">chis.healthEducationService</p>
				<p name="saveServiceId">chis.healthEducationService</p>
				<p name="queryAction">queryEducationRecordCount</p>
				<p name="logOutAction">logOutHealthEducationSet</p>
				<p name="removeAction">removeHealthEducation</p>
			</properties>
			<action id="create" name="新建" ref="chis.application.her.HER/HER/HE01_01" />
			<action id="update" name="查看" ref="chis.application.her.HER/HER/HE01_01" />
			<action id="logOut" name="作废" iconCls="common_writeOff" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="HE01_01" name="健康教育计划制定" script="chis.application.her.script.PlanSetModule"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanSet</p>
				<p name="loadServiceId">chis.healthEducationService</p>
				<p name="loadAction">loadHealthEducationPlan</p>
			</properties>
			<action id="action1" name="计划制定情况" ref="chis.application.her.HER/HER/HE01_01_01" />
			<action id="action2" name="计划执行" ref="chis.application.her.HER/HER/HE01_01_02" />
		</module>
		<module id="HE01_01_01" name="计划制定情况" script="chis.application.her.script.PlanSetForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanSet</p>
				<p name="saveServiceId">chis.healthEducationService</p>
				<p name="saveAction">saveHealthEducationPlan</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="HE01_01_02" name="计划执行" script="chis.application.her.script.PlanExeList"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanExe</p>
				<p name="listServiceId">chis.healthEducationService</p>
				<p name="listAction">queryPlanExeData</p>
			</properties>
			<action id="update" name="查看" iconCls="update"
				ref="chis.application.her.HER/HER/HE02_01" />
		</module>
		<module id="HE02" name="计划执行" script="chis.application.her.script.PlanExeRecord">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanExe</p>
				<p name="updateCls">chis.application.her.script.PlanExeModule</p>
				<p name="navField">executeUnit</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="rootVisible">true</p>
				<p name="navParentKey">%user.manageUnit.id</p>
				<p name="listServiceId">chis.healthEducationService</p>
				<p name="listAction">loadPlanExe</p>
			</properties>
			<action id="update" name="查看" iconCls="update"
				ref="chis.application.her.HER/HER/HE02_01" />
		</module>
		<module id="HE02_01" name="计划执行情况" script="chis.application.her.script.PlanExeModule"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanExe</p>
				<p name="loadServiceId">chis.healthEducationService</p>
				<p name="loadAction">loadPlanExeData</p>
			</properties>
			<action id="action1" name="计划制定情况" ref="chis.application.her.HER/HER/HE02_01_01" />
			<action id="action2" name="计划执行" ref="chis.application.her.HER/HER/HE02_01_02" />
		</module>
		<module id="HE02_01_01" name="计划执行内容" script="chis.application.her.script.PlanExeForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationPlanExe</p>
				<p name="isAutoScroll">true</p>
			</properties>
		</module>
		<module id="HE02_01_02" name="健康教育内容" script="chis.application.her.script.PlanRecordList" 	type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationRecord</p>
				<p name="listServiceId">chis.healthEducationService</p>
				<p name="listAction">queryPlanRecordData</p>
				<p name="removeServiceId">chis.healthEducationService</p>
				<p name="removeAction">removePlanRecord</p>
				<p name="refHECModule">chis.application.her.HER/HER/HE02_01_0201</p>
			</properties>
			<action id="createHEC" name="新增" iconCls="create" group="create||update" />
			<action id="updateHEC" name="查看" iconCls="update" group="updateHEC" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="HE02_01_0201" name="健康教育内容" 	script="chis.application.her.script.HealthEducationModule" type="1">
			<action id="action1" name="健康教育内容" ref="chis.application.her.HER/HER/HE02_01_0201_01"  type="tab" />
			<action id="action2" name="健康教育处方" ref="chis.application.her.HER/HER/HE02_01_0201_02" 	type="tab" />
		</module>
		<module id="HE02_01_0201_01" name="计划执行内容" script="chis.application.her.script.EducationForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_EducationRecord</p>
				<p name="saveServiceId">chis.healthEducationService</p>
				<p name="saveAction">savePlanRecord</p>
				<p name="fileViewList">chis.application.her.HER/HER/HE02_01_0201_0101</p>
				<p name="emType">txt,rar,doc,docx,xls,xlsx,ppt,rtf,pdf,html,png,jpg</p>
				<p name="dirType">educationMaterial</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="fileUpload" name="上传资料" iconCls="healthDoc_import"
				group="update" />
			<action id="fileView" name="资料查看" iconCls="hypertension_export" />
			<action id="printTeach" name="打印"  iconCls="print" />
		</module>
		<module id="HE02_01_0201_0101" name="文件查看"
			script="chis.application.her.script.FileUploadList" type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_FileUpload</p>
				<p name="removeServiceId">chis.educationMaterialService</p>
				<p name="removeAction">removeFile</p>
			</properties>
			<action id="fileDownLoad" name="查看" iconCls="hypertension_export" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="HE02_01_0201_02" name="健康教育处方"
			script="chis.application.her.script.HealthEducationRecipelForm" type="1">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_JHZX</p>
				<p name="refRecipeImportModule">chis.application.psy.PSY/PSY/I01-1-2-3-1</p> 
			</properties>
			<action id="save" name="确定" iconCls="save" group="update" />
			<action id="printRecipe" name="打印健康处方"  iconCls="print" />
		</module>
		<module id="HE03" name="健康处方维护" type="1" script="chis.application.her.script.RecipelRecordList">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_RecipelRecordList</p>
				<p name="createRef">chis.application.her.HER/HER/HE03_01</p>  
			</properties>
			<action id="add" name="新增"  />
			<action id="modify" name="查看" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="HE03_01" name="健康处方维护表单" type="1"
			script="chis.application.her.script.RecipelRecordForm">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_RecipelRecord</p>
			</properties>
			<action id="save" name="保存" />
			<action id="create" name="新建" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>

		<module id="HE04" name="健康处方发放记录" script="chis.application.her.script.HealthRecipeList">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord</p>
			</properties>
			<action id="update" name="查看" ref="chis.application.her.HER/HER/HE04_01"/>
			<action id="print" name="打印"/>
		</module>
		<module id="HE04_01" name="健康处方发放记录" type="1"
			script="chis.application.her.script.HealthRecipeForm">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
			<action id="printRecipe" name="打印健康处方"  iconCls="print" />
		</module>
		<module id="HE04_02" name="健康处方发放记录" type="1" script="chis.application.her.script.HealthRecipeList">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_GR</p>
			</properties>
			<action id="update" name="查看" ref="chis.application.her.HER/HER/HE04_03"/>
			<action id="print" name="打印"/>
		</module>
		<module id="HE04_03" name="健康处方发放记录" type="1"
			script="chis.application.her.script.HealthRecipeForm">
			<properties>
				<p name="entryName">chis.application.her.schemas.HER_HealthRecipeRecord_GR</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
			<action id="printRecipe" name="打印健康处方"  iconCls="print" />
		</module>
	</catagory>
</application>