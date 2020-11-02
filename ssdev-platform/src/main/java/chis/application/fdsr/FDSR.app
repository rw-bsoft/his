<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.fdsr.FDSR" name="家医服务">
	<catagory id="FDSR" name="家医服务">
		<module id="FDSR01" name="家医服务列表" script="chis.application.fdsr.script.FdsrList">
			<properties>
				<p name="entryName">chis.application.fdsr.schemas.FDSR</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
            <action id="createRecord" name="新建" iconCls="create"/>
            <action id="remove" name="删除" iconCls="remove"/>
            <action id="modify" name="查看" iconCls="update"/>
            <action id="print"  name="导出" />
            <!--<action id="check" name="审核" iconCls="update"/>-->
		</module>
		<module id="FDSR02" name="家医服务整体" script="chis.application.fdsr.script.FdsrModule" type="1">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="ScreenList" name="家医服务列表" ref="chis.application.fdsr.FDSR/FDSR/FDSR0201" />
			<action id="ScreenForm" name="家医服务记录" ref="chis.application.fdsr.FDSR/FDSR/FDSR0202" />
		</module>
		<module id="FDSR0201" name="家医服务列表" script="chis.application.fdsr.script.FdsrPersonalList" type="1">
			<properties>
				<p name="entryName">chis.application.fdsr.schemas.FDSR</p>
				<p name="selectFirst">true</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="FDSR0202" name="家医服务表单" script="chis.application.fdsr.script.FdsrForm" icon="default" type="1">
			<properties>
				<p name="entryName">chis.application.fdsr.schemas.FDSR</p>
			</properties>
			<action id="save" name="确定" group="create||update" />
			<action id="add" name="新增" group="create" />
		</module>
	</catagory>
</application>