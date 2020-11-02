<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.pub.PUB" name="公共服务" 	type="1">
	<catagory id="DV" name="服务有效性认证">
		<module id="DV01" name="服务有效性认证" 		script="chis.application.pub.script.DataValidityList">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_DataValidity</p>
			</properties>
			<action id="matching" name="自动匹配" iconCls="healthDoc_autoMatch" />
			<action id="quality" name="手工质检" iconCls="common_treat" />
			<action id="print" name="打印" />
		</module>
		<module id="DV02" name="请选择时间范围"  type="1" 	script="chis.application.pub.script.DataValidityList_date">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_DataValidityForm</p>
			</properties>
			<action id="opsure" name="确定"  iconCls="save"/>
			<action id="opall" name="全部" iconCls="healthDoc_addAll" />
		</module>
		<module id="DV03" name="服务有效性维护"
			script="chis.application.pub.script.DataValidityCercleForm">
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_DataValidityCercle</p>
			</properties>
			<action id="save" name="确定" />
		</module>
	</catagory>
	<catagory id="DRUG" name="药品维护">
		<module id="DRUG01" name="药品维护" script="chis.application.pub.script.DrugDirectoryList"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_DrugDirectory</p>  
				<p name="createCls">chis.application.pub.script.DrugDirectoryForm</p>  
				<p name="updateCls">chis.application.pub.script.DrugDirectoryForm</p> 
			</properties>  
			<action id="update" name="查看" iconCls="update"/>  
			<!--<action id="importDrug" name="导入药品" iconCls="healthDoc_import"/> -->
		</module> 
	</catagory>
	<catagory id="PHT" name="健康处方维护">
		<module id="PHT01" name="健康处方维护" script="chis.application.pub.script.PelpleHealthTeachList"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach</p>  
			</properties>  
			<action id="create" name="新增" ref="chis.application.pub.PUB/PHT/PHT01_01"/>  
			<action id="update" name="查看" ref="chis.application.pub.PUB/PHT/PHT01_01"/>  
			<action id="remove" name="删除"/> 
		</module> 
		<module id="PHT01_01" name="健康处方维护" script="chis.application.pub.script.PelpleHealthTeachModule"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach</p>  
			</properties>  
			<action id="HealthTeach" name="健康处方" ref="chis.application.pub.PUB/PHT/PHT01_02" type="tab"/>  
			<action id="HealthDiagnose" name="疾病" ref="chis.application.pub.PUB/PHT/PHT01_03" type="tab"/> 
		</module> 
		<module id="PHT01_02" name="健康处方维护—处方" script="chis.application.pub.script.PelpleHealthTeachForm"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthTeach</p>  
			</properties>  
			<action id="create" name="新建"/>  
			<action id="save" name="保存"/>  
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module> 
		<module id="PHT01_03" name="健康处方维护—疾病" script="chis.application.pub.script.PelpleHealthDiagnoseList"> 
			<properties> 
				<p name="entryName">chis.application.pub.schemas.PUB_PelpleHealthDiagnose</p> 
				<p name="refModule">chis.application.pub.PUB/PHT/PHT01_03_01</p> 
			</properties>  
			<action id="addDiagnose" name="新增" iconCls="add"/>   
			<action id="remove" name="删除"/> 
			<action id="clear" name="清空" iconCls="inventory"/>
		</module>
		
		<module id="PHT01_03_01" name="疾病编码" script="chis.application.pub.script.PelpleDiagnoseTabModule">
			<action id="westjbbm" viewType="list" name="西医疾病编码"
				ref="chis.application.pub.PUB/PHT/PHT01_03_02" />
			<action id="chineseDisease" viewType="list" name="中医疾病编码"
				ref="chis.application.pub.PUB/PHT/PHT01_03_03" />
			<action id="chineseSymptom" viewType="list" name="中医证侯编码"
				ref="chis.application.pub.PUB/PHT/PHT01_03_04" />
		</module>
		<module id="PHT01_03_02" name="西医疾病编码" type="1"
			script="chis.application.pub.script.DiseaseSelectListView">
			<properties>
				<p name="entryName">chis.application.his.schemas.GY_JBBM</p>
				<p name="height">500</p>
			</properties>
			<action id="confirmSelect" name="确定" iconCls="read"/>   
			<action id="showOnlySelected" name="查看已选" iconCls="update"/> 
		</module>
		<module id="PHT01_03_03" name="中医疾病编码" type="1"
			script="chis.application.pub.script.ConfigChineseDiseaseModule">
			<properties>
				<p name="navDic">chis.dictionary.ZYJBFL_tree</p>
				<p name="refList">chis.application.pub.PUB/PHT/PHT01_03_03_01</p>
			</properties>
		</module>
		<module id="PHT01_03_03_01" name="中医疾病编码维护" type="1"
			script="chis.application.pub.script.DiseaseSelectListView">
			<properties>
				<p name="entryName">chis.application.pub.schemas.EMR_ZYJB</p>
			</properties>
			<action id="confirmSelect" name="确定" iconCls="read"/>   
			<action id="showOnlySelected" name="查看已选" iconCls="update"/> 
		</module>
		<module id="PHT01_03_04" name="中医证侯编码" type="1"
			script="chis.application.pub.script.ConfigChineseSymptomModule">
			<properties>
				<p name="navDic">chis.dictionary.ZYZHFL_tree</p>
				<p name="refList">chis.application.pub.PUB/PHT/PHT01_03_04_01</p>
			</properties>
		</module>
		<module id="PHT01_03_04_01" name="中医证侯编码维护" type="1"
			script="chis.application.pub.script.DiseaseSelectListView">
			<properties>
				<p name="entryName">chis.application.pub.schemas.EMR_ZYZH</p>
			</properties>
			<action id="confirmSelect" name="确定" iconCls="read"/>   
			<action id="showOnlySelected" name="查看已选" iconCls="update"/> 
		</module>
	</catagory>
	<catagory id="FamilyTeam" name="家庭团队信息">
		<module id="FamilyTeam01" name="家庭团队维护" script="chis.application.pub.script.FamilyTeamList"> 
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_FamilyTeam</p>
				<p name="refModule">chis.application.pub.PUB/FamilyTeam/FamilyDoctor01</p>
			</properties>
			<action id="addTeam" name="新建" iconCls="create"/> 
			<action id="update" name="查看" ref="chis.application.pub.PUB/FamilyTeam/FamilyTeam02" iconCls="update" />
			<action id="addPerson" name="成员维护" iconCls="add"/>
		</module>
		<module id="FamilyTeam02" name="家庭团队维护表单" type="1" script="chis.application.pub.script.FamilyTeamForm"> 
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_FamilyTeam</p>
			</properties>
			<action id="save" name="确定" />
		</module>
		<module id="FamilyDoctor01" name="家庭医生维护" type="1" script="chis.application.pub.script.FamilyDoctorList"> 
			<properties>
				<p name="entryName">chis.application.pub.schemas.PUB_FamilyDoctor</p>
			</properties>
			<action id="addDoctor" name="新建" iconCls="create" /> 
			<action id="update" name="查看" iconCls="update" />  
		</module>
	</catagory>	
</application>