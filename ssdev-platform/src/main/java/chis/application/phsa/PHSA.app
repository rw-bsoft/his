<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.phsa.PHSA" name="统计分析" type="3">
	<catagory id="PHSA_GW" name="统计分析">
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
		<module id="PHSA_GW08" name="传染病" script="chis.application.phsa.script.PHSAInfectiousDiseaseList">
			<properties>
				<p name="entryName">chis.application.phsa.schemas.PHSA_CRB</p>
				<p name="listServiceId">chis.PHSAManageService</p>
				<p name="queryActionId">queryCRBDetails</p>
			</properties>
			<action id="refresh" name="刷新"  />
		</module>
	</catagory>
</application>