<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.phsa.PHSA" name="全院查询" type="3">
	<catagory id="PHSA" name="全院查询">
		<module id="PHSA00" name="首页" script="phis.application.phsa.script.PHSAHomeModule">
			<properties> 
				<p name="queryServiceId">PHSAManageService</p>
				<p name="queryActionId">queryHomeInfo</p>
				<p name="ZSR">phis.application.phsa.PHSA/PHSA/PHSA0001</p>
				<p name="DATA_DETAILS">phis.application.phsa.PHSA/PHSA/PHSA0002</p>
				<p name="JCFY">phis.application.phsa.PHSA/PHSA/PHSA0003</p>
			</properties>
		</module>
		<module id="PHSA0001" name="总收入明细"  type="1" script="phis.application.phsa.script.PHSA_ZSR_List">
			<properties> 
				<p name="entryName">phis.application.phsa.schemas.FYMX</p>
				<p name="listServiceId">PHSAManageService</p>
				<p name="queryActionId">zSRDetails</p>
				<p name="summaryable">true</p>
			</properties>
			<action id="refresh" name="刷新"  />
		</module>
		
		<module id="PHSA0002" name="数据明细"  type="1" script="phis.application.phsa.script.PHSA_Data_Details_List">
			<properties> 
				<p name="entryName">phis.application.phsa.schemas.DATA_DETAILS</p>
				<p name="listServiceId">PHSAManageService</p>
				<p name="queryActionId">dataDetails</p>
				<p name="summaryable">true</p>
			</properties>
			<action id="refresh" name="刷新"  />
		</module>
		<module id="PHSA0003" name="均次费用明细"  type="1" script="phis.application.phsa.script.PHSA_JCFY_Details_List">
			<properties> 
				<p name="entryName">phis.application.phsa.schemas.PHSA_JCFY</p>
				<p name="listServiceId">PHSAManageService</p>
				<p name="queryActionId">dataDetails</p>
				<p name="summaryable">true</p>
			</properties>
			<action id="refresh" name="刷新"  />
		</module>
		<module id="PHSA001" name="病人信息"
			script="phis.application.phsa.script.PHSAPeopleInfoModule">
			<properties>
				<p name="RecordsList1">phis.application.phsa.PHSA/PHSA/PHSA00101</p>
				<p name="RecordsList2">phis.application.phsa.PHSA/PHSA/PHSA00102</p>
			</properties>
		</module>
		<module id="PHSA00101" name="病人信息" type="1" script="phis.application.phsa.script.PHSAPeopleInfoList">
			<properties> 
				<p name="entryName">phis.application.phsa.schemas.TJ_BRXXLB_MZ</p>
			</properties>
		</module>
		<module id="PHSA00102" name="病人信息" type="1" script="phis.application.phsa.script.PHSAPeopleInfoList">
			<properties> 
				<p name="entryName">phis.application.phsa.schemas.TJ_BRXXLB_ZY</p>
			</properties>
		</module>
		<module id="PHSA01" name="药品采购分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_022</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA02" name="药品消耗分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_023</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA03" name="抗菌药物使用分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">JBYW_kjyw_007</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA04" name="基本药物使用分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">JBYW_jbyw_006</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA05" name="疾病统计分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_024</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA06" name="出院病人费用分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_025</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA07" name="门诊费用分析" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_019</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA08" name="门诊医师工作量统计" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_017</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA09" name="全院收入情况" script="hais.rpc.SwfEmbedPanel">
			<properties> 
				<p name="thematicId">HIS_ylyx_021</p>
				<p name="JGID_NAME">jgid</p>
			</properties>
		</module>
		<module id="PHSA10" name="医生排班" script="phis.application.phsa.script.SimpleDoctorPlanPrintView">
			<properties>
				<p name="serviceId">PHSAManageService</p>
				<p name="serviceAction">queryPerson</p>
			</properties>
		</module>
	</catagory>
	<catagory id="PHSA_GW" name="公共卫生">
		<module id="PHSA_GW01" name="孕产妇保健年报表" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_014</p>
			</properties>
		</module>
		<module id="PHSA_GW02" name="儿童保健年报表" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_011</p>
			</properties>
		</module>
		<module id="PHSA_GW03" name="妇幼管理指标" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_015</p>
			</properties>
		</module>
		<module id="PHSA_GW04" name="社区管理指标" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_023</p>
			</properties>
		</module>
		<module id="PHSA_GW05" name="重点人群随访情况" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_017</p>
			</properties>
		</module>
		<module id="PHSA_GW06" name="慢病防治工作指标" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_022</p>
			</properties>
		</module>
		<module id="PHSA_GW07" name="建档数指标" script="hais.rpc.SwfEmbedPanel">
			<properties>
				<p name="thematicId">EHR_gwtj_016</p>
			</properties>
		</module>
		<module id="PHSA_GW08" name="传染病" script="phis.application.phsa.script.PHSAInfectiousDiseaseList">
			<properties>
				<p name="entryName">phis.application.phsa.schemas.PHSA_CRB</p>
				<p name="listServiceId">PHSAManageService</p>
				<p name="queryActionId">queryCRBDetails</p>
			</properties>
			<action id="refresh" name="刷新"  />
		</module>
	</catagory>
</application>