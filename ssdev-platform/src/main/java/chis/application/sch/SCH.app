<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.sch.SCH" name="血吸虫管理"  type="1">
	<catagory id="SCH" name="血吸虫管理">
		<module id="X01" name="血吸虫病管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaRecord</p>
				<p name="manageUnitField">a.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.sch.SCH/SCH/X0101" />
		</module>
		<module id="X0101" name="血吸虫档案列表" script="chis.application.sch.script.SchistospmaRecordRecord"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaRecord</p>
			</properties>
			<action id="createDoc" name="新建" iconCls="create" group="create" />
			<action id="modify" name="查看" iconCls="update" group="update" />
			<action id="writeOff" name="注销" iconCls="common_writeOff" />
			<action id="visit" name="随访" iconCls="hypertension_visit"
				group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="X0102" name="血吸虫档案模块" script="chis.application.sch.script.SchistospmaRecordModule"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="action1" name="血吸虫档案列表" ref="chis.application.sch.SCH/SCH/X0102_1" />
			<action id="action2" name="血吸虫档案" ref="chis.application.sch.SCH/SCH/X0102_2" type="tab" />
			<action id="action3" name="血吸虫随访" ref="chis.application.sch.SCH/SCH/X0102_3" type="tab" />
		</module>
		<module id="X0102_1" name="血吸虫档案列表" script="chis.application.sch.script.SchistospmaRecordList"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaRecordModule</p>
			</properties>
		</module>
		<module id="X0102_2" name="血吸虫档案表单" script="chis.application.sch.script.SchistospmaRecordForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaRecord</p>
				<p name="saveServiceId">chis.schistospmaService</p>
				<p name="saveAction">saveSchistospmaRecord</p>
				<p name="addAction">addSchistospmaRecord</p>
				<p name="closeAction">closeSchistospmaRecord</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="close" name="结案" group="update" />
			<action id="add" name="新增" group="create" />
		</module>
		<module id="X0102_3" name="血吸虫档案随访模块" script="chis.application.sch.script.SchistospmaVisitModule"
			type="1">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="schistospmaVisitList" name="血吸虫档案随访列表" ref="chis.application.sch.SCH/SCH/X0102_3_1" />
			<action id="schistospmaVisitForm" name="血吸虫档案随访表单" ref="chis.application.sch.SCH/SCH/X0102_3_2" />
		</module>
		<module id="X0102_3_1" name="血吸虫档案随访列表" script="chis.application.sch.script.SchistospmaVisitList"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaVisitModule</p>
			</properties>
		</module>
		<module id="X0102_3_2" name="血吸虫档案随访表单" script="chis.application.sch.script.SchistospmaVisitForm"
			type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaVisit</p>
				<p name="saveServiceId">chis.schistospmaService</p>
				<p name="saveAction">saveSchistospmaVisitInfo</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="add" name="新增" group="create" />
		</module>
		<module id="X03" name="血吸虫综合治理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaManage</p>
				<p name="navField">regionCode</p>
				<p name="navDic">chis.dictionary.areaGrid</p>
				<p name="manageUnitField">inputUnit</p>
				<p name="rootVisible">true</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.sch.SCH/SCH/X0301" />
		</module>
		<module id="X0301" name="血吸虫综合治理列表"
			script="chis.application.sch.script.SchistospmaManageListView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaManage</p>
				<p name="refModule">chis.application.sch.SCH/SCH/X0302</p>
			</properties>
			<action id="createDoc" name="新建" group="create" iconCls="create" />
			<action id="modify" name="查看" group="update" iconCls="update" />
			<action id="remove" name="删除" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="X0302" name="血吸虫综合治理表单"
			script="chis.application.sch.script.SchistospmaManageFormView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SchistospmaManage</p>
			</properties>
			<action id="save" name="保存" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="X04" name="查螺灭螺基本情况" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailBaseInfomation</p>
				<p name="navField">regionCode</p>
				<p name="navDic">chis.dictionary.areaGrid</p>
				<p name="manageUnitField">inputUnit</p>
				<p name="rootVisible">true</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.sch.SCH/SCH/X0401" />
		</module>
		<module id="X0401" name="查螺灭螺列表"
			script="chis.application.sch.script.SnailBaseInfomationListView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailBaseInfomation</p>
			</properties>
			<action id="createInfo" name="新建" group="create" iconCls="create" />
			<action id="modify" name="查看" group="update" iconCls="update" />
			<action id="remove" name="删除" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="X0402" name="查螺灭螺基本情况表单"
			script="chis.application.sch.script.SnailBaseInfomationFormView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailBaseInfomation</p>
			</properties>
			<action id="save" name="确定" group="update" />
		</module>
		<module id="X05" name="查螺记录"
			script="chis.application.sch.script.SnailFindInfomationListView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailFindInfomation</p>
			</properties>
			<action id="createInfo" name="新建" group="create" iconCls="create" />
			<action id="modify" name="查看" group="update" iconCls="update" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="X0501" name="查螺记录表单"
			script="chis.application.sch.script.SnailFindInfomationFormView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailFindInfomation</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="X06" name="灭螺记录"
			script="chis.application.sch.script.SnailKillInfomationListView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailKillInfomation</p>
			</properties>
			<action id="createInfo" name="新建" group="create" iconCls="create" />
			<action id="modify" name="查看" group="update" iconCls="update" />
			<action id="remove" name="删除" group="update" />
		</module>
		<module id="X0601" name="灭螺记录表单"
			script="chis.application.sch.script.SnailKillInfomationFormView" type="1">
			<properties>
				<p name="entryName">chis.application.sch.schemas.SCH_SnailKillInfomation</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
	</catagory>
</application>