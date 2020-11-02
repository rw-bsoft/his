<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.sto.STO" name="药库管理">
	<catagory id="STO" name="药库管理">
		<module id="STO01" name="药品出入库汇总" script="phis.application.sto.script.StorehouseInOutSummaryModule" iconCls="STO01">
			<properties>
				<p name="refRList">phis.application.sto.STO/STO/STO0101</p>
				<p name="refLList">phis.application.sto.STO/STO/STO0102</p>
			</properties>
			<action id="Statistics" name="统计" iconCls = "group_link"/>
			<action id="look" name="明细" iconCls="read" />
		</module>
		<module id="STO0101" name="药品出库汇总" type="1"
			script="phis.application.sto.script.StorehouseOutSummaryModule" iconCls="STO0101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CRKHZ_CK</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="serviceAction">loadStorehouseOutSummary</p>
				<p name="refList">phis.application.sto.STO/STO/STO010101</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="print" name="打印" />
			<action id="export" name="导出" iconCls="excel"/>
		</module>
		<module id="STO0102" name="药品入库汇总" type="1"
			script="phis.application.sto.script.StorehouseInSummaryModule" iconCls="STO0102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CRKHZ_RK</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="serviceAction">loadStorehouseInSummary</p>
				<p name="refList">phis.application.sto.STO/STO/STO010201</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="print" name="打印" />
			<action id="export" name="导出" iconCls="excel"/>
		</module>
		<module id="STO010101" name="药品出库汇总明细" type="1"
			script="phis.application.sto.script.StorehouseOutSummaryDetailList" iconCls="STO010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPCKHZ</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="serviceAction">loadStorehouseOutSummaryDetailList</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="print" name="打印" />
			<action id="export" name="导出" iconCls="excel"/>
		</module>
		<module id="STO010201" name="药品入库汇总明细" type="1"
			script="phis.application.sto.script.StorehouseInSummaryDetailList" iconCls="STO010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPRKHZ</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="serviceAction">loadStorehouseInSummaryDetailList</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="print" name="打印" />
			<action id="export" name="导出" iconCls="excel"/>
		</module>
		
		<module id="STO02" name="药品失效报警" 
			script="phis.application.sto.script.StorehouseDrugsExpireTipsModule" iconCls="STO010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPXX_KCYJ</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="serviceAction">loadStorehouseDrugsExpireTipsList</p>
				<p name="queryDateAction">querySX_PREALARM</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="query" name="刷新" />
			<action id="print" name="打印" />
		</module>
		<module id="STO31" name="采购计划" 
			script="phis.application.sto.script.StorehouseProcurementPlanList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JH01</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="initializationServiceActionID">initialQuery</p>
				<p name="serviceAction">queryJHDS</p>
				<p name="removeActionId">removeStorehouseProcurementPlan</p>
				<p name="addRef">phis.application.sto.STO/STO/STO3101</p>
			</properties>
			<action id="add" name="新增" />
			<action id="remove" name="删除" />
			<action id="sp" name="审批" iconCls="update"/>
			<action id="upload" name="上传采购计划" iconCls="update"/>
			<action id="uploadProducts" name="同步商品(中医院)" iconCls="update"/>
			<action id="uploadSuppliers" name="同步供应商(中医院)" iconCls="update"/>
		</module>
		<module id="STO3101" name="采购计划单详细module" type="1"
			script="phis.application.sto.script.StorehouseProcurementPlanDetailModule" >
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO310101</p>
				<p name="refList">phis.application.sto.STO/STO/STO310102</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryZdjhActionId">queryZdjh</p>
				<p name="saveActionId">saveCgjh</p>
			</properties>
			<action id="zdjh" name="自动计划" iconCls="save24" scale="large"/>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="sp" name="审批" iconCls="save24" scale="large"/>
			<action id="print" name="打印" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO310101" name="采购计划单详细form" type="1"
			script="phis.application.sto.script.StorehouseProcurementPlanDetailForm" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JH01</p>
				<p name="loadServiceId">storehouseManageService</p>
				<p name="loadMethod">queryJhdForm</p>
			</properties>
		</module>
		<module id="STO310102" name="采购计划单详细list" type="1"
			script="phis.application.sto.script.StorehouseProcurementPlanDetailList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JH02</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="serviceAction">queryJhdList</p>
				<p name="queryKcslActionId">queryKcsl</p>
				<p name="serviceId">storehouseManageService</p>		
			</properties>
			<action id="create" name="增加" />
			<action id="remove" name="删除" />
		</module>
		<module id="STO03" name="采购入库" 
			script="phis.application.sto.script.StorehouseCheckInModule" iconCls="STO03">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01</p>
				<p name="refLeftList">phis.application.sto.STO/STO/STO0301</p>
				<p name="refRightList">phis.application.sto.STO/STO/STO0302</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="STO0301" name="未确认入库单"  type="1"
			script="phis.application.sto.script.StorehouseUndeterminedCheckInList" iconCls="STO0301">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_WQR</p>
				<p name="verificationDeleteActionId">verificationCheckInDelete</p>
				<p name="removeActionId">removeCheckInData</p>
				<p name="addRef">phis.application.sto.STO/STO/STO030101</p>
				<p name="commitRef">phis.application.sto.STO/STO/STO030102</p>
				<p name="downloadsRef">phis.application.sto.STO/STO/STO030103</p>
				<p name="conditionText">入库方式</p>
				<p name="selectItemId">RKFS</p>
				<p name="imgUrl">phis/resources/images/bogus.png</p>
				<p name="removeByFiled">RKDH</p>
				<p name="readRef">phis.application.sto.STO/STO/STO0303</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="确认"  iconCls="archiveMove_commit"/>
			<action id="downloads" name="省平台配送单下载"  iconCls="arrow-down"/>
			<action id="print" name="打印"/>
		</module>
		<module id="STO0302" name="确认入库单"  type="1"
			script="phis.application.sto.script.StorehouseCheckInList" iconCls="STO0302">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_YQR</p>
				<p name="imgUrl">images/grid.png</p>
				<p name="readRef">phis.application.sto.STO/STO/STO030101</p>
			</properties>
			<action id="look" name="查看"  iconCls="read"></action>
			<action id="print" name="打印"/>
			<action id="export" name="导出" iconCls="excel"/>
		</module>

		<module id="STO0303" name="药库入库单" type="1" script="phis.application.sto.script.StorehouseCheckInPrintView">
		</module>
		<module id="STO0703" name="药库出库单" type="1" script="phis.application.sto.script.StorehouseOutPrintView">
		</module>
		<module id="STO030101" name="采购入库详细信息" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailModule" iconCls="STO030101">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO03010101</p>
				<p name="refList">phis.application.sto.STO/STO/STO03010102</p>
				<p name="refJhd">phis.application.sto.STO/STO/STO03010103</p>
				<p name="name">采购入库单</p>
				<p name="saveCheckInActionId">saveCheckIn</p>
				<p name="saveCheckInToInventoryActionId">saveCheckInToInventory</p>
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPriceChangesServiceId">pharmacyManageService</p>
				<p name="queryPriceChangesServiceAction">queryPriceChanges</p>
			</properties>
			<action id="jhd" name="计划单" iconCls="save24" scale="large"/>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO03010101" name="采购入库详细信息form" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailForm" iconCls="STO03010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_FORM</p>
				<p name="loadCheckInActionId">loadCheckInData</p>
				<p name="serviceId">storehouseManageService</p>
			</properties>
		</module>
		<module id="STO03010102" name="采购入库详细信息list" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailList" iconCls="STO03010102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK02</p>
				<p name="refList">phis.application.sto.STO/STO/STO0301010201</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="print" name="打印"/>
		</module>
		<module id="STO0301010201" name="药库库存" type="1"
			script="phis.application.sto.script.StoreroomInventoryManageList" iconCls="STO0301010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_KCMX_CGRK</p>
			</properties>
			<action id="confirm" name="确定" />
			<action id="close" name="取消" />
		</module>
		<module id="STO03010103" name="计划单" type="1"
			script="phis.application.sto.script.StorehouseProcurementPlanSelectList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JH01_YR</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="serviceAction">queryStorehouseProcurementPlanSelectRecord</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryAction">queryStorehouseProcurementPlanDetailRecord</p>
			</properties>
			<action id="confirm" name="确定" />
			<action id="close" name="取消" />
		</module>
		<module id="STO030102" name="采购入库详细信息_确认" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailModule" iconCls="STO030102">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO03010201</p>
				<p name="refList">phis.application.sto.STO/STO/STO03010202</p>
				<p name="name">采购入库单</p>
				<p name="saveCheckInActionId">saveCheckIn</p>
				<p name="saveCheckInToInventoryActionId">saveCheckInToInventory</p>
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPriceChangesServiceId">pharmacyManageService</p>
				<p name="queryPriceChangesServiceAction">queryPriceChanges</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO03010201" name="采购入库详细信息form_确认" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailForm" iconCls="STO03010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_FORM_QR</p>
				<p name="conditionId">RKFS</p>
				<p name="loadCheckInActionId">loadCheckInData</p>
				<p name="serviceId">storehouseManageService</p>
			</properties>
		</module>
		<module id="STO030103" name="选择配送单日期" script="phis.application.sto.script.Datelist" iconCls="STO030103" type="1">
			<properties>
				<p name="downloadsRef">phis.application.sto.STO/STO/STO0301</p>
				<p name="entryName">phis.application.sto.schemas.date</p>
			</properties>
			<action id="commit" name="确定" iconCls="commit" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="STO03010202" name="采购入库详细信息listm_确认" type="1"
			script="phis.application.sto.script.StorehouseCheckInDetailList" iconCls="STO03010202">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK02_QR</p>
				<p name="refList">STO0301010201</p>
			</properties>
		</module>
		<module id="STO04" name="财务验收" 
			script="phis.application.sto.script.StorehouseFinancialAcceptanceModule" iconCls="STO04">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_KCMX_CGRK</p>
				<p name="refUList">phis.application.sto.STO/STO/STO0401</p>
				<p name="refList">phis.application.sto.STO/STO/STO0402</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryServiceAction">initialQuery</p>
			</properties>
		</module>
		<module id="STO0401" name="未验收入库单" type="1"
			script="phis.application.sto.script.StorehouseUndeterminedFinancialAcceptanceList" iconCls="STO0401">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_CWYS_WYS</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryCheckInWayActionID">queryCheckInWay</p>
				<p name="verificationActionId">verificationFinancialAcceptanceNum</p>
				<p name="readRef">phis.application.sto.STO/STO/STO040101</p>
				<p name="commitRef">phis.application.sto.STO/STO/STO040102</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="commit" name="审核"  iconCls="archiveMove_commit"/>
		</module>
		<module id="STO0402" name="已验收入库单" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceList" iconCls="STO0402">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_CWYS_YYS</p>
				<p name="readRef">phis.application.sto.STO/STO/STO040101</p>
				<p name="dateQueryServicesId">storehouseManageService</p>
				<p name="dateQueryActionId">dateQuery</p>
				<p name="dataQueryActionId">financialAcceptanceDataQuery</p>
				<p name="initDateQueryActionId">initDateQuery</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0303</p>
			</properties>
			<action id="look" name="查看"  iconCls="read"/>
			<action id="print" name="打印" scale="large"/>
		</module>
		<module id="STO040101" name="财务验收详细信息_查看" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadModule" iconCls="STO040101">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO04010101</p>
				<p name="refList">phis.application.sto.STO/STO/STO04010102</p>
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0303</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
			<action id="print" name="打印" scale="large"/>
		</module>
		<module id="STO04010101" name="财务验收详细信息form_查看" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadForm" iconCls="STO04010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_CWYS_FORM</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="loadCheckInActionId">loadCheckInData</p>	
			</properties>
		</module>
		<module id="STO04010102" name="财务验收详细信息list_查看" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadList" iconCls="STO04010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK02_CWYS</p>	
			</properties>
		</module>
		<module id="STO040102" name="财务验收详细信息_审核" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailModule" iconCls="STO040102">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO04010201</p>
				<p name="refList">phis.application.sto.STO/STO/STO04010202</p>
				<p name="serviceId">storehouseManageService</p>	
				<p name="verificationActionId">verificationFinancialAcceptance</p>	
				<p name="saveActionId">saveFinancialAcceptance</p>
			</properties>
			<action id="save" name="确定" iconCls="save24" scale="large"/>	
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO04010201" name="财务验收详细信息form_审核" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailForm" iconCls="STO04010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_CWYS_SH_FORM</p>
				<p name="serviceId">storehouseManageService</p>	
				<p name="loadCheckInActionId">loadCheckInData</p>	
			</properties>
		</module>
		<module id="STO04010202" name="财务验收详细信息list_审核" type="1"
			script="phis.application.sto.script.StorehouseFinancialAcceptanceDetailList" iconCls="STO04010202">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK02_CWYS_SH</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryActionId">queryYPKL</p>	
			</properties>
		</module>
		
		<module id="STO05" name="付款处理"  script="phis.application.sto.script.StorehousePaymentModule" iconCls="STO05">
			<properties>
				<p name="refNorthList">phis.application.sto.STO/STO/STO0501</p>	
				<p name="refCenterList">phis.application.sto.STO/STO/STO0502</p>	
				<p name="refPaymentList">phis.application.sto.STO/STO/STO0503</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryServiceAction">initialQuery</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="payment" name="付款处理" iconCls="coins"/>
			<action id="printSum" name="打印汇总表" iconCls="print"/>
			<action id="printDet" name="打印明细表" iconCls="print"/>
		</module>
		<module id="STO0501" name="付款处理NorthList"  type="1"  script="phis.application.sto.script.StorehousePaymentList" iconCls="STO0501">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_FKCL01</p>			
			</properties>
		</module>
		<module id="STO050101" name="付款处理NorthList打印" type="1" script="phis.application.sto.script.StorehousePaymentPrintView" iconCls="STO050101">
		</module>
		<module id="STO0502" name="付款处理CenterList" type="1"  script="phis.application.sto.script.StorehousePaymentDetailsList" iconCls="STO0502">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_FKCL02</p>	
			</properties>
		</module>
		<module id="STO050201" name="付款处理CenterList打印" type="1" script="phis.application.sto.script.StorehousePaymentDetailsPrintView" iconCls="STO050101">
		</module>
		<module id="STO0503" name="付款处理窗口"  type="1"  script="phis.application.sto.script.StorehousePaymentModuleList" iconCls="STO0503">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_FKCL</p>			
			</properties>
			<action id="payment" name="付款" iconCls="coins"/>
			<action id="print" name="打印"/>
			<action id="cancel" name="关闭"  iconCls = "common_cancel"/>
		</module>
		<module id="STO050301" name="付款处理打印" type="1" script="phis.application.sto.script.StorehousePaymentProcessingPrintView" iconCls="STO050301">
		</module>
		
		<module id="STO06" name="其他入库" 
			script="phis.application.sto.script.StorehouseCheckInModule" iconCls="STO06">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01</p>
				<p name="refLeftList">phis.application.sto.STO/STO/STO0601</p>
				<p name="refRightList">phis.application.sto.STO/STO/STO0602</p>			
			</properties>
		</module>
		<module id="STO0601" name="未确认入库单" type="1"
			script="phis.application.sto.script.StorehouseUndeterminedOtherCheckInList" iconCls="STO0601">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_QT_WQR</p>	
				<p name="verificationDeleteActionId">verificationCheckInDelete</p>
				<p name="removeActionId">removeCheckInData</p>
				<p name="addRef">phis.application.sto.STO/STO/STO060101</p>
				<p name="commitRef">phis.application.sto.STO/STO/STO060101</p>
				<p name="conditionText">入库方式</p>
				<p name="selectItemId">RKFS</p>
				<p name="imgUrl">phis/resources/images/bogus.png</p>
				<p name="removeByFiled">RKDH</p>	
				<p name="readRef">phis.application.sto.STO/STO/STO0303</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="确认"  iconCls="archiveMove_commit"/>
			<action id="print" name="打印"/>
		</module>
		<module id="STO0602" name="确认入库单" type="1"
			script="phis.application.sto.script.StorehouseCheckInList" iconCls="STO0602">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_QT_YQR</p>		
				<p name="imgUrl">images/grid.png</p>
				<p name="readRef">phis.application.sto.STO/STO/STO060101</p>	
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0303</p>
			</properties>
			<action id="look" name="查看"  iconCls="read"></action>
			<action id="print" name="打印"/>
		</module>
		<module id="STO060101" name="其他入库详细信息" type="1"
			script="phis.application.sto.script.StorehouseOtherCheckInDetailModule" iconCls="STO060101">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO06010101</p>
				<p name="refList">phis.application.sto.STO/STO/STO06010102</p>
				<p name="name">其他入库单</p>
				<p name="saveCheckInActionId">saveCheckIn</p>
				<p name="saveCheckInToInventoryActionId">saveCheckInToInventory</p>
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPriceChangesServiceId">pharmacyManageService</p>
				<p name="queryPriceChangesServiceAction">queryPriceChanges</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO06010101" name="其他入库详细信息form" type="1"
			script="phis.application.sto.script.StorehouseOtherCheckInDetailForm" iconCls="STO06010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK01_QT_FORM</p>
				<p name="loadCheckInActionId">loadCheckInData</p>
				<p name="serviceId">storehouseManageService</p>		
			</properties>
		</module>
		<module id="STO06010102" name="其他入库详细信息list" type="1"
			script="phis.application.sto.script.StorehouseOtherCheckInDetailList" iconCls="STO06010102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RK02_QT</p>	
				<p name="refList">phis.application.sto.STO/STO/STO0301010201</p>	
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="STO07" name="出库处理" 
			script="phis.application.sto.script.StorehouseCheckOutModule" iconCls="STO07">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01</p>
				<p name="refLeftList">phis.application.sto.STO/STO/STO0701</p>
				<p name="refRightList">phis.application.sto.STO/STO/STO0702</p>			
			</properties>
		</module>
		<module id="STO0701" name="未确认出库单" type="1"
			script="phis.application.sto.script.StorehouseUndeterminedCheckOutList" iconCls="STO0701">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_WQR</p>
				<p name="verificationDeleteActionId">verificationCheckOutDelete</p>
				<p name="removeActionId">removeCheckOutData</p>
				<p name="addRef">phis.application.sto.STO/STO/STO070101</p>
				<p name="commitRef">phis.application.sto.STO/STO/STO070102</p>
				<p name="conditionText">出库方式</p>
				<p name="selectItemId">CKFS</p>
				<p name="imgUrl">phis/resources/images/bogus.png</p>
				<p name="removeByFiled">CKDH</p>		
				<p name="backActionId">saveCheckOutBack</p>	
				<p name="queryActionId">queryDyfs</p>	
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0703</p>
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="add" name="新增" />
			<action id="back" name="退回" iconCls="arrow_undo"/>
			<action id="upd" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="确认"  iconCls="archiveMove_commit"/>
			<action id="print" name="打印"/>
		</module>
		<module id="STO0702" name="确认出库单" type="1"
			script="phis.application.sto.script.StorehouseCheckOutList" iconCls="STO0702">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_QR</p>	
				<p name="imgUrl">images/grid.png</p>
				<p name="readRef">phis.application.sto.STO/STO/STO070102</p>		
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0703</p>
			</properties>
			<action id="look" name="查看"  iconCls="read"></action>
			<action id="print" name="打印"/>
			<action id="export" name="导出" iconCls="excel"/>
			<action id="upload" name="上传消耗信息到省药品平台" iconCls=""/>
		</module>
		<module id="STO070101" name="药库出库详细信息" type="1"
			script="phis.application.sto.script.StorehouseCheckOutDetailModule" iconCls="STO070101">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO07010101</p>
				<p name="refList">phis.application.sto.STO/STO/STO07010102</p>
				<p name="name">药品出库单</p>
				<p name="saveCheckOutActionId">saveStorehouseCheckOut</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO07010101" name="药库出库详细信息form" type="1"
			script="phis.application.sto.script.StorehouseCheckOutDetailForm" iconCls="STO07010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_FORM</p>	
				<p name="queryActionId">queryStorehouseCheckOut</p>
				<p name="serviceId">storehouseManageService</p>	
			</properties>
		</module>
		<module id="STO07010102" name="药库出库详细信息list" type="1"
			script="phis.application.sto.script.StorehouseCheckOutDetailList" iconCls="STO07010102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK02_LIST</p>	
				<p name="queryKcslActionId">queryKcsl</p>
				<p name="serviceId">storehouseManageService</p>		
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="STO070102" name="药库出库提交详细信息" type="1"
			script="phis.application.sto.script.StorehouseCheckOutCommitDetailModule" iconCls="STO070102">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO07010201</p>
				<p name="refList">phis.application.sto.STO/STO/STO07010202</p>
				<p name="name">药品出库单</p>
				<p name="saveCheckOutActionId">saveStorehouseCheckOutCommit</p>
				<p name="queryActionId">queryCheckOutDetail</p>
				<p name="refStorehouseListPrint">phis.application.sto.STO/STO/STO0703</p> 
			</properties>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
			<action id="print" name="打印" scale="large"/>
		</module>
		<module id="STO07010201" name="药库出库提交详细信息form" type="1"
			script="phis.application.sto.script.StorehouseCheckOutDetailForm" iconCls="STO07010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_FORM_COMMIT</p>
				<p name="queryActionId">queryStorehouseCheckOut</p>
				<p name="serviceId">storehouseManageService</p>			
			</properties>
		</module>
		<module id="STO07010202" name="药库出库详细信息list" type="1"
			script="phis.application.sto.script.StorehouseCheckOutCommitDetailList" iconCls="STO07010202">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK02_LIST_COMMIT</p>	
				<p name="refList">phis.application.sto.STO/STO/STO0301010201</p>	
			</properties>
		</module>
		<!-- <module id="STO08" name="药库财务月报2"  script="phis.application.sto.script.StorehouseMonthlyReportPrintView" iconCls="STO08">
				<properties>
				</properties>script="phis.prints.script.StorehouseMonthlyReportPrintView"
			</module>
			-->
		<module id="STO08" name="药库财务月报"  script="phis.application.sto.script.StorehouseMonthlyReportPrintView" iconCls="STO08">
			<properties>
			</properties>
		</module>
		<module id="STO09" name="药品调价"  script="phis.application.sto.script.StorehouseMedicinesPriceAdjustModule" iconCls="STO09">
			<properties>
				<p name="entryName"></p>	
				<p name="refList">phis.application.sto.STO/STO/STO0902</p>
				<p name="refUList">phis.application.sto.STO/STO/STO0901</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryServiceAction">initialQuery</p>
			</properties>
		</module>
		<module id="STO0901" name="未执行调价单" type="1"
			script="phis.application.sto.script.StorehouseMedicinesUndeterminedPriceAdjustList" iconCls="STO0901">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_TJ01</p>
				<p name="removePriceAdjustDataActionId">removePriceAdjustData</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="verificationPriceAdjustDeleteActionId">verificationPriceAdjustDelete</p>
				<p name="removeByFiled">TJDH</p>
				<p name="addRef">phis.application.sto.STO/STO/STO090101</p>
				<p name="commitRef">phis.application.sto.STO/STO/STO090102</p>			
			</properties>
			<action id="refresh" name="刷新"   />
			<action id="add" name="新增"/>
			<action id="upd" name="修改" iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="commit" name="执行" iconCls="archiveMove_commit"/>
		</module>
		<module id="STO0902" name="已执行调价单" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPriceAdjustList" iconCls="STO0902">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_TJ01_ZX</p>	
				<p name="serviceId">storehouseManageService</p>
				<p name="dateQueryActionId">priceAdjustDateQuery</p>
				<p name="readRef">phis.application.sto.STO/STO/STO090102</p>
			</properties>
			<action id="look" name="查看"  iconCls="read"></action>
			<action id="print" name="导出" iconCls="print"  ></action>
		</module>
		<module id="STO090101" name="药品调价单详细信息" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailModule" iconCls="STO090101">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO09010101</p>
				<p name="refList">phis.application.sto.STO/STO/STO09010102</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="savePriceAdjustActionId">savePriceAdjust</p>
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPriceChangesServiceAction">queryPriceChanges</p>
				<p name="queryPriceChangesServiceId">pharmacyManageService</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO09010101" name="药品调价单详细信息form" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailForm" iconCls="STO09010101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_TJ01</p>
			</properties>
		</module>
		<module id="STO09010102" name="药品调价单详细信息list" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailList" iconCls="STO09010102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_TJ02</p>
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryPljcActionId">queryPljc</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="STO090102" name="药房药品调价单详细信息" type="1"
			script="phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailModule" iconCls="STO090102">
			<properties>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="refForm">phis.application.sto.STO/STO/STO09010201</p>
				<p name="refList">phis.application.sto.STO/STO/STO09010202</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryExecutionDataActionId">queryPriceAdjustExecutionData</p>
				<p name="queryExecutionedDataActionId">queryPriceAdjustExecutionedData</p>
				<p name="savePriceAdjustActionId">savePriceAdjustToInventory</p>
			</properties>
			<action id="save" name="确认" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO09010201" name="药品调价单详细信息form" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPriceAdjustDetailForm" iconCls="STO09010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_TJ01_ZX</p>		
			</properties>
		</module>
		<module id="STO09010202" name="药房药品调价单详细信息list" type="1"
			script="phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailList" iconCls="STO09010202">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YF_TJJL_ZX</p>		
			</properties>
		</module>
		
		<module id="STO10" name="库存盘点" 
			script="phis.application.sto.script.StorehouseStoreroomInventoryList" iconCls="STO10">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PD01</p>
				<p name="addRef">phis.application.sto.STO/STO/STO1001</p>
				<p name="removeByFiled">PDDH</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryServiceAction">initialQuery</p>
				<p name="removeActionId">removeInventory</p>
				<p name="queryKCPD_PCAction">queryKCPD_PC</p>
			</properties>
			<action id="add" name ="新增" />
			<action id="upd" name="修改"  iconCls="update"/>
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="STO1001" name="库存盘点" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryModule" iconCls="STO1001">
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO100101</p>
				<p name="refList">phis.application.sto.STO/STO/STO100102</p>
				<p name="refList_pc">phis.application.sto.STO/STO/STO100103</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryPCActionId">quertyInventoryData_PC_KCSB</p>
				<p name="saveActionId">saveInventory</p>
				<p name="commitActionId">saveCommitInventory</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="commit" name="提交"  scale="large" iconCls="sure24"/>
			<action id="save" name="保存"  scale="large" iconCls="save24"/>
		    <!--zhaojian 2017-09-21 库存盘点增加导出功能-->
			<action id="print" name="打印" />
		</module>
		<module id="STO100101" name="库存盘点form" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryForm" iconCls="STO100101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PD01_FORM</p>
			</properties>
		</module>
		 <!--zhaojian 2017-09-21 库存盘点增加导出功能-->
		<module id="STO10010101" name="库存盘点报表" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryPrintView" iconCls="STO10010101">
			<properties>
			</properties>
		</module>
		<module id="STO100102" name="库存盘点list" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryCollectList" iconCls="STO100102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PD02_HZ</p>
				<p name="queryActionId">quertyInventoryData</p>
				<p name="queryDetailActionId">quertyInventoryDataDetail</p>
				<p name="detailRef">phis.application.sto.STO/STO/STO10010201</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
		</module>
		<module id="STO10010201" name="库存_按批次" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryCollectConfirmList" iconCls="STO10010201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PD02_MX</p>
			</properties>
			<action id="commit" name="确认" />
		</module>
		<module id="STO100103" name="库存盘点detailslist" type="1"
			script="phis.application.sto.script.StorehouseStoreroomInventoryDetailsList" iconCls="STO100102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PD02_MX</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="queryActionId">quertyInventoryData_PC</p>
			</properties>
		</module>
		
		<module id="STO11" name="库存查询" 
			script="phis.application.sto.script.StorehouseStockSearchTabModule" iconCls="STO11">
			<properties>
			</properties>
			<action id="booksInventory" viewType="list" name="帐册库存" ref="phis.application.sto.STO/STO/STO1101" />
			<action id="physicalDetails" viewType="list" name="实物明细" ref="phis.application.sto.STO/STO/STO1102" />
			<action id="PharmacyInventoryDetails" viewType="list" name="药房实物明细" ref="phis.application.sto.STO/STO/STO1103" />
		</module>
		<module id="STO1101" name="帐册库存" type="1"
			script="phis.application.sto.script.StorehousePriceBooksInventoryList" iconCls="STO1101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_ZCKC</p>
				<p name="fullserviceId">phis.storehouseManageService </p>
				<p name="serviceActionId">stockSearch</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="STO1102" name="实物明细" type="1"
			script="phis.application.sto.script.StorehousePricePhysicalDetailsList" iconCls="STO1102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_SWMX</p>
				<p name="fullserviceId">phis.storehouseManageService </p>
				<p name="serviceActionId">physicalDetails</p>
			</properties>
			<action id="print" name="打印" />
			<action id="upload" name="上传库存信息至省药品平台" />
		</module>
		<module id="STO1103" name="药房实物明细" type="1"
			script="phis.application.sto.script.StorehousePharmacyInventoryDetailsList" iconCls="STO1103">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YFKCMX</p>
				<p name="fullserviceId">phis.storehouseManageService </p>
				<p name="serviceActionId">pharmacyStockSearch</p>
			</properties>
		</module>
		<module id="STO110101" name="帐册库存报表" type="1"
			script="phis.application.sto.script.StorehousePriceBooksInventoryPrintView" iconCls="STO110101">
			<properties>
			</properties>
		</module>
		<module id="STO110201" name="实物明细报表" type="1"
			script="phis.application.sto.script.StorehousePricePhysicalDetailsPrintView" iconCls="STO110201">
			<properties>
			</properties>
		</module>
		
		<module id="STO12" name="月底过帐" 
			script="phis.application.sto.script.StorehouseMonthly" iconCls="STO12">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JZJL</p>	
				<p name="refForm">phis.application.sto.STO/STO/STO1201</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryServiceAction">initialQuery</p>	
			</properties>
			<action id="monthly" name="月结"  iconCls="month_edit"/>
		</module>
		<module id="STO1201" name="月底过帐form" type="1"
			script="phis.application.sto.script.StorehouseMonthlyForm" iconCls="STO1201">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_JZJL_FORM</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="serviceActionId">saveStorehouseMonthly</p>	
			</properties>
			<action id="monthly" name="月结" scale ="large"  iconCls="save24"/>
			<action id="cancel" name="关闭" scale ="large"  iconCls="close24"/>
		</module>
		
		<module id="STO13" name="出库方式维护" 
			script="phis.application.sto.script.StorehouseCheckOutWayList" iconCls="STO13">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CKFS</p>	
				<p name="verifiedUsingServiceId">storehouseManageService</p>	
				<p name="verifiedUsingActionId">verifiedUsing</p>
				<p name="updateCls">phis.application.sto.script.StorehouseCheckOutWayForm</p>	
				<p name="createCls">phis.application.sto.script.StorehouseCheckOutWayForm</p>
				<p name="removeByFiled">FSMC</p>	
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="STO14" name="入库方式维护" 
			script="phis.application.sto.script.StorehouseCheckInWayList" iconCls="STO14">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_RKFS</p>	
				<p name="verifiedUsingServiceId">storehouseManageService</p>	
				<p name="verifiedUsingActionId">verifiedUsing</p>
				<p name="updateCls">phis.application.sto.script.StorehouseCheckInWayForm</p>	
				<p name="createCls">phis.application.sto.script.StorehouseCheckInWayForm</p>
				<p name="removeByFiled">FSMC</p>	
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="STO15" name="初始转帐" 
			script="phis.application.sto.script.StorehouseInitialTransfer" iconCls="STO15">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CDXX_CSZC</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryInitialServiceAction">initialQuery</p>
				<p name="queryServiceAction">querySystemInit</p>	
				<p name="transferAction">saveInitialTransfer</p>
				<p name="refInitialTransferPrint">phis.application.sto.STO/STO/STO1501</p>
			</properties>
			<action id="transfer" name="转帐" />
			<action id="print" name="打印" iconCls="print-preview"/>
		</module>
		<module id="STO1501" name="初始转帐" type="1"
			script="phis.prints.script.InitialTransferPrintView" />
		
		<module id="STO21" name="初始帐册" 
			script="phis.application.sto.script.StorehouseInitialBooks" iconCls="STO21">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CDXX_CSZC</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryServiceAction">querySystemInit</p>
				<p name="queryInitialServiceAction">initialQuery</p>	
				<p name="refMoudel">phis.application.sto.STO/STO/STO2101</p>
			</properties>
		</module>
		<module id="STO2101" name="初始帐册module" type="1"
			script="phis.application.sto.script.StorehouseInitialBooksModule" iconCls="STO2101">
			<properties>
				<p name="serviceId">storehouseManageService</p>	
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPljcActionId">queryPljc</p>
				<p name="saveActionId">saveMedicinesStorehouseInitialData</p>
				<p name="refForm">phis.application.sto.STO/STO/STO210101</p>
				<p name="refList">phis.application.sto.STO/STO/STO210102</p>
			</properties>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO210101" name="初始帐册form" type="1"
			script="phis.application.sto.script.StorehouseInitialBooksForm" iconCls="STO210101">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CDXX_CSZC</p>	
				<p name="colCount">4</p>	
				<p name="serviceId">storehouseManageService</p>
				<p name="queryServiceAction">initialDataQuery</p>	
			</properties>
		</module>
		<module id="STO210102" name="初始帐册list" type="1"
			script="phis.application.sto.script.StorehouseInitialBooksList" iconCls="STO210102">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_KCMX_CSZC</p>	
				<p name="showButtonOnTop">true</p>	
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		
		<module id="STO16" name="私用信息维护" 
			script="phis.application.sto.script.StorehouseMedicinesPirvateManageModule" iconCls="STO16">
			<properties>
			</properties>
			<action id="medicinesPirvateManageTab" viewType="list" name="药品私用信息维护" ref="phis.application.sto.STO/STO/STO1601" />
			<action id="priceManageTab" viewType="list" name="价格管理" ref="phis.application.sto.STO/STO/STO1602" />
			<action id="packageTab" viewType="list" name="药品包装" ref="phis.application.sto.STO/STO/STO1603" />	
		</module>
		<module id="STO1601" name="药品私用信息维护" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateManageList" iconCls="STO1601">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPXX</p>	
				<p name="impref">phis.application.sto.STO/STO/STO160102</p>	
				<p name="xgref">phis.application.sto.STO/STO/STO160101</p>	
				<p name="Updateref">phis.application.sto.STO/STO/STO160101</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="invalidActionId">invalidPrivateMedicines</p>	
			</properties>
			<action id="import" name="调入" iconCls="ransferred_all" />
			<action id="xg" name="修改"  />
			<action id="invalid" name="作废" iconCls="writeoff"/>
			<action id="change" name="转移药库" iconCls="ransferred_all"/>
		</module>
		<module id="STO160101" name="药品私用信息维护模版" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateModule" iconCls="STO160101">
			<properties>
				<p name="serviceId">storehouseManageService</p>	
				<p name="saveServiceActionId">saveMedicinesPrivateInformation</p>	
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="mdspropTab" viewType="form" name="药品属性" ref="phis.application.sto.STO/STO/STO16010101" />
			<action id="priceTab" viewType="priceList" name="药品价格" ref="phis.application.sto.STO/STO/STO16010102" />
			<action id="mdsaliasTab" viewType="list" name="药品别名" ref="phis.application.sto.STO/STO/STO16010104"/>
			<action id="mdslimitTab" viewType="editlist" name="用药限制" ref="phis.application.sto.STO/STO/STO16010103" />
		</module>
		<module id="STO16010101" name="药品私用信息维护-修改" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateManageForm" iconCls="STO16010101">
			<properties>
			</properties>
		</module>
		<module id="STO16010102" name="价格" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateManagePriceList" iconCls="STO16010102">
			<properties>
			</properties>
		</module>
		<module id="STO1601010201" name="设置计算公式" type="1"  script="phis.application.mds.script.MedicinesManagePriceCalculateModule">
			<properties> 
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="close"/>
		</module>
		<module id="STO16010103" name="用药限制" type="1"
			script="phis.application.sto.script.StorehouseMedicinesLimitEditorList" iconCls="STO16010103">
			<properties>
			</properties>
		</module>
		<module id="STO16010104" name="药品别名" type="1"
			script="phis.application.sto.script.StorehouseMedicinesAliasEditorList" iconCls="STO16010104">
			<properties>
			</properties>
		</module>
		<module id="STO160102" name="药库药品信息调入" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateImportModule" iconCls="STO160102">
			<properties>
				<p name="navDic">phis.dictionary.medicinesCode</p>	
				<p name="entryName">phis.application.mds.schemas.YK_YPXX_DR</p>	
				<p name="refList">phis.application.sto.STO/STO/STO16010201</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="saveActionId">saveMedicinesPrivateImportInformation</p>	
				<p name="saveAllActionId">saveAllMedicinesPrivateImportInformation</p>	
				<p name="listActionid">medicinesPrivateInformationList</p>	
				<p name="saveSchema">phis.application.sto.schemas.YK_YPXX</p>	
				<p name="fullserviceId">phis.storehouseManageService</p>
			</properties>
			<action id="timeQuery" name="查询" iconCls ="query"/>
			<!--<action id="allImport" name="全部调入" iconCls ="common_select"/>-->
			<action id="save" name="确认" iconCls ="common_select"/>
			<!--<action id="print" name="打印" />-->
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="STO16010201" name="药库药品信息调入" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPirvateImportList" iconCls="STO16010201">
			<properties>
				<p name="enableCnd">0</p>	
				<p name="autoLoadData">0</p>	
				<p name="entryName">phis.application.sto.schemas.YK_TYPK_IMP</p>	
			</properties>
		</module>
		<module id="STO1602" name="价格管理" type="1"
			script="phis.application.mds.script.MedicinesPriceManageList" iconCls="STO1602">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CDXX_JGGL</p>	
			</properties>
		</module>
		<module id="STO1603" name="药品包装信息管理" type="1"
			script="phis.application.sto.script.StorehouseMedicinesPrivateManagePackageList" iconCls="STO1603">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPXX_BZ</p>	
				<p name="refmediPackageMsgYk">phis.application.sto.STO/STO/STO160301</p>	
			</properties>
		</module>
		<module id="STO17" name="药库设置" 
			script="phis.application.sto.script.StorehouseBasicInfomationList" iconCls="STO17">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YKLB</p>	
				<p name="verifiedUsingServiceId">storehouseManageService</p>	
				<p name="verifiedUsingActionId">verifiedUsing_yklb</p>	
				<p name="updateCls">phis.application.sto.script.StorehouseBasicInfomationForm</p>	
				<p name="createCls">phis.application.sto.script.StorehouseBasicInfomationForm</p>	
				<p name="removeByFiled">YKMC</p>	
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<!--以下的 最后做  不知道要不要-->
		<module id="STO18" name="采购历史查询" 
			script="phis.application.sto.script.StorehousePurchaseHistoryModule" iconCls="STO18">
			<properties>
				<p name="refSPHTab">phis.application.sto.STO/STO/STO1801</p>	
			</properties>
			<action id="refresh" name="刷新" />
			<action id="export" name="导出" iconCls="excel" />
			<action id="close" name="关闭" />
		</module>
		<module id="STO1801" name="药库采购历史查询" 
			script="phis.application.sto.script.StorehousePurchaseHistoryTabModule" type="1" iconCls="STO18">
			<properties>
			</properties>
			<action id="drugTab" viewType="list" name="药品" ref="phis.application.sto.STO/STO/STO180101"  />
			<action id="supplierTab" viewType="list" name="供应商" ref="phis.application.sto.STO/STO/STO180102"  />
		</module>
		<module id="STO180101" name="药库采购历史查询_药品" 
			script="phis.application.sto.script.StorehousePH_DrugModule" type="1" iconCls="STO180101">
			<properties>
				<p name="refLeftList">phis.application.sto.STO/STO/STO18010101</p>	
				<p name="refRightList">phis.application.sto.STO/STO/STO18010102</p>	
			</properties>
		</module>
		<module id="STO18010101" name="药库采购历史查询_药品记录列表" 
			script="phis.application.sto.script.StorehousePH_DrugRecordList" type="1" iconCls="STO18010101">
			<properties>
				<p name="summaryable">true</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">querySPHDrugRecord</p>	
				<p name="entryName">phis.application.sto.schemas.YK_DURG_RECORD</p>	
			</properties>
		</module>
		<module id="STO18010102" name="药库采购历史查询_药品明细列表" 
			script="phis.application.sto.script.StorehousePH_DrugDetailsList" type="1" iconCls="STO18010101">
			<properties>
				<p name="summaryable">true</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">querySPHDrugDetails</p>	
				<p name="entryName">phis.application.sto.schemas.YK_DURG_DETAILS</p>	
			</properties>
		</module>
		<module id="STO180102" name="药库采购历史查询 _供应商" 
			script="phis.application.sto.script.StorehousePH_SupplierModule" type="1" iconCls="STO180102">
			<properties>
				<p name="refLeftList">phis.application.sto.STO/STO/STO18010201</p>	
				<p name="refRightList">phis.application.sto.STO/STO/STO18010202</p>	
			</properties>
		</module>
		<module id="STO18010201" name="药库采购历史查询_供应商记录列表" 
			script="phis.application.sto.script.StorehousePH_SupplierRecordList" type="1" iconCls="STO18010201">
			<properties>
				<p name="summaryable">true</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">querySPHSupplierRecord</p>	
				<p name="entryName">phis.application.sto.schemas.YK_SUPPLIER_RECORD</p>	
			</properties>
			<action id="print" name="打印"/>
		</module>
		<module id="STO18010202" name="药库采购历史查询_供应商明细列表" 
			script="phis.application.sto.script.StorehousePH_SupplierDetailsList" type="1" iconCls="STO18010202">
			<properties>
				<p name="summaryable">true</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">querySPHSupplierDetails</p>	
				<p name="entryName">phis.application.sto.schemas.YK_SUPPLIER_DETAILS</p>	
			</properties>
			<action id="print" name="打印"/>
		</module>
		
		<module id="STO19" name="保管员帐薄" 
			script="phis.application.sto.script.StorehouseStoreManBook" iconCls="STO19">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_BGYZB</p>	
				<p name="serviceId">phis.storehouseManageService</p>	
				<p name="serviceAction">getStoreManBook</p>	
			</properties>
		</module>
		
		<module id="STO20" name="调价历史查询" 
			script="phis.application.sto.script.StorehousePriceHistoryList" iconCls="STO20">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PRICEHISTORY_RECORD</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">queryPriceHistory</p>	
				<p name="refPriceHistoryDetails">phis.application.sto.STO/STO/STO2001</p>	
			</properties>
			<action id="mx" name="明细" iconCls="read"/>
			<action id="query" name="查询" />
		</module>
		<module id="STO2001" name="调价历史查询" type="1"
			script="phis.application.sto.script.StorehousePriceHistoryDetailsList" iconCls="STO20">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PRICEHISTORY_DETAILS</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">queryPriceHistoryDetails</p>	
			</properties>
		</module>
		<module id="STO22" name="药品养护" 
			script="phis.application.sto.script.StorehouseMedicinesConservationList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YH01</p>
				<p name="serviceId">storehouseManageService</p>	
				<p name="initializationServiceActionID">initialQuery</p>	
				<p name="removeActionId">removeConservation</p>
				<p name="addRef">phis.application.sto.STO/STO/STO2201</p>	
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="增加" />
			<action id="remove" name="删除" />
		</module>
		<module id="STO2201" name="药品养护详细信息" type="1"
			script="phis.application.sto.script.StorehouseMedicinesConservationDetailModule" >
			<properties>
				<p name="refForm">phis.application.sto.STO/STO/STO220101</p>
				<p name="refList">phis.application.sto.STO/STO/STO220102</p>
				<p name="serviceId">storehouseManageService</p>	
				<p name="saveActionId">saveConservation</p>
				<p name="commitActionId">saveConservationCommit</p>
				<p name="name">药品养护单</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="STO220101" name="药品养护详细信息form" type="1"
			script="phis.application.sto.script.StorehouseMedicinesConservationDetailForm" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YH01_FORM</p>
			</properties>
		</module>
		<module id="STO220102" name="药品养护详细信息list" type="1"
			script="phis.application.sto.script.StorehouseMedicinesConservationDetailList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YH02</p>
				<p name="fullServiceId">phis.storehouseManageService</p>
				<p name="queryActionId">queryConservationDetail</p>
			</properties>
			<action id="print" name="打印"/>
		</module>
		<module id="STO23" name="药品高低储查询"
			script="phis.application.sto.script.StorehouseGDCList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_GDC</p>
				<p name="serviceAction">queryYKGDC</p> 
			</properties>
			<action id="print" name="打印"/>
			<action id="refresh" name="刷新" />
		</module>
		<module id="STO2301" type="1" name="药品高低储打印"
			script="phis.prints.script.StorehouseGDCPrintView">
		</module>
		<module id="STO24" name="药库出库科室汇总" script="phis.prints.script.DepartmentConsumptionSummaryPrintView">
			<properties>
				<p name="refkddmModule">phis.application.sto.STO/STO/STO2401</p>
			</properties>
			<action id="query" name="查询" />
			<action id="details" name="明细" iconCls="read"/>
			<action id="print" name="打印" iconCls="print"/>
		</module>
		<module id="STO2401" type="1" name="药库出库科室汇总" script="phis.application.sto.script.StoreDepartmentConsumptionList">			
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SUM</p>
				<p name="closeAction">hide</p>
				<p name="showButtonOnTop">0</p>
				<p name="disablePagingTbr">1</p>
				<p name="width">200</p>
				<p name="height">200</p>
			</properties>
			<action id="sure" name="确定" iconCls="commit"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="STO29" name="基本药物报表" script="phis.application.pha.script.EssentialDrugsModule">
			<properties>			
			</properties>
			<action id="printTab"  name="基本药物列表报表" ref="phis.application.sto.STO/STO/STO2901" />
			<action id="txprintTab"  name="基本药物图像报表" ref="phis.application.sto.STO/STO/STO2902" />
		</module>
		<module id="STO2901" name="基本药物列表报表" type="1" script="phis.prints.script.EssentialDrugsPrintView">
		</module>
		<module id="STO2902" name="基本药物图像报表" type="1" script="phis.prints.script.EssentialGraphTX">
		</module>
		
		<module id="STO30" name="自备药信息维护" script="phis.script.TabModule"> 
			<action id="basicTab" viewType="list" name="药品基本信息维护" ref="phis.application.sto.STO/STO/STO3001"/>
			<action id="priceManageTab" viewType="list" name="价格管理" ref="phis.application.sto.STO/STO/STO3002" />
			<action id="packageTab" viewType="list" name="药品包装信息管理" ref="phis.application.sto.STO/STO/STO3003" />		
		</module>
		<module id="STO3001" name="药品基本信息维护" type="1"  script="phis.application.mds.script.MedicinesManageList">
			<properties> 
				<p name="initCnd">['eq',['$','a.ZFYP'],['i',1]]</p>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK</p>
				<p name="impref">phis.application.sto.STO/STO/STO300102</p>
				<p name="serviceId">medicinesManageService</p>
				<p name="invalidActionId">invalidMedicines</p>
				<p name="refbasicMediMsg">phis.application.sto.STO/STO/STO300103</p>
				<p name="ZBY">1</p> 
			</properties>
	        <action id="create" name="新增" ref="phis.application.sto.STO/STO/STO300101" />
			<action id="update" name="修改" ref="phis.application.sto.STO/STO/STO300101" />
			<action id="invalid" name="作废" iconCls="writeoff"/>
			<action id="print" name="打印" />
		</module> 
		<module id="STO300103" name="药品基本信息维护打印" type="1" script="phis.application.mds.script.MedicinesBasicPrintView">
		</module>
		<module id="STO3002" name="价格管理"  type="1" script="phis.application.mds.script.MedicinesPriceManageList">
			<properties>
				<p name="initCnd">['eq',['$','c.ZFYP'],['i',1]]</p>
				<p name="entryName">phis.application.mds.schemas.YK_YPCD_JGGL</p>
			</properties>
			<!--<action id="print" name="打印" />-->
		</module> 
		<module id="STO3003" name="药品包装信息维护"  type="1"   script="phis.application.mds.script.MedicinesManagePackageList">
			<properties>
				<p name="initCnd">['eq',['$','a.ZFYP'],['i',1]]</p>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_BZ</p>
			 	<p name="headPlug">true</p> 
			 	<p name="ZBY">1</p> 
			  <p name="refmediPackageMsg">phis.application.sto.STO/STO/STO300301</p> 
			</properties>
			<action id="print" name="打印" />
		</module>  
		<module id="STO300301" name="药品包装信息维护打印" type="1" script="phis.application.mds.script.MediPackageMsgPrintView">
		</module>
		<module id="STO300101" name="药品公共信息维护module"  type="1"  script="phis.application.mds.script.MedicinesManageModule">
			<properties>
				<p name="serviceId">medicinesManageService</p>
				<p name="saveServiceAction">saveMedicinesInfomation</p>
			</properties>
			<action id="mdsproptab" viewType="form" name="药品属性" ref="phis.application.sto.STO/STO/STO30010101" />
			<action id="mdspricetab" viewType="priceList" name="药品价格" ref="phis.application.sto.STO/STO/STO30010102" />
			<action id="mdsaliastab" viewType="list" name="药品别名" ref="phis.application.sto.STO/STO/STO30010103"  />
			<action id="mdslimittab" viewType="editlist" name="用药限制" ref="phis.application.sto.STO/STO/STO30010104" />
			<action id="ypsmtab" viewType="text" name="药品说明" ref="phis.application.sto.STO/STO/STO30010105" />
		</module> 
		<module id="STO30010101" name="药品属性"   type="1"  script="phis.application.mds.script.MedicinesManageForm">
			<properties>
				<p name="serviceId">medicinesManageService</p>
				<p name="verifiedUsingServiceAction">verifiedUsing</p>
				<p name="queryPljcActionId">queryPljc</p>	
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_ZBY</p>
				<p name="fldDefaultWidth">100</p>
			</properties>
		</module>
		<module id="STO30010102" name="价格" type="1"   script="phis.application.mds.script.MedicinesManagePriceList">
			<properties> 
				<p name="entryName">phis.application.mds.schemas.YK_YPCD</p>
				<p name="serviceId">medicinesManageService</p>
				<p name="removeServiceActionId">reomovePriceInformation</p>	 
				<p name="queryPljcActionId">queryPljc</p>	
			</properties>
		</module>
		<module id="STO30010103" name="药品别名" type="1"   script="phis.application.mds.script.MedicinesAliasEditorList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_YPBM</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>  
		<module id="STO30010104" name="用药限制"  type="1" script="phis.application.mds.script.MedicinesLimitEditorList">
			<properties>
				<p name="entryName">phis.application.mds.schemas.GY_BRXZ_YYXZ</p>
				<p name="serviceId">phis.medicinesManageService</p>
				<p name="listMedicinesLimitServiceId">limitInformationList</p>
			</properties>
		</module> 
		<module id="STO30010105" name="药品说明" type="1" script="phis.application.mds.script.MedicinesDescriptionForm">
			<properties>
				<p name="entryName">phis.application.mds.schemas.YK_TYPK_YPSM</p>
			</properties>
		</module>
		
		
		<module id="STO32" name="会计帐薄" 
			script="phis.application.sto.script.StorehouseAccountingBooksList" >
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YPXX_KJZB</p>
				<p name="fullserviceId">phis.storehouseManageService</p>
				<p name="serviceAction">queryStorehouseAccountingBooks</p>
				<p name="serviceId">storehouseManageService</p>
				<p name="initializationServiceActionID">initialQuery</p>
				<p name="refModule">phis.application.sto.STO/STO/STO3201</p>
				<p name="querySfyjAction">querySfyj</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="detail" name="明细" iconCls="read" />
			<action id="print" name="打印" />
		</module>
		<module id="STO3201" name="会计帐薄明细" type="1"
			script="phis.application.sto.script.StorehouseAccountingBooksDetailModule" >
			<properties>
				<p name="cgrkRef">phis.application.sto.STO/STO/STO030101</p>
				<p name="qtrkRef">phis.application.sto.STO/STO/STO060101</p>
				<p name="ckRef">phis.application.sto.STO/STO/STO070102</p>
				<p name="tjRef">phis.application.sto.STO/STO/STO090102</p>
			</properties>
			<action id="detail" name="明细" iconCls="read" />
			<action id="print" name="打印" />
			<action id="cancel" name="退出"  iconCls = "common_cancel"/>
		</module>
		<module id="STO33" name="采购分析" script="phis.application.sto.script.StorehousePurchaseAnalysis" >
			<properties>
				<p name="navDic">phis.dictionary.medicinesCode</p>
				<p name="entryName">phis.application.sto.schemas.YK_ANALYSIS_PURCHASE</p>
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="serviceAction">loadPurchaseAnalysicList</p>
			</properties>
		</module>
		<module id="STO34" name="出库分析" script="phis.application.sto.script.StorehouseOutBoundAnalysis" iconCls="STO01">
			<properties>
				<p name="navDic">phis.dictionary.medicinesCode</p>
				<p name="entryName">phis.application.sto.schemas.YK_ANALYSIS_PURCHASE</p>
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="serviceAction">loadOutBoundAnalysicList</p>
			</properties>
		</module>
		
		<module id="STO35" name="抗菌药采购分析" 
			script="phis.application.sto.script.AntiMicrobialStorehousePurchaseAnalysis">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_ANALYSIS_PURCHASE_ANTIMICROBIAL</p>	
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="serviceAction">loadAntiMicrobialPurchaseAnalysicList</p>
				<p name="AntiMicrobialStorehousePurchaseDetails">phis.application.sto.STO/STO/STO3501</p>	
			</properties>
		</module>
		<module id="STO3501" name="抗菌药采购分析明细" type="1"
			script="phis.application.sto.script.AntiMicrobialStorehousePurchaseDetail">
			<properties>
				<p name="entryName">phis.application.sto.schemas.ANTI_MICROBIAL_DETAILS</p>	
				<p name="serviceId">phis.storehouseManageService</p>	
				<p name="listActionId">loadAntiMicrobialPurchaseDetailList</p>	
			</properties>
			<action id="print" name="打印" />
		</module>
		
		<module id="STO36" name="抗菌药出库分析" 
			script="phis.application.sto.script.AntiMicrobialStorehouseOutBoundAnalysis">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_ANALYSIS_OUTBOUND_ANTIMICROBIAL</p>	
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="serviceAction">loadAntiMicrobialOutBoundAnalysicList</p>
				<p name="AntiMicrobialStorehouseOutBoundDetails">phis.application.sto.STO/STO/STO3601</p>	
			</properties>
		</module>
		<module id="STO3601" name="抗菌药出库分析明细" type="1"
			script="phis.application.sto.script.AntiMicrobialStorehouseOutBoundDetail">
			<properties>
				<p name="entryName">phis.application.sto.schemas.ANTI_MICROBIAL_OUTBOUND_DETAILS</p>	
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="listActionId">loadAntiMicrobialOutBoundDetailList</p>	
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="STO37" name="基本药物出库分析" 
			script="phis.application.sto.script.BasicDrugOutAnalysis">
			<properties>
				<p name="entryName">phis.application.sto.schemas.BasicDrugOutAnalysis</p>	
				<p name="serviceId">phis.storehouseManageService</p>
				<p name="serviceAction">loadBasicDrugOutAnalysicList</p>				
			</properties>
		</module>
	</catagory>
</application>
