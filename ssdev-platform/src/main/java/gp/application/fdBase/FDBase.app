<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.fdBase.FDBase" name="家医基本情况" type="0">
	<catagory id="FDBase" name="家医基本情况">
		<module id="FDBase01" name="家庭已签约记录" 
			script="gp.application.fdBase.script.FamilyContractRecordList" type="1">
			<properties>
				<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p> 
			</properties>
		</module>
		<module id="FDBase02" name="个人已签约记录" 
			script="gp.application.fdBase.script.PersonageContractRecordList" type="1">
			<properties>
				<p name="entryName">gp.application.hr.schemas.EHR_HealthRecord</p> 
			</properties>
		</module>
		<!-- ============= -->
		<module id="FV01" name="家庭年度维护率" type="1" script="gp.application.fdBase.script.FamilyRecordVindicateRateList">
			<properties>
				<p name="entryName">gp.application.fdBase.schemas.EHR_FamilyRecordContract</p>
				<p name="refModel">gp.application.fdBase.FDBase/FDBase/HV01</p>
			</properties>
		</module>
		<module id="HV01" name="个档年度维护率" type="1" script="gp.application.fdBase.script.HealthRecordVindicateRateList">
			<properties>
				<p name="entryName">gp.application.hr.schemas.EHR_HealthRecord</p>
			</properties>
		</module>
	</catagory>
</application>	