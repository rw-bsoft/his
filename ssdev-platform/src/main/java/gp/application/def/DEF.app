<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.def.DEF" name="肿瘤"  type="1">
	<catagory id="DEF" name="肿瘤">
		<module id="DEF01" name="助理残疾人总建档数" type="1" script="gp.application.def.script.AllFDCreateDEFNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecord</p>
			</properties>
		</module>
		<module id="DEF02" name="GP医生残疾人总建档数" type="1" script="gp.application.def.script.FDCreateDEFNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecord</p>
			</properties>
		</module>
		<module id="DEF03" name="助理当月新建残疾人档案数" type="1" script="gp.application.def.script.CurrentMonthAllFDCreateDEFNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecord</p>
			</properties>
		</module>
		<module id="DEF04" name=" GP医生当月新建残疾人档案数" type="1" script="gp.application.def.script.CurrentMonthFDCreateDEFNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecord</p>
			</properties>
		</module>
		<module id="DEF05" name="助理当月残疾人随访数" type="1" script="gp.application.def.script.CurrentMonthAllFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF06" name="GP医生当月残疾人随访数" type="1" script="gp.application.def.script.CurrentMonthFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF07" name="GP医生责任辖区内当月应访残疾人数" type="1" script="gp.application.def.script.CurrentMonthFDShouldDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisitPlan</p>
			</properties>
		</module>
		<module id="DEF08" name="助理上月残疾人随访数" type="1" script="gp.application.def.script.PrecedingMonthAllFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF09" name="GP医生上月残疾人随访数" type="1" script="gp.application.def.script.PrecedingMonthFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF10" name="GP医生责任辖区内上月应访残疾人数" type="1" script="gp.application.def.script.PrecedingMonthFDShouldDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisitPlan</p>
			</properties>
		</module>
		<module id="DEF11" name="助理全年残疾人随访数" type="1" script="gp.application.def.script.CurrentYearAllFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF12" name=" GP医生全年残疾人随访数" type="1" script="gp.application.def.script.CurrentYearFDDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisit</p>
			</properties>
		</module>
		<module id="DEF13" name="GP医生责任辖区内全年应访残疾人数" type="1" script="gp.application.def.script.CurrentYearFDShouldDEFVisitNum">
			<properties>
				<p name="entryName">gp.application.def.schemas.DEF_DeformityRecordVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>