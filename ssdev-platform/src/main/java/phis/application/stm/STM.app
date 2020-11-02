<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.stm.STM" name="皮试管理">
	<catagory id="STM" name="皮试管理">
		<module id="STM10" name="皮试处理"
			script="phis.application.stm.script.ClinicSkinTestMgmtModule">
			<properties>
				<p name="refSkinTestRecordModule">phis.application.stm.STM/STM/STM1001</p>
				<p name="refSkinTestPrescriptionModule">phis.application.stm.STM/STM/STM1004</p>
				<p name="refSkinTestResultForm">phis.application.stm.STM/STM/STM1007</p>
			</properties>
			<action id="start" name="开始" iconCls="start_stm" />
			<action id="over" name="结束" iconCls="over_stm" />
			<action id="stop" name="停止" iconCls="stop_stm" />
			<action id="refresh" name="刷新" />
			<action id="close" name="退出" />
		</module>
		<module id="STM1001" name="皮试信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestRecordModule">
			<properties>
				<p name="refSkinTestDoingList">phis.application.stm.STM/STM/STM1002</p>
				<p name="refSkinTestRecordList">phis.application.stm.STM/STM/STM1003</p>
			</properties>
		</module>
		<module id="STM1002" name="皮试中记录" type="1"
			script="phis.application.stm.script.ClinicSkinTestDoingList">
			<properties>
				<p name="entryName">phis.application.stm.schemas.YS_MZ_PSJL_BG</p>
			</properties>
		</module>
		<module id="STM1003" name="待皮试记录" type="1"
			script="phis.application.stm.script.ClinicSkinTestRecordList">
			<properties>
				<p name="listServiceId">skintestQuery</p>
				<p name="entryName">phis.application.stm.schemas.YS_MZ_PSJL_DPS</p>
			</properties>
		</module>
		<module id="STM1004" name="皮试处方信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionModule">
			<properties>
				<p name="refSkinTestForm">phis.application.stm.STM/STM/STM1005</p>
				<p name="refSkinTestRecordList">phis.application.stm.STM/STM/STM1006</p>
			</properties>
		</module>
		<module id="STM1005" name="病人基本信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionForm">
			<properties>
				<p name="entryName">phis.application.stm.schemas.MS_CF01_PS</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="STM1006" name="皮试处方信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionList">
			<properties>
				<p name="render">pscl</p>
				<p name="entryName">phis.application.stm.schemas.MS_CF02_PS</p>
			</properties>
		</module>
		<module id="STM1007" name="皮试结果录入" type="1"
			script="phis.application.stm.script.ClinicSkinTestResultForm">
			<properties>
				<p name="entryName">phis.application.stm.schemas.YS_MZ_PSJG</p>
				<p name="colCount">3</p>
			</properties>
			<action id="confirm" name="确认" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="STM11" name="取消皮试"
			script="phis.application.stm.script.ClinicSkinTestCancelModule">
			<properties>
				<p name="refSkinTestCancelList">phis.application.stm.STM/STM/STM1101</p>
				<p name="refSkinTestCancelModule">phis.application.stm.STM/STM/STM1102</p>
			</properties>
			<action id="confirm" name="确认" />
			<action id="refresh" name="刷新" />
			<action id="close" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="STM1101" name="取消皮试记录" type="1"
			script="phis.application.stm.script.ClinicSkinTestCancelList">
			<properties>
				<p name="listServiceId">skintestQuery</p>
				<p name="entryName">phis.application.stm.schemas.YS_MZ_PSJL_DPS</p>
			</properties>
		</module>
		<module id="STM1102" name="皮试处方信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionModule">
			<properties>
				<p name="refSkinTestForm">phis.application.stm.STM/STM/STM1103</p>
				<p name="refSkinTestRecordList">phis.application.stm.STM/STM/STM1104</p>
			</properties>
		</module>
		<module id="STM1103" name="病人基本信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionForm">
			<properties>
				<p name="entryName">phis.application.stm.schemas.MS_CF01_PS</p>
				<p name="colCount">3</p>
			</properties>
		</module>
		<module id="STM1104" name="皮试处方信息" type="1"
			script="phis.application.stm.script.ClinicSkinTestPrescriptionList">
			<properties>
				<p name="render">qxps</p>
				<p name="entryName">phis.application.stm.schemas.MS_CF02_PS</p>
			</properties>
		</module>
		<module id="STM12" name="皮试查询"
			script="phis.application.stm.script.ClinicSkinTestResultList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.YS_MZ_PSJL</p>
			</properties>
		</module>
	</catagory>
</application>