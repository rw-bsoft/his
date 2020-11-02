<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.idr.IDR" name="传染病管理"  type="1">
	<catagory id="IDR" name="传染病管理">
		<module id="DCIDR_01" name="传染病管理" script="chis.script.CombinedDocList"> 
			<properties> 
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>  
				<p name="manageUnitField">c.manaUnitId</p>  
				<p name="areaGridField">c.regionCode</p>  
				<p name="navDic">chis.@manageUnit</p>  
				<p name="navField">manaUnitId</p> 
			</properties>  
			<action id="list" name="列表视图" viewType="list" ref="chis.application.idr.IDR/IDR/DCIDR_02"/> 
		</module>  
		<module id="DCIDR_02" name="传染病管理" type="1"  script="chis.application.idr.script.IDR_ReportListView"> 
			<properties> 
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
			</properties>  
			<action id="createByEmpi" name="新建" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>   
			<action id="print"  name="打印"/> 
		</module>  
		<module id="DCIDR_03" name="传染病管理" type="1" script="chis.application.idr.script.IDR_ReportModule">
			<properties>
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="DCIDRList" name="个人传染病列表" ref="chis.application.idr.IDR/IDR/DCIDR_0301"/> 
			<action id="DCIDRForm"  name="个人传染病表单" ref="chis.application.idr.IDR/IDR/DCIDR_0302"/> 
		</module>
		<module id="DCIDR_0301" name="个人传染病列表" type="1" script="chis.application.idr.script.IDR_ReportList">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.IDR_ReportList</p>
			</properties>
		</module>
		<module id="DCIDR_0302" name="个人传染病表单" type="1" script="chis.application.idr.script.IDR_ReportForm">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
				<p name="saveServiceId">chis.idrReportService</p>
				<p name="saveAction">saveIdrReport</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="create" name="新建" iconCls="create"/>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="print"  name="打印"/>
		</module>
		
		<module id="DCIDR_03_PHIS" name="传染病管理" type="1" script="chis.application.idr.script.IDR_ReportPhisModule">
			<properties>
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="DCIDRList" name="个人传染病列表" ref="chis.application.idr.IDR/IDR/DCIDR_03_PHIS01"/> 
			<action id="DCIDRrModule"  name="传染病管理-右" ref="chis.application.idr.IDR/IDR/DCIDR_03_PHIS02"/> 
		</module>
		<module id="DCIDR_03_PHIS01" name="个人传染病列表" type="1" script="chis.application.idr.script.IDR_ReportPhisList">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.IDR_ReportList</p>
			</properties>
		</module>
		<module id="DCIDR_03_PHIS02" name="传染病管理-右" type="1" script="chis.application.idr.script.IDR_ReportPhisRightModule">
			<action id="DCIDR_MZZD_List"  name="个人传染病表单" ref="chis.application.idr.IDR/IDR/DCIDR_03_PHIS02_01"/> 
			<action id="DCIDRForm"  name="个人传染病表单" ref="chis.application.idr.IDR/IDR/DCIDR_03_PHIS02_02"/> 
		</module>
		<module id="DCIDR_03_PHIS02_01" name="个人传染病门诊诊断列表" type="1" script="chis.application.idr.script.IDR_ReportPhisMZZDList">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.MS_BRZD_IDR</p>
				<!--
				<p name="listServiceId">chis.idrReportService</p>
				<p name="listAction">getClinicDiagnosisList</p>
				-->
			</properties>
		</module>
		<module id="DCIDR_03_PHIS02_02" name="个人传染病表单" type="1" script="chis.application.idr.script.IDR_ReportPhisForm">
			<properties>                                             
				<p name="entryName">chis.application.idr.schemas.IDR_Report</p>
				<p name="saveServiceId">chis.idrReportService</p>
				<p name="saveAction">saveIdrReport</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="create" name="新建" iconCls="create"/>  
			<action id="save" name="确定" group="create||update"/> 
			<action id="print"  name="打印"/>
		</module>
		
		<module id="DCIDR_18" name="三小场所列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_19" name="宾馆列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_20" name="其他娱乐场所列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_21" name="建筑工地列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_22" name="集贸市场列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_23" name="流动人口聚集地" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_24" name="居委列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_25" name="学校列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_26" name="工厂列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_27" name="场所干预记录列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_28" name="场所档案信息列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_29" name="被干预人员信息列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_30" name="被干预人员档案匹配" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_31" name="个体干预人员信息" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_32" name="高危库" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_33" name="患者人群列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_34" name="初筛阳性高危人群" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_35" name="送检单列表" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_36" name="HIV确诊患者" script="chis.application.idr.script.Cdc51"></module> 
		
		
		<module id="DCIDR_04" name="肠道门诊就诊卡" script="chis.application.idr.script.Cdc51"></module> 
		<module id="DCIDR_05" name="发热门诊就诊卡" script="chis.application.idr.script.Cdc52"></module>
		<module id="DCIDR_06" name="肝炎门诊就诊卡" script="chis.application.idr.script.Cdc53"></module>
		<module id="DCIDR_07" name="性病门诊就诊卡" script="chis.application.idr.script.Cdc54"></module>
		<module id="DCIDR_09" name="传染病报告卡" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_10" name="传染病管理列表" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_11" name="病家消毒记录单" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_12" name="肠道症候群列表" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_13" name="监测月报表" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_14" name="感染性腹泻病原谱" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_15" name="腹泻病监测报告" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_16" name="霍乱监测报表" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_17" name="菌痢监测报表" script="chis.application.idr.script.Cdc51"></module>
		<module id="DCIDR_08" name="送检单样品" script="chis.application.idr.script.Cdc55"></module>
	</catagory>
</application>