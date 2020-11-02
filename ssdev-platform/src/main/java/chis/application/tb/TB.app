<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.tb.TB" name="肺结核管理"  type="1">
	<catagory id="TB" name="肺结核管理">
		<module id="TB_ListModule" name="肺结核患者管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_IDR_Report</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.tb.TB/TB/TB_List"/>
		</module>
		<module id="TB_List" name="肺结核患者管理" type="1"  script="chis.application.tb.script.TuberculosisListView">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_IDR_Report</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create"/>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="print"  name="打印"/>
		</module>
		
		<module id="TB_Module" name="肺结核患者管理" type="1" script="chis.application.tb.script.TuberculosisModule">
			<properties> 
				<p name="isAutoScroll">true</p> 
			</properties>
			<action id="TBModule" name="肺结核患者管理" ref="chis.application.tb.TB/TB/TB_01" type="tab"/>
			<action id="TBFirstVisit" name="肺结核患者第一次入户随访" ref="chis.application.tb.TB/TB/TB_02" type="tab"/>
			<action id="TBVisit" name="肺结核患者随访" ref="chis.application.tb.TB/TB/TB_03" type="tab"/>
		</module>
		
		<module id="TB_01" name="肺结核患者管理" type="1" script="chis.application.idr.script.IDR_ReportModule">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_IDR_Report</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="TBList" name="列表" ref="chis.application.tb.TB/TB/TB_0101"/> 
			<action id="TBForm" name="表单" ref="chis.application.tb.TB/TB/TB_0102"/> 
		</module>
		<module id="TB_0101" name="肺结核患者列表" type="1" script="chis.application.tb.script.TB_IDR_ReportList">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.IDR_ReportList</p>
			</properties>
		</module>
		<module id="TB_0102" name="肺结核患者表单" type="1" script="chis.application.tb.script.TB_IDR_ReportForm">
			<properties>                                             
				<p name="entryName">chis.application.tb.schemas.TB_IDR_Report</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="create" name="新建" iconCls="create"/>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="print"  name="打印"/>
		</module>
		
		<module id="TB_02" name="肺结核患者第一次入户随访" type="1" script="chis.application.tb.script.TuberculosisFirstVisitHtmlForm">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_TuberculosisFirstVisit</p>
			</properties>
			<action id="save" name="保存" />
		</module>

		<module id="TB_03" name="肺结核患者随访整体模块" type="1"  script="chis.application.tb.script.TuberculosisVisitModule">
			<action id="TuberculosisVisitList" name="肺结核患者随访计划列表"  ref="chis.application.tb.TB/TB/TB_0301" />
			<action id="TuberculosisVisitForm" name="肺结核患者随访信息表单"  ref="chis.application.tb.TB/TB/TB_Visit" />
		</module>
		<module id="TB_0301" name="肺结核患者随访列表" type="1"  script="chis.application.tb.script.TuberculosisVisitList">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_TuberculosisVisitList</p>
			</properties>
		</module>
		<module id="TB_Visit" name="肺结核患者随访" type="1" script="chis.application.tb.script.TuberculosisVisitHtmlForm">
			<properties>
				<p name="entryName">chis.application.tb.schemas.TB_TuberculosisVisit</p>
			</properties>
			<action id="create" name="新建" />
			<action id="save" name="保存" />
			<action id="delete" name="删除" iconCls="remove" />
		</module>
	</catagory>
</application>