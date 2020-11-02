<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.bd.BASEDATA"
	name="基础数据维护">
		<catagory id="BDHIS" name="基本医疗目标值">
			<module id="BDHIS01" name="居委基数维护" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
			<module id="BDHIS02" name="绩效监管" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
				<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
		</catagory>
		<catagory id="BDCHIS" name="公共卫生目标值">
			<module id="BDCHIS01" name="高血压" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
			<module id="BDCHIS02" name="糖尿病" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
			<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
			<module id="BDCHIS03" name="健康教育" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
				<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
			<module id="BDCHIS04" name="儿童保健" script="chis.script.CombinedDocList">
				<properties>
					<p name="entryName">chis.application.fhr.schemas.EHR_FamilyRecord</p>
				</properties>
				<action id="list" name="列表视图" viewType="list" ref="chis.application.fhr.FHR/FHR/B01_" />
			</module>
		</catagory>
</application>

