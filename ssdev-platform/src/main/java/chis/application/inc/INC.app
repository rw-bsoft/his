<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.inc.INC" name="接诊记录"  type="1">
	<catagory id="INC"   name="接诊记录">
		<module id="I01"  name="接诊记录" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.inc.schemas.INC_IncompleteRecord</p>
				<p name="manageUnitField">c.manaUnitId</p> 
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.inc.INC/INC/I01_1"/> 
		</module>
		<module id="I01_1"  name="接诊记录列表" script="chis.application.inc.script.IncompleteRecordList" type="1" icon="default">
			<properties>
				<p name="entryName">chis.application.inc.schemas.INC_IncompleteRecord</p>
				<p name="navField">c.regionCode</p>
				<p name="navDic">c.manageUnitId</p>
			</properties>
			<action id="create" name="新建" iconCls="create" ref="chis.application.inc.INC/INC/I01_1_1" />
			<action id="update" name="查看" iconCls="update" ref="chis.application.inc.INC/INC/I01_1_1"/>
			<action id="remove" name="删除" />
			<action id="print" name="打印" iconCls="print"/>
		</module>
		<module id="I01_1_1" name="接诊记录表单" script="chis.application.inc.script.IncompleteRecordForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.inc.schemas.INC_IncompleteRecord_Form</p>
			</properties>
			<action id="create" name="新建"/>
			<action id="save" name="确定"/>
			<action id="print" name="打印" iconCls="print"/>
			<action id="getYLData" name="获取门诊数据" iconCls="common_treat"/>
		</module>
	</catagory>
</application>