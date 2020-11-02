<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.dr.DR" name="双向转诊" type="0">
	<catagory id="DR" name="转诊申请">
		<module id="DR01" name="转诊申请表单" script="chis.application.dr.script.DRReferralsMoudle">
			<properties>
				<p name="entryName">chis.application.dr.schemas.DR_DemographicInfo</p>
			</properties>
			<action id="refers" name="上转申请" iconCls="create"/>
			<action id="refersReport" name="下转申请" iconCls="create"/>
			<action id="print" name="导出"/>
		</module>
		<module id="DR0101" name="转诊申请表单" script="chis.application.dr.script.DRReferralsForm" type="1">
			<properties>
				<p name="entryName">chis.application.dr.schemas.DR_ReferralsForm</p>
				<p name="demographicInfo">chis.application.dr.schemas.DR_DemographicInfo</p>
				<p name="serviceControllorId">chis.drApplyService</p>
				<p name="getMPIControllorId">chis.drApplyService</p>
				<p name="saveAction">saveSendExchange</p>
				<p name="updateAction">update</p>
				<p name="getMPIAction">getMPI</p>
			</properties>
		</module>
		<module id="DR0102" name="下转申请" type="1" script="chis.application.dr.script.DRExchangeReportForm">
			<properties>
				<p name="entryName">chis.application.dr.schemas.drIt_sendExchangeReport</p>
				<p name="demographicInfo">chis.application.dr.schemas.DR_DemographicInfo</p>
				<p name="serviceControllorId">chis.drApplyService</p>
				<p name="saveAction">saveSendExchangeReport</p>
				<p name="updateAction">updateReport</p>
				<p name="getMPIAction">getMPI</p>
			</properties>
		</module>
		<module id="DR010201" type="1" name="选择个人记录" script="chis.application.dr.script.DRMPIBaseSelect">
			<properties>
				<p name="entryName">chis.application.dr.schemas.DR_DemographicInfo</p>
			</properties>
		</module>
		<module id="DR11_03" type="1" name="打印记录" script="dr.referralMgr.referral.DRReferralsPrint">
			<properties>
				<p name="entryName">dr.schema.dr.DR_ReferralsList</p>
				<p name="loadServiceId">dr.drReferralsControllor</p>
				<p name="loadAction">load</p>
				<p name="printId">dr.referralInfo</p>
			</properties>
			<action id="cancel" name="返回" iconCls="hypertension_export"/>
		</module>
		<module id="DR02" name="上转记录查询" script="chis.application.dr.script.DRReferralsListMoudle">
			<properties>
				<p name="entryName">chis.application.dr.schemas.DR_ReferralsForm2</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="print" name="导出"/>
		</module>
		<module id="DR0201" name="上转记录表单" script="chis.application.dr.script.DRReferralsListView" type="1">
			<properties>
				<p name="entryName">chis.application.dr.schemas.DR_ReferralsForm2</p>
			</properties>
		</module>
		<module id="DR03" name="下转记录查询" script="chis.application.dr.script.DRReferralsReportListMoudle">
			<properties>
				<p name="entryName">chis.application.dr.schemas.drIt_sendExchangeReportForm</p>
			</properties>
			<action id="modify" name="查看" iconCls="update"/>
			<action id="print" name="导出"/>
		</module>
		<module id="DR0301" name="下转记录表单" script="chis.application.dr.script.DRReferralsReportListView" type="1">
			<properties>
				<p name="entryName">chis.application.dr.schemas.drIt_sendExchangeReportForm</p>
			</properties>
		</module>
		<!--转诊申请 zhaojian 2019-08-08-->
		<module id="DR04" name="转诊申请" type="1"
			script="chis.application.dr.script.DRReferralsApply">
		</module>
		<module id="DR08" name="下转管理" 
			script="chis.application.dr.script.DRReferralsDownApplyList">
		</module>
		<module id="DR05" name="上转管理" 
			script="chis.application.dr.script.DRReferralsApplyList">
		</module>
		<module id="DR06" name="下转转入审核" 
			script="chis.application.dr.script.DRReferralsDownReferralsAuditList">
		</module>
		<module id="DR09" name="上转转入审核" 
			script="chis.application.dr.script.DRReferralsReferralsAuditList">
		</module>
		<module id="DR07" name="下转转入审核"  type="1"
			script="chis.application.dr.script.DRReferralsDownReferralsAuditList">
		</module>
		<module id="DR10" name="上转转入审核"  type="1"
			script="chis.application.dr.script.DRReferralsReferralsAuditList">
		</module>
	</catagory>
</application>