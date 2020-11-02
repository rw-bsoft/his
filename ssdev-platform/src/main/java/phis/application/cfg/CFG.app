<?xml version="1.0" encoding="UTF-8"?>

<application id="phis.application.cfg.CFG" name="业务维护" type="3">
	<catagory id="CFG" name="业务维护">
		<module id="CFG2000" name="系统参数配置"
			script="phis.application.cfg.script.ConfigSystemParamsList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_XTCS</p>
				<p name="createCls">phis.application.cfg.script.SystemParameterForm
				</p>
				<p name="updateCls">phis.application.cfg.script.SystemParameterForm
				</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG200001" name="系统参数配置(新)"
			script="phis.application.cfg.script.ConfigSystemParamsList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_XTCS</p>
				<p name="createCls">phis.application.cfg.script.SystemParameterForm
				</p>
				<p name="updateCls">phis.application.cfg.script.SystemParameterForm
				</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG200002" name="系统参数分类维护" type="1"
			script="phis.application.cfg.script.ConfigSystemParamsModule">
			<properties>
				<p name="navDic">phis.dictionary.businessType</p>
				<p name="entryName">phis.application.cfg.schemas.GY_XTCS</p>
				<p name="refUsedList">phis.application.cfg.CFG/CFG/CFG20000201</p>
				<p name="refUnUsedList">phis.application.cfg.CFG/CFG/CFG20000202</p>
			</properties>
			<action id="save" name="保存" />
			<action id="close" name="关闭" />
		</module>
		<module id="CFG20000201" name="已选择系统参数" type="1"
			script="phis.application.cfg.script.ConfigSystemParamsUsedList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_XTCS</p>
			</properties>
		</module>
		<module id="CFG20000202" name="未选择系统参数" type="1"
			script="phis.application.cfg.script.ConfigSystemParamsUnUsedList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_XTCS</p>
			</properties>
		</module>
		<module id="CFG2001" name="业务权限维护"
			script="phis.application.cfg.script.ConfigPharmacyPermissionsModule">
			<properties>
				<p name="refLList">phis.application.cfg.CFG/CFG/CFG200101</p>
				<p name="refRList">phis.application.cfg.CFG/CFG/CFG200102</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CFG200101" name="药房权限维护左list" type="1"
			script="phis.application.cfg.script.ConfigPharmacyPermissionsLList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.GY_QXKZ_CFG</p>
			</properties>
		</module>
		<module id="CFG200102" name="统一权限全部用户list" type="1"
			script="phis.application.cfg.script.ConfigPharmacyPermissionsRList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.SYS_Personnel_CFG</p>
				<p name="listServiceId">configConsociateControlService</p>
				<p name="serviceAction">simpleQuery</p>
			</properties>
		</module>
		<module id="CFG21" name="业务锁维护"
			script="phis.application.cfg.script.ConfigBusinessLockList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YWLB</p>
				<p name="createCls">phis.application.cfg.script.ConfigBusinessLockForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigBusinessLockForm
				</p>
			</properties>
			<!-- <action id="create" name="新增" /> -->
			<action id="update" name="修改" />
			<!--<action id="remove" name="删除" /> -->
		</module>
		<module id="CFG22" name="业务锁管理"
			script="phis.application.cfg.script.ConfigBusinessLockMgmtList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SDJL_CFG</p>
			</properties>
			<action id="remove" name="注销" />
		</module>
		<module id="CFG90" name="抗生素权限维护" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.SYS_Personnel</p>
				<p name="initCnd">['eq',['$','a.ORGANIZCODE'],['$','%user.manageUnit.Ref']]
				</p>
			</properties>
			<action id="update" name="抗生素权限" iconCls="antibiotics"
				ref="phis.application.cfg.CFG/CFG/CFG9001" />
		</module>
		<module id="CFG9001" name="抗生素权限" type="1"
			script="phis.application.cfg.script.ConfigAntibioticsPermissionsEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_KSQX</p>
				<p name="showButtonOnTop">true</p>
				<p name="closeAction">hide</p>
				<p name="serviceId">configAntibioticsPermissionsService</p>
				<p name="serviceActionSave">saveAntibioticsPermissions</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG91" name="输入法切换"
			script="phis.application.cfg.script.ConfigHibitsForm">
			<properties>
				<p name="showWinOnly">true</p>
				<p name="entryName">phis.application.cfg.schemas.SYS_UserHabits</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<!--<module id="COL010102" name="费用对照" script="phis.application.cfg.script.FeeCollateModule"> 
			<properties> <p name="refFeeList">phis.application.cfg.CFG/CFG/COL01010201</p> 
			<p name="refFYBCollate">phis.application.cfg.CFG/CFG/COL01010202</p> </properties> 
			<action id="refresh" name="刷新"></action> <action id ="update" name="更新"></action> 
			</module> <module id="COL01010201" name="费用对照LIST" script="phis.application.cfg.script.FeeCollateList" 
			type="1"> <properties> <p name="listServiceId">medicareService</p> <p name="serviceAction">queryFeeCollateList</p> 
			<p name="entryName">phis.application.cfg.schemas.YB_FEE_LIST</p> </properties> 
			</module> <module id="COL01010202" name="费用与医保对照模块" script="phis.application.cfg.script.FeeYBCollateModule" 
			type="1"> <properties> <p name="refCYBCForm">phis.application.cfg.CFG/CFG/COL0101020202</p> 
			<p name="refCYBCTimeList">phis.application.cfg.CFG/CFG/COL0101020201</p> 
			<p name="refCYBCList">phis.application.cfg.CFG/CFG/COL0101020203</p> </properties> 
			<action id="save" name="保存"></action> <action id ="close" name="退出"></action> 
			<action id ="update" name="查看更新"></action> <action id ="create" name="生成拼音码"></action> 
			</module> <module id="COL0101020201" name="费用与医保对照时间List" script="phis.application.cfg.script.FeeYBCollateTimeList" 
			type="1"> <properties> <p name="entryName">phis.application.cfg.schemas.YB_FYDZ_TIME</p> 
			</properties> <action id="create" name="增加"></action> <action id ="remove" 
			name="删除"></action> </module> <module id="COL0101020202" name="费用与医保对照FORM" 
			script="phis.application.cfg.script.FeeYBCollateForm" type="1"> <properties> 
			<p name="entryName">phis.application.cfg.schemas.YB_KA03_FORM</p> <p name="colCount">3</p> 
			</properties> </module> <module id="COL0101020203" name="费用与医保对照List" script="phis.application.cfg.script.FeeYBCollateList" 
			type="1"> <properties> <p name="entryName">phis.application.cfg.schemas.YB_KA03</p> 
			</properties> </module> -->

		<module id="CFG01" name="疾病编码维护" script="phis.script.TabModule">
			<action id="westjbbm" viewType="list" name="西医疾病编码维护"
				ref="phis.application.cfg.CFG/CFG/CFG0101" />
			<action id="chineseDisease" viewType="list" name="中医疾病编码维护"
				ref="phis.application.cfg.CFG/CFG/CFG0102" />
			<action id="chineseSymptom" viewType="list" name="中医证侯编码维护"
				ref="phis.application.cfg.CFG/CFG/CFG0103" />
		</module>
		<module id="CFG0101" name="西医疾病编码维护" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_JBBM</p>
				<p name="removeByFiled">JBMC</p>
				<p name="createCls">phis.application.cfg.script.ConfigDiseaseCodingForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigDiseaseCodingForm</p>
				<p name="removeByFiled">ICD10</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG0102" name="中医疾病编码维护" type="1"
			script="phis.application.cfg.script.ConfigChineseDiseaseModule">
			<properties>
				<p name="navDic">phis.dictionary.ZYJBFL_tree</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG010201</p>
			</properties>
		</module>
		<module id="CFG010201" name="中医疾病编码维护" type="1"
			script="phis.application.cfg.script.ConfigChineseDiseaseList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYJB</p>
				<p name="removeByFiled">JBMC</p>
				<p name="createCls">phis.application.cfg.script.ConfigDiseaseCodingZYJBForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigDiseaseCodingZYJBForm
				</p>
				<p name="unionRef">phis.application.cfg.CFG/CFG/CFG010202</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="union" name="添加关联" iconCls="merge" />
		</module>
		<module id="CFG010202" name="中医疾病编码维护" type="1"
			script="phis.application.cfg.script.ConfigChineseDiseaseEditorList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_JBZZ</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
			<action id="close" name="关闭" />
		</module>
		<module id="CFG0103" name="中医证侯编码维护" type="1"
			script="phis.application.cfg.script.ConfigChineseSymptomModule">
			<properties>
				<p name="navDic">phis.dictionary.ZYZHFL_tree</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG010301</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG010301" name="中医证侯编码维护" type="1"
			script="phis.application.cfg.script.ConfigChineseSymptomList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_ZYZH</p>
				<p name="removeByFiled">ZHMC</p>
				<p name="createCls">phis.application.cfg.script.ConfigDiseaseCodingZYZHForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigDiseaseCodingZYZHForm
				</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>

		<module id="CFG02" name="给药途径维护" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.ZY_YPYF</p>
				<p name="createCls">phis.application.cfg.script.ConfigDoseRoadForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigDoseRoadForm</p>
				<p name="removeByFiled">XMMC</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG03" name="给药频次维护"
			script="phis.application.cfg.script.ConfigDoseFrequencyList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SYPC</p>
				<p name="exectimeRef">phis.application.cfg.CFG/CFG/CFG0302</p>
				<p name="frequencyRef">phis.application.cfg.CFG/CFG/CFG0303</p>
				<p name="frequencyNORef">phis.application.cfg.CFG/CFG/CFG0304</p>
				<p name="removeByFiled">PCMC</p>
				<p name="cnds">['eq',['$','a.JGID'],["$",'%user.manageUnit.id']]</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="removeFrequency" name="删除" iconCls="remove" />
			<action id="commit" name="保存" iconCls="save" />
			<action id="exectime" name="执行时间" iconCls="clock_go" />
			<action id="frequency" name="频次周率" iconCls="clock_link" />
		</module>
		<module id="CFG0302" name="执行时间维护" type="1"
			script="phis.application.cfg.script.ConfigDoseTimeCycleForm">
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG0303" name="频次周率维护" type="1"
			script="phis.application.cfg.script.ConfigDoseFrequencyCycleForm">
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG0304" name="频次周率维护" type="1"
			script="phis.application.cfg.script.ConfigDoseFrequencyNOCycleForm">
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="CFG0301" name="发药时间维护" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SYPC</p>
			</properties>
		</module>


		<module id="CFG04" name="病人性质维护"
			script="phis.application.cfg.script.ConfigPatientPropertiesModule">
			<properties>
				<p name="navDic">phis.dictionary.patientProperties_tree</p>
				<p name="navParentKey">-2</p>
				<p name="entryName">phis.application.cfg.schemas.GY_BRXZ</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG0402</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG0401" name="病人性质form" type="1"
			script="phis.application.cfg.script.ConfigPatientPropertiesForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_BRXZ</p>
				<p name="serviceId">configCostService</p>
				<p name="saveServiceAction">saveCostDetail</p>
			</properties>
		</module>
		<module id="CFG0402" name="病人性质" type="1"
			script="phis.application.cfg.script.ConfigPatientPropertiesTabModule">
			<action id="conceitProportionTab" viewType="list" name="自负比例"
				ref="phis.application.cfg.CFG/CFG/CFG040201" />
			<action id="drugLimitTab" viewType="list" name="药品限制"
				ref="phis.application.cfg.CFG/CFG/CFG040202" />
			<action id="costLimitTab" viewType="list" name="费用限制"
				ref="phis.application.cfg.CFG/CFG/CFG040203" />
		</module>
		<module id="CFG040201" name="自负比例" type="1"
			script="phis.application.cfg.script.ConfigConceitProportionEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_ZFBL</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CFG040202" name="药品限制" type="1"
			script="phis.application.cfg.script.ConfigDrugLimitEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YPJY_BRXZ</p>
			</properties>
			<action id="insert" name="新增" iconCls="create" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>
		<module id="CFG040203" name="费用限制" type="1"
			script="phis.application.cfg.script.ConfigCostLimitEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FYJY_BRXZ</p>
			</properties>
			<action id="insert" name="新增" iconCls="create" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>

		<module id="CFG05" name="收费项目维护" script="phis.script.TabModule">
			<properties>
			</properties>
			<action id="medicalProjectTab" viewType="list" name="医疗项目"
				ref="phis.application.cfg.CFG/CFG/CFG0501" />
			<action id="drugProjectTab" viewType="list" name="药品项目"
				ref="phis.application.cfg.CFG/CFG/CFG0502" />
			<action id="otherProjectTab" viewType="list" name="其他项目"
				ref="phis.application.cfg.CFG/CFG/CFG0503" />
			<action id="projectDetailTab" viewType="list" name="项目明细"
				ref="phis.application.cfg.CFG/CFG/CFG0506" />
		</module>
		<module id="CFG0501" name="医疗项目" type="1"
			script="phis.application.cfg.script.ConfigMedicalProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM_XG</p>
				<p name="serviceId">configChargingProjectsDelService</p>
				<p name="serviceAction">removeChpingProjects</p>
				<p name="createCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="removeByFiled">SFMC</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="update" name="修改" />
			<action id="detail" name="明细" iconCls="detail"
				ref="phis.application.cfg.CFG/CFG/CFG0504" />
			<action id="merge" name="项目归并" iconCls="merge"
				ref="phis.application.cfg.CFG/CFG/CFG0505" />
		</module>

		<module id="CFG0502" name="药品项目" type="1"
			script="phis.application.cfg.script.ConfigDrugProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM_YPLX</p>
				<p name="createCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="removeByFiled">SFMC</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="update" name="修改" />
			<action id="detail" name="明细" iconCls="detail"
				ref="phis.application.cfg.CFG/CFG/CFG0504" />
			<action id="merge" name="项目归并" iconCls="merge"
				ref="phis.application.cfg.CFG/CFG/CFG0505" />
		</module>
		<module id="CFG0503" name="其他项目" type="1"
			script="phis.application.cfg.script.ConfigOtherProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM_XG</p>
				<p name="createCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigChargingProjectsForm
				</p>
				<p name="removeByFiled">SFMC</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="update" name="修改" />
			<action id="detail" name="明细" iconCls="detail"
				ref="phis.application.cfg.CFG/CFG/CFG0504" />
			<action id="merge" name="项目归并" iconCls="merge"
				ref="phis.application.cfg.CFG/CFG/CFG0505" />
		</module>
		<module id="CFG0506" name="项目明细" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF</p>
			</properties>
		</module>
		<module id="CFG0505" name="项目归并" type="1"
			script="phis.application.cfg.script.ConfigProjectsMergeForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM_GB</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG0504" name="收费项目明细" type="1"
			script="phis.application.cfg.script.ConfigChargingProjectsDetailList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF</p>
				<p name="serviceId">configChargingProjectsDetailDelService</p>
				<p name="serviceAction">removeChargingProjectsDetail</p>
				<p name="showButtonOnTop">true</p>
				<p name="removeByFiled">FYMC</p>
				<p name="closeAction">hide</p>
				<!--<p name="createCls">phis.application.cfg.script.ConfigChpingProjectsDetailForm 
					</p> <p name="updateCls">phis.application.cfg.script.ConfigChpingProjectsDetailForm 
					</p> -->
			</properties>
			<action id="create" name="新增"
				ref="phis.application.cfg.CFG/CFG/CFG050401" iconCls="add" />
			<action id="update" name="修改"
				ref="phis.application.cfg.CFG/CFG/CFG050401" />
			<!--<action id="remove" name="删除" /> -->
			<action id="print" name="打印" />
			<action id="logOut" name="作废" iconCls="writeoff" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG050401" name="收费项目明细" type="1"
			script="phis.application.cfg.script.ConfigChargingProjectsDetailModule">
			<properties>
				<p name="serviceId">configCostService</p>
				<p name="saveServiceAction">saveCostDetail</p>
			</properties>
			<action id="cfgproptab" viewType="form" name="费用属性"
				ref="phis.application.cfg.CFG/CFG/CFG05040101" />
			<action id="cfgaliastab" viewType="list" name="费用别名"
				ref="phis.application.cfg.CFG/CFG/CFG05040102" />
			<action id="cfglimittab" viewType="editlist" name="费用限制"
				ref="phis.application.cfg.CFG/CFG/CFG05040103" />
		</module>
		<module id="CFG05040101" name="费用属性" type="1"
			script="phis.application.cfg.script.ConfigChargingProjectsDetailForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF</p>
				<p name="fldDefaultWidth">100</p>
			</properties>
		</module>
		<module id="CFG05040102" name="费用别名" type="1"
			script="phis.application.cfg.script.ConfigProjectsAliasEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FYBM</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG05040103" name="费用限制" type="1"
			script="phis.application.cfg.script.ConfigCostEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FYJY_SFXMWH</p>
				<p name="autoLoadData">true</p>
				<p name="modal">false</p>
				<p name="showButtonOnTop">true</p>
			</properties>
		</module>

		<module id="CFG06" name="付款类别维护"
			script="phis.application.cfg.script.ConfigPaymentCategoryList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FKLB</p>
				<p name="removeByFiled">LBMC</p>
				<p name="createCls">phis.application.cfg.script.ConfigPaymentCategoryForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigPaymentCategoryForm</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG07" name="付款方式维护" script="phis.script.TabModule">
			<action id="ConfigPaymentTypeTab" viewType="list" name="门诊使用"
				ref="phis.application.cfg.CFG/CFG/CFG0701" />
			<action id="HosPaymentTypeTab" viewType="list" name="住院使用"
				ref="phis.application.cfg.CFG/CFG/CFG0702" />
		</module>
		<module id="CFG0701" name="门诊使用" type="1"
			script="phis.application.cfg.script.ConfigPaymentTypeList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FKFS_MZ</p>
				<p name="initCnd">['eq',['$','SYLX'],['$','1']]</p>
				<p name="createCls">phis.application.cfg.script.ConfigPaymentWayForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigPaymentWayForm</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="logOut" name="作废" iconCls="writeoff" />
			<action id="default" name="默认" iconCls="default" />
			<action id="mError" name="货币误差" iconCls="coins" />
		</module>
		<module id="CFG0702" name="住院使用" type="1"
			script="phis.application.cfg.script.ConfigPaymentTypeList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FKFS_ZY</p>
				<p name="initCnd">['eq',['$','SYLX'],['$','2']]</p>
				<p name="createCls">phis.application.cfg.script.ConfigPaymentWayForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigPaymentWayForm</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="logOut" name="作废" iconCls="writeoff" />
			<action id="default" name="默认" iconCls="default" />
			<action id="mError" name="货币误差" iconCls="coins" />
		</module>

		<module id="CFG08" name="机构收费项目维护" script="phis.script.TabModule">
			<properties>
			</properties>
			<action id="medicalProject" viewType="list" name="医疗项目"
				ref="phis.application.cfg.CFG/CFG/CFG0801" />
			<action id="drugProject" viewType="list" name="药品项目"
				ref="phis.application.cfg.CFG/CFG/CFG0802" />
			<action id="otherProject" viewType="list" name="其他项目"
				ref="phis.application.cfg.CFG/CFG/CFG0803" />
		</module>
		<module id="CFG0801" name="医疗项目" type="1"
			script="phis.application.cfg.script.ConfigOrganMedicalProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM</p>
			</properties>
			<action id="detail" name="明细" iconCls="default"
				ref="phis.application.cfg.CFG/CFG/CFG0804" />
			<action id="callInAll" name="全部调入" iconCls="ransferred_all" />
		</module>
		<module id="CFG0802" name="药品项目" type="1"
			script="phis.application.cfg.script.ConfigOrganDrugProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM</p>
			</properties>
			<action id="detail" name="明细" iconCls="default"
				ref="phis.application.cfg.CFG/CFG/CFG0804" />
			<action id="callInAll" name="全部调入" iconCls="ransferred_all" />
		</module>
		<module id="CFG0803" name="其他项目" type="1"
			script="phis.application.cfg.script.ConfigOrganOtherProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_SFXM</p>
			</properties>
			<action id="detail" name="明细" iconCls="default"
				ref="phis.application.cfg.CFG/CFG/CFG0804" />
			<action id="callInAll" name="全部调入" iconCls="ransferred_all" />
		</module>
		<module id="CFG0804" name="收费项目明细" type="1"
			script="phis.application.cfg.script.ConfigProjectsDetailList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLMX_DR</p>
				<p name="showButtonOnTop">true</p>
				<p name="updateCls">phis.application.cfg.script.ConfigProjectsDetailForm
				</p>
			</properties>
			<!--<action id="callInAll" name="全部调入" iconCls="ransferred_all"/> -->
			<action id="callIn" name="调入" iconCls="ransferred" />
			<action id="update" name="修改" />
			<action id="logout" name="作废" iconCls="writeoff" />
			<action id="print" name="打印" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG080401" name="调入页面" type="1"
			script="phis.application.cfg.script.ConfigProjectsCallinList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF_DR</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="调入" iconCls="ransferred_all" />
			<action id="exit" name="关闭" iconCls="common_cancel" />
		</module>


		<module id="CFG09" name="附加项目维护"
			script="phis.application.cfg.script.WardAddProjectsModule">
			<properties>
				<p name="refYlxmList">phis.application.cfg.CFG/CFG/CFG0901</p>
				<p name="refFjxmList">phis.application.cfg.CFG/CFG/CFG0902</p>
			</properties>
		</module>
		<module id="CFG0901" name="医疗项目" type="1" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF_DZ</p>
				<p name="initCnd">['and',['and',['and',['eq',['$','a.ZFPB'],["i",0]],['eq',['$','c.ZFPB'],["i",0]]],['eq',['$','a.ZYSY'],["i",1]]],['eq',['$','c.JGID'],["$",'%user.manageUnit.id']]]
				</p>
				<p name="cnds">['and',['and',['and',['eq',['$','a.ZFPB'],["i",0]],['eq',['$','c.ZFPB'],["i",0]]],['eq',['$','a.ZYSY'],["i",1]]],['eq',['$','c.JGID'],["$",'%user.manageUnit.id']]]
				</p>
			</properties>
		</module>
		<module id="CFG0902" name="附加项目" type="1"
			script="phis.application.cfg.script.WardAppendProjectList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_XMGL</p>
			</properties>
			<action id="create" name="新增" iconCls="add" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>

		<module id="CFG10" name="系统初始化"
			script="phis.application.cfg.script.SystemInitializationModule">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_GNJ</p>
				<p name="showWinOnly">true</p>
			</properties>
		</module>
		<module id="CFG11" name="常用组套维护"
			script="phis.application.cfg.script.AdvicePersonalComboModule">
			<properties>
				<p name="openBy">office</p>
				<p name="refComboNameList">phis.application.cfg.CFG/CFG/CFG1101</p>
				<p name="refComboNameDetailList">phis.application.cfg.CFG/CFG/CFG1102</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">saveCommonlyUsedDrugs</p>
				<p name="cnds">2</p>
			</properties>
			<action id="westernDrug" name="西药" value="1" />
			<action id="ChineseDrug" name="成药" value="2" />
			<action id="herbs" name="草药" value="3" />
			<action id="others" name="项目" value="4" />
			<action id="mix" name="文嘱" value="5" />
			<action id="common" name="常用" value="5" />
			<action id="sets" name="组套" value="2" />
		</module>
		<module id="CFG1101" name="组套名称" type="1"
			script="phis.application.cfg.script.AdvicePersonalComboNameList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
				<p name="closeAction">true</p>
				<p name="removeByFiled">ZTMC</p>
				<p name="queryWidth">80</p>
				<p name="addRef">phis.application.cfg.CFG/CFG/CFG110101</p>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceAction">updatePrescriptionStack</p>
				<p name="serviceActionDel">removePrescriptionDel</p>
				<p name="updateCls">phis.application.cfg.script.AdviceComboNameForm</p>
				<p name="autoLoadData">0</p><!--关闭默认加载 -->
				<p name="cnds">2</p>
			</properties>
			<action id="add" name="新增" iconCls="new" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="CFG110101" name="组套-新增" type="1"
			script="phis.application.cfg.script.AdviceComboNameForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.BQ_ZT01</p>
			</properties>
			<action id="new" name="新增" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG1102" name="组套明细" type="1"
			script="phis.application.cfg.script.AdvicePersonalComboNameDetailList">
			<properties>
				<p name="serviceId">clinicComboService</p>
				<p name="serviceActionSave">savePrescriptionDetails</p>
				<p name="serviceActionDel">removePrescriptionDetails</p>
			</properties>
			<action id="insert" name="插入" iconCls="insertgroup" />
			<action id="newGroup" name="新组" iconCls="newgroup" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>

		<module id="CFG12" name="病历权限维护"
			script="phis.application.cfg.script.CaseHistoryControlModule">
			<properties>
				<p name="refLList">phis.application.cfg.CFG/CFG/CFG1201</p>
				<p name="refRList">phis.application.cfg.CFG/CFG/CFG1202</p>
			</properties>
		</module>
		<module id="CFG1201" name="医疗角色" type="1"
			script="phis.application.cfg.script.CaseHistoryControlLList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YLJS</p>
			</properties>
			<action id="create" name="增加"
				ref="phis.application.cfg.CFG/CFG/CFG120101" />
			<action id="update" name="修改"
				ref="phis.application.cfg.CFG/CFG/CFG120101" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG120101" name="医疗角色from" type="1"
			script="phis.application.cfg.script.CaseDoctorRoleForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YLJS</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG1202" name="角色病历权限右list" type="1"
			script="phis.application.cfg.script.CaseHistoryControlRList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YLJSBLQX</p>
			</properties>
			<action id="saveContory" name="保存" iconCls="save" />
		</module>
		<module id="CFG13" name="病历强制解锁"
			script="phis.application.cfg.script.EMRForcedUnlockList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_BLSD</p>
				<p name="refUnlockRecord">phis.application.cfg.CFG/CFG/CFG1301</p>
			</properties>
			<action id="forcedUnlock" name="强制解锁" iconCls="lock" />
			<action id="unlockRecord" name="解锁记录" iconCls="unlock" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG1301" name="强制解锁记录" type="1"
			script="phis.application.cfg.script.EMRForcedUnlockRecordList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_QZJSJL</p>
			</properties>
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG14" name="医学表达式维护"
			script="phis.application.cfg.script.MedicalExpressionModule">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YXBDS_DY</p>
				<p name="refExpList">phis.application.cfg.CFG/CFG/CFG1401</p>
			</properties>
		</module>
		<module id="CFG1401" name="医学表达式列表" type="1"
			script="phis.application.cfg.script.MedicalExpressionList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YXBDS_DY</p>
			</properties>
			<action id="create" name="新增"
				ref="phis.application.cfg.CFG/CFG/CFG1402" />
			<action id="update" name="修改属性"
				ref="phis.application.cfg.CFG/CFG/CFG1402" />
			<action id="editExp" name="编辑图片" iconCls="update" />
			<action id="logout" name="注销" iconCls="writeoff" />
		</module>
		<module id="CFG1402" name="医学表达式" type="1"
			script="phis.application.cfg.script.MedicalExpressionForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_YXBDS_DY</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="CFG15" name="手术内码维护" script="phis.application.cfg.script.ConfigOPSCodingList">
			<properties>
				<p name="removeByFiled">SSDM</p>
				<p name="entryName">phis.application.cic.schemas.GY_SSDM_WH</p>
				<p name="createCls">phis.application.cfg.script.ConfigOPSCodingForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigOPSCodingForm</p>
			</properties>
<!--			<action id="create" name="新增" />-->
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="lsjdr" name="引入" iconCls="ransferred_all" />
		</module>

		<module id="CFG1501" name="手术内码引入"  type="1" script="phis.application.cfg.script.LsjdrList">
			<properties>
				<p name="entryName">phis.application.cic.schemas.GY_SSDM_YR</p>
			</properties>
		</module>

		<module id="CFG50" name="账簿类别维护"
			script="phis.application.cfg.script.ConfigBooksCategoryList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_ZBLB</p>
				<p name="serviceId">configBooksCategoryService</p>
				<p name="serviceAction">updateConfigBooksCategory</p>
				<p name="createCls">phis.application.cfg.script.ConfigBooksCategoryForm</p>
				<p name="updateCls">phis.application.cfg.script.ConfigBooksCategoryForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>
		<module id="CFG51" name="核算类别维护"
			script="phis.application.cfg.script.ConfigAccountingCategoryModule">
			<properties>
				<p name="navDic">phis.dictionary.AccountingCategory_tree</p>
				<p name="navParentKey">-1</p>
				<p name="entryName">phis.application.cfg.schemas.WL_HSLB</p>
				<p name="winState" type="jo">{'pos':[50,50]}</p>
				<p name="refForm">phis.application.cfg.CFG/CFG/CFG5101</p>
				<p name="serviceId">configAccountingCategoryModuleService</p>
			</properties>
			<action id="create" name="新增" />
			<action id="save" name="保存" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG5101" name="核算类别信息" type="1"
			script="phis.application.cfg.script.ConfigAccountingCategoryFrom">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_HSLB_ZBLB</p>
			</properties>
			<!--<action id="reSet" name="重置" iconCls="new"/> <action id="save" name="保存"/> -->
		</module>
		<module id="CFG52" name="供货单位维护"
			script="phis.application.cfg.script.ConfigSupplyUnitList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_GHDW</p>
				<p name="serviceId">phis.configSupplyUnitService</p>
			</properties>
			<action id="create" name="新增"
				ref="phis.application.cfg.CFG/CFG/CFG5201" />
			<action id="update" name="修改"
				ref="phis.application.cfg.CFG/CFG/CFG5201" />
			<action id="execute" name="注销" iconCls="writeoff" />
		</module>
		<module id="CFG5201" name="供货单位维护module" type="1"
			script="phis.application.cfg.script.ConfigSupplyUnitModule">
			<action id="baseinfomationtab" viewType="form" name="基本信息"
				ref="phis.application.cfg.CFG/CFG/CFG520101" />
			<action id="certificateinftab" viewType="list" name="证件信息"
				ref="phis.application.cfg.CFG/CFG/CFG520102" />
		</module>
		<module id="CFG520101" name="基本信息" type="1"
			script="phis.application.cfg.script.ConfigSupplyUnitForm">
		</module>
		<module id="CFG520102" name="证件信息" type="1"
			script="phis.application.cfg.script.ConfigSupplyUnitCertificateForm">
			<action id="create" name="新增证件" />
			<action id="remove" name="删除证件" />
			<action id="editPhoto" name="编辑图片" iconCls="update" />
			<action id="removePhoto" name="删除图片" iconCls="remove" />
		</module>
		<module id="CFG520103" name="编辑图片" type="1"
			script="phis.application.cfg.script.ConfigSupplyUnitCertificatePhotoForm">
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG53" name="生产厂家维护"
			script="phis.application.cfg.script.ManufacturerForWZList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_SCCJ</p>
			</properties>
			<action id="create" name="新增"
				ref="phis.application.cfg.CFG/CFG/CFG5301"></action>
			<action id="update" name="修改"
				ref="phis.application.cfg.CFG/CFG/CFG5301"></action>
			<action id="canceled" name="注销" iconCls="writeoff"></action>
			<action id="refresh" name="刷新"></action>
		</module>
		<module id="CFG5301" name="生产厂家——增加" type="1"
			script="phis.application.cfg.script.ManufacturerForWZModule">
			<properties>
				<p name="serviceId">configManufacturerForWZService</p>
				<p name="saveServiceAction">saveOperatForManufacturer</p>
			</properties>
			<action id="baseMsgtab" viewType="form" name="基本信息"
				ref="phis.application.cfg.CFG/CFG/CFG530101" />
			<action id="aliasMsgtab" viewType="priceList" name="别名信息"
				ref="phis.application.cfg.CFG/CFG/CFG530102" />
			<action id="cardtab" viewType="editlist" name="证件信息"
				ref="phis.application.cfg.CFG/CFG/CFG530104" />
			<action id="viewMsgtab" viewType="list" name="查看物资"
				ref="phis.application.cfg.CFG/CFG/CFG530103" />
		</module>
		<module id="CFG530101" name="基本信息" type="1"
			script="phis.application.cfg.script.ManufacturerForWZForm">
		</module>
		<module id="CFG530102" name="厂家别名" type="1"
			script="phis.application.cfg.script.ManufacturerNameEditorList">
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG530104" name="证件信息" type="1"
			script="phis.application.cfg.script.CredentialsMsgModule">
		</module>
		<module id="CFG53010401" name="证件信息列表" type="1"
			script="phis.application.cfg.script.CredentialsMsgEditorList">
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
			<action id="uploadImage" name="上传图片" iconCls="update" />
			<action id="removeImage" name="删除图片" iconCls="remove" />
		</module>
		<module id="CFG53010402" name="物资信息" type="1"
			script="phis.application.cfg.script.SubstancesSelectList">
			<action id="compar" name="对照" iconCls="update" />
		</module>
		<module id="CFG530103" name="查看物资" type="1"
			script="phis.application.cfg.script.SubstancesViewModule">
		</module>
		<module id="CFG53010301" name="物资信息" type="1"
			script="phis.application.cfg.script.SubstancesViewList">
		</module>
		<module id="CFG53010302" name="证件信息" type="1"
			script="phis.application.cfg.script.CredentialsViewList">
		</module>
		<module id="CFG5301040101" name="编辑图片" type="1"
			script="phis.application.cfg.script.ConfigSupplyCertificatePhotoForm">
			<properties>
				<p name="entryName">phis.application.cfg.WL_PHOTO</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG54" name="医院库房维护"
			script="phis.application.cfg.script.ConfigTreasuryInformationList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_KFXX</p>
				<p name="serviceId">configTreasuryInformationService</p>
				<p name="serviceAction">updateConfigTreasuryInformation</p>
				<p name="createCls">phis.application.cfg.script.ConfigTreasuryInformationForm
				</p>
				<p name="updateCls">phis.application.cfg.script.ConfigTreasuryInformationForm
				</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="execute" name="启用" iconCls="commit" />
			<action id="initialize" name="初始化" iconCls="commit" />
		</module>
		<module id="CFG55" name="分类类别维护"
			script="phis.application.cfg.script.ConfigClassifyModule">
			<properties>
				<p name="refClassifyComboList">phis.application.cfg.CFG/CFG/CFG5501</p>
				<p name="refClassifyComboDetailList">phis.application.cfg.CFG/CFG/CFG5502</p>
				<p name="serviceId">configClassifyService</p>
			</properties>
		</module>
		<module id="CFG5501" name="类别名称" type="1"
			script="phis.application.cfg.script.ConfigClassifyComboList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_FLLB</p>
				<p name="serviceId">configClassifyService</p>
			</properties>
			<action id="create" name="新增" iconCls="new"
				ref="phis.application.cfg.CFG/CFG/CFG550101" />
			<action id="execute" name="启用" iconCls="commit" />
		</module>

		<module id="CFG550101" name="类别名称" type="1"
			script="phis.application.cfg.script.ConfigClassifyCombolistSaveForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_FLLB</p>
				<p name="width">280</p>
				<p name="colCount">1</p>
				<p name="serviceId">configClassifyService</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>

		<module id="CFG5502" name="类别明细" type="1"
			script="phis.application.cfg.script.ConfigClassifyComboDetailList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_FLGZ</p>
				<p name="serviceId">configClassifyService</p>
			</properties>
			<action id="insert" name="新增" iconCls="new" />
			<action id="remove" name="删除" />
			<action id="save" name="保存" />
		</module>
		<module id="CFG56" name="物资分类维护"
			script="phis.application.cfg.script.SubstancesClassModule">
			<properties>
				<p name="navDic">phis.dictionary.materialInformation_tree</p>
				<p name="navParentKey">-1</p>
				<p name="entryName">phis.application.cfg.schemas.WL_FLZD</p>
				<p name="winState" type="jo">{'pos':[50,50]}</p>
				<p name="refCategoriesGoodsModule">phis.application.cfg.CFG/CFG/CFG5602</p>
				<p name="addRef">phis.application.cfg.CFG/CFG/CFG560101</p>
			</properties>
			<action id="add" name="新增" />
			<action id="xg" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG560101" name="物资分类类别-新增" type="1"
			script="phis.application.cfg.script.SubstancesClassForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_FLZD</p>
			</properties>
			<action id="new" name="新增" />
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG5602" name="物资分类" type="1"
			script="phis.application.cfg.script.SubstancesModule">
			<properties>
				<p name="refClassList">phis.application.cfg.CFG/CFG/CFG560201</p>
				<p name="refNoClassList">phis.application.cfg.CFG/CFG/CFG560202
				</p>
			</properties>
		</module>
		<module id="CFG560201" name="已分类物质" type="1"
			script="phis.application.cfg.script.AlreadyClassList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_FLWH</p>
				<p name="autoLoadData">1</p>
				--><!--关闭默认加载 -->
			</properties>
			<action id="updateStage" name="保存" iconCls="save" />
		</module>
		<module id="CFG560202" name="未分类物质" type="1"
			script="phis.application.cfg.script.AlreadyNoClassList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD</p>
				<p name="autoLoadData">1</p>
			</properties>
		</module>
		<module id="CFG57" name="物资信息管理"
			script="phis.application.cfg.script.MaterialInformationManagementModule">
			<properties>
				<p name="navDic">phis.dictionary.materialInformation_tree</p>
				<p name="navParentKey">-1</p>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_WH</p>
				<p name="serviceId">materialInformationManagement</p>
				<p name="refModule">phis.application.cfg.CFG/CFG/CFG5701</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="invalid" name="注销" iconCls="writeoff" />
			<!--<action id="save" name="保存" /> -->
			<!--<action id="remove" name="删除" /> -->
			<!--<action id="copy" name="复制" iconCls=""/> -->
			<action id="introduce" name="引入" iconCls="ransferred_all" />
		</module>
		<module id="CFG5702" name="引入页面" type="1"
			script="phis.application.cfg.script.MaterialIntroduceList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_YR</p>
				<p name="mutiSelect">true</p>
				<p name="serviceId">materialInformationManagement</p>
			</properties>
			<action id="callIn" name="引入" iconCls="ransferred_all" />
			<action id="exit" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG5703" name="高低储" type="1"
			script="phis.application.cfg.script.MaterialGDCInfoTab">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_GDC</p>
			</properties>
			<action id="save" name="保存" />
			<action id="cancel" name="关闭" iconCls="close" />
		</module>
		<module id="CFG5701" name="物资信息维护" type="1"
			script="phis.application.cfg.script.MaterialInformationTabs">
			<properties>
				<p name="serviceId">materialInformationManagement</p>
			</properties>
			<action id="baseInfoTab" viewType="form" type="tab" name="基本信息"
				ref="phis.application.cfg.CFG/CFG/CFG570101" />
			<action id="manufacturerTab" viewType="form" type="tab" name="生产厂家"
				ref="phis.application.cfg.CFG/CFG/CFG570102" />
			<action id="itemAliasTab" viewType="list" type="tab" name="物品别名"
				ref="phis.application.cfg.CFG/CFG/CFG570103" />
		</module>
		<module id="CFG570101" name="基本信息" type="1"
			script="phis.application.cfg.script.MaterialBaseInfoTab">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_JBXX</p>
				<p name="serviceId">materialInformationManagement</p>
			</properties>
		</module>
		<module id="CFG570102" name="生产厂家" type="1"
			script="phis.application.cfg.script.MaterialManufacturerTab">
			<properties>
				<p name="refCJXXList">phis.application.cfg.CFG/CFG/CFG57010201</p>
				<p name="refCJZJXXList">phis.application.cfg.CFG/CFG/CFG57010202</p>
			</properties>
		</module>
		<module id="CFG57010201" name="生产厂家信息" type="1"
			script="phis.application.cfg.script.ManufacturerList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZCJ_WZZD</p>
				<p name="serviceId">materialInformationManagement</p>
			</properties>
			<action id="create" name="新增" />
			<!-- <action id="invalid" name="注销" iconCls="writeoff" /> -->
			<action id="remove" name="删除" />
		</module>
		<module id="CFG57010202" name="厂家证件信息" type="1"
			script="phis.application.cfg.script.ManufacturersdocumentsList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_ZJXX</p>
			</properties>
		</module>
		<module id="CFG570103" name="物品别名" type="1"
			script="phis.application.cfg.script.MateriaIitemAliasTab">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZBM</p>
			</properties>
			<action id="create" name="新增"
				ref="phis.application.cfg.CFG/CFG/CFG5701" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG5701020211" name="厂家证件信息 " type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_JBXX</p>
			</properties>
			<action id="create" name="新增" />
			<action id="invalid" name="注销" iconCls="writeoff" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG58" name="物资信息管理(二级)"
			script="phis.application.cfg.script.EjkfIntroduceList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_EJJK</p>
			</properties>
			<action id="introduce" name="引入" iconCls="ransferred_all" />
			<action id="save" name="保存" iconCls="save" />
			<action id="invalid" name="注销" iconCls="writeoff" />
			<!--<action id="remove" name="删除" /> -->
		</module>
		<module id="CFG5801" name="引入页面" type="1"
			script="phis.application.cfg.script.MaterialEjkfIntroduceList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_EJYR</p>
				<p name="mutiSelect">true</p>
			</properties>
			<action id="callIn" name="引入" iconCls="ransferred_all" />
			<action id="exit" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG59" name="科室权限维护"
			script="phis.application.cfg.script.ConfigDepartmentModule">
			<properties>
				<p name="refHsqxYgList">phis.application.cfg.CFG/CFG/CFG5901</p>
				<p name="refHsqxKsListList">phis.application.cfg.CFG/CFG/CFG5902</p>
				<p name="refDepartmentList">phis.application.cfg.CFG/CFG/CFG5903</p>
			</properties>
		</module>
		<module id="CFG5901" name="医生列表" type="1"
			script="phis.application.cfg.script.ConfigHsqxYgList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_HSQX_YG</p>
				<p name="createCls">phis.application.cfg.script.ConfigHsqxYgForm</p>
				<p name="removeByFiled">PERSONNAME</p>
			</properties>
			<action id="create" name="新增" iconCls="new" />
			<action id="remove" name="删除" />
			<action id="execute" name="护士长" iconCls="nurse" />
		</module>
		<module id="CFG5902" name="科室信息" type="1"
			script="phis.application.cfg.script.ConfigHsqxKsListList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_HSQX_KS</p>
			</properties>
			<action id="save" name="保存" />
			<action id="execute" name="默认" iconCls="commit" />
		</module>
		<module id="CFG5903" name="科室选择List" type="1"
			script="phis.application.cfg.script.ConfigDepartmentList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.SYS_Office_SELECT</p>
			</properties>
		</module>
		<module id="CFG60" name="二级库房科室对照"
			script="phis.application.cfg.script.ConfigDepartmentEJKFModule">
			<properties>
				<p name="refHsqxYgList">phis.application.cfg.CFG/CFG/CFG6001</p>
				<p name="refHsqxKsListList">phis.application.cfg.CFG/CFG/CFG6002</p>
				<p name="refDepartmentList">phis.application.cfg.CFG/CFG/CFG6003</p>
			</properties>
		</module>
		<module id="CFG6001" name="库房列表" type="1"
			script="phis.application.cfg.script.ConfigDepartmentEJKFlist">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_KFXX_EJDZ</p>
			</properties>
		</module>
		<module id="CFG6002" name="库房科室信息" type="1"
			script="phis.application.cfg.script.ConfigDepartmentEJKFMforKSList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_KFDZ</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<module id="CFG6003" name="科室信息" type="1"
			script="phis.application.cfg.script.ConfigDepartmentAllKSList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.SYS_Office_SELECT</p>
			</properties>
		</module>
		<module id="CFG61" name="流转方式维护"
			script="phis.application.cfg.script.CirculationMethodsModule">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_LZFS</p>
				<p name="dicId">phis.dictionary.circulationModules</p>
				<p name="rootVisible">true</p>
				<p name="serviceId">circulationMethodsModuleService</p>
				<p name="createCls">phis.application.cfg.script.CirculationMethodsForm</p>
				<p name="updateCls">phis.application.cfg.script.CirculationMethodsForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="invalid" name="注销" iconCls="writeoff" />
		</module>
		<module id="CFG62" name="库房账册初始化"
			script="phis.application.cfg.script.ConfigTreasuryBooksInitializationModule">
			<action id="inventoryInitialTab" viewType="list" name="库存初始"
				ref="phis.application.cfg.CFG/CFG/CFG6201" />
			<action id="departmentsInTab" viewType="list" name="科室在用"
				ref="phis.application.cfg.CFG/CFG/CFG6202" />
			<action id="assetsTab" viewType="list" name="资产设备"
				ref="phis.application.cfg.CFG/CFG/CFG6203" />
		</module>
		<module id="CFG6201" name="库存初始" type="1"
			script="phis.application.cfg.script.ConfigInventoryInitialList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC</p>
				<p name="neworupdRef">phis.application.cfg.CFG/CFG/CFG620101</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
			<action id="execute" name="转账" iconCls="commit" />
		</module>
		<module id="CFG620101" name="库存初始" type="1"
			script="phis.application.cfg.script.ConfigInventoryInModule">
			<properties>
				<p name="refForm">phis.application.cfg.CFG/CFG/CFG62010101</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG62010102</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG62010101" name="物资信息(form)" type="1"
			script="phis.application.cfg.script.ConfigInventoryInForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC_FORM</p>
			</properties>
		</module>
		<module id="CFG62010102" name="库存初始信息(editorList)" type="1"
			script="phis.application.cfg.script.ConfigInventoryInEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC_LIST</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG6202" name="科室在用" type="1"
			script="phis.application.cfg.script.ConfigDepartmentsInList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC_KSZY</p>
				<p name="neworupdRef">phis.application.cfg.CFG/CFG/CFG620201</p>
			</properties>
			<action id="refresh" name="刷新" iconCls="arrow_refresh" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG620201" name="科室在用" type="1"
			script="phis.application.cfg.script.ConfigDepartmentsInModule">
			<properties>
				<p name="refForm">phis.application.cfg.CFG/CFG/CFG62020101</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG62020102</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG62020101" name="物资信息(form)" type="1"
			script="phis.application.cfg.script.ConfigDepartmentsInForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC_KSZY_FORM</p>
			</properties>
		</module>
		<module id="CFG62020102" name="库存初始信息(editorList)" type="1"
			script="phis.application.cfg.script.ConfigDepartmentsInEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSKC_KSZY_LIST</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG6203" name="资产设备" type="1"
			script="phis.application.cfg.script.ConfigssetsList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSZC</p>
				<p name="neworupdRef">phis.application.cfg.CFG/CFG/CFG620301</p>
			</properties>
			<action id="refresh" name="刷新" iconCls="arrow_refresh" />
			<action id="add" name="新增" />
			<action id="upd" name="修改" iconCls="update" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG620301" name="资产设备" type="1"
			script="phis.application.cfg.script.ConfigassetsModule">
			<properties>
				<p name="refForm">phis.application.cfg.CFG/CFG/CFG62030101</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG62030102</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG62030101" name="物资信息(form)" type="1"
			script="phis.application.cfg.script.ConfigassetsForm">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSZC_FORM</p>
			</properties>
		</module>
		<module id="CFG62030102" name="资产设备信息(editorList)" type="1"
			script="phis.application.cfg.script.ConfigassetsEditorList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_CSZC_LIST</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG63" name="HIS物流物品对照"
			script="phis.application.cfg.script.LogisticsInventoryControlModule">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF_WLDZ</p>
				<p name="refList">phis.application.cfg.CFG/CFG/CFG6301</p>
				<p name="refModule">phis.application.cfg.CFG/CFG/CFG6302</p>
				<p name="serviceId">configLogisticsInventoryControlService</p>
				<p name="saveActionId">saveLogisticsInformation</p>
			</properties>
			<action id="refresh" name="刷新"></action>
			<action id="add" name="增加"></action>
			<action id="remove" name="取消"></action>
			<action id="save" name="保存"></action>
		</module>
		<module id="CFG6301" name="HIS物流物品对照" type="1"
			script="phis.application.cfg.script.LogisticsInventoryControlLeftList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_YLSF_WLDZ</p>
			</properties>
		</module>
		<module id="CFG6302" name="HIS物流物品对照" type="1"
			script="phis.application.cfg.script.LogisticsInventoryControlRightModule">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_YPDZ</p>
				<p name="refLeftList">phis.application.cfg.CFG/CFG/CFG630201</p>
				<p name="refRightList">phis.application.cfg.CFG/CFG/CFG630202</p>
			</properties>
		</module>
		<module id="CFG630201" name="HIS物流物品对照" type="1"
			script="phis.application.cfg.script.LogisticsInventoryControlRightModuleLeftList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.GY_FYWZ</p>
			</properties>
		</module>
		<module id="CFG630202" name="HIS物流物品对照" type="1"
			script="phis.application.cfg.script.LogisticsInventoryControlRightModuleRightList">
			<properties>
				<p name="serviceId">phis.configLogisticsInventoryControlService</p>
				<p name="queryActionId">queryLogisticsInformation</p>
				<p name="entryName">phis.application.cfg.schemas.WL_WZZD_YPDZ</p>
			</properties>
		</module>

		<!-- 门诊电子病历维护维护 -->
		<module id="CFG48" name="病历类别维护"
			script="phis.application.emr.script.EMRMedicalRecordTemplatesManageModule">
			<properties>
				<p name="refBllbTree">phis.application.cfg.CFG/CFG/CFG4801</p>
				<p name="refBlmbList">phis.application.cfg.CFG/CFG/CFG4802</p>
			</properties>
		</module>
		<module id="CFG4801" name="病历类别维护" type="1"
			script="phis.application.emr.script.EMRMedicalRecordTemplatesManageTree">
		</module>
		<module id="CFG4802" name="病历类别维护" type="1"
			script="phis.application.emr.script.EMRMedicalRecordTemplatesManageList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_KBM_BLLB_LIST</p>
				<p name="createCls">phis.application.emr.script.EMRMedicalRecordTemplatesManageForm
				</p>
				<p name="updateCls">phis.application.emr.script.EMRMedicalRecordTemplatesManageForm
				</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG4803" name="病历类别维护" type="1"
			script="phis.application.emr.script.EMRMedicalRecordTemplatesManageForm">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_KBM_BLLB_FORM</p>
			</properties>
			<action id="save" name="保存" />
			<action id="verify" name="校验" iconCls="update" />
			<action id="cancel" name="关闭" iconCls="common_cancel" />
		</module>
		<module id="CFG49" name="页眉页脚设置"
			script="phis.application.cfg.script.EMRHeadersfootersEditorList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.SYS_Office_YMYJ</p>
			</properties>
			<action id="save" name="保存" iconCls="save" />
		</module>
		<module id="CFG99" name="特殊符号维护" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_BLZD_TSFH</p>
				<p name="createCls">phis.application.emr.script.EMRTsfhManageForm</p>
				<p name="updateCls">phis.application.emr.script.EMRTsfhManageForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<module id="CFG98" name="科室模板订阅"
			script="phis.application.emr.script.EMRKsTemplatesManageModule">
			<properties>
				<p name="refBllbTree">phis.application.cfg.CFG/CFG/CFG9801</p>
				<p name="refBlmbList">phis.application.cfg.CFG/CFG/CFG9802</p>
				<p name="refKsList">phis.application.cfg.CFG/CFG/CFG9803</p>
			</properties>
		</module>
		<module id="CFG9801" name="病历模板tree" type="1"
			script="phis.application.emr.script.EMRPersonalModeManageTree">
		</module>
		<module id="CFG9802" name="病历模版List" type="1"
			script="phis.application.emr.script.EMRKsTemplatesManageList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.CHTEMPLATE_CHRECORDTEMPLATE
				</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">1</p>
			</properties>
			<action id="save" name="订阅" />
			<action id="review" name="预览" iconCls="page_white_magnify" />
		</module>
		<module id="CFG9803" name="科室List" type="1"
			script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_KSMBDY</p>
				<p name="disablePagingTbr">1</p>
			</properties>
		</module>
		<module id="CFG97" name="公有模板管理"
			script="phis.application.emr.script.EMRTemplatesManageModule">
			<properties>
				<p name="refBllbTree">phis.application.cfg.CFG/CFG/CFG9701</p>
				<p name="refBlmbList">phis.application.cfg.CFG/CFG/CFG9702</p>
			</properties>
		</module>
		<module id="CFG9701" name="病历模板tree" type="1"
			script="phis.application.emr.script.EMRPersonalModeManageTree">
		</module>
		<module id="CFG9702" name="病历模版List" type="1"
			script="phis.application.emr.script.EMRTemplatesManageList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.CHTEMPLATE_CHRECORDTEMPLATE
				</p>
				<p name="autoLoadData">false</p>
				<p name="disablePagingTbr">1</p>
			</properties>
			<action id="save" name="保存" />
			<action id="review" name="预览" iconCls="page_white_magnify" />
		</module>
		<!-- <module id="CFG80" name="门诊默认病历模板" script="phis.application.emr.script.EMRClinicDefaultTemplateModule"> 
			<properties> <p name="refLList">phis.application.cfg.CFG/CFG/CFG8001</p> 
			<p name="refRList">phis.application.cfg.CFG/CFG/CFG8002</p> </properties> 
			<action id="save" name="保存" /> </module> <module id="CFG8001" name="病历模版List" 
			type="1" script="phis.application.emr.script.EMRClinicDefaultTemplateList"> 
			<properties> <p name="entryName">phis.application.emr.schemas.SYS_Office</p> 
			</properties> </module> <module id="CFG8002" name="科室List" type="1" script="phis.application.emr.script.EMRClinicDefaultSelectList"> 
			<properties> <p name="entryName">phis.application.emr.schemas.CHTEMPLATE_MZBLMB</p> 
			</properties> </module> -->
		<module id="CFG81" name="接口元素定义"
			script="phis.application.emr.script.EMRJkysManageList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_JKYS</p>
				<p name="disablePagingTbr">1</p>
			</properties>
			<action id="loadYljs" name="更新签名元素" iconCls="confirm" />
			<action id="save" name="保存" />
		</module>
		<module id="CFG83" name="护理诊断维护" script="phis.script.SimpleList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_HLZD</p>
				<p name="createCls">phis.application.emr.script.EMRHLZDManageForm</p>
				<p name="updateCls">phis.application.emr.script.EMRHLZDManageForm</p>
			</properties>
			<action id="create" name="新增" />
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<!-- 门诊电子病历维护维护 end -->

		<module id="CFG82" name="抗菌药物使用原因维护"
			script="phis.application.cfg.script.AntimicrobialDrugUseCausesList">
			<properties>
				<p name="entryName">phis.application.cfg.schemas.EMR_CYDY</p>
				<p name="serviceId">phis.AntimicrobialDrugUseCausesService</p>
				<p name="serviceAction">saveAntimicrobialDrug</p>
			</properties>
			<action id="create" name="新增"></action>
			<action id="update" name="修改"></action>
			<action id="remove" name="删除"></action>
		</module>		
		<module id="CFG101" name="定位编码维护" script="phis.application.emr.script.EMRDwbmWh">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_DWBM_WH</p>
				<p name="createCls">phis.application.emr.script.EMRDwbmWhForm</p>
				<p name="updateCls">phis.application.emr.script.EMRDwbmWhForm</p>
				<p name="disablePagingTbr">1</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="create" name="添加"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		
		<!-- start 缺陷编码维护Module -->
		<module id="CFG102" name="缺陷编码维护" script="phis.application.emr.script.EMRQxbmWhModule">
			<properties>
				<p name="refLList">phis.application.cfg.CFG/CFG/CFG103</p>
				<p name="refRList">phis.application.cfg.CFG/CFG/CFG104</p>
			</properties>
		</module>
		<!-- end 缺陷编码维护Module -->
		
		<!-- start 缺陷编码维护左边列表 定位编码列表 CFG102_left_list-->
		<module id="CFG103" name="CFG102_left_list" type="1" script="phis.application.emr.script.EMRQxbmWhLeft">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_CFG102_LEFT</p>
				<p name="disablePagingTbr">1</p>
			</properties>
		</module>
		<!-- end 缺陷编码维护左边列表 -->
		
		<!-- start 缺陷编码维护右边列表 缺陷编码列表 CFG102_right_list -->
		<module id="CFG104" name="缺陷编码维护" type="1" script="phis.application.emr.script.EMRQxbmWh">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_QXBM_WH</p>
				<p name="createCls">phis.application.emr.script.EMRQxbmWhForm</p>
				<p name="updateCls">phis.application.emr.script.EMRQxbmWhForm</p>
			</properties>
			<action id="create" name="添加"/>
			<action id="update" name="修改" />
			<action id="remove" name="删除" />
		</module>
		<!-- end 缺陷编码维护右边列表  -->
		
		<!-- start 约定定位编码维护Module -->
		<module id="CFG105" name="约定定位编码维护" script="phis.application.emr.script.EMRYddwbmWhModule">
			<properties>
				<p name="refTree">phis.application.cfg.CFG/CFG/CFG9801</p>
				<p name="refRList">phis.application.cfg.CFG/CFG/CFG106</p>
			</properties>
		</module>
		<!-- end 约定定位编码维护Module -->
		
		<!-- start 约定定位编码维护右边列表 CFG105_right_list -->
		<module id="CFG106" name="CFG105_right_list" type="1" script="phis.application.emr.script.EMRYddwbmWhLeft">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_CFG102_LEFT</p>
				<p name="disablePagingTbr">1</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="save" name="保存" />
		</module>
		<!-- end 约定定位编码维护右边列表  -->
		
		<!-- 病历质控  -->
		<module id="CFG84" name="时限时间维护" script="phis.application.emr.script.EMRTimeLimitSetList">
			<properties>
				<p name="entryName">phis.application.emr.schemas.EMR_SXSJ</p>
			</properties>
			<action id="save" name="保存" />
		</module>
		<!-- 病历质控 end -->
	</catagory>
</application>