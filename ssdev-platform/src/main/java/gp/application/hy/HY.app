<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.hy.HY" name="高血压管理"  type="1">
	<catagory id="HY" name="高血压管理">
		<module id="HY01" name="高血压统计" type="1" script="gp.application.hy.script.HypertensionStatisticsModule">
			<action id="HY0101" name="表格展示" ref="gp.application.hy.HY/HY/HY0101" type="tab"/>
			<action id="HY0102" name="图表展示" ref="gp.application.hy.HY/HY/HY0102" type="tab"/>
		</module>
		<module id="HY0101" name="表格展示" type="1" script="gp.application.hy.script.HypertensionStatisticsTable">
			
		</module>
		<module id="HY0102" name="图表展示" type="1" script="gp.application.hy.script.HypertensionStatisticsChart">
			
		</module>
		<module id="HY02" name="新建卡数（月）" type="1" script="gp.application.hy.script.HypertensionMonthCreateNumber">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
		</module>
		
		<module id="HY03" name="管理人数" type="1" script="gp.application.hy.script.HypertensionManageNumber">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecordManage</p>
			</properties>
		</module>	
		
		<module id="HY04" name="高血压实际实际随访人次（月）" type="1" script="gp.application.hy.script.HypertensionVisitNumber">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisitNumber</p>
			</properties>
		</module>
			
		
		<module id="HYP01" name="助理高血压建档总数" type="1" script="gp.application.hy.script.fda.HypertensionRecordFdaAllList">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
		</module>
		<module id="HYP02" name="GP医生高血压建档总数" type="1" script="gp.application.hy.script.fda.HypertensionRecordFdAllList">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
		</module>
		<module id="HYP03" name="助理当月新建高血压档案数" type="1" script="gp.application.hy.script.fda.CurrentMonthFdaCreateHYPNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
		</module>
		<module id="HYP04" name="GP医生当月新建高血压档案数" type="1" script="gp.application.hy.script.fda.CurrentMonthFdCreateHYPNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionRecord</p>
			</properties>
		</module>
		<module id="HYP05" name="助理当月高血压随访数" type="1" script="gp.application.hy.script.fda.CurrentMonthFdaHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP06" name="GP医生当月高血压随访数" type="1" script="gp.application.hy.script.fda.CurrentMonthFdHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP07" name="GP医生责任辖区内当月应访高血压数" type="1" script="gp.application.hy.script.fda.CurrentMonthFDShouldHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisitPlan</p>
			</properties>
		</module>
		<module id="HYP08" name="助理上月高血压随访数" type="1" script="gp.application.hy.script.fda.PrecedingMonthFdaHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP09" name="GP医生上月高血压随访数" type="1" script="gp.application.hy.script.fda.PrecedingMonthFdHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP10" name="GP医生责任辖区内上月应访高血压数" type="1" script="gp.application.hy.script.fda.PrecedingMonthFDShouldHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisitPlan</p>
			</properties>
		</module>
		<module id="HYP11" name="助理年度高血压随访数" type="1" script="gp.application.hy.script.fda.CurrentYearFdaHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP12" name="GP医生年度高血压随访数" type="1" script="gp.application.hy.script.fda.CurrentYearFdHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisit</p>
			</properties>
		</module>
		<module id="HYP13" name="GP医生责任辖区内年度应访高血压数" type="1" script="gp.application.hy.script.fda.CurrentYearFDShouldHYPVisitNum">
			<properties>
				<p name="entryName">gp.application.hy.schemas.MDC_HypertensionVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>