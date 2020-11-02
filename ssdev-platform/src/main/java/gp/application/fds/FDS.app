<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.fds.FDS" name="家医服务" type="0">
	<catagory id="FDS" name="家医服务">
		<module id="FDS01" name="家庭签约" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="gp.application.fds.FDS/FDS/FDS02" />
		</module>
		<module id="FDS02" name="家庭档案列表" type="1"
			script="gp.application.fds.script.FamilyRecordList">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				<p name="navField">RegionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="saveServiceId">chis.familyRecordService</p>
				<p name="listServiceId">chis.familyListQuery</p>
				<p name="removeServiceId">chis.familyRecordService</p>
				<p name="removeAction">removeFamilyRecord</p>
				<p name="refId">chis.application.fhr.FHR/FHR/B011</p>
			</properties>
			<action id="create" name="新建" ref="chis.application.fhr.FHR/FHR/B011" />
			<action id="update" name="查看" ref="chis.application.fhr.FHR/FHR/B011" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
			
			<action id="jtqy" name="家庭签约" iconCls="update"/>
		</module>
		
		<module id="FDS03" name="签约情况"  type="1">

		</module>
		<module id="FDS03_01" name="签约情况历史记录"  type="1">

		</module>
		<module id="FDS03_02" name="签约内容"  type="1">

		</module>
	</catagory>
</application>