<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.dia.DIA" name="糖尿病"  type="1">
	<catagory id="DIA" name="糖尿病">
		<!--  助理首页  -->
		<module id="DIA01" name="助理糖尿病建档总数" type="1" script="gp.application.dia.script.fda.DiabetesRecordFdaAllList">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecord</p>
			</properties>
		</module>
		<module id="DIA02" name="GP医生糖尿病建档总数" type="1" script="gp.application.dia.script.fda.DiabetesRecordFdAllList">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecord</p>
			</properties>
		</module>
		<module id="DIA03" name="助理当月新建糖尿病档案数" type="1" script="gp.application.dia.script.fda.CurrentMonthFdaCreateDIANum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecord</p>
			</properties>
		</module>
		<module id="DIA04" name="GP医生当月新建糖尿病档案数" type="1" script="gp.application.dia.script.fda.CurrentMonthFdCreateDIANum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecord</p>
			</properties>
		</module>
		<module id="DIA05" name="助理当月糖尿病随访数" type="1" script="gp.application.dia.script.fda.CurrentMonthFdaDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA06" name="GP医生当月糖尿病随访数" type="1" script="gp.application.dia.script.fda.CurrentMonthFdDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA07" name="GP医生责任辖区内当月应访糖尿病数" type="1" script="gp.application.dia.script.fda.CurrentMonthFDShouldDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisitPlan</p>
			</properties>
		</module>
		<module id="DIA08" name="助理上月糖尿病随访数" type="1" script="gp.application.dia.script.fda.PrecedingMonthFdaDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA09" name="GP医生上月糖尿病随访数" type="1" script="gp.application.dia.script.fda.PrecedingMonthFdDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA10" name="GP医生责任辖区内上月应访糖尿病数" type="1" script="gp.application.dia.script.fda.PrecedingMonthFDShouldDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisitPlan</p>
			</properties>
		</module>
		<module id="DIA11" name="助理年度糖尿病随访数" type="1" script="gp.application.dia.script.fda.CurrentYearFdaDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA12" name="GP医生年度糖尿病随访数" type="1" script="gp.application.dia.script.fda.CurrentYearFdDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisit</p>
			</properties>
		</module>
		<module id="DIA13" name="GP医生责任辖区内年度应访糖尿病数" type="1" script="gp.application.dia.script.fda.CurrentYearFDShouldDIAVisitNum">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisitPlan</p>
			</properties>
		</module>
		<module id="DIA20" name="高血压统计" type="1" script="gp.application.dia.script.DiabetesStatisticsModule">
			<action id="DIA2001" name="表格展示" ref="gp.application.dia.DIA/DIA/DIA2001" type="tab"/>
			<action id="DIA2002" name="图表展示" ref="gp.application.dia.DIA/DIA/DIA2002" type="tab"/>
		</module>
		<module id="DIA2001" name="表格展示" type="1" script="gp.application.dia.script.DiabetesStatisticsTable"/>
		<module id="DIA2002" name="图表展示" type="1" script="gp.application.dia.script.DiabetesStatisticsChart"/>
		<module id="DIA21" name="新建卡数（月）" type="1" script="gp.application.dia.script.DiabetesMonthCreateNumber">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecord</p>
			</properties>
		</module>	
		
		<module id="DIA22" name="管理人数" type="1" script="gp.application.dia.script.DiabetesManageNumber">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesRecordManage</p>
			</properties>
		</module>	
		
		<module id="DIA23" name="糖尿病实际实际随访人次（月）" type="1" script="gp.application.dia.script.DiabetesVisitNumber">
			<properties>
				<p name="entryName">gp.application.dia.schemas.MDC_DiabetesVisitNumber</p>
			</properties>
		</module>	
		
	</catagory>
</application>