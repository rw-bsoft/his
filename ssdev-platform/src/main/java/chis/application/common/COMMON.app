<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.common.COMMON" name="公用模块" type="1">
	<catagory id="COMMON" name="公用模块">
		<module id="CM01" type="1" name="药品情况" script="chis.application.common.script.DrugInfoListModule">
			<properties>
				<p name="refList">chis.application.common.COMMON/COMMON/CM02</p>
			</properties>
		</module>
		<module id="CM02" type="1" name="药品情况列表" script="chis.application.common.script.DrugInfoListView">
			<properties>
				<p name="entryName">phis.application.chis.schemas.CF02</p>
			</properties>
		</module>
		
		<module id="CM03" type="1" name="就诊情况" script="chis.application.common.script.JzInfoListModule">
			<properties>
				<p name="refList">chis.application.common.COMMON/COMMON/CM04</p>
			</properties>
		</module>
		<module id="CM04" type="1" name="就诊情况列表" script="chis.application.common.script.JzInfoListView">
			<properties>
				<p name="entryName">phis.application.chis.schemas.JZLS</p>
			</properties>
		</module>
		<module id="CM05" name="建档情况(按年)" script="chis.application.common.script.EmbedPanel">
			<properties>
				<p name="url"><![CDATA[http://32.33.1.60:8085/hcms/thematic/EHR_jkda_062]]></p>
			</properties>
		</module>
		<module id="CM06" name="老年人档案(按年)" script="chis.application.common.script.EmbedPanel">
			<properties>
				<p name="url"><![CDATA[http://32.33.1.60:8085/hcms/thematic/EHR_jkda_058]]></p>
			</properties>
		</module>
		<module id="CM07" name="高血压管理(按季)" script="chis.application.common.script.EmbedPanel">
			<properties>
				<p name="url"><![CDATA[http://32.33.1.60:8085/hcms/thematic/EHR_mbgl_051]]></p>
			</properties>
		</module>
		<module id="CM08" name="监管-糖尿病管理(按季)" script="chis.application.common.script.EmbedPanel">
			<properties>
				<p name="url"><![CDATA[http://32.33.1.60:8085/hcms/thematic/EHR_mbgl_052]]></p>
			</properties>
		</module>									
	</catagory>
</application>