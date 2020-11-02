<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.his.HIS" name="诊疗管理" type="1">
	<catagory id="HIS" name="诊疗管理">
		<module id="N01" name="门诊记录" type="1" script="chis.application.his.script.MedicalHistoryList">
			<properties>
				<p name="entryName">chis.application.his.schemas.HIS_MedicalHistory</p>
				<p name="listServiceId">chis.medicalHistoryService</p> 
				<p name="serviceAction">queryHistoryList</p> 
			</properties>
		</module>
		<module id="N02" name="门诊诊断记录列表" type="1"  script="chis.application.his.script.HisRecordListView">
			<properties>
				<p name="entryName">HIS_ClinicDiag</p>
			</properties>
		</module>
		<module id="N03" name="门诊检验单列表" type="1"	 script="chis.application.his.script.HisRecordListView">
			<properties>
				<p name="entryName">HIS_ClnicLab</p>
				<p name="refModule">N03-1</p>
				<p name="foreignKeyField">clnicLabId</p>
			</properties>
		</module>
		<module id="N04" name="门诊处方列表" type="1" 	script="chis.application.his.script.HisRecordListView">
			<properties>
				<p name="entryName">HIS_Recipe</p>
				<p name="refModule">chis.application.his.HIS/HIS/N04-1</p>
				<p name="foreignKeyField">recipeNo</p>
			</properties>
		</module>
		<module id="N03-1" name="门诊检验单明细信息列表" type="1" 	script="chis.application.his.script.HisSubRecordListView ">
			<properties>
				<p name="entryName">HIS_ClnicLabDetail</p>
				<p name="searchField">clnicLabId</p>
			</properties>
		</module>
		<module id="N04-1" name="门诊处方明细信息列表" type="1"  script="chis.application.his.script.HisSubRecordListView ">
			<properties>
				<p name="entryName">HIS_RecipeDetailOther</p>
				<p name="searchField">recipeId</p>
			</properties>
		</module>
		<module id="N05" name="住院记录" script="chis.application.his.script.HISHospitalRecordsModule" type="1"> 
			<properties> 
				<p name="topLeft">chis.application.his.HIS/HIS/N05-1</p>
				<p name="topRight">chis.application.his.HIS/HIS/N05-2</p>
				<p name="bottomRight">chis.application.his.HIS/HIS/N05-3</p>
				<p name="bottomLeft">chis.application.his.HIS/HIS/N05-4</p>
			</properties>
		</module> 
		<module id="N05-1" name="诊断信息" type="1"  script="chis.application.his.script.WardPatientDiseaseList">
			<properties> 
				<p name="entryName">phis.application.war.schemas.ZY_RYZD_BQ</p>  
			</properties> 
		</module>
		<module id="N05-2" name="病历信息" type="1"  script="chis.application.his.script.MedicalRecordsList">
			<properties> 
				<p name="entryName">chis.application.his.schemas.EMR_BL01_QUERYLIST</p>
				<p name="basyView">phis.application.war.WAR/WAR/WAR34020101</p> 
			</properties>
		</module>
		<module id="N05-3" name="诊疗信息" type="1"  script="chis.application.his.script.HisDiagnosisTreatModule">
			<properties>
				<p name="adviceList">chis.application.his.HIS/HIS/N05-3-1</p>
				<p name="chargeList">chis.application.his.HIS/HIS/N05-3-2</p>
			</properties>
		</module>
		<module id="N05-3-1" name="医嘱格式" type="1"  script="chis.application.his.script.HospitalCostsListDoctorFormatTab">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD_YZGS</p>  
			</properties>
		</module>
		<module id="N05-3-2" name="费用格式" type="1"  script="chis.application.his.script.HospitalCostsListList">
			<properties> 
				<p name="entryName">phis.application.hos.schemas.ZY_FYMX_FYQD</p>  
			</properties>
		</module>
		<module id="N05-4" name="过敏药物" script="chis.application.his.script.SkinTestHistroyList" type="1">
			<properties> 
				<p name="entryName">chis.application.his.schemas.YS_MZ_PSJL</p>
				<p name="modal">true</p>
				<p name="closeAction">hide</p>
			</properties>
		</module>
		<module id="N06" name="门诊记录" script="chis.application.his.script.HISClinicRecordsModule" type="1"> 
			<properties> 
				<p name="bottomList">chis.application.his.HIS/HIS/N05-4</p>
				<p name="first">chis.application.his.HIS/HIS/N06-1</p>
				<p name="second">chis.application.his.HIS/HIS/N06-2</p>
				<p name="third">chis.application.his.HIS/HIS/N06-3</p>
				<p name="fourth">chis.application.his.HIS/HIS/N06-4</p>
			</properties>
		</module>
		<module id="N06-1" name="就诊记录" type="1" script="chis.application.his.script.MedicalHistoryList">
			<properties>
				<p name="entryName">chis.application.his.schemas.HIS_MedicalHistory</p>
				<p name="listServiceId">chis.medicalHistoryService</p> 
				<p name="serviceAction">queryHistoryList</p> 
			</properties>
		</module>
		<module id="N06-2" name="诊断记录" type="1"  script="chis.application.his.script.HISClinicDiseaseList">
			<properties> 
				<p name="entryName">chis.application.his.schemas.MS_BRZD</p>  
				<p name="listServiceId">chis.hospitalDischargeService</p> 
				<p name="serviceAction">queryList</p> 
			</properties>
		</module>
		<module id="N06-3" name="用药记录" type="1" script="chis.application.his.script.HISClinicChargesList">
			<properties>
				<p name="entryName">chis.application.his.schemas.MS_CF02</p>
				<p name="listServiceId">chis.hospitalDischargeService</p> 
				<p name="serviceAction">queryList</p> 
			</properties>
		</module>
		<module id="N06-4" name="处置记录" type="1"  script="chis.application.his.script.HisDisposeRecordList">
			<properties>
				<p name="entryName">chis.application.his.schemas.MS_YJ02</p>
				<p name="listServiceId">chis.hospitalDischargeService</p> 
				<p name="serviceAction">queryList</p> 
			</properties>
		</module>
	</catagory>
</application>