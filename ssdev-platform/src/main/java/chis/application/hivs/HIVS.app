<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hivs.HIVS" name="HIV筛查">
	<catagory id="HIVS" name="HIV筛查">
		<module id="HIVS01" name="HIV人群筛查列表" script="chis.application.hivs.script.ScreeningList">
			<properties>
				<p name="entryName">chis.application.hivs.schemas.HIVS_Screening</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
            <action id="createRecord" name="新建" iconCls="create"/>
            <action id="remove" name="删除" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
            <action id="check" name="审核" iconCls="update"/>
		</module>
		<module id="HIVS02" name="HIV筛查整体" script="chis.application.hivs.script.ScreeningModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="ScreenList" name="HIV随访列表" ref="chis.application.hivs.HIVS/HIVS/HIVS0201" />
			<action id="ScreenForm" name="HIV随访记录" ref="chis.application.hivs.HIVS/HIVS/HIVS0202" />
		</module>
		<module id="HIVS0201" name="HIV个人筛查列表" script="chis.application.hivs.script.ScreeningPersonalList" type="1">
			<properties>
				<p name="entryName">chis.application.hivs.schemas.HIVS_Screening</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="HIVS0202" name="HIV个人筛查表单" script="chis.application.hivs.script.ScreeningForm" icon="default" type="1">
			<properties>
				<p name="entryName">chis.application.hivs.schemas.HIVS_Screening</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="add" name="新增" group="create" />
		</module>
	</catagory>
</application>