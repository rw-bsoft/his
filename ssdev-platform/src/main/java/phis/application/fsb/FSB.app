<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.fsb.FSB" name="家庭病床">
	<catagory id="FSB" name="家庭病床">
		<module id="FSB03" name="家庭病床申请单" type="1"
			script="phis.prints.script.FsbApplicationFormPrintView" />
		<module id="FSB01" name="家床申请"
			script="phis.application.fsb.script.FsbApplicationList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRSQ_LIST</p>
				<p name="refPrint">phis.application.fsb.FSB/FSB/FSB03</p>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB02</p>
			</properties>
			<action id="add" name="新增"/>
			<action id="upd" name="修改" iconCls="update"/>
			<action id="tjsq" name="提交" iconCls="commit" />
			<action id="thsq" name="退回" iconCls="common_cancel" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="FSB02" name="家床申请" type="1"
			script="phis.application.fsb.script.FsbApplicationForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRSQ_FORM</p>
			</properties>
			<action id="create" name="新建" />
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
			<action id="createInfo" name="新建个人基本信息"  iconCls="create"/>
			<!--
				<action id="blyy" name="病历引用" />
				-->
		</module>
		<module id="FSB0201" name="家床申请" type="1"
			script="phis.application.fsb.script.FsbApplicationForEmrForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRSQ_FORM</p>
			</properties>
			<action id="save" name="确定" />
			<!--
				<action id="blyy" name="病历引用" />
				-->
		</module>
		<module id="FSB04" name="家床登记"
			script="phis.application.fsb.script.FsbRegistrationForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_RYDJ</p>
				<p name="refImportList">phis.application.fsb.FSB/FSB/FSB0401</p>
				<p name="refFsbMediRecordPrint">phis.application.fsb.FSB/FSB/FSB0402</p>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB0403</p>
				<p name="refPayment">phis.application.fsb.FSB/FSB/FSB0603</p>
			</properties>
			<action id="add" name="新建" />
			<action id="import" name="调入" iconCls="ransferred_all" />
			<action id="reset" name="重置" iconCls="page_refresh" />
			<action id="save" name="保存" />
		</module>
		<!--家床登记 [调入]功能 -->
		<module id="FSB0401" type="1" name="家床申请调入列表"
			script="phis.application.fsb.script.FsbRegistrationImportList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRSQ_LIST</p>
				<p name="initCnd">['and',['eq',['$','a.JGID'],["$",'%user.manageUnit.id']],['eq',['$','SQZT'],['i','2']]]
				</p>
			</properties>
			<action id="import" name="确认" iconCls="ransferred_all" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB0402" name="家庭登记打印" type="1"
			script="phis.prints.script.FsbMediRecordPrintView" />
		<module id="FSB0403" name="家床申请" type="1"
			script="phis.application.fsb.script.FsbRegistrationCreateForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRSQ_FORM</p>
			</properties>
			<action id="create" name="新建" />
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB05" name="病人管理(家床中心)"
			script="phis.application.fsb.script.FamilySickBedPatientManagementList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_BRGL_LIST</p>
				<p name="updateCls">phis.application.fsb.script.FamilySickBedPatientManagementForm
				</p>
				<p name="canceledCls">phis.application.fsb.script.FamilySickBedPatientManagementForm
				</p>
				<p name="cards">phis.application.fsb.FSB/FSB/FSB0502</p>
				<p name="hosReferrals">phis.application.fsb.FSB/FSB/FSB0503</p>
			</properties>
			<action id="print" name="协议查看" iconCls="print" />
			<action id="transform" name="转换" iconCls="transfer" />
			<action id="update" name="修改" />
			<action id="canceled" name="注销" iconCls="writeoff" />
			<!-- <action id="home" name="首页" iconCls="home"/> -->
			<action id="cards" name="帐卡" iconCls="coins" />
			<!-- <action id="hosReferrals" name="住院转诊申请" iconCls="transfer"/> -->
			<!-- add by wuGang.Lo start -->
			<!-- -->
			<!-- <action id="hzyb" name="市医保" iconCls="money"/> <action id="syb" name="省医保" 
				iconCls="money_yen"/> -->
			<!-- add by wuGang.Lo end -->
			<action id="refresh" name="刷新" />
		</module>
		<module id="FSB0501" name="病人管理form" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientManagementForm">
			<properties>
				<p name="refHospMediRecordPrint">phis.application.fsb.FSB/FSB/FSB050101</p>
			</properties>
			<action id="transform" name="转换" iconCls="transfer" />
			<action id="canceled" name="注销" iconCls="writeoff" />
			<action id="save" name="保存" />
			<action id="ybdk" name="医保读卡" iconCls="money" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB0502" name="费用帐卡" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB080201</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB050203</p>
				<p name="refcontributions">phis.application.fsb.FSB/FSB/FSB050201</p>
				<p name="refinventory">phis.application.fsb.FSB/FSB/FSB050202</p>
			</properties>
			<action id="contributions" name="缴款记录" iconCls="money_add" />
			<action id="inventory" name="费用清单" iconCls="inventory" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB050201" name="缴款记录" type="1"
			script="phis.application.fsb.script.FamilySickBedPaymentRecordList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_JSCX</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!--该处在住院管理-》病人管理模块下 -->
		<module id="FSB050202" name="费用清单" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB08020501</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB08020502</p>
				<p name="refDayList">phis.application.fsb.FSB/FSB/FSB08020503</p>
			</properties>
			<action id="whole" name="全部" value="1" />
			<action id="medical" name="医疗" value="2" />
			<action id="drugs" name="药品" value="3" />
			<action id="refresh" name="刷新" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB050203" name="结算管理list" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JSGL_LIST</p>
				<p name="reftabModule">phis.application.fsb.FSB/FSB/FSB05020301</p>
				<p name="openBy">hospitalPatient</p>
			</properties>
		</module>
		<module id="FSB05020301" name="明细项目" type="1"
			script="phis.application.fsb.script.FamilySickBedDetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期"
				ref="phis.application.fsb.FSB/FSB/FSB05020302" />
			<action id="mxxm" viewType="list" name="按明细"
				ref="phis.application.fsb.FSB/FSB/FSB05020303" />
		</module>
		<module id="FSB05020302" name="收费项目" type="1"
			script="phis.application.fsb.script.FamilySickBedRQDetalisModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB0502030201</p>
				<p name="refDetailsList">phis.application.fsb.FSB/FSB/FSB0502030202</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB05020303" name="明细项目" type="1"
			script="phis.application.fsb.script.FamilySickBedDetalisModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB0502030301</p>
				<p name="refDetailsList">phis.application.fsb.FSB/FSB/FSB0502030302</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB0502030201" name="收费项目" type="1"
			script="phis.application.fsb.script.FamilySickBedFeesDetalisList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_SFXM</p>
				<p name="openBy">sfxm</p>
			</properties>
		</module>
		<module id="FSB0502030301" name="明细项目" type="1"
			script="phis.application.fsb.script.FamilySickBedFeesDetalisList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_MXXM</p>
				<p name="openBy">mxxm</p>
			</properties>
		</module>
		<module id="FSB0502030202" name="收费项目" type="1"
			script="phis.application.fsb.script.FamilySickBedDetalisList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_RQMX</p>
			</properties>
		</module>
		<module id="FSB0502030302" name="收费项目" type="1"
			script="phis.application.fsb.script.FamilySickBedDetalisList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_MX</p>
			</properties>
		</module>
		<module id="FSB06" name="缴款管理"
			script="phis.application.fsb.script.FsbPaymentProcessingTabModule">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK</p>
			</properties>
			<action id="jkcl" viewType="list" name="缴款处理"
				ref="phis.application.fsb.FSB/FSB/FSB0601" />
			<action id="jkcx" viewType="list" name="缴款查询"
				ref="phis.application.fsb.FSB/FSB/FSB0602" />
		</module>
		<!--缴款处理 tab1 -->
		<module id="FSB0601" name="缴款处理" type="1"
			script="phis.application.fsb.script.FsbPaymentProcessingModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB060101</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB060102</p>
			</properties>
			<action id="canceled" name="注销" iconCls="writeoff" />
		</module>
		<module id="FSB060101" name="缴款处理form" type="1"
			script="phis.application.fsb.script.FsbPaymentProcessingForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_JKFORM</p>
				<p name="colCount">1</p>
				<p name="refPayment">phis.application.fsb.FSB/FSB/FSB0603</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new" />
			<action id="beforeSave" name="保存" iconCls="save" />
		</module>
		<module id="FSB060102" name="缴款处理list" type="1"
			script="phis.application.fsb.script.FsbPaymentProcessingList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_JK</p>
				<p name="refPaymentList">phis.application.fsb.FSB/FSB/FSB0603</p>
			</properties>
			<action id="canceled" name="注销" iconCls="writeoff" />
			<action id="print" name="重打收据" />
		</module>
		<!--缴款查询 tab2 -->
		<module id="FSB0602" name="缴款查询" type="1"
			script="phis.application.fsb.script.FsbPaymentQueriesModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB060201</p>
			</properties>
			<action id="new" name="重置" iconCls="new" />
		</module>
		<module id="FSB060201" name="家床病人缴款记录" type="1"
			script="phis.application.fsb.script.FsbPaymentQueriesList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_CX</p>
			</properties>
		</module>
		<module id="FSB0603" name="缴款收据" type="1" script="phis.prints.script.FsbPaymentPrintView">
		</module>
		
		<module id="FSB07" name="费用记账" script="phis.script.TabModule">
			<action id="HospitalCostProcessingModuleTab" viewType="list" name="明细记账" ref="phis.application.fsb.FSB/FSB/FSB0701" />
			<action id="HosPaymentTypeTab" viewType="list" name="记账查询" ref="phis.application.fsb.FSB/FSB/FSB0702" />
		</module>
		<module id="FSB0701" name="明细记账" type="1" script="phis.application.fsb.script.FsbCostProcessingModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB070101</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB070102</p>
				<p name="refRefundList">phis.application.fsb.FSB/FSB/FSB070103</p>
			</properties>
		</module>
		<module id="FSB0702" name="记账查询" type="1" script="phis.application.fsb.script.FsbAccountingQueryModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB070201</p>
			</properties>
			<action id="reSet" name="重置" iconCls="new"/>
		</module>
		<module id="FSB070101" name="费用记账联" type="1" script="phis.application.fsb.script.FsbCostAccountingForm">
			<properties> 
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYCF</p>  
			</properties>
			<action id="reSet" name="重置" iconCls="new"/>
			<action id="save" name="保存"/>
		</module>
		<module id="FSB070102" name="明细记账" type="1" script="phis.application.fsb.script.FsbDetailsAccountingList">
			<properties> 
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYCL</p>  
			</properties>
		</module>
		<module id="FSB070103" name="退费明细选择" type="1" script="phis.application.fsb.script.FsbRefundsDetailsList">
			<properties> 
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_TF</p>  
				<p name="listServiceId">fsbCostProcessingService</p>
				<p name="serviceAction">queryRefundInfo1</p> 
			</properties>
			<action id="commit" name="确定" iconCls="commit"/>
			<action id="cancel" name="返回" iconCls="common_cancel"/>
		</module>
		<module id="FSB070201" name="记账查询list" type="1" script="phis.application.fsb.script.FsbCostList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYCX</p>  
				<p name="listServiceId">fsbCostProcessingService</p>
				<p name="serviceAction">queryCostList</p> 
			</properties>
		</module>

		<module id="FSB08" name="结算管理"
			script="phis.application.fsb.script.FamilySickBedSettlementModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB0801</p>
				<p name="refModule">phis.application.fsb.FSB/FSB/FSB0802</p>
			</properties>
		</module>
		<module id="FSB0801" name="在院病人list" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientSelectionLeftList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_CYLB</p>
			</properties>
		</module>
		<module id="FSB0802" name="结算管理" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB080201</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB080202</p>
				<p name="zyjsModule">phis.application.fsb.FSB/FSB/FSB080206</p>
				<p name="fyqdModule">phis.application.fsb.FSB/FSB/FSB080205</p>
				<p name="jkjlList">phis.application.fsb.FSB/FSB/FSB080204</p>
				<p name="ssybjsModule">phis.application.fsb.FSB/FSB/FSB080203</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add" />
			<action id="inventory" name="清单" iconCls="inventory" />
			<action id="settle" name="结算" iconCls="money_yen" />
			<action id="invalid" name="作废" iconCls="writeoff" />
			<action id="ybdk" name="医保读卡" iconCls="money" />
			<!--<action id="choose" name="选择" iconCls="cart"/> -->
			<action id="new" name="清空" />
			<action id="printSet" name="打印设置" iconCls="print" />

			<!-- <action id="syb" name="省医保" iconCls="money_yen"/> -->
		</module>
		<module id="FSB080201" name="结算管理form" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JSGL_FORM</p>
				<p name="colCount">3</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="FSB080202" name="结算管理list" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JSGL_LIST</p>
				<p name="reftabModule">phis.application.fsb.FSB/FSB/FSB08020201</p>
				<p name="openBy">hospital</p>
			</properties>
		</module>
		<module id="FSB08020201" name="明细项目" type="1"
			script="phis.application.fsb.script.FsbDetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期"
				ref="phis.application.fsb.FSB/FSB/FSB05020302" />
			<action id="mxxm" viewType="list" name="按明细"
				ref="phis.application.fsb.FSB/FSB/FSB05020303" />
		</module>
		<module id="FSB080203" name="病人选择" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientSelectionModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB08020301</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB08020302</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB08020301" name="病人选择form" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientSelectionForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRXZ</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="FSB08020302" name="出院病人list" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientSelectionList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_CYLB</p>
			</properties>
		</module>
		<module id="FSB080204" name="缴款记录" type="1"
			script="phis.application.fsb.script.FamilySickBedPaymentRecordList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_JSCX</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!--该处在结算管理模块下 -->
		<module id="FSB080205" name="费用清单" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB08020501</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB08020502</p>
				<p name="refDayList">phis.application.fsb.FSB/FSB/FSB08020503</p>
			</properties>
			<action id="whole" name="全部" value="1" />
			<action id="medical" name="医疗" value="2" />
			<action id="drugs" name="药品" value="3" />
			<action id="refresh" name="刷新" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB08020501" name="费用清单form" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYQD_FORM</p>
				<p name="labelWidth">55</p>
				<p name="colCount">5</p>
			</properties>
		</module>
		<module id="FSB08020502" name="费用清单module" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListTabModule">
			<action id="HospitalCostsListDetailedFormatTab" viewType="list"
				name="明细格式" ref="phis.application.fsb.FSB/FSB/FSB0802050201" />
			<action id="HospitalCostsListSumFormatTab" viewType="list"
				name="汇总格式" ref="phis.application.fsb.FSB/FSB/FSB0802050203" />
			<action id="HospitalCostsListDoctorFormatTab" viewType="list"
				name="医嘱格式" ref="phis.application.fsb.FSB/FSB/FSB0802050202" />
		</module>
		<module id="FSB08020503" name="日清单" type="1"
			script="phis.prints.script.FamilySickBedDayListPrintView" />
		<module id="FSB0802050201" name="明细格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD</p>
			</properties>
		</module>
		<module id="FSB0802050203" name="汇总格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsSumListTab">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD_HZGS</p>
			</properties>
		</module>
		<module id="FSB0802050202" name="医嘱格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD_YZGS</p>
			</properties>
		</module>
		<module id="FSB080206" name="结算处理" type="1"
			script="phis.application.fsb.script.FamilySickBedSettleAccountsModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB08020601</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB08020602</p>
			</properties>
		</module>
		<module id="FSB08020601" name="结算form" type="1"
			script="phis.application.fsb.script.FamilySickBedSettleAccountsForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JCJS_FORM</p>
				<p name="colCount">2</p>
				<p name="labelWidth">40</p>
				<p name="refHospitalSettlementPrint">phis.application.fsb.FSB/FSB/FSB0802060101</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB08020602" name="结算缴款记录" type="1"
			script="phis.application.fsb.script.FamilySickBedSettleAccountsList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TBKK_ZYJS</p>
			</properties>
		</module>
		<module id="FSB0802060101" name="住院发票打印" type="1"
			script="phis.prints.script.FamilySickBedInvoiceByxPrintView" />
		<module id="FSB10" name="病人管理"
			script="phis.application.fsb.script.FamilySickBedPatientList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY</p>
				<p name="refBrxxModule">phis.application.fsb.FSB/FSB/FSB1001</p>
				<p name="refZljhModule">phis.application.fsb.FSB/FSB/FSB12</p>
				<p name="refCcglModule">phis.application.fsb.FSB/FSB/FSB13</p>
				<p name="refCczglForm">phis.application.fsb.FSB/FSB/FSB19</p>
				<p name="refZkglModule">phis.application.fsb.FSB/FSB/FSB0502</p>
				<p name="retTysqModule">phis.application.fsb.FSB/FSB/FSB24</p>
				<p name="refHljh">phis.application.fsb.FSB/FSB/FSB1306</p>
				<p name="refHljl">phis.application.fsb.FSB/FSB/FSB1307</p>
				<p name="refFsbAdviceQueryModule">phis.application.fsb.FSB/FSB/FSB1305</p>
				<p name="refAmqcApplyList">phis.application.war.WAR/WAR/WAR39</p>
			</properties>
			<action id="brxx" name="病人信息" iconCls="user" />
			<action id="zljh" name="诊疗计划" iconCls="page_white_edit" />
			<action id="ccgl" name="查床管理" iconCls="page_white_edit" />
			<action id="hljh" name="护理计划" iconCls="page_white_edit" />
			<action id="hljl" name="护理记录" iconCls="page_white_edit" />
			<action id="yzcl" name="医嘱处理" iconCls="advice" />
			<action id="zkgl" name="帐卡" iconCls="coins" />
			<action id="cczgl" name="撤床证" iconCls="page_edit" />
			<action id="tysq" name="退药申请" iconCls="page_edit" />
			<action id="amqcApply" name="抗菌药申请" iconCls="page_paintbrush"/>
		</module>
		<module id="FSB1001" name="病人信息" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientTab">
			<action id="patientBaseTab" name="基本信息"
				ref="phis.application.fsb.FSB/FSB/FSB100101" type="tab" />
			<action id="patientAllergyMedTab" name="过敏药物"
				ref="phis.application.fsb.FSB/FSB/FSB100102" type="tab" />
			<action id="save" name="确认" type="button" />
			<action id="close" name="关闭" type="button" iconCls="common_cancel" />
		</module>
		<module id="FSB100101" name="病人信息Form" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRXX</p>
			</properties>
		</module>
		<module id="FSB100102" name="病人过敏药物list" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientAllergyMedList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.GY_PSJL_JC</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="FSB11" name="诊疗计划模版维护"
			script="phis.application.fsb.script.FamilySickBedClinicPlanModule">
			<properties>
				<p name="refPlanNameList">phis.application.fsb.FSB/FSB/FSB1101</p>
				<p name="refPlanDetailsList">phis.application.fsb.FSB/FSB/FSB1102</p>
			</properties>
		</module>
		<module id="FSB1101" name="诊疗计划模版名称" type="1"
			script="phis.application.fsb.script.FamilySickBedClinicPlanNameList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZL_ZT01</p>
				<p name="removeByFiled">ZTMC</p>
				<p name="queryWidth">80</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="FSB1102" name="诊疗计划模版明细" type="1"
			script="phis.application.fsb.script.FamilySickBedClinicPlanDetailList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZL_ZT02</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>
		<module id="FSB12" name="诊疗计划" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientPlanModule">
			<properties>
				<p name="refPatientPlanForm">phis.application.fsb.FSB/FSB/FSB1201</p>
				<p name="refPatientPlanList">phis.application.fsb.FSB/FSB/FSB1202</p>
				<p name="refPatientPlanAssistant">phis.application.fsb.FSB/FSB/FSB1203</p>
			</properties>
		</module>
		<module id="FSB1201" name="病人信息Form" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientPlanForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_Form</p>
			</properties>
		</module>
		<module id="FSB1202" name="诊疗计划List" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientPlanList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZLJH</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="print" name="打印" />
			<action id="close" name="关闭" />
		</module>
		<module id="FSB1203" name="计划模版名称" type="1"
			script="phis.application.fsb.script.FamilySickBedPlanNameList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZL_ZT01</p>
			</properties>
		</module>
		<module id="FSB13" name="医嘱处理" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceModule">
			<properties>
				<p name="refWardDoctorAdviceForm">phis.application.fsb.FSB/FSB/FSB1301</p>
				<p name="refWardDoctorAdviceTab">phis.application.fsb.FSB/FSB/FSB1302</p>
				<p name="refFamilySickBedCheckForm">phis.application.fsb.FSB/FSB/FSB1303</p>
				<p name="refFamilyPlanChooseList">phis.application.fsb.FSB/FSB/FSB1304</p>
				<p name="refWardQuickInputTab">phis.application.war.WAR/WAR/WAR030406</p>
				<p name="refFsbAdviceExecuteModule">phis.application.fsb.FSB/FSB/FSB22</p>
				<p name="refFsbAdviceQueryModule">phis.application.fsb.FSB/FSB/FSB1305</p>
				<p name="refWardHerbEnrty">phis.application.war.WAR/WAR/WAR030408</p>
				<p name="refFsbAdviceJFModule">phis.application.fsb.FSB/FSB/FSB13020101</p>
			</properties>
			<action id="import" name="调入" iconCls="commit" />
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="remove" name="删除" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="singleStop" name="单停" iconCls="pill" />
			<action id="allStop" name="全停" iconCls="pill_delete" />
			<action id="assignedEmpty" name="赋空" iconCls="page_white_delete" />
			<action id="confirm" name="执行" iconCls="images_send" />
			<action id="submit" name="提交" iconCls="pill_go" />
			<action id="goback" name="退回" iconCls="arrow_undo" />
			<action id="save" name="保存" />
			<action id="query" name="查询" />
			<action id="refresh" name="刷新" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB1301" name="医嘱处理form" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_Form</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="FSB1302" name="医嘱处理tab" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceTab">
			<action id="longAdviceTab" name="长期医嘱"
				ref="phis.application.fsb.FSB/FSB/FSB130201" type="tab" />
			<action id="tempAdviceTab" name="临时医嘱"
				ref="phis.application.fsb.FSB/FSB/FSB130202" type="tab" />
		</module>
		<module id="FSB130201" name="医嘱处理list(长期)" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_CQ</p>
				<p name="adviceType">longtime</p>
			</properties>
		</module>
		<module id="FSB130202" name="医嘱处理list(临时)" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_LS</p>
				<p name="adviceType">temporary</p>
				<p name="refAntibacterialApplyForm">phis.application.war.WAR/WAR/WAR3901</p>
			</properties>
		</module>
		<module id="FSB13020101" name="计费明细查询" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceJFList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_YZCX</p>
			</properties>
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB1303" name="查床记录" type="1"
			script="phis.application.fsb.script.FamilySickBedCheckForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_CCJL</p>
				<p name="refDiagnoseList">phis.application.fsb.FSB/FSB/FSB130301</p>
				<p name="refDiagnoseEditorList">phis.application.fsb.FSB/FSB/FSB130302</p>
			</properties>
			<action id="new" name="新增" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>
		<module id="FSB130301" name="疾病诊断(显示)" type="1"
			script="phis.script.EditorList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRZD</p>
				<p name="disablePagingTbr">true</p>
				<p name="showButtonOnPT">true</p>
			</properties>
		</module>
		<module id="FSB130302" name="疾病诊断" type="1"
			script="phis.application.fsb.script.FamilySickBedDiagnoseList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRZD_EDIT</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="close" name="关闭" />
		</module>
		<module id="FSB1304" name="诊疗计划选择" type="1"
			script="phis.application.fsb.script.FamilySickBedPlanChooseList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZLJH</p>
			</properties>
			<action id="query" name="查询" />
			<action id="import" name="调入" iconCls="commit"/>
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB1305" name="医嘱查询" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceQueryModule">
			<properties>
				<p name="refFamilySickBedAdviceForm">phis.application.fsb.FSB/FSB/FSB130501</p>
				<p name="refFamilySickBedAdviceList">phis.application.fsb.FSB/FSB/FSB130502</p>
			</properties>
			<action id="assignedEmpty" name="取消医嘱停嘱" iconCls="pill_go" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB130501" name="医嘱处理查询form" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_Form</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="FSB130502" name="医嘱处理查询list" type="1"
			script="phis.application.fsb.script.FamilySickBedAdviceQueryList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_CQ</p>
				<p name="disableContextMenu">true</p>
			</properties>
		</module>
		<module id="FSB1306" name="护理计划" type="1"
			script="phis.application.fsb.script.FsbNursePlanModule">
			<properties>
				<p name="NPTree">phis.application.fsb.FSB/FSB/FSB130601</p>
				<p name="NPDataView">phis.application.fsb.FSB/FSB/FSB130602</p>
				<p name="NPDataShow">phis.application.fsb.FSB/FSB/FSB130603</p>
			</properties>
			<action id="createPlan" name="新增" iconCls="add"/>
			<action id="removePlan" name="删除" iconCls="remove"/>
			<action id="printPlan" name="打印" iconCls="print"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="FSB130601" name="左边树列表" type="1"
			script="phis.application.fsb.script.FsbNursePlanTree">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_HLJH</p>
			</properties>
		</module>
		<module id="FSB130602" name="护理计划修改及新增" type="1"
			script="phis.application.fsb.script.FsbNursePlanDataView">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_HLJH</p>
				<p name="NPList">phis.application.fsb.FSB/FSB/FSB130605</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="inportHLZD" name="引入护理诊断" iconCls="add" />
		</module>
		<module id="FSB130603" name="护理计划展示" type="1"
			script="phis.application.fsb.script.FsbNursePlanDataShowModule">
		</module>
		<module id="FSB130604" name="护理计划打印" type="1"
			script="phis.prints.script.FsbNursePlanDataShowPrintView">
		</module>
		<module id="FSB130605" name="护理诊断引入" type="1"
			script="phis.application.war.script.NursePlanDataInportList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.EMR_HLZD</p>
			</properties>
			<action id="inport" name="引入" iconCls="add" />
		</module>
		<module id="FSB1307" name="护理记录" type="1"
			script="phis.application.fsb.script.FsbNurseRecordModule">
			<properties>
				<p name="NPTree">phis.application.fsb.FSB/FSB/FSB130701</p>
				<p name="NPDataView">phis.application.fsb.FSB/FSB/FSB130702</p>
				<p name="NPDataShow">phis.application.fsb.FSB/FSB/FSB130703</p>
			</properties>
			<action id="createPlan" name="新增" iconCls="add"/>
			<action id="removePlan" name="删除" iconCls="remove"/>
			<action id="printPlan" name="打印" iconCls="print"/>
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="FSB130701" name="左边树列表" type="1"
			script="phis.application.fsb.script.FsbNurseRecordTree">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_HLJH</p>
			</properties>
		</module>
		<module id="FSB130702" name="护理记录修改及新增" type="1"
			script="phis.application.fsb.script.FsbNurseRecordDataView">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_HLJL</p>
				<p name="NPList">phis.application.fsb.FSB/FSB/FSB130705</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="inportHLJH" name="引入护理计划" iconCls="add" />
		</module>
		<module id="FSB130703" name="护理记录展示" type="1"
			script="phis.application.fsb.script.FsbNurseRecordDataShowModule">
		</module>
		<module id="FSB130704" name="护理记录打印" type="1"
			script="phis.prints.script.FsbNurseRecordDataShowPrintView">
		</module>
		<module id="FSB130705" name="护理计划引入" type="1"
			script="phis.application.fsb.script.FsbNursePlanImportList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_HLJH</p>
			</properties>
			<action id="query" name="查询" />
			<action id="import" name="引入" iconCls="add" />
			<action id="close" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="FSB19" name="撤床证" type="1"
			script="phis.application.fsb.script.FamilySickBedCancelBedForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRRY_CC</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="取消" iconCls="update" />
			<action id="print" name="打印" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB20" name="票据号码设置" script="phis.script.TabModule">
			<action id="jcInvoiceNumberTab" viewType="list" name="发票号码维护"
				ref="phis.application.fsb.FSB/FSB/FSB2001" />
			<action id="jcpaymentReceiptTab" viewType="list" name="缴款收据维护"
				ref="phis.application.fsb.FSB/FSB/FSB2002" />
		</module>
		<module id="FSB2001" name="发票号码维护" type="1"
			script="phis.application.fsb.script.JcInvoiceNumberConfigList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YGPJ_FP</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">1</p>
				<p name="createCls">phis.application.fsb.script.JcInvoiceNumberConfigForm</p>
				<p name="updateCls">phis.application.fsb.script.JcInvoiceNumberConfigForm</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="FSB2002" name="缴款收据维护" type="1"
			script="phis.application.fsb.script.JcPaymentReceiptNumberConfigList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YGPJ_JK</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">2</p>
				<p name="createCls">phis.application.fsb.script.JcPaymentReceiptNumberConfigForm
				</p>
				<p name="updateCls">phis.application.fsb.script.JcPaymentReceiptNumberConfigForm
				</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="FSB21" name="发药药房设置"
			script="phis.application.fsb.script.JcWardOutPharmacySet">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYYF</p>
				<p name="initCnd">['and',['eq',['$','a.JGID'],['$','%user.manageUnit.id']],['eq',['$','a.GNFL'],['i',4]]]
				</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="updateStage" name="注销" iconCls="writeoff" />
			<action id="commit" name="保存" iconCls="save" />
			<!-- <action id="RemoveCell" name="删除行" iconCls="remove" /> -->
		</module>
		<module id="FSB31" name="结算查询"
			script="phis.application.fsb.script.FamilySickBedHistorySettleQueriesModule">
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB3101</p>
				<!--<p name="refHospitalSettlementPrint">FSB3102</p> -->
				<p name="refHospitalSettlementPrint">phis.application.fsb.FSB/FSB/FSB3105</p>
				<p name="cards">phis.application.fsb.FSB/FSB/FSB3103</p>
			</properties>
			<action id="newQuery" name="重置" iconCls="new" />
			<action id="printFp" name="打印" iconCls="print" />
			<action id="cards" name="帐卡" iconCls="coins" />
		</module>
		<module id="FSB3101" name="历史结算查询list" type="1"
			script="phis.application.fsb.script.FamilySickBedHistorySettleQueriesList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JCJS_LSJS</p>
			</properties>
		</module>
		<module id="FSB3103" name="费用帐卡" type="1"
			script="phis.application.fsb.script.FamilySickBedPatientCardsViewModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB310301</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB310302</p>
				<p name="refcontributions">phis.application.hos.HOS/HOS/HOS040201</p>
				<p name="refinventory">phis.application.fsb.FSB/FSB/FSB3104</p>
			</properties>
			<action id="contributions" name="缴款" iconCls="money_add" />
			<action id="inventory" name="清单" iconCls="inventory" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB310301" name="结算管理form" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementQueryForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JSGL_FORM</p>
				<p name="colCount">3</p>
				<p name="labelWidth">55</p>
			</properties>
		</module>
		<module id="FSB310302" name="结算管理list" type="1"
			script="phis.application.fsb.script.FamilySickBedSettlementManagementQueryList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JSGL_LIST</p>
				<p name="reftabModule">phis.application.fsb.FSB/FSB/FSB31030201</p>
				<p name="openBy">hospitalQuery</p>
			</properties>
		</module>
		<module id="FSB31030201" name="明细项目" type="1"
			script="phis.application.fsb.script.FamilySickBedDetailsTabModule">
			<action id="sfxm" viewType="list" name="按日期"
				ref="phis.application.fsb.FSB/FSB/FSB05020302" />
			<action id="mxxm" viewType="list" name="按明细"
				ref="phis.application.fsb.FSB/FSB/FSB05020303" />
		</module>
		<!--该处在结算查询模块下 -->
		<module id="FSB3104" name="费用清单" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListQueryModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB08020501</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB310402</p>
				<p name="refDayList">phis.application.fsb.FSB/FSB/FSB08020503</p>
			</properties>
			<action id="whole" name="全部" value="1" />
			<action id="medical" name="医疗" value="2" />
			<action id="drugs" name="药品" value="3" />
			<action id="refresh" name="刷新" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB310402" name="费用清单module" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListTabModule">
			<properties>
				<p name="openBy">Query</p>
			</properties>
			<action id="HospitalCostsListDetailedFormatTabQuery" viewType="list"
				name="明细格式" ref="phis.application.fsb.FSB/FSB/FSB31040201" />
			<action id="HospitalCostsListSumFormatTabQuery" viewType="list"
				name="汇总格式" ref="phis.application.fsb.FSB/FSB/FSB31040203" />
			<action id="HospitalCostsListDoctorFormatTabQuery" viewType="list"
				name="医嘱格式" ref="phis.application.fsb.FSB/FSB/FSB31040202" />
		</module>
		<module id="FSB31040201" name="明细格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD</p>
			</properties>
		</module>
		<module id="FSB31040203" name="汇总格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsSumListTab">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD_HZGS</p>
			</properties>
		</module>
		<module id="FSB31040202" name="医嘱格式" type="1"
			script="phis.application.fsb.script.FamilySickBedCostsListDoctorFormatTab">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYMX_FYQD_YZGS</p>
			</properties>
		</module>
		<module id="FSB32" name="日终结账"
			script="phis.prints.script.FamilySickBedChargesDailyPrintView">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_JZXX</p>
				<p name="serviceid">familySickBedizationCheckoutService</p>
				<p name="queryDateActionid">queryDate</p>
				<p name="isreckonActionid">isreckon</p>
				<p name="create_jzrb">create_jzrb</p>
				<p name="check">Wf_Check</p>
				<p name="save_jzrb">save_jzrb</p>
				<p name="query_ZY_JZXX">query_ZY_JZXX</p>
				<p name="detail">phis.application.fsb.FSB/FSB/FSB3201</p>
				<p name="refCancelCommit">phis.application.fsb.FSB/FSB/FSB3202</p>
				<p name="refQueryList">phis.application.fsb.FSB/FSB/FSB3203</p>
			</properties>
		</module>
		<module id="FSB3203" name="日报选择" type="1" script="phis.application.fsb.script.FsbDateChoseList">
			<properties>
				<p name="listServiceId">familySickBedizationCheckoutService</p>
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
		<module id="FSB3202" name="取消日报" type="1" script="phis.application.fsb.script.FamilySickBedCancelCommitModule">
			<properties>
				<p name="serviceId">familySickBedizationCheckoutService</p>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB320201</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB320202</p>
			</properties>
		</module>
		<module id="FSB320201" name="取消日报form" type="1"
			script="phis.application.fsb.script.FamilySickBedCancelCommitForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_JZRQ</p>
			</properties>
		</module>
		<module id="FSB320202" name="取消日报list" type="1"
			script="phis.application.fsb.script.FamilySickBedCancelCommitList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_QXJZ</p>
				<p name="listServiceId">familySickBedizationCheckoutService</p>
			</properties>
		</module>
		<module id="FSB3201" name="明细记账" type="1"
			script="phis.application.fsb.script.FsbPatientDepartmentTabModule">
			<action id="accountsDetail" name="结算明细"
				ref="phis.application.fsb.FSB/FSB/FSB320101" />
			<action id="deliveryDetail" name="缴款明细"
				ref="phis.application.fsb.FSB/FSB/FSB320102" />
			<action id="refundDetail" name="退款明细"
				ref="phis.application.fsb.FSB/FSB/FSB320103" />
		</module>
		<module id="FSB320101" name="结算明细" type="1"
			script="phis.prints.script.FsbAccountsDetailPrintView">
		</module>
		<module id="FSB320102" name="缴款明细" type="1"
			script="phis.prints.script.FsbDeliveryDetailPrintView">
		</module>
		<module id="FSB320103" name="退款明细" type="1"
			script="phis.prints.script.FsbRefundDetailPrintView">
		</module>
		<module id="FSB33" name="日终汇总"
			script="phis.prints.script.FamilySickBedChargesSummaryPrintView">
			<properties>
				<p name="serviceId">familySickBedGenerateVerificationService</p>
				<p name="serviceAction">generateVerification</p>
				<p name="serviceActionAfter">generateAfterVerification</p>
				<p name="serviceActionTwo">collectVerification</p>
				<p name="serviceActionQuery">queryVerification</p>
				<p name="serviceActionSave">saveCollect</p>
				<p name="refQueryList">phis.application.fsb.FSB/FSB/FSB3301</p>
			</properties>
		</module>
		<module id="FSB3301" name="汇总选择"  type="1" script="phis.script.SimpleList">
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
		<module id="FSB22" name="诊疗医嘱执行" script="phis.application.fsb.script.FamilySickBedSubmissionTabModule">
			<action id="ypyzzxTab" name="药品医嘱执行" ref="phis.application.fsb.FSB/FSB/FSB2201" />
			<action id="xmyzzxTab" name="项目医嘱执行" ref="phis.application.fsb.FSB/FSB/FSB2202" />
			<action id="xmyztjTab" name="项目医嘱提交" ref="phis.application.fsb.FSB/FSB/FSB2203" />
		</module>
		<module id="FSB2201" name="药品医嘱执行" type="1" script="phis.application.fsb.script.FamilySickBedDrugSubmissionModule">
			<properties>
				<p name="leftRef">phis.application.fsb.FSB/FSB/FSB220101</p>
				<p name="rightRef">phis.application.fsb.FSB/FSB/FSB220102</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionId">saveJcDrugSubmission</p>
			</properties>
			<action id="refresh" name="刷新"  />
			<action id="zx" name="执行"  iconCls="archiveMove_commit"/>
			<action id="print" name="打印" />
			<action id="cancel" name="关闭"  iconCls="common_cancel"/>
		</module>
		<module id="FSB220101" name="药品医嘱执行病人列表" type="1" script="phis.application.fsb.script.FamilySickBedDrugSubmissionLeftList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZXBR</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">queryJcDrugSubmissionPatient</p>
			</properties>
		</module>
		<module id="FSB220102" name="药品医嘱执行医嘱明细" type="1" script="phis.application.fsb.script.FamilySickBedDrugSubmissionRightList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_TJ</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
		</module>
		
		<module id="FSB2202" name="项目医嘱执行" type="1" script="phis.application.fsb.script.FamilySickBedProjectExecutionModule">
			<properties>
				<p name="leftRef">phis.application.fsb.FSB/FSB/FSB220201</p>
				<p name="rightRef">phis.application.fsb.FSB/FSB/FSB220202</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionId">saveProjectExecution</p>
			</properties>
			<action id="refresh" name="刷新"  />
			<action id="zx" name="执行"  iconCls="archiveMove_commit"/>
			<action id="print" name="打印" notReadOnly="true"/>
			<action id="cancel" name="关闭"  iconCls="common_cancel"/>
		</module>
		<module id="FSB220201" name="项目医嘱执行病人列表" type="1" script="phis.application.fsb.script.FamilySickBedProjectExecutionLeftList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZXBR</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">queryProjectExecutionPatient</p>
			</properties>
		</module>
		<module id="FSB220202" name="项目医嘱执行医嘱明细" type="1" script="phis.application.fsb.script.FamilySickBedProjectExecutionRightList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_ZX</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">detailChargeQuery</p>
			</properties>
		</module>
		
		<module id="FSB2203" name="项目医嘱提交" type="1" script="phis.application.fsb.script.FamilySickBedProjectSubmissionModule">
			<properties>
				<p name="leftRef">phis.application.fsb.FSB/FSB/FSB220301</p>
				<p name="rightRef">phis.application.fsb.FSB/FSB/FSB220302</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionId">saveProject</p>
				<p name="commitActionId">saveProjectSubmission</p>
			</properties>
			<action id="refresh" name="刷新"  />
			<action id="save" name="保存"  />
			<action id="tj" name="提交"  iconCls="archiveMove_commit"/>
			<action id="cancel" name="关闭"  iconCls="common_cancel"/>
		</module>
		<module id="FSB220301" name="项目医嘱提交病人列表" type="1" script="phis.application.fsb.script.FamilySickBedProjectSubmissionLeftList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_ZXBR</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">queryProjectSubmissionPatient</p>
			</properties>
		</module>
		<module id="FSB220302" name="项目医嘱提交医嘱明细" type="1" script="phis.application.fsb.script.FamilySickBedProjectSubmissionRightList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_XMTJ</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">queryProjectSubmissionDetail</p>
			</properties>
		</module>
		
		<module id="FSB23" name="家床发药" script="phis.application.fsb.script.FamilySickBedDispensing" >
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB2301</p>
				<p name="refModule">phis.application.fsb.FSB/FSB/FSB2302</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionID">savefamilySickBedDispensing</p>
				<p name="saveFullRefundActionID">saveMedicineFullRefund</p>
				<p name="saveRefundActionID">saveMedicineRefund</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="initializationServiceActionID">initialization</p>
			</properties>
			<action id="sx" name="刷新"  iconCls="refresh"/>
			<action id="fy" name="发药" iconCls="antibiotics"/>
			<action id="qt" name="全退" iconCls="pill_add"/>
			<action id="thzx" name="退回中心" iconCls="pill_go"/>
		</module>
		<module id="FSB2301" name="家床提交module" script="phis.application.fsb.script.FamilySickBedDispensingModule" type="1" >
			<properties>
				<p name="refTopList">phis.application.fsb.FSB/FSB/FSB230101</p>
				<p name="refUnderList">phis.application.fsb.FSB/FSB/FSB230102</p>
			</properties>
		</module>
		<module id="FSB230101" name="家床待发药记录方式" script="phis.application.fsb.script.FamilySickBedWaitDispensing"  type="1">
			<properties>
				<p name="fullserviceId">phis.familySickBedManageService</p>
				<p name="queryServiceActionID">queryDispensingFs</p>
				<p name="entryName">phis.application.fsb.schemas.JC_TJ01_FS</p>
			</properties>
		</module>
		<module id="FSB230102" name="家床待发药记录(病人)" script="phis.application.fsb.script.FamilySickBedDispensingLeftList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TJ02_BR</p>
				<p name="fullServiceId">phis.familySickBedManageService</p>
				<p name="serviceAction">queryDispensing_br</p>
			</properties>
		</module>
		<module id="FSB2302" name="家床提交右边tab" script="phis.application.fsb.script.FamilySickBedDispensingRightTabModule"  type="1">
			<properties>
			</properties>
			<action id="yzfy" name="医嘱发药" ref="phis.application.fsb.FSB/FSB/FSB230201" viewType="mx"/>
			<action id="hzfy" name="汇总发药" ref="phis.application.fsb.FSB/FSB/FSB230202" viewType="hz"/>
			<action id="thbq" name="退回中心" ref="phis.application.fsb.FSB/FSB/FSB230203" viewType="th"/>
		</module>
		<module id="FSB230201" name="家床发药" script="phis.application.fsb.script.FamilySickBedDispensingRightList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TJ02_YZFY</p>
				<p name="fullserviceId">phis.familySickBedManageService</p>
				<p name="queryServiceActionID">queryDispensing</p>
			</properties>
		</module>
		<module id="FSB230202" name="汇总发药" script="phis.application.fsb.script.FamilySickBedDispensingRightCollectList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRYZ_FY</p>
			</properties>
		</module>
		<module id="FSB230203" name="退回中心" script="phis.application.fsb.script.FamilySickBedDispensingRightDetailList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TJ02_FY</p>
			</properties>
		</module>
		
		
		<module id="FSB24" name="退药申请" type="1"
			script="phis.application.fsb.script.FamilySickBedMedicalBackApplicationModule">
			<properties>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB1301</p>
				<p name="refLeftList">phis.application.fsb.FSB/FSB/FSB2402</p>
				<p name="refRightModule">phis.application.fsb.FSB/FSB/FSB2403</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionId">saveBackApplication</p>
				<p name="commitActionId">saveCommitBackApplication</p>
			</properties>
			<action id="save" name="保存" />
			<action id="confirm" name="执行" iconCls="commit" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="FSB2402" name="已发药品list" type="1"
			script="phis.application.fsb.script.FamilySickBedMedicalBackApplicationLeftList">
			<properties>
				<p name="serviceId">familySickBedManageService</p>
				<p name="queryActionId">queryDispensingRecords</p>
				<p name="entryName">phis.application.fsb.schemas.YF_JCFYMX_TY</p>
			</properties>
		</module>
		<module id="FSB2403" name="退药申请右边module" type="1"
			script="phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightModule">
			<properties>
				<p name="refTopList">phis.application.fsb.FSB/FSB/FSB240301</p>
				<p name="refUnderList">phis.application.fsb.FSB/FSB/FSB240302</p>
			</properties>
		</module>
		<module id="FSB240301" name="退药申请右上list" type="1"
			script="phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightTopList">
			<properties>
				<p name="serviceId">familySickBedManageService</p>
				<p name="queryActionId">queryTurningBackNumber</p>
				<p name="entryName">phis.application.fsb.schemas.JC_TYMX_TYSQ</p>
			</properties>
		</module>
		<module id="FSB240302" name="退药申请右下list" type="1"
			script="phis.application.fsb.script.FamilySickBedMedicalBackApplicationRightUnderList">
			<properties>
				<p name="serviceId">familySickBedManageService</p>
				<p name="queryActionId">querytyRecords</p>
				<p name="entryName">phis.application.fsb.schemas.JC_TYMX_TYSQ_YT</p>
			</properties>
		</module>
		
		<module id="FSB25" name="家床退药" script="phis.application.fsb.script.FamilySickBedBackMedicine" >
			<properties>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB2501</p>
				<p name="refModule">phis.application.fsb.FSB/FSB/FSB2502</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionID">saveHospitalPharmacyBackMedicine</p>
				<p name="saveFullRefundActionID">saveBackMedicineFullRefund</p>
				<p name="saveRefundActionID">saveBackMedicineRefund</p>
			</properties>
			<action id="sx" name="刷新" iconCls="refresh" />
			<action id="ty" name="退药" iconCls="pill_delete"/>
			<action id="thzx" name="退回中心" iconCls="pill_go"/>
			<action id="qt" name="全退" iconCls="pill_add"/>
		</module>
		<module id="FSB2501" name="退药处理左边LIST" script="phis.application.fsb.script.FamilySickBedBackMedicineLeftList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_BRTY</p>
				<p name="fullserviceId">phis.familySickBedManageService</p>
				<p name="queryServiceActionID">queryBackMedicine</p>
			</properties>
		</module>
		<module id="FSB2502" name="家床退药右边tab" script="phis.application.fsb.script.FamilySickBedBackMedicineUnderTabModule" type="1" >
			<properties>
			</properties>
			<action id="tycl" viewType="tycl" name="退药处理" ref="phis.application.fsb.FSB/FSB/FSB250201" />
			<action id="thbq" viewType="thbq" name="退回中心" ref="phis.application.fsb.FSB/FSB/FSB250202" />
		</module>
		<module id="FSB250201" name="退药处理" script="phis.application.fsb.script.FamilySickBedBackMedicineRightList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TYMX_TY</p>
			</properties>
		</module>
		<module id="FSB250202" name="退回中心" script="phis.application.fsb.script.FamilySickBedBackMedicineToWardList"  type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_TYMX_TY</p>
			</properties>
		</module>		
		<module id="FSB26" name="家庭病床建床情况统计" script="phis.prints.script.FamilySickBedStatisticalListPrintView" />
		<module id="FSB27" name="家庭病床费用统计" script="phis.prints.script.FamilySickBedCostStatisticalListPrintView" />
		
		<module id="FSB28" name="家床发药查询" script="phis.application.fsb.script.FamilySickBedHistoryDispensing" >
			<properties>
				<p name="refLeftList">phis.application.fsb.FSB/FSB/FSB2801</p>
				<p name="refRightList">phis.application.fsb.FSB/FSB/FSB2802</p>
				<p name="initializationServiceActionID">initialization</p>
				<p name="initializationServiceID">pharmacyManageService</p>
				<p name="entryName">phis.application.fsb.schemas.JC_FYJL_LSCX</p>
			</properties>
			<action id="query" name="查询" />
			<action id="new" name="重置" />
			<action id="print" name="打印" iconCls="print"/>
		</module>
		<module id="FSB2801" name="病区发药查询左边list" script="phis.application.fsb.script.FamilySickBedHistoryDispensingLeftList" type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_FYJL_LSCX</p>
			</properties>
		</module>
		<module id="FSB2802" name="病区发药查询右边list" script="phis.application.fsb.script.FamilySickBedHistoryDispensingRightList" type="1">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.YF_JCFYMX_LSCX</p>
				<p name="summaryable">true</p>
			</properties>
		</module>
		
		<module id="FSB29" name="家床记账" script="phis.application.fsb.script.FamilySickBedPharmacyAccountingModule">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YPJZ_FORM</p>
				<p name="refForm">phis.application.fsb.FSB/FSB/FSB2901</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB2902</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="saveActionId">saveFamilySickBedPharmacyAccounting</p>
			</properties>
			<action id="xy" name="西药方(alt+1)" iconCls="newclinic"/>
			<action id="zy" name="中药方(alt+2)" iconCls="pill_add"/>
			<action id="cy" name="草药方(alt+3)" iconCls="page_add"/>
			<action id="new" name="取消(alt+Q)" />
			<action id="save" name="记账(alt+A)" />
		</module>
		<module id="FSB2901" name="住院记账Form"  type="1" script="phis.application.fsb.script.FamilySickBedPharmacyAccountingForm">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YPJZ_FORM</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="loadActionId">loadJcxx</p>
				<p name="colCount">5</p>
				<p name="showButtonOnTop">0</p>
			</properties>
		</module>
		<module id="FSB2902" name="住院记账List" type="1"  script="phis.application.fsb.script.FamilySickBedPharmacyAccountingList">
			<properties>
				<p name="entryName">phis.application.fsb.schemas.JC_YPJZ_LIST</p>
				<p name="serviceId">familySickBedManageService</p>
				<p name="queryActionId">queryJzmx</p>
				<p name="refList">phis.application.fsb.FSB/FSB/FSB290201</p>
			</properties>
			<action id="create" name="新增(alt+C)" />
			<action id="remove" name="删除(alt+D)" />
		</module >
		<module id="FSB290201" name="记账记录" type="1"
			script="phis.application.fsb.script.FamilySickBedPharmacyAccountingRecordList" >
			<properties>
				<p name="entryName">phis.application.fsb.schemas.YF_JCYFYMX_JZ</p>
			</properties>
			<action id="confirm" name="确定" />
			<action id="close" name="取消" />
		</module>
	</catagory>
</application>