<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.rvcf.RVCF" name="离休干部"  type="1">
	<catagory id="RVCF" name="离休干部">
		<!--  助理首页  -->
		<module id="RVCF01" name="助理离休干部建档总数" type="1" script="gp.application.rvcf.script.fda.RetiredVeteranCadresRecordFdaAllList">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord</p>
			</properties>
		</module>
		<module id="RVCF02" name="GP医生离休干部建档总数" type="1" script="gp.application.rvcf.script.fda.RetiredVeteranCadresRecordFdAllList">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord</p>
			</properties>
		</module>
		<module id="RVCF03" name="助理当月新建离休干部档案数" type="1" script="gp.application.rvcf.script.fda.CurrentMonthFdaCreateRVCFNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord</p>
			</properties>
		</module>
		<module id="RVCF04" name="GP医生当月新建离休干部档案数" type="1" script="gp.application.rvcf.script.fda.CurrentMonthFdCreateRVCFNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresRecord</p>
			</properties>
		</module>
		<module id="RVCF05" name="助理当月离休干部随访数" type="1" script="gp.application.rvcf.script.fda.CurrentMonthFdaRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF06" name="GP医生当月离休干部随访数" type="1" script="gp.application.rvcf.script.fda.CurrentMonthFdRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF07" name="GP医生责任辖区内当月应访离休干部数" type="1" script="gp.application.rvcf.script.fda.CurrentMonthFDShouldRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisitPlan</p>
			</properties>
		</module>
		<module id="RVCF08" name="助理上月离休干部随访数" type="1" script="gp.application.rvcf.script.fda.PrecedingMonthFdaRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF09" name="GP医生上月离休干部随访数" type="1" script="gp.application.rvcf.script.fda.PrecedingMonthFdRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF10" name="GP医生责任辖区内上月应访离休干部数" type="1" script="gp.application.rvcf.script.fda.PrecedingMonthFDShouldRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisitPlan</p>
			</properties>
		</module>
		<module id="RVCF11" name="助理年度离休干部随访数" type="1" script="gp.application.rvcf.script.fda.CurrentYearFdaRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF12" name="GP医生年度离休干部随访数" type="1" script="gp.application.rvcf.script.fda.CurrentYearFdRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisit</p>
			</properties>
		</module>
		<module id="RVCF13" name="GP医生责任辖区内年度应访离休干部数" type="1" script="gp.application.rvcf.script.fda.CurrentYearFDShouldRVCFVisitNum">
			<properties>
				<p name="entryName">gp.application.rvcf.schemas.RVC_RetiredVeteranCadresVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>