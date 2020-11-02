<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.op.OP" name="老年人"  type="1">
	<catagory id="OP" name="老年人">
		<!--  助理首页  -->
		<module id="OP01" name="助理老年人建档总数" type="1" script="gp.application.op.script.fda.OldPeopleRecordFdaAllList">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleRecord</p>
			</properties>
		</module>
		<module id="OP02" name="GP医生老年人建档总数" type="1" script="gp.application.op.script.fda.OldPeopleRecordFdAllList">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleRecord</p>
			</properties>
		</module>
		<module id="OP03" name="助理当月新建老年人档案数" type="1" script="gp.application.op.script.fda.CurrentMonthFdaCreateOPRNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleRecord</p>
			</properties>
		</module>
		<module id="OP04" name="GP医生当月新建老年人档案数" type="1" script="gp.application.op.script.fda.CurrentMonthFdCreateOPRNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleRecord</p>
			</properties>
		</module>
		<module id="OP05" name="助理当月老年人随访数" type="1" script="gp.application.op.script.fda.CurrentMonthFdaOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP06" name="GP医生当月老年人随访数" type="1" script="gp.application.op.script.fda.CurrentMonthFdOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP07" name="GP医生责任辖区内当月应访老年人数" type="1" script="gp.application.op.script.fda.CurrentMonthFDShouldOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisitPlan</p>
			</properties>
		</module>
		<module id="OP08" name="助理上月老年人随访数" type="1" script="gp.application.op.script.fda.PrecedingMonthFdaOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP09" name="GP医生上月老年人随访数" type="1" script="gp.application.op.script.fda.PrecedingMonthFdOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP10" name="GP医生责任辖区内上月应访老年人数" type="1" script="gp.application.op.script.fda.PrecedingMonthFDShouldOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisitPlan</p>
			</properties>
		</module>
		<module id="OP11" name="助理年度老年人随访数" type="1" script="gp.application.op.script.fda.CurrentYearFdaOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP12" name="GP医生年度老年人随访数" type="1" script="gp.application.op.script.fda.CurrentYearFdOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisit</p>
			</properties>
		</module>
		<module id="OP13" name="GP医生责任辖区内年度应访老年人数" type="1" script="gp.application.op.script.fda.CurrentYearFDShouldOPRVisitNum">
			<properties>
				<p name="entryName">gp.application.op.schemas.MDC_OldPeopleVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>