<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.hos.HOS" name="住院管理">
	<catagory id="HOS" name="住院管理">
		<module id="HOS03" name="入院登记" script="phis.application.hos.script.HospitalAdmissionForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_RYDJ</p>
				<p name="colCount">5</p>
				<p name="refPayment">phis.application.hos.HOS/HOS/HOS11</p>
				<p name="ybxxForm">YB0103</p>
				<p name="sjybxxForm">phis.application.hos.HOS/HOS/SJYB0103</p>
				<p name="refHospMediRecordPrint">phis.application.hos.HOS/HOS/HOS040101</p>
				<p name="refImportList">phis.application.hos.HOS/HOS/HOS0301</p>
				<p name="njjbForm">phis.application.hos.HOS/HOS/HOS0109</p>
			</properties>
			<action id="add" name="新建" iconCls="create"/>
			<action id="import" name="调入" iconCls="ransferred_all"/>
			<action id="ybdk" name="医保读卡" iconCls="money"/>
			<action id="zynhdk" name="农合读卡" iconCls="money_add"/>
			<action id="njjb" name="南京金保" iconCls="money"/>
			<action id="reset" name="重置" iconCls="page_refresh"/>
			<action id="save" name="保存"/>
			<!--
				<action id="hzyb" name="市医保" iconCls="money"/>
				<action id="szyb" name="省医保" iconCls="money_yen"/>
			-->
		</module>
		<module id="HOS0301" type="1" name="预约病人列表" script="phis.application.hos.script.HospitalAdmissionImportList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_YYBR</p>
			</properties>
			<action id="import" name="确认" iconCls="ransferred_all"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS0302" name="农合住院登记" type="1" script="phis.application.hos.script.HospitalZynhdjList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.NHDJ_BRDA</p>
				<p name="initCnd">['eq',['$','a.SBHM'],["s","1"]]</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="readcard" name="农合卡" iconCls="start_stm" />
			<action id="readybcard" name="市民卡" iconCls="start_stm" />
			<action id="save" name="确定" iconCls="save" />
			<action id="readcard_hh" name="农合卡(火狐)" iconCls="start_stm" />
		</module>  
		<module id="HOS04" name="病人管理" script="phis.application.hos.script.HospitalPatientManagementList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_BRGL_LIST</p>
				<p name="transformCls">phis.application.hos.script.HospitalPatienttransformForm</p>
				<p name="updateCls">phis.application.hos.script.HospitalPatientManagementForm</p>
				<p name="canceledCls">phis.application.hos.script.HospitalPatientManagementForm</p>
				<p name="cards">phis.application.hos.HOS/HOS/HOS0402</p>
				<p name="hosReferrals">phis.application.hos.HOS/HOS/HOS0403</p>
			</properties>
			<action id="transform" name="转换" iconCls="transfer"/>
			<action id="update" name="修改"/>
			<action id="canceled" name="注销" iconCls="writeoff"/>
			<action id="home" name="首页" iconCls="home"/>
			<action id="cards" name="帐卡" iconCls="coins"/>
			<action id="hosReferrals" name="住院转诊申请" iconCls="transfer"/>
			<!-- add by wuGang.Lo start -->
			<!--
				<action id="hzyb" name="市医保" iconCls="money"/>
				<action id="syb" name="省医保" iconCls="money_yen"/>
			-->
			<!-- add by wuGang.Lo end -->
			<action id="refresh" name="刷新"   />
		</module>
		<module id="HOS0401" name="病人管理form" type="1" script="phis.application.hos.script.HospitalPatientManagementForm">
			<properties> 
				<p name="refHospMediRecordPrint">phis.application.hos.HOS/HOS/HOS040101</p>
			</properties>
			<action id="transform" name="转换" iconCls="transfer"/>
			<action id="canceled" name="注销" iconCls="writeoff"/>
			<action id="save" name="保存"/>
			<action id="ybdk" name="医保读卡" iconCls="money"/>
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS0402" name="费用帐卡" type="1" script="phis.application.hos.script.HospitalPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS080201</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS040203</p>
				<p name="refcontributions">phis.application.hos.HOS/HOS/HOS040201</p>
				<p name="refinventory">phis.application.hos.HOS/HOS/HOS040202</p>
			</properties>
			<action id="contributions" name="缴款记录" iconCls="money_add"/>
			<action id="inventory" name="费用清单" iconCls="inventory"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS040101" name="住院病案首页" type="1" script="phis.prints.script.HospMediRecordPrintView"><!--打印-->
		</module>
		<module id="HOS040201" name="缴款记录" type="1" script="phis.application.hos.script.HospitalPaymentRecordList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_JSCX</p>  
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<!--该处在住院管理-》病人管理模块下-->
		<module id="HOS040202" name="费用清单" type="1" script="phis.application.hos.script.HospitalCostsListModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS08020501</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS08020502</p>
				<p name="refDayList">phis.application.hos.HOS/HOS/HOS08020503</p>
			</properties>
			<action id="whole" name="全部" value="1"/>
			<action id="medical" name="医疗" value="2"/>
			<action id="drugs" name="药品" value="3"/>
			<action id="refresh" name="刷新"/>
			<action id="print" name="打印"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS040203" name="结算管理list" type="1" script="phis.application.hos.script.HospitalSettlementManagementList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_LIST</p>  
				<p name="reftabModule">phis.application.hos.HOS/HOS/HOS04020301</p>
				<p name="openBy">hospitalPatient</p>
			</properties>
		</module>
		<module id="HOS04020301" name="明细项目" type="1" script="phis.application.hos.script.DetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期" ref="phis.application.hos.HOS/HOS/HOS04020302" />
			<action id="mxxm" viewType="list" name="按明细" ref="phis.application.hos.HOS/HOS/HOS04020303" />
		</module>
		<module id="HOS04020302" name="收费项目"  type="1" script="phis.application.hos.script.HospitalRQDetalisModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS0402030201</p>
				<p name="refDetailsList">phis.application.hos.HOS/HOS/HOS0402030202</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS04020303" name="明细项目"  type="1" script="phis.application.hos.script.HospitalDetalisModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS0402030301</p>
				<p name="refDetailsList">phis.application.hos.HOS/HOS/HOS0402030302</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS0402030201" name="收费项目"  type="1" script="phis.application.hos.script.HospitalFeesDetalisList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_SFXM</p>
				<p name="openBy">sfxm</p>
			</properties>
		</module>
		<module id="HOS0402030301" name="明细项目"  type="1" script="phis.application.hos.script.HospitalFeesDetalisList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_MXXM</p>
				<p name="openBy">mxxm</p>
			</properties>
		</module>
		<module id="HOS0402030202" name="收费项目"  type="1" script="phis.application.hos.script.HospitalDetalisList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_RQMX</p>
			</properties>
		</module>
		<module id="HOS0402030302" name="收费项目"  type="1" script="phis.application.hos.script.HospitalDetalisList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_MX</p>
			</properties>
		</module>
		<!-- 住院双向转诊开始-->
		<module id="HOS0403" name="住院转诊模块" type="1" script="phis.application.drc.script.DRApplicationModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS040301</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS040302</p>
			</properties>
		</module>
		<module id="HOS040301" name="住院转诊单" type="1" script="phis.application.drc.script.DRHospitalizationApplicationForm">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_SendExchange</p>
				<p name="serviceId">referralService</p>
				<p name="saveServiceAction">saveZySendExchange</p>
			</properties>
			<action id="submit" name="提交" />
			<action id="cancel" name="取消" iconCls="common_cancel"/>
		</module>
		<module id="HOS040302" name="住院转诊信息" type="1" script="phis.script.TabModule">
			<action id="HOS04030201" viewType="module" name="住院转诊记录" ref="phis.application.hos.HOS/HOS/HOS04030201" />
		</module>
		<module id="HOS04030201" name="住院转诊记录" type="1" script="phis.application.drc.script.DRHospitalizationApplicationList">
			<properties>
				<p name="entryName">phis.application.drc.schemas.DR_ZYHOSPITALREGRECORD</p>
			</properties>
			<action id="print" name="打印"/>
		</module>
		<!-- 住院双向转诊结束-->
		<module id="HOS05" name="床位管理" script="phis.application.hos.script.HospitalBedManagementList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ_CWGL</p>
				<p name="serviceId">hospitalRetreatBedVerificationService</p>
				<p name="serviceAction">saveRetreatBedVerification</p>  
				<p name="refHospitalBedspaceAllocationModule">phis.application.hos.HOS/HOS/HOS0501</p> 
				<p name="refHospitalBedspaceToBedModule">phis.application.hos.HOS/HOS/HOS0502</p>   
			</properties>
			<action id="allocation" name="分配" iconCls="brick_edit" />
			<action id="tobed" name="转床" iconCls="arrow_switch"/>
			<action id="retreatbed" name="退床" iconCls="arrow_undo"/>
			<action id="refresh" name="刷新"  />
			<action id="print" name="打印" />
		</module>
		<module id="HOS14" name="床位管理报表" type="1" script="app.modules.print.HospitalBedManagementPrintView"><!--打印-->
		</module>
		<module id="HOS0501" name="床位分配" type="1" script="phis.application.hos.script.HospitalBedspaceAllocationModule">
			<properties> 
				<p name="serviceId">hospitalBedVerificationService</p>
				<p name="serviceAction">getBedVerification</p>
				<p name="serviceActionBedSave">saveBedVerification</p>  
				<p name="refHospitalBedInformationForm">phis.application.hos.HOS/HOS/HOS050101</p>
				<p name="refHospitalPatientInformationList">phis.application.hos.HOS/HOS/HOS050102</p>   
			</properties>
		</module>
		<module id="HOS050101" name="床位信息" type="1" script="phis.application.hos.script.HospitalBedInformationForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ_XX</p>  
			</properties>
		</module>
		<module id="HOS050102" name="病人信息" type="1" script="phis.application.hos.script.HospitalPatientInformationList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_XX</p>  
			</properties>
		</module>
		<module id="HOS0502" name="床位转床" type="1" script="phis.application.hos.script.HospitalBedspaceToBedModule">
			<properties> 
				<p name="serviceId">hospitalBedspaceToBedService</p>
				<p name="serviceActionSave">saveBedToBedVerification</p>  
				<p name="refHospitalPatientBedsInformationForm">phis.application.hos.HOS/HOS/HOS050201</p>
				<p name="refHospitalToBedInformationList">phis.application.hos.HOS/HOS/HOS050202</p>   
			</properties>
		</module>
		<module id="HOS050201" name="病人床位信息" type="1" script="phis.application.hos.script.HospitalPatientBedsInformationForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_CW</p>  
			</properties>
		</module>
		<module id="HOS050202" name="可转床位信息" type="1" script="phis.application.hos.script.HospitalToBedInformationList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ_KZ</p>  
			</properties>
		</module>
		<module id="HOS06" name="缴款管理" script="phis.application.hos.script.HospitalPaymentProcessingTabModule">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK</p>  
			</properties>
			<action id="paymentprocessing" viewType="list" name="缴款处理" ref="phis.application.hos.HOS/HOS/HOS0601" />
			<action id="paymentqueries" viewType="list" name="缴款查询" ref="phis.application.hos.HOS/HOS/HOS0602" />	
		</module>
		<module id="HOS0601" name="缴款处理"  type="1" script="phis.application.hos.script.HospitalPaymentProcessingModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS060101</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS060102</p>
				<p name="zybrList">phis.application.hos.HOS/HOS/HOS060103</p>
			</properties>
			<action id="canceled" name="注销" iconCls="writeoff"/>
		</module>
		<module id="HOS060101" name="缴款处理form"  type="1" script="phis.application.hos.script.HospitalPaymentProcessingForm">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_JKFORM</p>
				<p name="colCount">1</p>
				<p name="refPayment">phis.application.hos.HOS/HOS/HOS11</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new"/>
			<action id="beforeSave" name="保存" iconCls="save" />
			<action id="printSet" name="打印设置" iconCls="print" />
		</module>
		<module id="HOS060102" name="缴款处理list"  type="1" script="phis.application.hos.script.HospitalPaymentProcessingList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_JK</p>
				<p name="refPaymentList">phis.application.hos.HOS/HOS/HOS11</p>
			</properties>
			<action id="canceled" name="注销" iconCls="writeoff"/>
			<action id="print" name="重打收据"/>
		</module>
		<module id="HOS060103" name="病人列表" type="1" 
			script="phis.application.hos.script.HospitalZYBRList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_JKGL</p>
			</properties>
		</module>
		<module id="HOS0602" name="缴款查询"  type="1" script="phis.application.hos.script.HospitalPaymentQueriesModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS060201</p>
			</properties>
			<action id="new" name="重置" iconCls="new"/>
		</module>
		<module id="HOS060201" name="住院病人缴款记录"  type="1" script="phis.application.hos.script.HospitalPaymentQueriesList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_CX</p>
			</properties>
		</module>
		<module id="HOS07" name="费用记账" script="phis.script.TabModule">
			<action id="HospitalCostProcessingModuleTab" viewType="list" name="明细记账" ref="phis.application.hos.HOS/HOS/HOS0701" />
			<action id="HosPaymentTypeTab" viewType="list" name="记账查询" ref="phis.application.hos.HOS/HOS/HOS0702" />
		</module>
		<module id="HOS0701" name="明细记账" type="1" script="phis.application.hos.script.HospitalCostProcessingModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS070101</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS070102</p>
				<p name="refRefundList">phis.application.hos.HOS/HOS/HOS070103</p>
			</properties>
		</module>
		<module id="HOS0702" name="记账查询" type="1" script="phis.application.hos.script.HospitalAccountingQueryModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS070201</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new"/>
		</module>
		<module id="HOS070101" name="费用记账联" type="1" script="phis.application.hos.script.HospitalCostAccountingForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCF</p>  
			</properties>
			<action id="reSet" name="重置" iconCls="new"/>
			<action id="save" name="保存"/>
		</module>
		<module id="HOS070102" name="明细记账" type="1" script="phis.application.hos.script.HospitalDetailsAccountingList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCL</p>  
			</properties>
		</module>
		<module id="HOS070103" name="退费明细选择" type="1" script="phis.application.hos.script.HospitalRefundsDetailsList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_TF</p>  
				<p name="listServiceId">hospitalCostProcessingService</p>
				<p name="serviceAction">queryRefundInfo1</p> 
			</properties>
			<action id="commit" name="确定" iconCls="commit"/>
			<action id="cancel" name="返回" iconCls="common_cancel"/>
		</module>
		<module id="HOS070201" name="记账查询list" type="1" script="phis.application.hos.script.HospitalCostList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYCX</p>  
				<p name="listServiceId">hospitalCostProcessingService</p>
				<p name="serviceAction">queryCostList</p> 
			</properties>
		</module>
		<module id="HOS08" name="结算管理" script="phis.application.hos.script.HospitalSettlementModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS0801</p>
				<p name="refModule">phis.application.hos.HOS/HOS/HOS0802</p>
			</properties>
		</module>
		<module id="HOS0801" name="结算管理左边list" type="1" script="phis.application.hos.script.HospitalPatientSelectionLeftList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_CYLB</p>  
			</properties>
		</module>
		<module id="HOS0802" name="结算管理右边form加list" type="1" 
		script="phis.application.hos.script.HospitalSettlementManagementModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS080201</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS080202</p>
				<p name="zyjsModule">phis.application.hos.HOS/HOS/HOS080206</p>
				<p name="fyqdModule">phis.application.hos.HOS/HOS/HOS080205</p>
				<p name="jkjlList">phis.application.hos.HOS/HOS/HOS080204</p>
				<p name="ssybjsModule">phis.application.hos.HOS/HOS/HOS080203</p>
				<p name="njjbForm">phis.application.hos.HOS/HOS/HOS0109</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add"/>
			<action id="inventory" name="清单" iconCls="inventory"/>
			<action id="settle" name="结算" iconCls="money_yen"/>
			<action id="invalid" name="作废" iconCls="writeoff"/>
			<!--<action id="ybdk" name="医保读卡" iconCls="money"/>-->
			<!--<action id="zyjsnhdk" name="农合读卡" iconCls="money_add"/>-->
			<!--<action id="choose" name="选择" iconCls="cart"/>-->
			<action id="new" name="清空"/>
			<action id="printSet" name="打印设置" iconCls="print"/>
			<action id="print" name="打印" iconCls="print"/>
			<!--
				<action id="syb" name="省医保" iconCls="money_yen"/>
			-->
		</module>
		<module id="HOS0109" name="南京金保人员信息" type="1" script="phis.application.njjb.script.Njjbmessageform_ZYJS">
			<action id="qr" name="确定" iconCls="save" />
			<!-- <action id="dk" name="读卡" iconCls="read" /> -->
		</module>
		<module id="HOS080201" name="结算管理form" type="1" script="phis.application.hos.script.HospitalSettlementManagementForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_FORM</p>
				<p name="colCount">3</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="HOS080202" name="结算管理list" type="1" script="phis.application.hos.script.HospitalSettlementManagementList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_LIST</p>
				<p name="reftabModule">phis.application.hos.HOS/HOS/HOS08020201</p>
				<p name="openBy">hospital</p>
			</properties>
		</module>
		<module id="HOS08020201" name="明细项目" type="1" script="phis.application.hos.script.DetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期" ref="phis.application.hos.HOS/HOS/HOS04020302" />
			<action id="mxxm" viewType="list" name="按明细" ref="phis.application.hos.HOS/HOS/HOS04020303" />
		</module>
		<module id="HOS080203" name="病人选择" type="1" script="phis.application.hos.script.HospitalPatientSelectionModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS08020301</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS08020302</p>
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS08020301" name="病人选择form" type="1" script="phis.application.hos.script.HospitalPatientSelectionForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRXZ</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="HOS08020302" name="出院病人list" type="1" script="phis.application.hos.script.HospitalPatientSelectionList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_CYLB</p>  
			</properties>
		</module>
		<module id="HOS080204" name="缴款记录" type="1" script="phis.application.hos.script.HospitalPaymentRecordList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_JSCX</p>  
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<!--该处在结算管理模块下-->
		<module id="HOS080205" name="费用清单" type="1" script="phis.application.hos.script.HospitalCostsListModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS08020501</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS08020502</p>
				<p name="refDayList">phis.application.hos.HOS/HOS/HOS08020503</p>
			</properties>
			<action id="whole" name="全部" value="1"/>
			<action id="medical" name="医疗" value="2"/>
			<action id="drugs" name="药品" value="3"/>
			<action id="refresh" name="刷新"/>
			<action id="print" name="打印"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS08020501" name="费用清单form" type="1" script="phis.application.hos.script.HospitalCostsListForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYQD_FORM</p>
				<p name="labelWidth">55</p>
				<p name="colCount">6</p>
			</properties>
		</module>
		<module id="HOS08020502" name="费用清单module" type="1" script="phis.application.hos.script.CostsListTabModule">
			<action id="HospitalCostsListDetailedFormatTab" viewType="list" name="明细格式" ref="phis.application.hos.HOS/HOS/HOS0802050201" />
			<action id="HospitalCostsListSumFormatTab" viewType="list" name="汇总格式" ref="phis.application.hos.HOS/HOS/HOS0802050203" />
			<action id="HospitalCostsListDoctorFormatTab" viewType="list" name="医嘱格式" ref="phis.application.hos.HOS/HOS/HOS0802050202" />
		</module>	
		<module id="HOS08020503" name="日清单" type="1" script="phis.prints.script.DayListPrintView" />
		<module id="HOS0802050201" name="明细格式" type="1" script="phis.application.hos.script.HospitalCostsListList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD</p>  
			</properties>
		</module>
		<module id="HOS0802050203" name="汇总格式" type="1" script="phis.application.hos.script.HospitalCostsSumListTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_HZGS</p>  
			</properties>
		</module>
		<module id="HOS0802050202" name="医嘱格式" type="1" script="phis.application.hos.script.HospitalCostsListDoctorFormatTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_YZGS</p>  
			</properties>
		</module>
		<!--结算管理结算页面-->
		<module id="HOS080206" name="结算处理" type="1" script="phis.application.hos.script.HospitalSettleAccountsModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS08020601</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS08020602</p>
			</properties>
		</module>
		<module id="HOS08020601" name="结算form" type="1" script="phis.application.hos.script.HospitalSettleAccountsForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_ZYJS_FORM</p>
				<p name="colCount">2</p>
				<p name="labelWidth">40</p>
				<p name="refHospitalSettlementPrint">phis.application.hos.HOS/HOS/HOS1305</p>
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS08020602" name="结算缴款记录" type="1" script="phis.application.hos.script.HospitalSettleAccountsList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_TBKK_ZYJS</p>  
			</properties>
		</module>
		<module id="HOS080207" name="农合读卡" type="1" script="phis.application.hos.script.ZYXNHdkList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.NHDJ_BRDA</p>
				<p name="initCnd">['eq',['$','a.SBHM'],["s","1"]]</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="readcard" name="农合卡" iconCls="start_stm" />
			<action id="readsmcard" name="市民卡" iconCls="start_stm" />
			<action id="readcard_hh" name="农合卡(火狐)" iconCls="start_stm" />
		</module>
		<module id="HOS13" name="结算查询" script="phis.application.hos.script.HospitalHistorySettleQueriesModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS1301</p>
				<!--<p name="refHospitalSettlementPrint">HOS1302</p>-->
				<p name="refHospitalSettlementPrint">phis.application.hos.HOS/HOS/HOS1305</p>
				<p name="cards">phis.application.hos.HOS/HOS/HOS1303</p>
			</properties>
			<action id="newQuery" name="重置" iconCls="new"/>
			<action id="printFp" name="打印" iconCls="print"/>
			<action id="cards" name="帐卡" iconCls="coins"/>
		</module>
		<module id="HOS1301" name="历史结算查询list" type="1" script="phis.application.hos.script.HospitalHistorySettleQueriesList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_ZYJS_LSJS</p>
			</properties>
		</module>
		<module id="HOS1302" name="住院发票打印"  type="1" script="phis.prints.script.HospitalChargesPrintView">
		</module>
		<module id="HOS1305" name="住院发票打印"  type="1" script="phis.prints.script.HospitalInvoiceByxPrintView">
		</module>
		<module id="HOS1306" name="住院发票打印"  type="1" script="phis.prints.script.SecurityHospitalPrintView">
		</module>
		<module id="HOS1303" name="费用帐卡" type="1" script="phis.application.hos.script.HospitalPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS130301</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS130302</p>
				<p name="refcontributions">phis.application.hos.HOS/HOS/HOS040201</p>
				<p name="refinventory">phis.application.hos.HOS/HOS/HOS1304</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add"/>
			<action id="inventory" name="清单" iconCls="inventory"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS130301" name="结算管理form" type="1" script="phis.application.hos.script.HospitalSettlementManagementQueryForm">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_FORM_ZK</p>
				<p name="colCount">3</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="HOS130302" name="结算管理list" type="1" script="phis.application.hos.script.HospitalSettlementManagementQueryList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JSGL_LIST</p>
				<p name="reftabModule">phis.application.hos.HOS/HOS/HOS13030201</p>
				<p name="openBy">hospitalQuery</p>
			</properties>
		</module>
		<module id="HOS13030201" name="明细项目" type="1" script="phis.application.hos.script.DetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期" ref="phis.application.hos.HOS/HOS/HOS04020302" />
			<action id="mxxm" viewType="list" name="按明细" ref="phis.application.hos.HOS/HOS/HOS04020303" />
		</module>
		<!--该处在结算查询模块下-->
		<module id="HOS1304" name="费用清单" type="1" script="phis.application.hos.script.HospitalCostsListQueryModule">
			<properties>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS08020501</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS130402</p>
				<p name="refDayList">phis.application.hos.HOS/HOS/HOS08020503</p>
			</properties>
			<action id="whole" name="全部" value="1"/>
			<action id="medical" name="医疗" value="2"/>
			<action id="drugs" name="药品" value="3"/>
			<action id="refresh" name="刷新"/>
			<action id="print" name="打印"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS130402" name="费用清单module" type="1" script="phis.application.hos.script.CostsListTabModule">
			<properties>
				<p name="openBy">Query</p>
			</properties>
			<action id="HospitalCostsListDetailedFormatTabQuery" viewType="list" name="明细格式" ref="phis.application.hos.HOS/HOS/HOS13040201" />
			<action id="HospitalCostsListSumFormatTabQuery" viewType="list" name="汇总格式" ref="phis.application.hos.HOS/HOS/HOS13040203" />
			<action id="HospitalCostsListDoctorFormatTabQuery" viewType="list" name="医嘱格式" ref="phis.application.hos.HOS/HOS/HOS13040202" />
		</module>	
		<module id="HOS13040201" name="明细格式" type="1" script="phis.application.hos.script.HospitalCostsListList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD</p>  
			</properties>
		</module>
		<module id="HOS13040203" name="汇总格式" type="1" script="phis.application.hos.script.HospitalCostsSumListTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_HZGS</p>  
			</properties>
		</module>
		<module id="HOS13040202" name="医嘱格式" type="1" script="phis.application.hos.script.HospitalCostsListDoctorFormatTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_YZGS</p>  
			</properties>
		</module>	
		<module id="HOS09" name="日终结账" script="phis.prints.script.PatientDepartmentChargesDailyPrintView">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_JZXX</p>  
				<p name="serviceid">hospitalizationCheckoutService</p>
				<p name="queryDateActionid">queryDate</p> 
				<p name="isreckonActionid">isreckon</p> 
				<p name="create_jzrb">create_jzrb</p>
				<p name="check">Wf_Check</p>
				<p name="save_jzrb">save_jzrb</p>
				<p name="query_ZY_JZXX">query_ZY_JZXX</p>
				<p name="detail">phis.application.hos.HOS/HOS/HOS0901</p>
				<p name="refCancelCommit">phis.application.hos.HOS/HOS/HOS0902</p>
				<p name="refQueryList">phis.application.hos.HOS/HOS/HOS0903</p>
			</properties>
		</module>
		<module id="HOS0903" name="日报选择" type="1" script="phis.application.hos.script.HosDateChoseList">
			<properties>
				<p name="listServiceId">hospitalizationCheckoutService</p>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_JZRQ</p>
				<p name="closeAction">hide</p>
				<p name="showButtonOnTop">0</p>
				<p name="disablePagingTbr">1</p>
				<p name="width">200</p>
				<p name="height">200</p>
			</properties>
			<action id="sure" name="确定" iconCls="commit"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS0902" name="取消日报" type="1" script="phis.application.hos.script.HosCancelCommitModule">
			<properties>
				<p name="serviceId">hospitalizationCheckoutService</p>
				<p name="refForm">phis.application.hos.HOS/HOS/HOS090201</p>
				<p name="refList">phis.application.hos.HOS/HOS/HOS090202</p>
			</properties>
		</module>
		<module id="HOS090201" name="取消日报form" type="1" script="phis.application.hos.script.HosCancelCommitForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_JZRQ</p>
			</properties>
		</module>
		<module id="HOS090202" name="取消日报list" type="1" script="phis.application.hos.script.HosCancelCommitList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_QXJZ</p>
				<p name="listServiceId">hospitalizationCheckoutService</p>
			</properties>
		</module>
		<module id="HOS0901" name="明细记账" type="1" script="phis.application.hos.script.PatientDepartmentTabModule">
			<action id="accountsDetail"  name="结算明细" ref="phis.application.hos.HOS/HOS/HOS090101" />
			<action id="deliveryDetail"  name="缴款明细" ref="phis.application.hos.HOS/HOS/HOS090102" />
			<action id="refundDetail"  name="退款明细" ref="phis.application.hos.HOS/HOS/HOS090103" />
		</module>
		<module id="HOS090101" name="结算明细" type="1" script="phis.prints.script.AccountsDetailPrintView">
		</module>
		<module id="HOS090102" name="缴款明细" type="1" script="phis.prints.script.DeliveryDetailPrintView">
		</module>
		<module id="HOS090103" name="退款明细" type="1" script="phis.prints.script.RefundDetailPrintView">
		</module>
		<module id="HOS10" name="日终汇总" script="phis.prints.script.PatientDepartmentChargesSummaryPrintView">
			<properties> 
				<p name="serviceId">hospitalGenerateVerificationService</p>
				<p name="serviceAction">generateVerification</p>
				<p name="serviceActionAfter">generateAfterVerification</p>  
				<p name="serviceActionTwo">collectVerification</p>
				<p name="serviceActionQuery">queryVerification</p>
				<p name="serviceActionSave">saveCollect</p>
				<p name="refQueryList">phis.application.hos.HOS/HOS/HOS1001</p>
			</properties> 
		</module>
		<module id="HOS1001" name="汇总选择"  type="1" script="phis.script.SimpleList">
			<properties>
				<p name="listServiceId">chargesProduce</p>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_HZRQ</p>
				<p name="closeAction">hide</p>
				<p name="showButtonOnTop">0</p>
				<p name="disablePagingTbr">1</p>
				<p name="width">200</p>
				<p name="height">200</p>
			</properties>
			<action id="sure" name="确定" iconCls="commit"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="HOS11" name="缴款收据" type="1" script="phis.prints.script.PaymentPrintView">
		</module>
		<module id="HOS01" name="票据管理" script="phis.script.TabModule">
			<!--<properties>
					<p name="winState" type="jo">{pos:[0,0]}</p>
				</properties>-->
			<action id="zyInvoiceNumberTab" viewType="list" name="发票号码维护" ref="phis.application.hos.HOS/HOS/HOS0101" />
			<action id="paymentReceiptTab" viewType="list" name="缴款收据维护" ref="phis.application.hos.HOS/HOS/HOS0102" />
		</module>
		<module id="HOS0101" name="发票号码维护" type="1" script="phis.application.hos.script.InvoiceNumberConfigList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_YGPJ_FP</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">1</p>
				<p name="createCls">phis.application.hos.script.InvoiceNumberConfigForm</p>
				<p name="updateCls">phis.application.hos.script.InvoiceNumberConfigForm</p>
			</properties>
			<action id="create" name="新增" iconCls="add"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="HOS0102" name="缴款收据维护" type="1" script="phis.application.hos.script.PaymentReceiptNumberConfigList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_YGPJ_JK</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">2</p>
				<p name="createCls">phis.application.hos.script.PaymentReceiptNumberConfigForm</p>
				<p name="updateCls">phis.application.hos.script.PaymentReceiptNumberConfigForm</p>
			</properties>
			<action id="create" name="新增" iconCls="add"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="HOS02" name="床位设置" script="phis.application.hos.script.HospitalBedSetList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_CWSZ</p>
				<p name="removeByFiled">BRCH</p>
				<p name="updateCls">phis.application.hos.script.HospitalBedSetForm</p>
				<p name="createCls">phis.application.hos.script.HospitalBedSetForm</p>
			</properties>
			<action id="create" name="新增"/>
			<action id="update" name="修改"/>
			<action id="remove" name="删除"/>
			<!--<action id="print" name="打印"/>-->
		</module>
		<module id="HOS26" name="住院催款维护" script="phis.application.hos.script.HospitalDunningConfigTabModuel">
			<action id="natureDunningTab" viewType="list" name="按性质催款" ref="phis.application.hos.HOS/HOS/HOS2601" />
			<action id="departmentDunningTab" viewType="list" name="按科室催款" ref="phis.application.hos.HOS/HOS/HOS2602" />
		</module>
		<module id="HOS2601" name="按性质催款" type="1" script="phis.application.hos.script.HospitalDunningConfigEditorList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_CKWH_XZ</p>
				<p name="openby">nature</p>
			</properties>
			<action id="save" name="保存" />
			<action id="help" name="帮助说明"/>
		</module>
		<module id="HOS2602" name="按科室催款" type="1"
			script="phis.application.hos.script.HospitalDunningConfigEditorList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_CKWH_KS</p>
				<p name="openby">department</p>
			</properties>
			<action id="save" name="保存" />
			<action id="help" name="帮助说明"/>
		</module>
		<!--<module id="HOS12" name="一日清单" script="phis.application.hos.script.HospitalDayQueryModule">
				<properties>
					<p name="refList">phis.application.hos.HOS/HOS/HOS1201</p>
				</properties>
				<action id="new" name="重置" iconCls="new"/>
			</module>
			<module id="HOS1201" name="一日清单list" type="1" script="phis.application.hos.script.HospitalDayQueryList">
				<properties> 
					<p name="entryName">ZY_FYMX_YRQD</p>  
				</properties>
			</module>-->
		<module id="HOS15" name="催款管理" script="phis.application.hos.script.HospitalDebtDuePromptModuel">
			<properties>
			</properties>
			<action id="debtDueTab" viewType="list" name="欠费清单" ref="phis.application.hos.HOS/HOS/HOS1501" />
			<action id="promptTab" viewType="list" name="催款清单" ref="phis.application.hos.HOS/HOS/HOS1502" />
		</module>
		<module id="HOS1501" name="欠费清单" type="1" script="phis.application.hos.script.HospitalDebtDueList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_QFQD</p>
				<p name="listServiceId">hospitalDebtDueService</p>
				<p name="serviceAction">simpleQuery</p>
			</properties>
		</module>
		<module id="HOS1502" name="催款清单" type="1" script="phis.application.hos.script.HospitalPromptManagementForm">
			<properties>
			</properties>
		</module>
		<module id="HOS16" name="消耗明细查询" script="phis.application.hos.script.SuppliesxhmxList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_WZ_XHMX</p>
				<p name="relPrint">phis.application.hos.HOS/HOS/HOS1601</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="HOS1601" name="消耗明细打印" type="1" script="phis.prints.script.SuppliesxhmxPrintView">
		</module>
		<!-- 不需要
			<module id="HOS17" name="住院记账汇总" script="phis.prints.script.ChargeSummaryZYPrintView">
			</module>
			-->
		<module id="HOS17" name="科室收入核算" script="phis.prints.script.HospitalDepartmentChargesSummaryPrintView">
		</module>
		<module id="HOS171" name="科室收入核算" script="phis.prints.script.HospitalDepartmentChargesSummaryPrintView2">
		</module>
		<module id="HOS18" name="住院收入核算表" script="phis.prints.script.HospiatlIncomeAccountPrintView">
		</module>
		<module id="HOS19" name="在院病人汇总表" script="phis.prints.script.InHospiatlPatientCollectPrintView">
		</module>
		<module id="HOS20" name="住院病人汇总表" script="phis.prints.script.OutHospiatlPatientCollectPrintView">
		</module>
		<module id="HOS21" name="住院性质费用汇总表" script="phis.prints.script.HospiatlNaturePrintView">
		</module>
		<module id="HOS23" name="住院一日清单" script="phis.application.hos.script.HospitalCostModule">
			<properties>
				<p name="refList">phis.application.hos.HOS/HOS/HOS2301</p>
			</properties>
		</module>
		<module id="HOS2301" name="病人列表" type="1" script="phis.application.hos.script.HospitalCostPatientList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_YRQD</p>
			</properties>
		</module>
		<module id="HOS24" name="病房床位周转情况统计" script="phis.prints.script.HospitalBedTurnoverPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
			<action id="help" name="帮助说明"/>
		</module>
		<module id="HOS25" name="住院病人疾病分类报表" script="phis.prints.script.HospiatlPatientJbflPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
		</module>
		<module id="HOS12" name="医保费用上载" 
			script="phis.application.hos.script.YbFeeUploadModule">
			<properties>
				<p name="refLList">phis.application.hos.HOS/HOS/HOS1201</p>
				<p name="refRList">phis.application.hos.HOS/HOS/HOS1202</p>
				<p name="serviceId">phis.yBService</p>
				<p name="serviceActionSave">saveZyfymx</p>
				<p name="serviceQuery">queryZyBrry</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="confirm" name="医保费用上传" iconCls="money" />
			<action id="close" name="关闭" iconCls="common_cancel" hide="true"/>
		</module>
		<module id="HOS31" name="南京金保费用上传" script="phis.application.njjb.script.NJJBMedicareFeeUpList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_MEDIC_UP</p>
			</properties>
			<action id="upload" name="上传" iconCls="page_white_get"/>
			<action id="removeScbz" name="删除上传" iconCls="page_white_delete"/>
		</module>
		<module id="HOS1201" name="医保费用上载左边病人list" type="1"
			script="phis.application.hos.script.YbFeeUploadLeftList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_YB</p>
				<p name="serviceId">phis.yBService</p>
				<p name="serviceAction">queryZyBrry</p>
			</properties>
		</module>
		<module id="HOS1202" name="医保费用上载右边收费项目list" type="1"
			script="phis.application.hos.script.YbFeeUploadRightList">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_YB</p>
				<p name="serviceId">phis.yBService</p>
				<p name="serviceAction">getZyfymxQuery</p>
			</properties>
		</module>
		<module id="HOS27" name="农合费用上传" script="phis.application.hos.script.HospitalPatientNhFyxxSendModel">
			<properties>
				<p name="zybrTab">phis.application.hos.HOS/HOS/HOS2701</p>
				<p name="dscxxTab">phis.application.hos.HOS/HOS/HOS2702</p>
			</properties>
			<action id="send" name="上传费用" iconCls="arrow-up"/>
		</module>
		<module id="HOS2701" name="病人列表" type="1"  script="phis.application.hos.script.HospitalPatientNhFyxxSendBrlist">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_BRRY_NH</p>
				<p name="initCnd">['and',['in',['$','a.CYPB'],[0,1]],['eq',['$','a.JGID'],['$','%user.manageUnit.Ref']],['eq',['$','a.BRXZ'],['s','6000']]]</p>
				<p name="mutiSelect">true</p>
				<p name="height">300</p>
				<p name="width">613</p>
				<p name="modal">true</p>
			</properties>
		</module>
		<module id="HOS2702" name="费用列表" type="1"  script="phis.application.hos.script.HospitalPatientNhFyxxSendFylist">
			<properties>
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_NHSC</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
	</catagory>
</application>