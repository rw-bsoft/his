<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.psyc.PSYC" name="精神病"  type="1">
	<catagory id="PSYC" name="精神病">
		<!--  助理首页  -->
		<module id="PSYC01" name="助理精神病建档总数" type="1" script="gp.application.psyc.script.fda.PsychosisRecordFdaAllList">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisRecord</p>
			</properties>
		</module>
		<module id="PSYC02" name="GP医生精神病建档总数" type="1" script="gp.application.psyc.script.fda.PsychosisRecordFdAllList">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisRecord</p>
			</properties>
		</module>
		<module id="PSYC03" name="助理当月新建精神病档案数" type="1" script="gp.application.psyc.script.fda.CurrentMonthFdaCreatePSYCNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisRecord</p>
			</properties>
		</module>
		<module id="PSYC04" name="GP医生当月新建精神病档案数" type="1" script="gp.application.psyc.script.fda.CurrentMonthFdCreatePSYCNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisRecord</p>
			</properties>
		</module>
		<module id="PSYC05" name="助理当月精神病随访数" type="1" script="gp.application.psyc.script.fda.CurrentMonthFdaPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC06" name="GP医生当月精神病随访数" type="1" script="gp.application.psyc.script.fda.CurrentMonthFdPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC07" name="GP医生责任辖区内当月应访精神病数" type="1" script="gp.application.psyc.script.fda.CurrentMonthFDShouldPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisitPlan</p>
			</properties>
		</module>
		<module id="PSYC08" name="助理上月精神病随访数" type="1" script="gp.application.psyc.script.fda.PrecedingMonthFdaPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC09" name="GP医生上月精神病随访数" type="1" script="gp.application.psyc.script.fda.PrecedingMonthFdPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC10" name="GP医生责任辖区内上月应访精神病数" type="1" script="gp.application.psyc.script.fda.PrecedingMonthFDShouldPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisitPlan</p>
			</properties>
		</module>
		<module id="PSYC11" name="助理年度精神病随访数" type="1" script="gp.application.psyc.script.fda.CurrentYearFdaPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC12" name="GP医生年度精神病随访数" type="1" script="gp.application.psyc.script.fda.CurrentYearFdPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisit</p>
			</properties>
		</module>
		<module id="PSYC13" name="GP医生责任辖区内年度应访精神病数" type="1" script="gp.application.psyc.script.fda.CurrentYearFDShouldPSYCVisitNum">
			<properties>
				<p name="entryName">gp.application.psyc.schemas.PSY_PsychosisVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>