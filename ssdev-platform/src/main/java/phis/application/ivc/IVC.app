<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.ivc.IVC" name="划价收费">
	<catagory id="IVC" name="划价收费">
		<module id="IVC13" name="收费结算" script="phis.application.ivc.script.ClinicFeeModule">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC0102</p>
				<p name="refModule">phis.application.ivc.IVC/IVC/IVC1301</p>
			</properties>
		</module>
		<module id="IVC1301" name="收费结算" type="1" script="phis.application.ivc.script.ClinicFeeModule2">
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC130101</p>
				<p name="refModule">phis.application.ivc.IVC/IVC/IVC130102</p>
				<p name="serviceId">clearingInterfaceService</p>
				<p name="queryEnableActionId">queryWhetherEnable</p>
				<p name="bxlrForm">phis.application.ivc.IVC/IVC/YB03</p>
				<p name="cfdList">phis.application.ivc.IVC/IVC/IVC010105</p>
				<p name="jsModule">phis.application.ivc.IVC/IVC/IVC010103</p>
				<p name="refTjModule">phis.application.ivc.IVC/IVC/IVC0107</p>
				<!--<p name="refFPHBModule">phis.application.ivc.IVC/IVC/IVC0108</p>-->
				<p name="refJYModule">phis.application.ivc.IVC/IVC/GP0101</p>
			</properties>
			<action id="newPerson" name="新建档(F1)" iconCls="employees_add"/>
			<action id="save" name="预保存(F8)"/>
			<action id="js" name="费用结算(F9)" iconCls="coins"/>
			<action id="qx" name="清空(ctrl+S)" iconCls="inventory"/>
			<action id="fz" name="发票复制(F6)" iconCls="copy"/>
			<action id="xg" name="发票修改(F7)" iconCls="page_white_edit"/>
			<action id="ZDCR" name="自动插入(F10)" iconCls="treeInsert"/>
			<action id="printSet" name="发票设置" iconCls="print"/>
			<!--<action id="newPhysical" name="健康证结算(F11)" iconCls="employees_add"/>-->
			<!--<action id="ybdk" name="医保费用上传" iconCls="money"/>-->
			<!--<action id="mzjsnhdk" name="农合读卡(F11)" iconCls="money_add"/>-->
			<action id="print" name="发票打印"/>
			<action id="yycl" name="预约处理" iconCls="clock_go" />
		</module>
		<module id="IVC130101" name="门诊收费处理病人form"  type="1" script="phis.application.ivc.script.ClinicFeeForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_FEE</p>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC13010101</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="IVC13010101" name="门诊收费处理病人form_科室选择"  type="1" script="phis.application.ivc.script.ClinicFee_ksxz">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_SF_GHKS</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">true</p>
			</properties>
			<action id="commit" name="确定" />
			<action id="cancel" name="取消" iconCls="close"/>
		</module>
		
		<module id="IVC130102" name="门诊收费处理-收费信息" type="1" script="phis.application.ivc.script.ClinicFeeModule3">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC130103</p>
				<p name="refModule4">phis.application.ivc.IVC/IVC/IVC130104</p>
			</properties>
			<action id="XY" name="西药(F3)" iconCls="pill_add"/>
			<action id="ZY" name="中药(F4)" iconCls="newclinic"/>
			<action id="CY" name="草药(F5)" iconCls="page_add"/>
			<action id="JX" name="检查(F2)" iconCls="stock"/>
			<action id="insert" name="插入(ctrl+Q)" iconCls="insertgroup"/>
			<action id="newGroup" name="新组(ctrl+W)" iconCls="newgroup"/>
			<action id="remove" name="删除(ctrl+R)" />
			<action id="delGroup" name="删除组(ctrl+D)" iconCls="removeclinic"/>
		</module>
		<module id="IVC130103" name="门诊收费处理list" type="1" script="phis.application.ivc.script.ClinicFeeEditorList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_CF02_FEE</p>
				<!--由于开始只做界面,暂时不去查数据-->
				<p name="autoLoadData">false</p>	
			</properties>
		</module>
		<module id="IVC130104" name="门诊收费处理" type="1" script="phis.application.ivc.script.ClinicFeeModule4">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC130105</p>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC130106</p>
			</properties>
		</module>
		<module id="IVC130105" name="门诊收费处理" type="1" script="phis.application.ivc.script.ClinicFeeList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_FYGB_FEE</p>
				<p name="autoLoadData">false</p>	
			</properties>
		</module>
		<module id="IVC130106" name="门诊收费处理" type="1" script="phis.application.ivc.script.ClinicFeeForm2">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_FYGB_FEE</p>
				<!--由于开始只做界面,暂时不去查数据-->
				<p name="autoLoadData">false</p>	
			</properties>
		</module>
		<module id="IVC01" name="收费结算(原)" type="1" script="phis.application.ivc.script.ClinicModule">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC0102</p>
				<p name="refModule">phis.application.ivc.IVC/IVC/IVC0101</p>
			</properties>
		</module>
		<module id="IVC0102" name="收费病人列表" type="1" script="phis.application.ivc.script.ClinicList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_GHMX_SF</p>
				<p name="showButtonOnTop">true</p>	
				<p name="serviceId">clinicChargesProcessingService</p>
				<p name="queryServiceActionID">queryGhmx</p>
			</properties>
		</module>
		<module id="IVC0101" name="收费结算" type="1" script="phis.application.ivc.script.ClinicChargesModule">
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC010101</p>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC010102</p>
				<p name="serviceId">clearingInterfaceService</p>
				<p name="queryEnableActionId">queryWhetherEnable</p>
				<p name="bxlrForm">phis.application.ivc.IVC/IVC/YB03</p>
				<p name="cfdList">phis.application.ivc.IVC/IVC/IVC010105</p>
				<p name="jsModule">phis.application.ivc.IVC/IVC/IVC010103</p>
			</properties>
			<action id="newPerson" name="新建档" iconCls="employees_add"/>
			<action id="ybdk" name="医保读卡" iconCls="money"/>
			<action id="cflr" name="处方录入" iconCls="pill_add"/>
			<action id="czlr" name="处置录入" iconCls="page_add"/>
			<action id="js" name="费用结算" iconCls="coins"/>
			<action id="qx" name="清空" iconCls="inventory"/>
			<action id="fz" name="发票复制" iconCls="copy"/>
			<action id="xg" name="发票修改" iconCls="page_white_edit"/>
			<action id="ZDCR" name="自动插入" iconCls="treeInsert"/>
			<!--<action id="printSet" name="打印设置" />
				<action id="print" name="发票打印" />-->
			<!--<action id="bxlr" name="报销录入" />-->
		</module>
		<module id="IVC010101" name="门诊收费处理病人form"  type="1" 
			script="phis.application.ivc.script.ClinicChargesPatientForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_MZSF</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="IVC01010101" name="审批编号录入list"  type="1" 
			script="phis.application.ivc.script.ExamineEnteringDetailList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_SPLR</p>
			</properties>
			<action id="commit" name="确定" />
		</module>
		<module id="IVC010102" name="门诊收费处理list" type="1" 
		script="phis.application.ivc.script.ClinicChargesList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_CF02_MZSF</p>
				<!--由于开始只做界面,暂时不去查数据-->
				<p name="autoLoadData">false</p>	
			</properties>
		</module>
		<!-- 门诊结算-收费module -->
		<module id="IVC010103" name="门诊结算" type="1" script="phis.application.ivc.script.ClinicSettlementModule">
			<properties>
				<p name="refSettlementForm">phis.application.ivc.IVC/IVC/IVC01010301</p>
				<p name="refSettlementPrint">phis.application.ivc.IVC/IVC/IVC0205</p>
			</properties>
		</module>
		<module id="IVC01010301" name="门诊结算-收费Form" type="1" script="phis.application.ivc.script.ClinicSettlementForm">
			<properties>
				<p name="serviceId">clearingInterfaceService</p>
				<p name="queryActionId">settlementInterfaceDataQuery</p>
				<p name="confirmActionId">outpatientQualificationQuery</p>
				<p name="qualificationActionId">outpatientQualification</p>
				<p name="cer">%user.manageUnit.properties.certificateType</p>
				<p name="dep">%user.manageUnit.properties.medicareId</p>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_JS</p>
			</properties>
		</module>

		<module id="GP0101" name="家医减免清单" type="1" script="phis.application.gp.script.GpSelectList">
			<properties>
				<p name ="serviceId">phis.GeneralPractitionersService</p>
				<p name="serviceAction">queryGpDetil</p>
				<p name="entryName">phis.application.gp.schemas.SCM_INCREASEITEMSDETIL</p>
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="取消"  iconCls="close"/>
		</module>


		<module id="IVC010104" name="处方录入" type="1" script="phis.application.ivc.script.ClinicPrescriptionEntrySFModule"> 
			<properties> 
				<p name="openedBy">clinicStation</p>
				<p name="clinicPrescriptionEntryForm">phis.application.cic.CIC/CIC/CIC0501</p>
				<p name="clinicPrescriptionEntryList">phis.application.cic.CIC/CIC/CIC0502</p>
				<p name="clinicPrescriptionQuickInputTab">phis.application.ivc.IVC/IVC/IVC01010401</p>
				<p name="refFjList">phis.application.cic.CIC/CIC/IVC01010402</p>
			</properties>  
		</module>
		<module id="IVC01010401" name="处方快速输入tab"  type="1" script="phis.application.cic.script.ClinicPrescriptionEntryQuickInputTab">
			<action id="clinicAll" viewType="list" name="全部" ref="phis.application.cic.CIC/CIC/CIC050303" />
		</module>
		<module id="IVC010105" name="单据选择" type="1" script="phis.application.ivc.script.ClinicPrescriptionList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_CFD</p>
				<p name="mutiSelect">true</p>
				<p name="height">300</p>
				<p name="width">613</p>
				<p name="modal">true</p>
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC010106" name="处置录入" type="1" script="phis.application.cic.script.ClinicDisposalEntrySFModule"> 
			<properties> 
				<p name="refDisposalEntryList">phis.application.cic.CIC/CIC/CIC0601</p> 
				<p name="refDisposalEntryTabList">phis.application.cic.CIC/CIC/CIC0602</p>
			</properties>
		</module>
		<module id="IVC010107" name="医保_门诊结算" type="1" script="phis.script.ws.MedicalInsuranceForm"> 
			<properties> 
				<p name="entryName">phis.application.ivc.schemas.YB_MZJS</p> 
				<p name="colCount">2</p>
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC010108" name="医保_门诊结算_碎石" type="1" script="phis.script.ws.MedicalInsuranceGravelForm"> 
			<properties> 
				<p name="entryName">phis.application.ivc.schemas.YB_MZJS_SS</p> 
			</properties>
			<action id="commit" name="确定"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC010109" name="农合读卡" type="1" script="phis.application.ivc.script.XNHdkList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.NHDJ_BRDA</p>
				<p name="initCnd">['eq',['$','a.SBHM'],["s","1"]]</p>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="readcard" name="农合卡" iconCls="start_stm" />
			<action id="readsmcard" name="市民卡" iconCls="start_stm" />
			<action id="readcard_hh" name="农合卡(火狐)" iconCls="start_stm" />
		</module>		
		<module id="IVC02" name="发票查询" script="phis.application.ivc.script.ClinicReceivablesInvoiceQueriesModule">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC0202</p>
				<p name="refMzqd">phis.application.ivc.IVC/IVC/IVC0207</p>
				<p name="serviceId">clearingInterfaceService</p>
				<p name="queryNatureActionId">queryNatureList</p>
				<p name="queryDeptActionId">queryDept</p>
				<p name="uploadInvoiceActionId">uploadInvoice</p>
				<p name="qualificationActionId">outpatientQualification</p>
			</properties>
			<action id="newQuery" name="重置" iconCls="create"/>
			<!-- 2013-09-26 隐藏发票打印按钮-->
			<!-- 2014-03-10 又要隐藏发票打印按
				<action id="fp" name="发票打印" iconCls="printing"/>-->
			
			<action id="mzqd" name="门诊清单" iconCls="page_white_paste"/>
			<action id="dj" name="单据查询" iconCls="dj"/>
			<action id="fpbd" name="发票补打" iconCls="printing"/>
			<!--<action id="bc" name="发票补传" iconCls="copy"/>
				<action id="dyzsfp" name="打印真实发票" iconCls="printing"/>-->
		</module>
		<module id="IVC0202" name="收款发票查询List" type="1" script="phis.application.ivc.script.ClinicReceivablesInvoiceQueriesList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_MZSK</p>
			</properties>
		</module>
		<module id="IVC0203" name="单据详情" type="1" script="phis.application.ivc.script.ClinicReceivablesInvoiceModule">
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC020301</p>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC010102</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC020301" name="门诊收费处理病人form"  type="1" script="phis.application.ivc.script.ClinicChargesPatientForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_MZXX_MZSF</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="IVC0204" name="发票打印"  type="1" script="phis.prints.script.ClinicChargesPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
			</properties>
		</module>
		<module id="IVC0205" name="发票打印"  type="1" script="phis.prints.script.ClinicChargesInvocePrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
			</properties>
		</module>
		<module id="IVC0206" name="医保发票打印" type="1"  script="phis.prints.script.SecurityClinicPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
			</properties>
		</module>
		<module id="IVC020601" name="门诊边诊疗边付费医保发票打印" type="1"  script="phis.prints.script.SecurityClinicPrintView2">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
			</properties>
		</module>
		<module id="IVC0207" name="门诊清单" type="1"  script="phis.prints.script.OutpatientListPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
			</properties>
		</module>
		<module id="IVC12" name="退费处理" script="phis.application.ivc.script.RefundProcessingModule">
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC1201</p>
				<p name="refModule">phis.application.ivc.IVC/IVC/IVC1202</p>
				<p name="jsModule">phis.application.ivc.IVC/IVC/IVC1203</p>
			</properties>
			<action id="tfcl" name="退费" iconCls="writeoff" />
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC1201" name="退费处理form1" type="1" script="phis.application.ivc.script.RefundProcessingForm1">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_TFCL_F1</p>
				<p name="colCount">4</p>
			</properties>
		</module>
		<module id="IVC1202" name="退费处理module1" type="1" script="phis.application.ivc.script.RefundProcessingModule1">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC120201</p>
				<p name="refModule">phis.application.ivc.IVC/IVC/IVC120202</p>
			</properties>
		</module>
		<module id="IVC120201" name="退费处理list1" type="1" script="phis.application.ivc.script.RefundProcessingList1">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_TFCL_L1</p>
			</properties>
		</module>
		<module id="IVC120202" name="退费处理module2" type="1" script="phis.application.ivc.script.RefundProcessingModule2">
			<properties>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC12020202</p>
			</properties>
		</module>
		<module id="IVC12020202" name="退费处理list2" type="1" script="phis.application.ivc.script.RefundProcessingList2">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_TFCL_L2</p>
			</properties>
		</module>
		<module id="IVC1203" name="退费结算" type="1" script="phis.application.ivc.script.RefundSettleModule">
			<properties>
				<!--<p name="leftForm">phis.application.ivc.IVC/IVC/IVC120201</p>-->
				<p name="leftForm">phis.application.ivc.IVC/IVC/IVC120301</p>
				<p name="rightForm">phis.application.ivc.IVC/IVC/IVC120302</p>
				<!--<p name="entryName">phis.application.ivc.schemas.MS_MZXX_JS</p>-->
			</properties>
		</module>
		<module id="IVC120301" name="退费结算leftForm" type="1" script="phis.application.ivc.script.RefundSettleLeftForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_TFJS</p>
				<p name="colCount">1</p>
			</properties>
		</module>
		<module id="IVC120302" name="退费结算rightForm" type="1" script="phis.application.ivc.script.RefundSettleRightForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_TFCL_F1</p>
				<p name="colCount">1</p>
			</properties>
		</module>
		<module id="IVC03" name="发票作废" script="phis.application.ivc.script.InvoiceInvalidList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_ZFFP</p>
				<p name="fpzfModule">phis.application.ivc.IVC/IVC/IVC0301</p>
			</properties>
			<action id="fpzf" name="作废" iconCls="writeoff" />
			<action id="qxzf" name="取消作废" iconCls="update"/>
			<action id="payInit" name="初始化" iconCls="commit" />
			<action id="start" name="开始" iconCls="commit" />
			<action id="end" name="结束" iconCls="commit" />
			<action id="finish" name="支付完成" iconCls="commit" />
		</module>
		<module id="IVC0301" name="发票作废" type="1" script="phis.application.ivc.script.ClinicFPZFChargesModule">
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC020301</p>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC010102</p>
				<p name="serviceId">clearingInterfaceService</p>
				<p name="querySettlementDateActionId">querySettlementDate</p>
				<p name="sjybxxForm">phis.application.ivc.IVC/IVC/SJYB0102</p>
			</properties>
			<!--<action id="dj" name="单据"/>-->
			<action id="nhskzf" name="农合卡作废" iconCls="money_add"/>
			<action id="smkskzf" name="市民卡作废" iconCls="money_add"/>
			<action id="nhskzf_hh" name="农合卡作废(火狐)" iconCls="money_add"/>
			<action id="fpzf" name="作废" iconCls="writeoff"/>
			<action id="qxzf" name="取消作废" iconCls="update"/>
			<action id="cancel" name="关闭" iconCls="common_cancel"/>
		</module>
		<module id="IVC0401" name="就诊号码维护" type="1" script="phis.application.ivc.script.NumberConfigList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_YGPJ_JZ</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">1</p>
				<p name="deptId">%user.manageUnit.id</p>
				<p name="createCls">phis.application.ivc.script.TreatmentNumberForm</p>
				<p name="updateCls">phis.application.ivc.script.TreatmentNumberForm</p>
			</properties>
			<action id="create" name="新增" iconCls="add"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="IVC0402" name="门诊号码维护" type="1" script="phis.application.ivc.script.NumberConfigList">
			<properties>
				
				<p name="entryName">phis.application.ivc.schemas.MS_YGPJ_MZ</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">3</p>
				<p name="deptId">%user.manageUnit.id</p>
				<p name="createCls">phis.application.ivc.script.OutPatientNumberForm</p>
				<p name="updateCls">phis.application.ivc.script.OutPatientNumberForm</p>				
			</properties>
			<action id="create" name="新增" iconCls="add"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="IVC0403" name="发票号码维护" type="1" script="phis.application.ivc.script.NumberConfigList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_YGPJ_FP</p>
				<p name="removeByFiled">QSHM</p>
				<p name="PJLX">2</p>
				<p name="deptId">%user.manageUnit.id</p>
				<p name="createCls">phis.application.ivc.script.InvoiceNumberForm</p>
				<p name="updateCls">phis.application.ivc.script.InvoiceNumberForm</p>
			</properties>
			<action id="create" name="新增" iconCls="add"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="IVC05" name="收费日报" script="phis.prints.script.ChargesDailyPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0501</p>
				<p name="serviceId">chargesDailyService</p>
				<p name="refCancelCommit">phis.application.ivc.IVC/IVC/IVC0502</p>
			</properties>
		</module>
		<module id="IVC0501" name="日报选择" type="1" script="phis.application.ivc.script.DateChoseList">
			<properties>
				<p name="listServiceId">chargesCheckout</p>
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
		<module id="IVC0502" name="取消日报" type="1" script="phis.application.ivc.script.CancelCommitModule">
			<properties>
				<p name="serviceId">chargesDailyService</p>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC050201</p>
				<p name="refList">phis.application.ivc.IVC/IVC/IVC050202</p>
			</properties>
		</module>
		<module id="IVC050201" name="取消日报form" type="1" script="phis.application.ivc.script.CancelCommitForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_JZRQ</p>
				<p name="serviceId">chargesDailyService</p>
			</properties>
		</module>
		<module id="IVC050202" name="取消日报list" type="1" script="phis.application.ivc.script.CancelCommitList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_HZRB_QXJZ</p>
				<p name="listServiceId">chargesDailyService</p>
			</properties>
		</module>
		<module id="IVC06" name="收费汇总" script="phis.script.TabModule">
			<action id="ChargesSummary" name="收费汇总" ref="phis.application.ivc.IVC/IVC/IVC06_1" type="tab"/>
			<action id="ChargesSummarySearch" name="收费汇总查询" ref="phis.application.ivc.IVC/IVC/IVC06_1_1" type="tab"/>
			<action id="ItemizeSummary" name="项目分类汇总" ref="phis.application.ivc.IVC/IVC/IVC06_2" type="tab"/>
			<action id="OutstandingChargesSummary" name="未结账收费汇总" ref="phis.application.ivc.IVC/IVC/IVC06_3" type="tab"/>
			<action id="mzhyfmxtj" name="门诊使用量统计" ref="phis.application.ivc.IVC/IVC/IVC06_4" type="tab"/>
			<action id="Hospitalcharges" name="医院收费汇总" ref="phis.application.ivc.IVC/IVC/IVC06_5" type="tab"/>
			<action id="Countrycharges" name="卫生室收费汇总" ref="phis.application.ivc.IVC/IVC/IVC06_6" type="tab"/>
			<action id="ClinicChargesSummary" name="门诊收费汇总" ref="phis.application.ivc.IVC/IVC/IVC06_7" type="tab"/>
			<action id="ClinicCompensateSummary" name="门诊补偿汇总(职工)" ref="phis.application.ivc.IVC/IVC/IVC06_8" type="tab"/>
	        <action id="ClinicCompensateSummaryjm" name="门诊补偿汇总(居民)" ref="phis.application.ivc.IVC/IVC/IVC06_9" type="tab"/>
                
		</module>
		<module id="IVC06_1" name="收费汇总" type="1" script="phis.prints.script.ChargesSummaryPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0601</p>
				<p name="serviceId">chargesSummaryService</p>
				<p name="serviceActionVerification">chargesSummaryBefVerification</p>
				<p name="serviceActionCheckOutVerification">chargesSummaryCheckOutBefVerification</p>
			</properties>
		</module>
		<module id="IVC06_1_1" name="收费汇总查询" type="1" script="phis.prints.script.ChargesSummaryPrintView_Search">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0601</p>
				<p name="serviceId">chargesSummaryService</p>
				<p name="serviceActionVerification">chargesSummaryBefVerification</p>
				<p name="serviceActionCheckOutVerification">chargesSummaryCheckOutBefVerification</p>
			</properties>
		</module>
		<module id="IVC06_2" name="项目分类汇总" type="1" script="phis.prints.script.ItemizeSummaryPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0601</p>
				<p name="serviceId">chargesSummaryService</p>
				<p name="serviceActionVerification">chargesSummaryBefVerification</p>
				<p name="serviceActionCheckOutVerification">chargesSummaryCheckOutBefVerification</p>
			</properties>
		</module>
		<module id="IVC06_3" name="未结账收费汇总" type="1" script="phis.prints.script.OutstandingChargesSummaryPrintView">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC0601</p>
				<p name="serviceId">chargesSummaryService</p>
				<p name="serviceActionVerification">chargesSummaryBefVerification</p>
				<p name="serviceActionCheckOutVerification">chargesSummaryCheckOutBefVerification</p>
			</properties>
		</module>
		<module id="IVC06_4" name="门诊使用量统计" type="1" script="phis.prints.script.ClinicallhyChargesPrintView">
		</module>
		<module id="IVC06_5" name="医院收费汇总统计" type="1" script="phis.prints.script.HospitalChargesPrintView">
		</module>
		<module id="IVC06_6" name="卫生室收费汇总统计" type="1" script="phis.prints.script.CountryChargesPrintView">
		</module>
		<module id="IVC06_7" name="门诊收费汇总" type="1" script="phis.prints.script.ClinicChargesSummaryPrintView">
		</module>
		<module id="IVC06_8" name="门诊补偿汇总(职工)" type="1" script="phis.application.xnh.script.NhclinicallChargesPrintView">
	        </module>
	    <module id="IVC06_9" name="门诊补偿汇总(居民)" type="1" script="phis.application.xnh.script.NhclinicallChargesjmPrintView">
	        </module>
		<module id="IVC0601" name="汇总选择"  type="1" script="phis.script.SimpleList">
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
		<module id="IVC07" name="门诊医生核算" script="phis.prints.script.DoctorsAccountingPrintView">
			<properties>
				<p name="navParentKey">%user.manageUnit.id</p>
				<p name="serviceId">accountsStatisticsService</p>
				<p name="serviceActionVerification">accountsStatisticsBefVerification</p>
				<p name="serviceActionSave">saveAccountsStatistics</p>
			</properties>
		</module>
		<module id="IVC08" name="性质费用核算" script="phis.prints.script.PropertiesAccountingPrintView">
			<properties>
			</properties>
		</module>
		<module id="IVC09" name="报表设置" script="phis.application.ivc.script.ReportSettingModule">
			<properties>
				<p name="refComboNameList">phis.application.ivc.IVC/IVC/IVC0901</p>
				<p name="refComboNameDetailList">phis.application.ivc.IVC/IVC/IVC0902</p> 
			</properties>
		</module>
		<module id="IVC0901" name="报表列表" type="1" script="phis.application.ivc.script.ReportSettingList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.GY_BBMC</p>
			</properties>
		</module>
		<module id="IVC0902" name="详细列表" type="1" script="phis.application.ivc.script.ReportSettingDetailList">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.GY_XMGB</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="IVC10" name="中医创建项目统计"
			script="phis.prints.script.ChineseMedicineProjectPrintView">
			<action id="query" name="查询" />
			<action id="print" name="打印" iconCls="print" />
			<action id="export" name="导出" iconCls="excel" />
		</module>		
		<module id="IVC04" name="票据号码维护" script="phis.script.TabModule">
			<!--<properties>
					<p name="winState" type="jo">{pos:[0,0]}</p>
				</properties>-->
			<action id="invoiceNumberTab" viewType="list" name="发票号码维护" ref="phis.application.ivc.IVC/IVC/IVC0403" />
			<action id="medicalNumberTab" viewType="list" name="就诊号码维护" ref="phis.application.ivc.IVC/IVC/IVC0401" />
			<action id="outpatientServiceTab" viewType="list" name="门诊号码维护" ref="phis.application.ivc.IVC/IVC/IVC0402" />
		</module>
		<!--<module id="IVC10" name="门诊发票查询" type="1" script="phis.application.ivc.script.OutpatientInvoicePrintView">
			</module>-->
		<module id="IVC11" name="基本统计" type="1" script="phis.application.ivc.script.ChargesSummary">
			<properties>
				<p name="entryName">phis.report.ChargesSummaryChart</p>
			</properties>
		</module>
		<module id="IVC0107" name="体检档案录入"  type="1"  script="phis.application.ivc.script.ClinicPhysicalTjModule" >
			<properties>
				<p name="refForm">phis.application.ivc.IVC/IVC/IVC010701</p>
				<p name="newMr">phis.application.ivc.IVC/IVC/IVC010702</p>
			</properties>
		</module>
		<module id="IVC010701" name="体检档案录入form"  type="1"     script="phis.application.ivc.script.ClinicPhysicalTjForm">
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MPI_DemographicInfo_TJ</p>
			</properties>
		</module>
		<module id="IVC010702" name="默认信息"  type="1"    script="phis.application.ivc.script.ClinicPhysicalMrModule">
			<properties>
				<!--phis.application.ivc.schemas.MS_GHMX_TJ-->
				<p name="newMr">phis.application.ivc.IVC/IVC/IVC01070201</p>
			</properties>
		</module>
		<module id="IVC01070201" name="体检档案录入"  type="1"  script="phis.application.ivc.script.ClinicPhysicalMrForm" >
			<properties>
				<p name="entryName">phis.application.ivc.schemas.GY_XTCS_MR</p>
			</properties>
		</module>
		<module id="IVC15" name="门诊费用情况查询" script="phis.application.ivc.script.ClinicOutpatientExpensesModule">
			<properties> 
				<p name="refUplist">phis.application.ivc.IVC/IVC/IVC1501</p>
				<p name="refDowList">phis.application.ivc.IVC/IVC/IVC1502</p>
			</properties>    
		</module>
		<module id="IVC1501" name="费用信息" script="phis.application.ivc.script.ClinicOutpatientExpensesInfo" type="1" > 
			<properties>
				<p name="entryName">phis.application.ivc.schemas.MS_FYXX</p>
				<p name="listServiceId">phis.clinicOutpatientExpensesInfoService</p>
				<p name="serviceAction">queryFYXXInfo</p>
			</properties>
		</module>
		<module id="IVC1502" name="费用明细" script="phis.application.ivc.script.ClinicOutpatientExpensesDetails" type="1">
			<properties>   
				<p name="entryName">phis.application.ivc.schemas.MS_FYMX</p>
			</properties>
		</module>
		<module id="IVC16" name="门诊科室工作量统计" script="phis.prints.script.ClinicOutpatientWorkloadPrintView">
			<properties>
			</properties>
		</module>
		
		<module id="IVC50" name="南京医保对账" script="phis.application.yb.script.SjybYbdzMainModule_X">
			<properties>
				<p name="refsybform">phis.application.ivc.IVC/IVC/IVC5001</p>
				<p name="refsyblist">phis.application.ivc.IVC/IVC/IVC5002</p>
				<p name="entryName">phis.application.yb.schemas.SJYB_DZ_ONLY</p>
				<p name="serviceId">NjjbService</p>
				<p name="showButtonOnTop">1</p>
				<p name="refSYBDZMxForm">phis.application.ivc.IVC/IVC/IVC5003</p>
			</properties>
			<action id="dz" name= "对账" iconCls="money"/>
			<action id="dzmx" name="明细对账" iconCls="detail"/>
		</module>
		<module id="IVC5001" name="抬头" script="phis.application.yb.script.SjybYbdzMainTopFrom" type="1">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SJYB_JEDZText_X</p>
				<p name="colCount">1</p>
				<p name="serviceId">NjjbService</p>
			</properties>
		</module>
		<module id="IVC5002" name="列表" script="phis.application.yb.script.SjybYbdzBottomList_X" type="1">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SJYB_DZ_ONLY</p>
				<p name="serviceId">NjjbService</p>
			</properties>
		</module>
		<module id="IVC5003" name ="南京医保明细对账" script="phis.application.yb.script.SjybYbdzMxMainModule_X" type="1">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SJYB_DZ_ONLY</p>
				<p name="serviceId">NjjbService</p>
				<p name="refBdList">phis.application.ivc.IVC/IVC/IVC500301</p>
				<p name="refZxList">phis.application.ivc.IVC/IVC/IVC500302</p>
			</properties>
			<action id="dzxz" name ="下载交易" />
			<action id="mxdz" name ="对账" />
			<action id="jycz" name ="医保冲正" />
		</module>
		<module id="IVC500301" name="南京医保明细对账本地" script="phis.application.yb.script.SjybYbdzMxBdList_X" type="1">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SJYB_DZ_ONLY</p>
				<p name="serviceId">phis.NjjbService</p>
			</properties>
		</module>
		<module id="IVC500302" name="南京医保医保明细对账医保中心" script="phis.application.yb.script.SjybYbdzMxZxList_X" type="1">
			<properties>
				<p name="entryName">phis.application.yb.schemas.SJYB_DZ_ONLY_X</p>
				<p name="serviceId">NjjbService</p>
			</properties>
		</module>	
		<module id="IVC60" name="汇总查询" script="phis.script.TabModule">
			<action id="ChargesSummary_ls" name="收费汇总" ref="phis.application.ivc.IVC/IVC/IVC60_1" type="tab"/>
		</module>
		<module id="IVC60_1" name="收费汇总" type="1" script="phis.prints.script.ChargesSummaryPrintView_ls">
			<properties>
				<p name="refQueryList">phis.application.ivc.IVC/IVC/IVC6001</p>
			</properties>
		</module>
		<module id="IVC6001" name="汇总选择"  type="1" script="phis.script.SimpleList">
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
		<module id="IVC70" name="移动支付对账" script="phis.script.TabModule">
			<action id="mxdz" viewType="list" name="明细对账" ref="phis.application.ivc.IVC/IVC/IVC7001"/>
		</module>
		<module id="IVC7001" name="明细对账" type="1" script="phis.application.pay.script.MobilepaydetailsList">
			<properties>
				<p name="entryName">phis.application.pay.schemas.MOBILEPAY_MXDZ</p>
				<p name="autoLoadData">false</p>
			</properties>
		</module>
	</catagory>
</application>