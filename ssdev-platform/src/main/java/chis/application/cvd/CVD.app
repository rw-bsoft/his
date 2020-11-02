<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.cvd.CVD" name="心脑血管监测" type="1">
	<catagory id="CVD" name="心脑血管监测">
		<module id="D00" name="心血管监测列表" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_AssessRegister</p>
				<p name="manageUnitField">c.manaUnitId</p>
				<p name="areaGridField">c.regionCode</p>
				<p name="navDic">chis.@manageUnit</p>
				<p name="navField">c.manaUnitId</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" 	ref="chis.application.cvd.CVD/CVD/M00_1" />
		</module>
		<module id="M00_1" name="心血管监测列表" script="chis.application.cvd.script.CVDRegisterList" type="1">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_AssessRegister</p>
			</properties>
			<action id="createByEmpi" name="新建" iconCls="create" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="delete" name="删除" iconCls="remove" />
			<action id="print" name="打印" />
		</module>
		<module id="M01" name="心血管监测模块" type="1"
			script="chis.application.cvd.script.AssessRegisterModule">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="resisterList" name="心血管评测列表" ref="chis.application.cvd.CVD/CVD/M01-1" />
			<action id="resisterForm" name="心血管评测表单" ref="chis.application.cvd.CVD/CVD/M01-2"
				type="tab" />
			<action id="resisterResult" name="评测结果表" ref="chis.application.cvd.CVD/CVD/M01-3"
				type="tab" />
			<action id="knowledgeInspection" name="知识检验" ref="chis.application.cvd.CVD/CVD/M01-4"
				type="tab" />
			<action id="predictionPictur" name="危险预测图" ref="chis.application.cvd.CVD/CVD/M01-5"
				type="tab" />
		</module>
		<module id="M01-1" name="心血管评测列表" type="1"
			script="chis.application.cvd.script.AssessRegisterListView">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_AssessRegisterList</p>
			</properties>
		</module>
		<module id="M01-2" name="心血管评测表单" type="1"
			script="chis.application.cvd.script.AssessRegisterForm">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_AssessRegister</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="create" name="新建" group="create||update" />
			<action id="remove" name="删除" iconCls="remove" group="update" />
		</module>
		<module id="M01-3" name="评测结果表" type="1"
			script="chis.application.cvd.script.AppraisalForm">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_Appraisal</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="print" name="打印" />
		</module>
		<module id="M01-4" name="知识检验" type="1" script="chis.application.cvd.script.TestList">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_Test</p>
			</properties>
			<action id="save" name="确定"  group="update" />
		</module>
		<module id="M01-5" name="心血管危险预测图" type="1"
			script="chis.application.cvd.script.AssessRegisterPic" />
		<module id="M09" name="干预维护" script="chis.script.BizSimpleListView">
			<properties>
				<p name="createCls">chis.application.cvd.script.SuggestionForm</p>
				<p name="updateCls">chis.application.cvd.script.SuggestionForm</p>
				<p name="entryName">chis.application.cvd.schemas.CVD_Suggestion</p>
				<p name="removeServiceId">chis.simpleRemove</p>
			</properties>
			<action id="create" name="新建"  />
			<action id="update" name="查看" />
		</module>
		<module id="M10" name="干预种类维护" script="chis.application.cvd.script.LifeStyleMaintain">
			<properties>
				<p name="createCls">chis.application.cvd.script.CategoryForm</p>
				<p name="updateCls">chis.application.cvd.script.CategoryForm</p>
				<p name="entryName">chis.application.cvd.schemas.CVD_Category</p>
				<p name="removeServiceId">chis.categoryRemove</p>
			</properties>
			<action id="create" name="新建" />
			<action id="update" name="查看" />
		</module>
		<module id="M11" name="知识测验项目维护" script="chis.application.cvd.script.TestDictList">
			<properties>
				<p name="createCls">chis.application.cvd.script.TestDictForm</p>
				<p name="updateCls">chis.application.cvd.script.TestDictForm</p>
				<p name="entryName">chis.application.cvd.schemas.CVD_TestDict</p>
			</properties>
			<action id="create" name="新建" />
			<action id="read" name="查看" />
			<action id="update" name="修改" />
		</module>
		<module id="M12" name="心血管疾病知识测验列表" script="chis.application.cvd.script.TestList"
			type="1">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_Test</p>
				<p name="superEntry">chis.application.cvd.schemas.CVD_TestDict</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		
		
		
		<module id="D01" name="心脑血管疾病管理" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseManagement</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.cvd.CVD/CVD/D01_01" />
		</module>
		<module id="D01_01" name="心脑血管疾病管理列表" script="chis.application.cvd.script.DiseaseManagementList" type="1">
			<properties> 
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseManagement</p>
			</properties>
			<action id="CreateByEmpi" name="新建" iconCls="create"/>
			<action id="modify" name="查看 " iconCls="update"/>
			<action id="check" name="核实" iconCls="create"/>
			<action id="remove" name="删除"/>
			<action id="print" name="打印" />
		</module>
		
		<module id="D02" name="心脑血管监测模块" type="1"
			script="chis.application.cvd.script.CVDRegisterModule">
			<properties>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="registrationList" name="心脑血管病历史记录" ref="chis.application.cvd.CVD/CVD/D02_03" />
			<action id="registrationForm" name="心脑血管病例登记表单" ref="chis.application.cvd.CVD/CVD/D02_01"
				type="tab" />
			<action id="verificationForm" name="心脑血管病例初访核实" ref="chis.application.cvd.CVD/CVD/D02_02"
				type="tab" />
		</module>
		
		<module id="D02_01" name="心脑血管病例登记表单" type="1"
			script="chis.application.cvd.script.DiseaseRegistrationForm">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseManagement</p>
				<p name="isAutoScroll">true</p>
			</properties>
			<action id="save" name="保存" group="create||update" /> 
			<action id="create" name="新建" group="create||update" />
			<action id="remove" name="删除" iconCls="remove" group="update" />
		</module>
		<module id="D02_03" name="心脑血管病历史记录" type="1"
			script="chis.application.cvd.script.DiseaseHistoryListView">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseHistory</p>
			</properties>
		</module>
		
		<module id="D02_02" name="心脑血管病例初访核实" type="1"
			script="chis.application.cvd.script.DiseaseVerificationForm">
			<properties> 
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseVerification</p> 
				<p name="isAutoScroll">true</p>
			</properties>  
			<action id="save" name="确定" group="create||update"/>
		</module>		

		
		
		
		<module id="D03" name="心脑血管疾病补报" script="chis.script.CombinedDocList">
			<properties>
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseOmission</p>
			</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.cvd.CVD/CVD/D03_01" />
		</module>
		<module id="D03_01" name="心脑血管疾病补报列表" script="chis.application.cvd.script.DiseaseOmissionList" type="1">
			<properties> 
				<p name="entryName">chis.application.cvd.schemas.CVD_DiseaseOmission</p>
			</properties>
			<action id="modify" name="查看 " iconCls="update"/>
			<action id="report" name="补报" iconCls="create"/>
			<action id="print" name="打印" />
		</module>		
	</catagory>
</application>
		