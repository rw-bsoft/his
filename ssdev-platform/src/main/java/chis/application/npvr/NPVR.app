<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.npvr.NPVR" name="非重点人群随访"  type="1">
	<catagory id="NPVR" name="非重点人群随访">
		<module id="B18" name="非重点人群随访" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.npvr.schemas.EHR_NormalPopulationVisit</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.npvr.NPVR/NPVR/B18-1" />
		</module>
	<module id="B18-1" type="1" name="非重点人群随访" 	script="chis.application.npvr.script.NormalPopulationVisitRecord">
			<properties>
				<p name="entryName">chis.application.npvr.schemas.EHR_NormalPopulationVisit</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">b.manaUnitId</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="B18-2" script="chis.application.npvr.script.NormalPopulationVisitModule"  name="非重点人群随访组合模块" type="1">
			<properties>
				<p name="entryName">chis.application.npvr.schemas.EHR_NormalPopulationVisit_Module</p>
				<p name="saveServiceId">chis.normalPopulationService</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="NormalPopulationList" name="计划列表" ref="chis.application.npvr.NPVR/NPVR/B18-2-1" />
			<action id="NormalPopulationForm" name="随访信息" ref="chis.application.npvr.NPVR/NPVR/B18-2-2"
				type="tab" />
		</module>
		<module id="B18-2-1" name="非重点人群随访计划列表" 	script="chis.application.npvr.script.NormalPopulationVisitList" type="1">
			<properties>
				<p name="entryName">chis.application.npvr.schemas.EHR_NormalPopulationVisit_Module</p>
			</properties>
		</module>
		<module id="B18-2-2" name="非重点人群随访form" 	script="chis.application.npvr.script.NormalPopulationVisitForm" type="1">
			<properties>
				<p name="entryName">chis.application.npvr.schemas.EHR_NormalPopulationVisit</p>
				<p name="saveServiceId">chis.normalPopulationService</p>
				<p name="isAutoScroll">true</p>
				<p name="saveAction">saveNormalPopulationVisitRecord</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="create" name="新建" group="create" />
		</module>
	</catagory>
</application>