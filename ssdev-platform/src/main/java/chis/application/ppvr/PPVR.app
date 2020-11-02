<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.ppvr.PPVR" name="贫困人群随访"  type="1">
	<catagory id="PPVR" name="贫困人群随访">
		<module id="B17" name="贫困人群随访" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.ppvr.schemas.EHR_PoorPeopleVisit</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.ppvr.PPVR/PPVR/B17-1" />
		</module>
		<module id="B17-1" type="1" name="贫困人群随访" 	script="chis.application.ppvr.script.PoorPeopleVisitRecord">
			<properties>
				<p name="entryName">chis.application.ppvr.schemas.EHR_PoorPeopleVisit</p>
				<p name="navField">b.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
		</module>
		<module id="B17-2" script="chis.application.ppvr.script.PoorPeopleVisitModule"	name="贫困人群随访" type="1">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="PoorPeopleList" name="计划列表" ref="chis.application.ppvr.PPVR/PPVR/B17-2-1" />
			<action id="PoorPeopleForm" name="随访信息" ref="chis.application.ppvr.PPVR/PPVR/B17-2-2" type="tab" />
		</module>
		<module id="B17-2-1" name="贫困人群随访计划列表" type="1"	script="chis.application.ppvr.script.PoorPeopleVisitList">
			<properties>
				<p name="entryName">chis.application.ppvr.schemas.EHR_PoorPeopleVisit_Module</p>
			</properties>
		</module>
		<module id="B17-2-2" name="贫困人群随访form" type="1" 	script="chis.application.ppvr.script.PoorPeopleVisitForm">
			<properties>
				<p name="entryName">chis.application.ppvr.schemas.EHR_PoorPeopleVisit</p>
				<p name="saveServiceId">chis.poorPeopleRecordService</p>
				<p name="saveAction">savePoorPeopleVisitRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="create" name="新建" group="create" />
		</module>
	</catagory>
</application>