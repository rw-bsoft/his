<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.tcm.TCM" name="中医馆服务">
	<catagory id="TCM" name="中医馆服务">
		<module id="TCM01" name="草药数据上传"
			script="phis.application.tcm.script.UploadHerbalMedicine">
			<action id="commit" name="上传" />
		</module>
		<module id="TCM02" name="西医疾病对照" 
			script="phis.application.tcm.script.DiagnosisContrast">
			<properties>
				<p name="refHISList">phis.application.tcm.TCM/TCM/TCM0201</p>
				<p name="refTCMList">phis.application.tcm.TCM/TCM/TCM0202</p>
				<p name="refDZList">phis.application.tcm.TCM/TCM/TCM0203</p>
			</properties>
		</module>
		<module id="TCM0201" name="西医疾病编码HIS" type="1" script="phis.application.tcm.script.DiagnosisContrastHISList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.GY_JBBM_HIS</p>
			</properties>
		</module>
		<module id="TCM0202" name="西医疾病编码TCM" type="1" script="phis.application.tcm.script.DiagnosisContrastTCMList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.GY_JBBM_TCM</p>
			</properties>
		</module>		
		<module id="TCM0203" name="已对照疾病编码" type="1" script="phis.application.tcm.script.DiagnosisContrastDZList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.GY_JBBM_DZ</p>
			</properties>
			<action id="save" name="添加" iconcls="add"/>
			<action id="remove" name="删除" />
			<action id="automatching" name="自动匹配" iconcls="arrow_switch" />
		</module>
		<module id="TCM03" name="中医疾病对照" 
			script="phis.application.tcm.script.ZyDiagnosisContrast">
			<properties>
				<p name="refHISList">phis.application.tcm.TCM/TCM/TCM0301</p>
				<p name="refTCMList">phis.application.tcm.TCM/TCM/TCM0302</p>
				<p name="refDZList">phis.application.tcm.TCM/TCM/TCM0303</p>
			</properties>
		</module>
		<module id="TCM0301" name="中医疾病编码HIS" type="1" script="phis.application.tcm.script.ZyDiagnosisContrastHISList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYJB_HIS</p>
			</properties>
		</module>
		<module id="TCM0302" name="中医疾病编码TCM" type="1" script="phis.application.tcm.script.ZyDiagnosisContrastTCMList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYJB_TCM</p>
			</properties>
		</module>		
		<module id="TCM0303" name="已对照疾病编码" type="1" script="phis.application.tcm.script.ZyDiagnosisContrastDZList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYJB_DZ</p>
			</properties>
			<action id="save" name="添加" iconcls="add"/>
			<action id="remove" name="删除" />
			<action id="automatching" name="自动匹配" iconcls="arrow_switch" />
		</module>
		<module id="TCM04" name="中医证候对照" 
			script="phis.application.tcm.script.ZhDiagnosisContrast">
			<properties>
				<p name="refHISList">phis.application.tcm.TCM/TCM/TCM0401</p>
				<p name="refTCMList">phis.application.tcm.TCM/TCM/TCM0402</p>
				<p name="refDZList">phis.application.tcm.TCM/TCM/TCM0403</p>
			</properties>
		</module>
		<module id="TCM0401" name="中医证候编码HIS" type="1" script="phis.application.tcm.script.ZhDiagnosisContrastHISList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYZH_HIS</p>
			</properties>
		</module>
		<module id="TCM0402" name="中医证候编码TCM" type="1" script="phis.application.tcm.script.ZhDiagnosisContrastTCMList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYZH_TCM</p>
			</properties>
		</module>		
		<module id="TCM0403" name="已对照证候编码" type="1" script="phis.application.tcm.script.ZhDiagnosisContrastDZList">
			<properties>
				<p name="entryName">phis.application.tcm.schemas.EMR_ZYZH_DZ</p>
			</properties>
			<action id="save" name="添加" iconcls="add"/>
			<action id="remove" name="删除" />
			<action id="automatching" name="自动匹配" iconcls="arrow_switch" />
		</module>
		<module id="TCM05" name="【中医馆服务】知识库" type="1"
			script="phis.application.tcm.script.KnowledgeBase">
		</module>
		<module id="TCM06" name="【中医馆服务】电子病历" type="1"
			script="phis.application.tcm.script.ClinicMedicalRecord">
		</module>
		<module id="TCM07" name="【中医馆服务】辩证论治" type="1"
			script="phis.application.tcm.script.SyndromeDifferentiationAndRreatment">
		</module>
		<module id="TCM08" name="【中医馆服务】治未病" type="1"
			script="phis.application.tcm.script.PreventiveTreatmentOfDiseases">
		</module>
		<module id="TCM09" name="【中医馆服务】远程教育" type="1"
			script="phis.application.tcm.script.DistanceLearning">
		</module>
		<module id="TCM10" name="【中医馆服务】远程会诊" type="1"
			script="phis.application.tcm.script.Telemedicine">
		</module>
	</catagory>
</application>