<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.piv.PIV" name="计划免疫"  type="1">
	<catagory id="PIV" name="计划免疫">
		<module id="E01" name="计划免疫档案" script="chis.application.piv.script.VaccinateRecordList">
			<properties>
				<p name="entryName">chis.application.piv.schemas.PIV_VaccinateRecord</p>
				<p name="navField">manaUnitId</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="rootVisible">true</p>
				<p name="navParentKey">%user.manageUnit.id</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="ET01" name="计免档案MODULE" type="1"
			script="chis.application.piv.script.VaccinateRecordModule">
			<properties>
				<p name="entryName">chis.application.piv.schemas.PIV_VaccinateRecord</p>
			</properties>
			<action id="form" name="计免档案" ref="chis.application.piv.PIV/PIV/ET02" />
		</module>
		<module id="ET02" name="计免档案表单" type="1"
			script="chis.application.piv.script.VaccinateRecordForm">
			<properties>
				<p name="entryName">chis.application.piv.schemas.PIV_VaccinateRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
		</module>
	</catagory>
</application>