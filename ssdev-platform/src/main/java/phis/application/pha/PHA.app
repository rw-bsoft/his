<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.pha.PHA" name="药房管理">
	<catagory id="PHA" name="药房管理">
		<module id="PHA31" name="处方划价"
			script="phis.application.pha.script.PharmacyPrescriptionEntryModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA3101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA3102</p>
			</properties>
		</module>
		<module id="PHA3101" name="处方录入Form" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionEntryForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFHJ</p>
				<p name="colCount">5</p>
				<p name="showButtonOnTop">0</p>
			</properties>
			<action id="reset" name="重置(F1)" iconCls="page_refresh" accessKey="F1" />
			<action id="close" name="关闭(F2)" iconCls="common_cancel" accessKey="F2"/>
		</module>
		<module id="PHA3102" name="处方录入List" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionEntryList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF02_YFHJ</p>
			</properties>
			<action id="XY" name="西药(alt+1)" iconCls="pill_add" />
			<action id="ZY" name="中药(alt+2)" iconCls="newclinic" />
			<action id="CY" name="草药(alt+3)" iconCls="page_add" />
			<action id="insert" name="插入(alt+C)" iconCls="insertgroup" />
			<action id="newGroup" name="新组(alt+G)" iconCls="newgroup" />
			<action id="remove" name="删除(alt+R)" />
			<action id="delGroup" name="删除组(alt+D)" iconCls="removeclinic" />
			<!-- <action id="copyClinic" name="调入处方" iconCls="copy" /> -->
			<action id="save" name="保存(alt+Z)" />
		</module>
		<module id="PHA01" name="处方审核"
			script="phis.application.pha.script.PharmacyPrescriptionAuditModule"
			iconCls="PHA01">
			<properties>
				<p name="refPrescriptionList">phis.application.pha.PHA/PHA/PHA0101</p>
				<p name="refAuditList">phis.application.pha.PHA/PHA/PHA0102</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryInitActionId">querySystemInit</p>
				<p name="queryEnableActionId">queryPharmacyAuditEnable</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="allAdopt" name="全部通过" iconCls="commit" />
			<action id="save" name="保存" />
		</module>
		<module id="PHA0101" name="病人处方列表" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionList"
			iconCls="PHA0101">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CF01_SH</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="serviceAction">queryPrescription</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
		</module>
		<module id="PHA0102" name="处方审核详细Module" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionModule"
			iconCls="PHA0102">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA010201</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA010202</p>
			</properties>
		</module>
		<module id="PHA010201" name="处方Form" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionForm"
			iconCls="PHA010201">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CF01_SH_FORM</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">queryPrescriptionDetail</p>
			</properties>
		</module>
		<module id="PHA010202" name="处方审核详细列表" type="1"
			script="phis.application.pha.script.PharmacyPrescriptionAuditEditorList"
			iconCls="PHA010202">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CF02_SH</p>
				<p name="summaryBar">summary_prescription</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="serviceAction">queryAuditPrescriptionDetail</p>
				<p name="adoptAllAction">updateToAdoptAll</p>
				<p name="auditAction">auditPrescription</p>
				<p name="auditProposal">phis.application.pha.PHA/PHA/PHA0103</p>
				<p name="passAudit">1</p>
				<p name="unAudit">0</p>
				<p name="unpassAudit">2</p>
			</properties>
		</module>
		<module id="PHA0103" name="审方意见维护" type="1"
			script="phis.application.pha.script.PharmacyAuditProposalModule"
			iconCls="PHA010202">
			<properties>
				<p name="refProposalList">phis.application.pha.PHA/PHA/PHA010301</p>
			</properties>
		</module>
		<module id="PHA010301" name="审方意见List" type="1"
			script="phis.application.pha.script.PharmacyAuditProposalList"
			iconCls="PHA02">
			<properties>
				<p name="entryName">phis.application.pha.schemas.GY_SFYJ</p>
			</properties>
		</module>
		<module id="PHA02" name="直接发药"
			script="phis.application.pha.script.PharmacyDispensingModule"
			iconCls="PHA02">
			<properties>
				<p name="refList">phis.application.pha.PHA/PHA/PHA0201</p>
				<p name="refModule">phis.application.pha.PHA/PHA/PHA0202</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="dispensingServiceActionID">saveDispensing</p>
				<p name="loadPharmacyWindowInfoActionID">loadPharmacyWindowInfo</p>
				<p name="savePharmacyWindowStatusActionID">savePharmacyWindowStatus</p>
				<p name="refPrescriptionPrint">phis.application.pha.PHA/PHA/PHA25</p>
				<p name="refPrescriptionChinePrint">phis.application.pha.PHA/PHA/PHA26</p>
				<p name="refInjectionCardPrint">phis.application.pha.PHA/PHA/PHA27</p>
			</properties>
			<action id="dispensing" name="发药" iconCls="drug" />
			<action id="print" name="打印" />
			<action id="injectionCardPrint" name="注射卡" iconCls="printing" />
			<action id="jkk" name="健康卡" iconCls="ransferred_all" />
		</module>
		<module id="PHA0201" name="待发药处方" type="1"
			script="phis.application.pha.script.PharmacyDispensingList" iconCls="PHA0201">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY</p>
				<p name="showButtonOnTop">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="queryServiceActionID">queryDispensing</p>
				<p name="refreshServiceActionID">queryPharmacyAutoRefresh</p>
			</properties>
		</module>
		<module id="PHA0202" name="待发药处方右边module" type="1"
			script="phis.application.pha.script.PharmacyDispensingRightModule"
			iconCls="PHA0202">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA020201</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA020202</p>
			</properties>
		</module>
		<module id="PHA020201" name="待发药处方FORM" type="1"
			script="phis.application.pha.script.PharmacyDispensingForm" iconCls="PHA020201">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY_FORM</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">queryPrescribingInformation</p>
				<p name="showButtonOnTop">true</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="PHA020202" name="待发药处方明细LIST" type="1"
			script="phis.application.pha.script.PharmacyDispensingDetailList"
			iconCls="PHA020202">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF02_YFFY_LIST</p>
				<!--begin add by zhaojian 2017-05-31 处方明细中增加库存数量 -->
				<p name="serviceId">phis.pharmacyManageService</p>
				<p name="queryServiceAction">queryPrescribingDetailInformation</p>
				<!--end add by zhaojian 2017-05-31 处方明细中增加库存数量 -->
				<p name="showButtonOnTop">true</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="PHA03" name="取消发药"
			script="phis.application.pha.script.PharmacyBackMedicineModule"
			iconCls="PHA03">
			<properties>
				<p name="refList">phis.application.pha.PHA/PHA/PHA0301</p>
				<p name="refModule">phis.application.pha.PHA/PHA/PHA0302</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveBackMedicineServiceActionID">saveBackMedicine</p>
				<p name="refPrescriptionPrint">phis.application.pha.PHA/PHA/PHA25</p>
				<p name="refPrescriptionChinePrint">phis.application.pha.PHA/PHA/PHA26</p>
				<p name="refInjectionCardPrint">phis.application.pha.PHA/PHA/PHA27</p>
			</properties>
			<action id="confirm" name="确认" iconCls="drug"></action>
			<action id="print" name="打印" />
			<action id="injectionCardPrint" name="注射卡" iconCls="printing" />
		</module>
		<module id="PHA0301" name="取消发药处方" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineList"
			iconCls="PHA0301">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="PHA0302" name="取消发药处方右边module" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineRightModule"
			iconCls="PHA0302">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA030201</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA030202</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="queryServiceAction">queryPharmacyDispensingDetail</p>
			</properties>
		</module>
		<module id="PHA030201" name="取消发药处方明细FORM" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineForm"
			iconCls="PHA030201">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY_FORM_QX</p>
				<p name="showButtonOnTop">true</p>
				<p name="colCount">3</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">queryBackMedicinePrescribingInformation</p>
			</properties>
		</module>
		<module id="PHA030202" name="取消发药处方明细LIST" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineDetailList"
			iconCls="PHA030202">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF02_YFFY_LIST</p>
				<!--begin add by zhaojian 2017-05-31 处方明细中增加库存数量 -->
				<p name="serviceId">phis.pharmacyManageService</p>
				<p name="queryServiceAction">queryPrescribingDetailInformation</p>
				<!--end add by zhaojian 2017-05-31 处方明细中增加库存数量 -->
				<p name="showButtonOnTop">true</p>
			</properties>
		</module>
		<module id="PHA30" name="处方退药"
			script="phis.application.pha.script.PharmacyBackPartMedicineModule">
			<properties>
				<p name="refList">phis.application.pha.PHA/PHA/PHA3001</p>
				<p name="refModule">phis.application.pha.PHA/PHA/PHA3002</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveBackMedicineServiceActionID">saveBackPartMedicine</p>
			</properties>
			<action id="confirm" name="确认" iconCls="drug"></action>
		</module>
		<module id="PHA3001" name="退药处方" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY</p>
				<p name="showButtonOnTop">true</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="PHA3002" name="处方退药处方右边module" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineRightModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA300201</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA300202</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="queryServiceAction">queryPharmacyDispensingDetail</p>
			</properties>
		</module>
		<module id="PHA300201" name="取消发药处方明细FORM" type="1"
			script="phis.application.pha.script.PharmacyBackMedicineForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF01_YFFY_FORM_QX</p>
				<p name="showButtonOnTop">true</p>
				<p name="colCount">3</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">queryBackMedicinePrescribingInformation</p>
			</properties>
		</module>
		<module id="PHA300202" name="取消发药处方明细LIST" type="1"
			script="phis.application.pha.script.PharmacyBackPartMedicineDetailList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.MS_CF02_YFTY_LIST</p>
				<p name="showButtonOnTop">true</p>
			</properties>
		</module>

		<module id="PHA04" name="月终过账"
			script="phis.application.pha.script.PharmacyAccountingStatementList"
			iconCls="PHA04">
			<properties>
				<p name="initializationServiceActionId">initialization</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="entryName">phis.application.pha.schemas.YF_JZJL</p>
				<p name="closeAction">true</p>
				<p name="showButtonOnTop">true</p>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA0401</p>
			</properties>
			<action id="monthly" name="月结" iconCls="month_edit" />
		</module>
		<module id="PHA0401" name="药品月终过账form" type="1"
			script="phis.application.pha.script.PharmacyAccountingStatementForm"
			iconCls="PHA0401">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_JZJL_FORM</p>
				<p name="showButtonOnTop">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryDataActionId">queryMedicinesAccountingStatementDate</p>
				<p name="saveActionId">saveMedicinesAccountingStatementDate</p>
			</properties>
			<action id="monthly" name="月结" scale="large" iconCls="save24" />
			<action id="cancel" name="关闭" scale="large" iconCls="close24" />
		</module>
		<!--update by caijy at 2016年8月17日 11:09:30 for 有保管员账簿了 对账没什么存在意义,而且该模块是zhangh做的 新手不熟悉框架问题太多 -->
		<module id="PHA05" name="药房对账"  type="1"
			script="phis.application.pha.script.PharmacyMedicinesBalanceSummaryList"
			iconCls="PHA05">
			<properties>
				<p name="headPlug">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="entryName">phis.application.pha.schemas.YF_YPDZ</p>
				<p name="serviceAction">queryBalanceSummaryInfo</p>
				<p name="initializationServiceAction">balanceInitialization</p>
				<p name="dateQueryActionId">dateBalanceQuery</p>
				<p name="refDetailList">phis.application.pha.PHA/PHA/PHA0501</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="query" name="查询" />
			<action id="reset" name="重置" iconCls="page_refresh" />
			<action id="detail" name="明细" />
		</module>
		<module id="PHA0501" name="药房对账明细" type="1"
			script="phis.application.pha.script.PharmacyMedicinesBalanceDetailList"
			iconCls="PHA0501">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YPDZ_MX</p>
				<p name="initializationServiceAction">balanceDetailInit</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="serviceAction">queryBalanceDetailInfo</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="PHA06" name="库存管理"
			script="phis.application.pha.script.PharmacyInventoryManagementList"
			iconCls="PHA06">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_KCMX_JY</p>
				<p name="showButtonOnTop">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="serviceAction">queryInventory</p>
				<p name="disableInventoryActionId">saveDisableInventory</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="refInventoryDetailsPrint">phis.application.pha.PHA/PHA/PHA0601</p>
			</properties>
			<action id="disable" name="禁用" iconCls="writeoff" />
			<action id="print" name="打印" />
		</module>
		<module id="PHA0601" name="药房库存明细" type="1"
			script="phis.prints.script.InventoryDetailsPrintView" />
		<module id="PHA29" name="冻结库存查询" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_KCDJ_CX</p>
			</properties>
		</module>
		<module id="PHA07" name="药房设置"
			script="phis.application.pha.script.PharmacyBasicInfomationList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YFLB</p>
				<p name="removeByFiled">YFMC</p>
				<p name="createCls">phis.application.pha.script.PharmacyBasicInfomationForm
				</p>
				<p name="updateCls">phis.application.pha.script.PharmacyBasicInfomationForm
				</p>
				<p name="winCls">phis.application.pha.PHA/PHA/PHA0701</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="cancellationActionId">pharmacyCancellation</p>
				<p name="removeByFiled">YFMC</p>
				<p name="receiveDrugsWay">phis.application.pha.PHA/PHA/PHA0702</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="cancellation" name="注销" iconCls="writeoff" />
			<action id="openWin" name="窗口" iconCls="windows" />
			<action id="receiveDrugsWay" name="领药方式" iconCls="pill_go" />
		</module>
		<module id="PHA0702" name="药房向药库领药方式维护" type="1"
			script="phis.application.pha.script.PharmacyMedicinesReceiveDrugsWayList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_YKLB_LYFS</p>
				<p name="receiveWay">phis.application.pha.PHA/PHA/PHA070201</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="queryActionId">queryReceiveWay</p>
				<p name="listActionId">listReceiveWay</p>
				<p name="saveActionId">saveReceiveWay</p>
				<p name="updateActionId">updateReceiveWay</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="PHA070201" name="领用方式" type="1"
			script="phis.application.pha.script.PharmacyMedicinesReceiveWayList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CKFS_LYFS</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="PHA0701" name="窗口设置" type="1"
			script="phis.application.pha.script.PharmacyWindowInfomationList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CKBH</p>
				<p name="removeByFiled">CKMC</p>
				<p name="createCls">phis.application.pha.script.PharmacyWindowInfomationForm
				</p>
				<p name="updateCls">phis.application.pha.script.PharmacyWindowInfomationForm
				</p>
				<p name="showButtonOnTop">true</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="closePharmacyWin" name="停用发药窗口" iconCls="common_cancel" />
		</module>
		<module id="PHA08" name="初始账簿"
			script="phis.application.pha.script.PharmacyInitialBooksModule">
			<properties>
				<p name="showButtonOnTop">true</p>
				<p name="cshList">phis.application.pha.PHA/PHA/PHA0801</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryInitActionId">querySystemInit</p>
				<p name="initialSignsQueriesActionId">initialSignsQueries</p>
			</properties>
		</module>
		<module id="PHA0801" name="账簿初始化" type="1"
			script="phis.application.pha.script.PharmacyInitialBooksList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_KCMX_CSH</p>
				<p name="showButtonOnTop">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveInventoryActionId">saveInventory</p>
				<p name="transferActionId">savePharmacyTransfer</p>
				<p name="refInitialBooksListPrint">phis.application.pha.PHA/PHA/PHA080101</p>
			</properties>
			<action id="transfer" name="转账" iconCls="transfer" />
			<action id="preserve" name="保存" iconCls="save" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="PHA080101" name="药房库存明细" type="1"
			script="phis.prints.script.InitialBooksListPrintView" />
		<module id="PHA09" name="药品信息维护"
			script="phis.application.pha.script.PharmacyMedicinesManageList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YPXX</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="importActionId">savePharmacyMedicinesInformations</p>
				<p name="invalidActionId">validationPharmacyMedicinesInvalid</p>
				<p name="queryInitActionId">querySystemInit</p>
			</properties>
			<action id="import" name="调入" iconCls="ransferred_all"></action>
			<action id="xg" name="修改" iconCls="update" />
			<action id="invalid" name="作废" iconCls="writeoff" />
		</module>
		<module id="PHA0901" name="药房药品信息修改" type="1"
			script="phis.application.pha.script.PharmacyMedicinesManageForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YPXX</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">queryPharmacyMedicinesInformation</p>
				<p name="saveServiceAction">savePharmacyMedicinesInformation</p>
				<p name="ValidationPackageServiceAction">validationPharmacyMedicinesPackage</p>
				<p name="colCount">3</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="PHA10" name="入库方式维护"
			script="phis.application.pha.script.PharmacyCheckInWayList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_RKFS</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="verifiedUsingActionId">verifiedUsing</p>
				<p name="updateCls">phis.application.pha.script.PharmacyCheckInWayForm</p>
				<p name="createCls">phis.application.pha.script.PharmacyCheckInWayForm</p>
				<p name="removeByFiled">FSMC</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA11" name="出库方式维护"
			script="phis.application.pha.script.PharmacyCheckOutWayList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CKFS</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="verifiedUsingActionId">verifiedUsing</p>
				<p name="updateCls">phis.application.pha.script.PharmacyCheckOutWayForm</p>
				<p name="createCls">phis.application.pha.script.PharmacyCheckOutWayForm</p>
				<p name="removeByFiled">FSMC</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA12" name="药品申领"
			script="phis.application.pha.script.PharmacyMedicinesApplyModule">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="queryDataServiceActionID">queryMedicinesApply</p>
				<p name="refUList">phis.application.pha.PHA/PHA/PHA1201</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA1202</p>
			</properties>
		</module>
		<module id="PHA1201" name="未确认申领单" type="1"
			script="phis.application.pha.script.PharmacyMedicinesUndeterminedApplyList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SL_WQR</p>
				<p name="showButtonOnTop">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA120101</p>
				<p name="removeByFiled">CKDH</p>
				<p name="gridDDGroup">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="verificationApplyDeleteActionId">verificationApplyDelete</p>
				<p name="removeApplyDataActionId">removeApplyData</p>
				<p name="updateApplyDataActionId">updateApplyData</p>
				<p name="queryStorehouseActionID">queryStorehouse</p>
				<p name="queryCkfsActionId">queryCkfs</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="commit" name="提交" />
			<action id="qr" name="确认" iconCls="save" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA1202" name="确认申领单" type="1"
			script="phis.application.pha.script.PharmacyMedicinesApplyList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SL_WQR</p>
				<p name="showButtonOnTop">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA120101</p>
				<p name="removeByFiled">CKDH</p>
				<p name="gridDDGroup">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="dateQueryActionId">dateQuery</p>
				<p name="initDateQueryActionId">initDateQuery</p>
			</properties>
			<action id="look" name="查看" iconCls="read" />
		</module>
		<module id="PHA120101" name="药品申领详细信息" type="1"
			script="phis.application.pha.script.PharmacyMedicinesApplyDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA12010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA12010102</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveApplyActionId">saveMedicinesApply</p>
				<p name="saveApplyCommitActionId">saveMedicinesApplyCommit</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确认" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA12010101" name="药品申领详细信息form" type="1"
			script="phis.application.pha.script.PharmacyMedicinesApplyDetailForm">
			<properties>
				<p name="colCount">4</p>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SL_FORM</p>
			</properties>
		</module>
		<module id="PHA12010102" name="药品申领详细信息list" type="1"
			script="phis.application.pha.script.PharmacyMedicinesApplyDetailList">
			<properties>
				<p name="showButtonOnTop">true</p>
				<p name="entryName">phis.application.sto.schemas.YK_CK02_SL_LIST</p>
				<p name="queryKcslActionId">queryKcsl_yfyk</p>
				<p name="serviceId">pharmacyManageService</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>

		<module id="PHA13" name="药房退药"
			script="phis.application.pha.script.PharmacyApplyRefundModule">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="queryDataServiceActionID">queryMedicinesApplyRefund</p>
				<p name="refUList">phis.application.pha.PHA/PHA/PHA1301</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA1302</p>
			</properties>
		</module>
		<module id="PHA1301" name="未确认退药单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedApplyRefundList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SLTY_WQR</p>
				<p name="showButtonOnTop">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA130101</p>
				<p name="removeByFiled">CKDH</p>
				<p name="gridDDGroup">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="verificationApplyDeleteActionId">verificationApplyDelete</p>
				<p name="removeApplyDataActionId">removeApplyData</p>
				<p name="updateApplyDataActionId">updateApplyData</p>
				<p name="queryStorehouseActionID">queryStorehouse</p>
				<p name="queryCkfsActionId">queryTyCkfs</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
		</module>
		<module id="PHA1302" name="确认退药单" type="1"
			script="phis.application.pha.script.PharmacyApplyRefundList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SLTY_WQR</p>
				<p name="showButtonOnTop">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA130101</p>
				<p name="removeByFiled">CKDH</p>
				<p name="gridDDGroup">true</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="dateQueryActionId">dateQuery</p>
				<p name="initDateQueryActionId">initDateQuery</p>
			</properties>
			<action id="look" name="查看" iconCls="read" />
		</module>
		<module id="PHA130101" name="药品退药详细信息" type="1"
			script="phis.application.pha.script.PharmacyApplyRefundDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA13010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA13010102</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="saveApplyActionId">saveMedicinesApply</p>
				<p name="saveApplyCommitActionId">saveMedicinesApplyRefundCommit</p>
				<p name="queryActionId">queryMedicinesApplyRefundDetail</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确认" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA13010101" name="药品退药详细信息form" type="1"
			script="phis.application.pha.script.PharmacyApplyRefundDetailForm">
			<properties>
				<p name="colCount">4</p>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_SLTY_FORM</p>
			</properties>
		</module>
		<module id="PHA13010102" name="药品退药详细信息list" type="1"
			script="phis.application.pha.script.PharmacyApplyRefundDetailList">
			<properties>
				<p name="showButtonOnTop">true</p>
				<p name="entryName">phis.application.sto.schemas.YK_CK02_SLTY_LIST</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>

		<module id="PHA14" name="药品入库"
			script="phis.application.pha.script.PharmacyCheckInModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_RK01</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="refUList">phis.application.pha.PHA/PHA/PHA1401</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA1402</p>
			</properties>
		</module>
		<module id="PHA1401" name="未确认入库单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedCheckInList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_RK01</p>
				<p name="showButtonOnTop">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA140101</p>
				<p name="removeCheckInDataActionId">removeCheckInData</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="verificationCheckInDeleteActionId">verificationCheckInDelete</p>
				<p name="removeByFiled">RKDH</p>
				<p name="refNoPharmacyInPrint">phis.application.pha.PHA/PHA/PHA23</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
			<action id="print" name="打印" />
		</module>
		<module id="PHA1402" name="确认入库单" type="1"
			script="phis.application.pha.script.PharmacyCheckInList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_RK01_QR</p>
				<p name="readRef">phis.application.pha.PHA/PHA/PHA140101</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="dateQueryActionId">dateQuery</p>
				<p name="initDateQueryActionId">initDateQuery</p>
				<p name="refYesPharmacyInPrint">phis.application.pha.PHA/PHA/PHA23</p>
			</properties>
			<action id="look" name="查看" iconCls="read" />
			<action id="print" name="打印" />
		</module>
		<module id="PHA140101" name="药品入库详细信息" type="1"
			script="phis.application.pha.script.PharmacyCheckInDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA14010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA14010102</p>
				<p name="dyForm">phis.application.pha.PHA/PHA/PHA14010103</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveCheckInActionId">saveCheckIn</p>
				<p name="saveCheckInToInventoryActionId">saveCheckInToInventory</p>
				<p name="queryControlPricesServiceId">storehouseManageService</p>
				<p name="queryControlPricesServiceAction">queryControlPrices</p>
				<p name="queryPriceChangesServiceAction">queryPriceChanges</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确认" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA14010101" name="药品入库详细信息form" type="1"
			script="phis.application.pha.script.PharmacyCheckInDetailForm">
			<properties>
				<p name="colCount">4</p>
				<p name="entryName">phis.application.pha.schemas.YF_RK01_FORM</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="loadCheckInActionId">loadCheckIn</p>
			</properties>
		</module>
		<module id="PHA14010102" name="药品入库详细信息list" type="1"
			script="phis.application.pha.script.PharmacyCheckInDetailList">
			<properties>
				<p name="showButtonOnTop">true</p>
				<p name="entryName">phis.application.pha.schemas.YF_RK02</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA14010103" name="药品入库详细信息下面打印部分" type="1"
			script="phis.application.pha.script.PharmacyCheckInPrintForm">
			<properties>
				<p name="colCount">2</p>
				<p name="entryName">phis.application.pha.schemas.YF_DY_DB</p>
			</properties>
		</module>
		<module id="PHA15" name="药品出库"
			script="phis.application.pha.script.PharmacyCheckOutModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CK01</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="refUList">phis.application.pha.PHA/PHA/PHA1501</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA1502</p>
			</properties>
		</module>
		<module id="PHA1501" name="未确认出库单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedCheckOutList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CK01</p>
				<p name="showButtonOnTop">true</p>
				<p name="gridDDGroup">true</p>
				<p name="closeAction">true</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA150101</p>
				<p name="verificationCheckOutDeleteActionId">verificationCheckOutDelete</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="removeCheckOutDataActionId">removeCheckOutData</p>
				<p name="removeByFiled">CKDH</p>
				<p name="refNoPharmacyOutPrint">phis.application.pha.PHA/PHA/PHA24</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
			<action id="print" name="打印" />
		</module>
		<module id="PHA1502" name="确认出库单" type="1"
			script="phis.application.pha.script.PharmacyCheckOutList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CK01_QR</p>
				<p name="gridDDGroup">true</p>
				<p name="readRef">phis.application.pha.PHA/PHA/PHA150201</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="dateQueryActionId">dateQuery</p>
				<p name="initDateQueryActionId">initDateQuery</p>
				<p name="refYesPharmacyOutPrint">phis.application.pha.PHA/PHA/PHA24</p>
			</properties>
			<action id="look" name="查看" iconCls="read" />
			<action id="print" name="打印" />
		</module>
		<module id="PHA150101" name="药品出库详细信息" type="1"
			script="phis.application.pha.script.PharmacyCheckOutDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA15010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA15010102</p>
				<p name="dyForm">phis.application.pha.PHA/PHA/PHA15010103</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="saveCheckOutActionId">saveCheckOut</p>
				<p name="saveCheckOutToInventoryActionId">saveCheckOutToInventory</p>
				<p name="queryActionId">queryCheckOutToInventory</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确认" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA15010101" name="药品出库详细信息form" type="1"
			script="phis.application.pha.script.PharmacyCheckOutDetailForm">
			<properties>
				<p name="colCount">4</p>
				<p name="entryName">phis.application.pha.schemas.YF_CK01</p>
			</properties>
		</module>
		<module id="PHA15010102" name="药品出库详细信息list" type="1"
			script="phis.application.pha.script.PharmacyCheckOutDetailList">
			<properties>
				<p name="showButtonOnTop">true</p>
				<p name="entryName">phis.application.pha.schemas.YF_CK02</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA15010103" name="药品出库详细信息下面打印部分" type="1"
			script="phis.application.pha.script.PharmacyCheckInPrintForm">
			<properties>
				<p name="colCount">2</p>
				<p name="entryName">phis.application.pha.schemas.YF_DY_DB</p>
			</properties>
		</module>
		<module id="PHA150201" name="药品出库详细信息查看" type="1"
			script="phis.application.pha.script.PharmacyCheckOutDetailForReadModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA15010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA15020101</p>
				<p name="dyForm">phis.application.pha.PHA/PHA/PHA15010103</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA15020101" name="药品出库详细信息list_查看" type="1"
			script="phis.application.pha.script.PharmacyCheckOutDetailForReadList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CK02_CK</p>
				<p name="showButtonOnTop">true</p>
			</properties>
		</module>

		<module id="PHA16" name="调拨申请"
			script="phis.application.pha.script.PharmacyMySimpleModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01</p>
				<p name="refLList">phis.application.pha.PHA/PHA/PHA1601</p>
				<p name="refRList">phis.application.pha.PHA/PHA/PHA1602</p>
				<p name="leftTitle">未确认调拨单</p>
				<p name="rightTitle">已确认调拨单</p>
			</properties>
		</module>
		<module id="PHA1601" name="未确认调拨申请单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedRequisitionList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_WQR</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA160101</p>
				<p name="commitRef">phis.application.pha.PHA/PHA/PHA160101</p>
				<p name="gridDDGroup">true</p>
				<p name="closeAction">true</p>
				<p name="removeByFiled">SQDH</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
				<p name="verificationDeleteActionId">verificationMedicinesRequisitionDelete</p>
				<p name="removeActionId">removeMedicinesRequisition</p>
				<p name="submitActionId">saveMedicinesRequisitionSubmit</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="submit" name="提交" iconCls="pill_go" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
		</module>
		<module id="PHA1602" name="已确认调拨申请单" type="1"
			script="phis.application.pha.script.PharmacyRequisitionList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_YQR</p>
				<p name="readRef">phis.application.pha.PHA/PHA/PHA160101</p>
				<p name="gridDDGroup">true</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="PHA160101" name="调拨申请详细信息" type="1"
			script="phis.application.pha.script.PharmacyRequisitionDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA16010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA16010102</p>
				<p name="saveActionId">saveMedicinesRequisition</p>
				<p name="commitActionId">saveRequisitionCommit</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确定" iconCls="save24" scale="large" />
			<!--<action id="print" name="打印" iconCls="print24" scale="large"/> -->
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA16010101" name="调拨申请详细信息form" type="1"
			script="phis.application.pha.script.PharmacyRequisitionDetailForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_FORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="PHA16010102" name="调拨申请详细信息list" type="1"
			script="phis.application.pha.script.PharmacyRequisitionDetailList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB02_LIST</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="showButtonOnTop">true</p>
				<p name="queryKcslActionId">queryKcsl_yfdbsq</p>
				<p name="queryTarHouseStore">queryTarHouseStore</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>

		<module id="PHA17" name="调拨出库"
			script="phis.application.pha.script.PharmacyMySimpleModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01</p>
				<p name="refLList">phis.application.pha.PHA/PHA/PHA1701</p>
				<p name="refRList">phis.application.pha.PHA/PHA/PHA1702</p>
				<p name="leftTitle">未确认出库调拨单</p>
				<p name="rightTitle">已确认出库调拨单</p>
			</properties>
		</module>
		<module id="PHA1701" name="未确认出库调拨单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedDeployInventoryList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_WQR</p>
				<p name="commitRef">phis.application.pha.PHA/PHA/PHA170101</p>
				<p name="gridDDGroup">true</p>
				<p name="closeAction">true</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
				<p name="submitActionId">saveMedicinesRequisitionSubmit</p>
				<p name="verificationDeleteActionId">verificationDeployInventorySubmit</p>
				<p name="backActionId">saveDeployInventoryBack</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="back" name="退回" iconCls="arrow_undo" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
		</module>
		<module id="PHA1702" name="已确认出库调拨单" type="1"
			script="phis.application.pha.script.PharmacyDeployInventoryList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_YQR_CK</p>
				<p name="readRef">phis.application.pha.PHA/PHA/PHA170101</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="gridDDGroup">true</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
			</properties>
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="PHA170101" name="调拨出库详细信息" type="1"
			script="phis.application.pha.script.PharmacyDeployInventoryDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA17010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA17010102</p>
				<p name="saveActionId">saveMedicinesRequisition</p>
				<p name="commitActionId">saveDeployInventoryCommit</p>
			</properties>
			<action id="commit" name="确定" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA17010101" name="调拨出库详细信息form" type="1"
			script="phis.application.pha.script.PharmacyDeployInventoryDetailForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_FORM_CK</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="PHA17010102" name="调拨出库详细信息list" type="1"
			script="phis.application.pha.script.PharmacyDeployInventoryDetailList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB02_LIST_CK</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
				<p name="showButtonOnTop">true</p>
				<p name="queryActionId">queryMedicinesRequisitionDetailData</p>
			</properties>
		</module>

		<module id="PHA18" name="调拨退药"
			script="phis.application.pha.script.PharmacyMySimpleModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01</p>
				<p name="refLList">phis.application.pha.PHA/PHA/PHA1801</p>
				<p name="refRList">phis.application.pha.PHA/PHA/PHA1802</p>
				<p name="leftTitle">未确认调拨单</p>
				<p name="rightTitle">已确认调拨单</p>
			</properties>
		</module>
		<module id="PHA1801" name="未确认调拨退药单" type="1"
			script="phis.application.pha.script.PharmacyUndeterminedInventoryBackList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_WQR</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA180101</p>
				<p name="commitRef">phis.application.pha.PHA/PHA/PHA180101</p>
				<p name="gridDDGroup">true</p>
				<p name="removeByFiled">SQDH</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
				<p name="submitActionId">saveMedicinesRequisitionSubmit</p>
				<p name="verificationDeleteActionId">verificationMedicinesRequisitionDelete</p>
				<p name="removeActionId">removeMedicinesRequisition</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="commit" name="确认" iconCls="archiveMove_commit" />
		</module>
		<module id="PHA1802" name="已确认调拨退药单" type="1"
			script="phis.application.pha.script.PharmacyInventoryBackList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_YQR</p>
				<p name="readRef">phis.application.pha.PHA/PHA/PHA180101</p>
				<p name="gridDDGroup">true</p>
				<p name="queryActionId">queryMedicinesRequisition</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
			<action id="look" name="查看" iconCls="read"></action>
		</module>
		<module id="PHA180101" name="调拨退药详细信息" type="1"
			script="phis.application.pha.script.PharmacyInventoryBackDetailModule">
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA18010101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA18010102</p>
				<p name="saveActionId">saveMedicinesRequisition</p>
				<p name="commitActionId">saveInventoryBackCommit</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large" />
			<action id="commit" name="确定" iconCls="save24" scale="large" />
			<action id="cancel" name="关闭" iconCls="close24" scale="large" />
		</module>
		<module id="PHA18010101" name="调拨退药详细信息form" type="1"
			script="phis.application.pha.script.PharmacyRequisitionDetailForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_FORM_TY</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="PHA18010102" name="调拨退药详细信息list" type="1"
			script="phis.application.pha.script.PharmacyInventoryBackDetailList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB02_LIST_TY</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="showButtonOnTop">true</p>
				<p name="queryKcslActionId">queryKcsl_yfdbsq</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>

		<module id="PHA19" name="盘点处理"
			script="phis.application.pha.script.PharmacyInventoryProcessingModule">
			<properties>
				<p name="serviceId">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="saveActionId">savePharmacyInventoryProcessing</p>
				<p name="hzActionId">savePharmacyInventoryProcessingHz</p>
				<p name="wcqActionId">checkPharmacyInventoryProcessingWc</p>
				<p name="jzActionId">savePdZdjz</p>
				<p name="wcActionId">savePharmacyInventoryProcessingWc</p>
				<p name="removeActionId">removePharmacyInventoryProcessing</p>
				<p name="entryName">phis.application.pha.schemas.YF_YK01</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA1901</p>
				<p name="refMoudle">phis.application.pha.PHA/PHA/PHA1902</p>
				<p name="refTab">phis.application.pha.PHA/PHA/PHA1903</p>
				<p name="refXGMoudle">phis.application.pha.PHA/PHA/PHA1902020201</p>
				<p name="refWcForm">phis.application.pha.PHA/PHA/PHA1902020202</p>
			</properties>
			<action id="ks" name="开始" iconCls="group_go" />
			<action id="remove" name="删除" />
			<action id="hz" name="汇总" iconCls="group_link" />
			<action id="wc" name="完成" iconCls="default" />
			<action id="xgsl" name="修改数量" iconCls="vcard_edit" />
			<action id="zdjz" name="自动校准" iconCls="update" />
			<action id="print" name="打印" iconCls="print" />
			<action id="excel" name="导出" iconCls="excel" /><!--zhaojian 2017-10-10-->
		</module>
		<module id="PHA1901" name="盘点处理左边时间list" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingLeftList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01_RQ</p>
			</properties>
		</module>
		<module id="PHA1902" name="盘点处理右边module" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01</p>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA190201</p>
				<p name="refModule">phis.application.pha.PHA/PHA/PHA190202</p>
			</properties>
		</module>
		<module id="PHA190201" name="盘点处理右边module上面的form" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModuleTopForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01_FORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="PHA190202" name="盘点处理右边module下面的moudle" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudle">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01</p>
				<p name="refLeftList">phis.application.pha.PHA/PHA/PHA19020201</p>
				<p name="refRightList">phis.application.pha.PHA/PHA/PHA19020202</p>
				<p name="refRightList_pc">phis.application.pha.PHA/PHA/PHA19020203</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryKCPD_PCAction">queryKCPD_PC</p>
				<p name="queryStateActionId">queryState_pc</p>
				<p name="fullserviceId">phis.pharmacyManageService</p>
			</properties>
		</module>
		<module id="PHA19020201" name="盘点处理右边module下面的moudle左边的list"
			type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleLeftList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01_STATE</p>
			</properties>
		</module>
		<module id="PHA19020202" name="盘点处理右边module下面的moudle右边的list"
			type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleRightList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_LIST</p>
			</properties>
			<!--<action id="transfer" name="盘前转实盘" iconCls="coins"></action> -->
		</module>
		<module id="PHA19020203" name="盘点处理右边module下面的moudle右边的list_不按批次"
			type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingRightModuleUnderMoudleRightList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_PC</p>
			</properties>
		</module>
		<module id="PHA1902020201" name="合并后单个药品的库存盘点" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingXGSLModule">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02</p>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA190202020101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA190202020102</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveActionId">savePharmacyInventoryProcessingXgsl</p>
			</properties>
			<action id="save" name="确认" iconCls="save24" scale="large"></action>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"></action>
		</module>
		<module id="PHA190202020101" name="合并后单个药品的库存盘点form" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingXGSLForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_FORM</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="PHA190202020102" name="合并后单个药品的库存盘点List" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingXGSLList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_GRLR_XGSL</p>
				<p name="headPlug">true</p>
			</properties>
		</module>
		<module id="PHA1902020202" name="药房盘点完成确认" type="1"
			script="phis.application.pha.script.PharmacyInventoryProcessingWcForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK01_WC</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryActionId">queryPharmacyInventoryProcessingWc</p>
				<arg name="colCount">1</arg>
			</properties>
			<action id="save" name="确认" scale="large" iconCls="save24"></action>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"></action>
		</module>
		<module id="PHA1903" name="未完成单子" type="1"
			script="phis.application.pha.script.PharmacyIncompleteListModule">
			<properties>
			</properties>
			<action id="rkd" viewType="rkd" name="入库单"
				ref="phis.application.pha.PHA/PHA/PHA190301" />
			<action id="ckd" viewType="ckd" name="出库单"
				ref="phis.application.pha.PHA/PHA/PHA190302" />
			<action id="dbrkd" viewType="dbrkd" name="调拨入库单"
				ref="phis.application.pha.PHA/PHA/PHA190303" />
			<action id="dbtyd" viewType="dbtyd" name="调拨退药单"
				ref="phis.application.pha.PHA/PHA/PHA190303" />
			<action id="sld" viewType="sld" name="申领单"
				ref="phis.application.pha.PHA/PHA/PHA190304" />
			<action id="tyd" viewType="tyd" name="退药单"
				ref="phis.application.pha.PHA/PHA/PHA190304" />
		</module>
		<module id="PHA190301" name="未确认入库单_盘点" type="1"
			script="phis.application.pha.script.PharmacyIncompleteList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_RK01_PD</p>
			</properties>
		</module>
		<module id="PHA190302" name="未确认出库单_盘点" type="1"
			script="phis.application.pha.script.PharmacyIncompleteList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_CK01_PD</p>
			</properties>
		</module>
		<module id="PHA190303" name="未确认调拨入库单_盘点" type="1"
			script="phis.application.pha.script.PharmacyIncompleteList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_DB01_PD</p>
			</properties>
		</module>
		<module id="PHA190304" name="未确认申领单_盘点" type="1"
			script="phis.application.pha.script.PharmacyIncompleteList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_CK01_PD</p>
			</properties>
		</module>

		<module id="PHA20" name="盘点录入"
			script="phis.application.pha.script.PharmacyInventoryEntryList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_GRLR_RL</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="initializationServiceActionID">savePharmacyInventoryInitData</p>
				<p name="removeActionId">removePharmacyInventoryEntry</p>
				<p name="saveActionId">savePharmacyInventoryEntry</p>
				<p name="wcActionId">savePharmacyInventoryEntryWc</p>
				<p name="czActionId">savePharmacyInventoryEntryCz</p>
				<p name="headPlug">true</p>
				<p name="refAddForm">phis.application.pha.PHA/PHA/PHA2001</p>
			</properties>
			<action id="add" name="增加"></action>
			<action id="remove" name="删除"></action>
			<action id="save" name="保存"></action>
			<action id="wc" name="完成" iconCls="update"></action>
			<action id="cz" name="重置" iconCls="page_refresh"></action>
			<action id="print" name="打印" iconCls="print" /><!--zhaojian 20170930 增加打印功能-->
		</module>
		<module id="PHA2001" name="盘点录入新增" type="1"
			script="phis.application.pha.script.PharmacyInventoryEntryAddForm">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YK02_GRLR_RL_XZ</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="savaActionid">savePharmacyInventoryEntryAdd</p>
				<p name="colCount">2</p>
			</properties>
			<action id="qd" name="确定" iconCls="save"></action>
			<action id="close" name="关闭"></action>
		</module>


		<module id="PHA21" name="药品发药统计"
			script="phis.application.pha.script.PharmacyDispensingStatisticsModule">
			<properties>
				<p name="refLeftList">phis.application.pha.PHA/PHA/PHA2101</p>
				<p name="refRightList">phis.application.pha.PHA/PHA/PHA2102</p>
				<p name="refddlist">phis.application.pha.PHA/PHA/PHA210201</p>
			</properties>
			<action id="tj" name="统计" iconCls="group_link"></action>
			<action id="dy1" name="打印汇总" iconCls="print"></action>
			<action id="dy2" name="打印明细" iconCls="print"></action>
		</module>
		<module id="PHA2101" name="药品发药统计方式" type="1"
			script="phis.application.pha.script.PharmacyStatisticalMethodList">
			<properties>
				<p name="serviceId">pharmacyManageService</p>
				<p name="listActionId">yffytjQuery</p>
				<p name="entryName">phis.application.pha.schemas.STATISTICALMETHODLIST</p>
				<p name="summaryable">true</p>
			</properties>
		</module>
		<module id="PHA2102" name="药品发药统计明细" type="1"
			script="phis.application.pha.script.PharmacyStatisticalDetailsList">
			<properties>
				<p name="serviceId">phis.pharmacyManageService</p>
				<p name="listActionId">yffyDetailsQuery</p>
				<p name="entryName">phis.application.pha.schemas.STATISTICALDETAILSLIST</p>
			</properties>
			<action id="print" name="导出" iconCls="print"></action>
		</module>
		<module id="PHA210201" name="药房发药统计明细-发药明细" type="1"
			script="phis.application.pha.script.PharmacyStatisticalDetailsDetailsList">
			<properties>
				<p name="serviceId">phis.pharmacyManageService</p>
				<p name="listActionId">yffyDetailsDetailsQuery</p>
				<p name="entryName">phis.application.pha.schemas.STATISTICALDETAILSLIST_MX</p>
			</properties>
			<action id="print" name="打印" ></action>
			<action id="close" name="关闭" ></action>
		</module>
		
		<module id="PHA22" name="药房财务月报"
			script="phis.application.pha.script.PharmacyDrugstoreMonthlyReportPrintView">
			<properties>
				<p name="serviceId">pharmacyManageService</p>
				<p name="queryServiceAction">initialization</p>
			</properties>
		</module>
		<module id="PHA23" name="药房入库单" type="1"
			script="phis.prints.script.PharmacyInPrintView">
		</module>
		<module id="PHA24" name="药房出库单" type="1"
			script="phis.prints.script.PharmacyOutPrintView">
		</module>
		<module id="PHA25" name="西药处方笺" type="1"
			script="phis.prints.script.PrescriptionPrintView">
		</module>
		<module id="PHA26" name="中草药处方笺" type="1"
			script="phis.prints.script.PrescriptionChinePrintView">
		</module>
		<module id="PHA27" name="注射卡" type="1"
			script="phis.prints.script.InjectionCardPrintView">
		</module>
		<module id="PHA28" name="特殊药品统计"
			script="phis.prints.script.HospitalSpecialDrugsPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="PHA2801" name="特殊药品明细" type="1"
			script="phis.prints.script.HospitalSpecialDrugsDetailsPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="PHA40" name="抗生素药品统计"
			script="phis.prints.script.HospitalAntibioticDrugsPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
			<action id="export" name="导出" iconCls="excel" />
		</module>
		<module id="PHA32" name="药房高低储提示"
			script="phis.application.pha.script.PharmacyGDCList">
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_GDC</p>
				<p name="disablePagingTbr">true</p>
				<p name="serviceId">phis.pharmacyManageService</p>
				<p name="serviceAction">queryYFGDC</p>
			</properties>
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="PHA33" name="保管员帐簿"
			script="phis.prints.script.PharmacyCustodianBooksPrintView">
			<properties>
			<p name="refModule">phis.application.pha.PHA/PHA/PHA3301</p>
			</properties>
			<action id="query" name="刷新" />
			<action id="detail" name="明细" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="PHA3301" name="保管员帐簿明细" type="1"
			script="phis.application.pha.script.PharmacyCustodianBooksDetailModule">
			<properties>
			<p name="refForm">phis.application.pha.PHA/PHA/PHA330101</p>
			<p name="refList">phis.application.pha.PHA/PHA/PHA330102</p>
			<p name="serviceId">phis.pharmacyManageService</p>
			<p name="listActionId">queryPharmacyCustodianBooksDetail</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="PHA330101" name="保管员帐簿明细form" type="1"
			script="phis.application.pha.script.PharmacyCustodianBooksDetailForm">
			<properties>
			<p name="entryName">phis.application.pha.schemas.YF_BGYZB</p>
			<p name="autoLoadData">false</p>
			</properties>
		</module>
		<module id="PHA330102" name="保管员帐簿明细list" type="1"
			script="phis.application.pha.script.PharmacyCustodianBooksDetailList">
			<properties>
			<p name="entryName">phis.application.pha.schemas.YF_BGYZB</p>
			<p name="autoLoadData">false</p>
			<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		<module id="PHA34" name="药品失效报警"
			script="phis.application.pha.script.PharmacyDrugsExpireTipsList">
			<properties>
			<p name="entryName">phis.application.pha.schemas.YF_SXBJ</p>
			<p name="serviceId">phis.pharmacyManageService</p>
			<p name="serviceAction">queryPharmacyDrugsExpireTips</p>
			</properties>
			<action id="query" name="刷新" />
			<action id="print" name="打印" iconCls="print" />
			<action id="export" name="导出" iconCls="excel" />
		</module>
		<!--上海版本的功能,暂时去掉-->
		<module id="PHA66" name="药品养护" type="1"
			script="phis.application.pha.script.PharmacyMaintainList" >
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YH01</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialQuery</p>
				<p name="removeActionId">removeConservation</p>
				<p name="addRef">phis.application.pha.PHA/PHA/PHA6601</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="增加" />
			<action id="remove" name="删除" />
		</module>
		<module id="PHA6601" name="药品养护详细信息" type="1"
			script="phis.application.pha.script.PharmacyMaintainDetailModule" >
			<properties>
				<p name="refForm">phis.application.pha.PHA/PHA/PHA660101</p>
				<p name="refList">phis.application.pha.PHA/PHA/PHA660102</p>
				<p name="serviceId">pharmacyManageService</p>
				<p name="saveActionId">saveConservation</p>
				<p name="commitActionId">saveConservationCommit</p>
				<p name="name">药品养护单</p>
			</properties>
			<action id="save" name="保存" iconCls="save24" scale="large"/>
			<action id="commit" name="确定" iconCls="save24" scale="large"/>
			<action id="cancel" name="关闭" iconCls="close24" scale="large"/>
		</module>
		<module id="PHA660101" name="药品养护详细信息form" type="1"
			script="phis.application.pha.script.PharmacyMaintainDetailForm" >
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YH01_FORM</p>
			</properties>
		</module>
		<module id="PHA660102" name="药品养护详细信息list" type="1"
			script="phis.application.pha.script.PharmacyMaintainDetailList" >
			<properties>
				<p name="entryName">phis.application.pha.schemas.YF_YH02</p>
				<p name="fullServiceId">phis.pharmacyManageService</p>
				<p name="queryActionId">queryConservationDetail</p>
			</properties>
			<action id="print" name="打印"/>
		</module>
		<module id="PHA77" name="调价历史查询" 
			script="phis.application.sto.script.StorehousePriceHistoryList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PRICEHISTORY_RECORD</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">queryPriceHistory2</p>	
				<p name="refPriceHistoryDetails">phis.application.pha.PHA/PHA/PHA7701</p>	
			</properties>
			<action id="mx" name="明细" iconCls="read"/>
			<action id="query" name="查询" />
		</module>
		<module id="PHA7701" name="调价历史查询" type="1"
			script="phis.application.sto.script.StorehousePriceHistoryDetailsList">
			<properties>
				<p name="entryName">phis.application.sto.schemas.YK_PRICEHISTORY_DETAILS</p>	
				<p name="serviceId">storehouseManageService</p>	
				<p name="listActionId">queryPriceHistoryDetails2</p>	
			</properties>
		</module>
	</catagory>
</application>