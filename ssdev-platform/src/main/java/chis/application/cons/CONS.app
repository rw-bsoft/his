<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.cons.CONS" name="会诊记录"  type="1">
	<catagory id="CONS"   name="会诊记录">
		<module id="CS01"  name="会诊记录" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.cons.schemas.CONS_ConsultationRecord</p>
				<p name="manageUnitField">c.manaUnitId</p> 
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.cons.CONS/CONS/CS01_1"/> 
		</module>
		<module id="CS01_1"  name="会诊记录列表" script="chis.application.cons.script.ConsultationRecordList" type="1" icon="default">
			<properties>
				<p name="entryName">chis.application.cons.schemas.CONS_ConsultationRecord</p>
				<p name="navField">c.regionCode</p>
				<p name="navDic">c.manageUnitId</p>
			</properties>
			<action id="create" name="新建" iconCls="create" ref="chis.application.cons.CONS/CONS/CS01_1_1" />
			<action id="update" name="查看" iconCls="update" ref="chis.application.cons.CONS/CONS/CS01_1_1"/>
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="CS01_1_1" name="会诊记录表单" script="chis.application.cons.script.ConsultationRecordForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.cons.schemas.CONS_ConsultationRecord_Form</p>
			</properties>
			<action id="create" name="新建"/>
			<action id="save" name="确定"/>
			<action id="print" name="打印" />
		</module>
		<module id="CS02" name="会诊申请" type="1"
			script="chis.application.cons.script.ConsultationApply">
		</module>
		<module id="CS03" name="会诊列表" 
			script="chis.application.cons.script.ConsultationList">
		</module>
	</catagory>
</application>