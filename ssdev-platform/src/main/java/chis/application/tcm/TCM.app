<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.tcm.TCM" name="中医管理"  type="1"> 
	<catagory id="TCM" name="中医管理">
		<module id="TCM01" name="中医体质登记" script="chis.application.tcm.script.TCMRegisterListView">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_TCMRegister</p>  
			</properties>
			<action id="TCMQToR" name="问卷登记" iconCls="create"/>
			<action id="addRegister" name="直接登记" iconCls="create"/>
			<action id="showTCMQ" name="查看问卷" iconCls="update"/>
			<action id="remove" name="删除"/>
			<action id="print" name="打印" />
		</module>
		<module id="TCM01_01" name="中医体质登记" type="1" script="chis.application.tcm.script.TCMRegisterModule">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="InquireList" name="中医体质登记列表" ref="chis.application.tcm.TCM/TCM/TCM03_0101" />
			<action id="InquireForm" name="中医体质登记表单" ref="chis.application.tcm.TCM/TCM/TCM01_0102" />
		</module>
		<module id="TCM01_0102" name="中医体质登记表单" type="1" script="chis.application.tcm.script.TCMRegisterForm">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_TCMRegister</p>  
				<p name="saveServiceId">chis.tcmQuestionnaireService</p>  
				<p name="saveAction">saveTCMRegister</p>
			</properties>
			<action id="save" name="保存" group="create||update"/> 
			<action id="create" name="新增" iconCls="create" group="create"/>  
		</module>
		<module id="TCM02" name="中医体质辨识问卷" script="chis.application.tcm.script.TCMQuestionnaireListView">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_IFCQResult</p>  
				<p name="refOQFModule">chis.application.tcm.TCM/TCM/TCM_OQF</p> 
			</properties> 
			<action id="TCMQToR" name="问卷登记" iconCls="create"/>
			<action id="showTCMQ" name="查看问卷" iconCls="update"/>
		</module> 
		<module id="TCM03" name="中医指导" script="chis.application.tcm.script.SickGuidanceListView">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_SickGuidance</p>  
			</properties>
			<action id="createSG" name="新增" iconCls="create"/>  
			<action id="modify" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TCM03_01" name="中医指导模块" type="1" script="chis.application.tcm.script.SickGuidanceModule">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_SickGuidance</p>  
			</properties>
			<action id="registerList" name="登记列表" ref="chis.application.tcm.TCM/TCM/TCM03_0101"/>
			<action id="guidanceList" name="指导列表" ref="chis.application.tcm.TCM/TCM/TCM03_0102"/>
			<action id="guidanceForm" name="指导表单" ref="chis.application.tcm.TCM/TCM/TCM03_0103"/>
		</module>
		<module id="TCM03_0101" name="登记列表" type="1" script="chis.application.tcm.script.RegisterList">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCMRegisterList</p>  
			</properties>
		</module>
		<module id="TCM03_0102" name="指导列表" type="1" script="chis.application.tcm.script.SickGuidanceList">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.SickGuidanceList</p>  
			</properties>
		</module>
		<module id="TCM03_0103" name="指导表单" type="1" script="chis.application.tcm.script.SickGuidanceForm">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_SickGuidance</p>  
				<p name="tcmGCImportModule">chis.application.tcm.TCM/TCM/TCM_GCLI</p>
				<p name="isAutoScroll">true</p>
				<p name="loadServiceId">chis.tcmQuestionnaireService</p>  
				<p name="loadAction">loadTCMGuidance</p>  
			</properties>
			<action id="save" name="保存" group="create||update"/> 
			<action id="createSG" name="新增" iconCls="create"/>  
			<action id="importContent" name="引入指导内容" iconCls="healthDoc_import"/>  
			<action id="printTCMSG" name="打印" iconCls="print"/>  
		</module>
		<module id="TCM04" name="体质分类统计" script="chis.application.tcm.script.HabitusClassifyStatisticsListView">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_HabitusClassifyStatistics</p>
				<p name="listServiceId">chis.tcmQuestionnaireService</p>  
				<p name="listAction">loadStatisticsHabitusTypeNumber</p> 
			</properties>
			<action id="print" name="打印" iconCls="print"/>
		</module>
		<module id="TCM_CRM" name="中医体质辨识" type="1" script="chis.application.tcm.script.TCMConstitutionRecognitionModule">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="TCMQTab" name="中医体质辨识" ref="chis.application.tcm.TCM/TCM/TCM_IFCQ" type="tab"/>  
			<action id="TCMRTab" name="中医登记" ref="chis.application.tcm.TCM/TCM/TCM01_01" type="tab"/>
		</module>
		<module id="TCM_IFCQ" name="中医体质辨识问卷" type="1" script="chis.application.tcm.script.TCMQuestionnaireModule">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="TCMIFCQList" name="中医体质辨识问卷列表" ref="chis.application.tcm.TCM/TCM/TCM_IFCQList" />
			<action id="TCMIFCQForm" name="中医体质辨识问卷表单" ref="chis.application.tcm.TCM/TCM/TCM_IFCQForm" />
		</module>
		<module id="TCM_IFCQList" name="中医体质辨识问卷列表" type="1" script="chis.application.tcm.script.TCMQuestionnaireList">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_IFCQResultList</p>  
			</properties>
		</module>
		<module id="TCM_IFCQForm" name="中医体质辨识问卷" type="1" script="chis.application.tcm.script.TCMQuestionnaireForm">
			<properties> 
				<p name="entryNameOP">chis.application.tcm.schemas.TCMQuestionnaireOldPeople</p>  
				<p name="entryNameUC">chis.application.tcm.schemas.TCMQuestionnaireCommonUse</p>  
				<p name="refModule">chis.application.tr.TR/TR/TR01_01</p> 
				<p name="saveServiceId">chis.tcmQuestionnaireService</p>  
				<p name="saveAction">saveTCMQ</p>
				<p name="loadServiceId">chis.tcmQuestionnaireService</p>  
				<p name="loadAction">loadTCMQ</p>  
			</properties>
			<action id="save" name="保存" group="create||update"/>  
			<action id="create" name="新增" group="create"/>  
		</module>
		<module id="TCM_GCL" name="中医指导知识库" script="chis.application.tcm.script.TCMGuidanceContentLibraryListView">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_GuidanceContentLibrary</p>
				<p name="createCls">chis.application.tcm.script.TCMGuidanceContentLibraryForm</p>  
				<p name="updateCls">chis.application.tcm.script.TCMGuidanceContentLibraryForm</p>    
			</properties>
			<action id="create" name="新增" iconCls="create"/>  
			<action id="update" name="查看" iconCls="update"/>  
			<action id="remove" name="删除"/>
		</module>
		<module id="TCM_GCLI" name="中医指导内容引入" type="1" script="chis.application.tcm.script.TCMGuidanceContentImportModule">
			<properties> 
				<p name="entryName">chis.application.tcm.schemas.TCM_GuidanceContentLibraryQuery</p> 
			</properties>
		</module>
		<module id="TCM_BTM" name="中医体质辨识及指导补录" type="1" script="chis.application.tcm.script.TCMBackTrackingModule">
			<properties>
				<p name="autoLoadData">false</p>
			</properties>
			<action id="TCMQTab" name="中医体质辨识" ref="chis.application.tcm.TCM/TCM/TCM_IFCQ" type="tab"/>  
			<action id="TCMRTab" name="中医登记" ref="chis.application.tcm.TCM/TCM/TCM01_01" type="tab"/>  
			<action id="TCMGTab" name="中医指导" ref="chis.application.tcm.TCM/TCM/TCM03_01" type="tab"/>  
		</module>
	</catagory>
</application>