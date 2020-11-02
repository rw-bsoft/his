<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.hmc.HMC" name="健康档案审核">
	<catagory id="HMC" name="档案审核">
		<module id="HMC01" name="健康档案审核" script="chis.application.hmc.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.hmc.HMC/HMC/HMC0101" />
		</module>
		<module id="HMC0101" type="1" name="个人健康档案列表"
			script="chis.application.hmc.script.HealthRecordList">
			<properties>
				<p name="healthBackFormRef">chis.application.hmc.HMC/HMC/HMC010101</p>
				<p name="removeServiceId">chis.healthRecordService</p>
				<p name="saveServiceId">chis.healthRecordService</p>
				<p name="serviceAction">healthRecordSave</p>
				<p name="serviceActionVerify">healthRecordVerify</p>
				<p name="serviceActionCancelVerify">healthRecordCancelVerify</p>
				<p name="serviceActionOpen">saveHealthRecordOpen</p>
				<p name="serviceActionCancelOpen">healthRecordCancelOpen</p>
				<p name="serviceActionRemark">saveHealthRecordRemark</p>
				<p name="serviceActionCancelRemark">healthRecordCancelRemark</p>
				<p name="listServiceId">chis.publicService</p>
				<p name="listAction">queryRecordList</p>
			</properties>
			<action id="modify" name="查看" iconCls="update" />
			<action id="verify" name="审核" iconCls="update" />
			<action id="cancelVerify" name="取消审核" iconCls="common_cancel" />
			<action id="open" name="开放" iconCls="update"/>
			<action id="cancelOpen" name="取消开放" iconCls="update"/>
			<action id="remark" name="标记" iconCls="update"/>
			<action id="cancelRemark" name="取消标记" iconCls="update"/>
			<action id="back" name="退回" iconCls="update"/>
		</module>
		<module id="HMC010101" name="档案退回" script="chis.application.hmc.script.HealthBackForm" type="1">
			<properties>
				<p name="entryName">chis.application.hr.schemas.EHR_HealthBackRecord</p>
				<p name="serviceId">chis.publicService</p>
			</properties>
		</module>
	</catagory>
</application>