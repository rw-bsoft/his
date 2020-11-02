<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.report.REPORT"
	name="统计分析">
		<catagory id="REPORT" name="中心管理员报表">
			<module id="CENTER0" name="综合情况" script="gp.application.index.script.center.ZhqkModule">
			</module>
			
			
			<module id="CENTER1" name="签约情况" script="gp.application.index.script.center.QyqkModule">
			</module>
			
			<module id="CENTER1_2_0" name="家庭档案签约户数" script="gp.application.report.script.qyqk.Qymx0ReportModule">
			</module>
			<module id="CENTER1_2_1" name="家庭档案新增档案数" script="gp.application.report.script.qyqk.Qymx1ReportModule">
			</module>
			<module id="CENTER1_2_2" name="家庭档案签约率" script="gp.application.report.script.qyqk.Qymx2ReportModule">
			</module>
			<module id="CENTER1_2_3" name="家庭档案维护率" script="gp.application.report.script.qyqk.Qymx3ReportModule">
			</module>
			<module id="CENTER1_2_4" name="健康档案签约人数" script="gp.application.report.script.qyqk.Qymx4ReportModule">
			</module>
			<module id="CENTER1_2_5" name="健康档案新增档案数" script="gp.application.report.script.qyqk.Qymx5ReportModule">
			</module>
			<module id="CENTER1_2_6" name="健康档案签约率" script="gp.application.report.script.qyqk.Qymx6ReportModule">
			</module>
			<module id="CENTER1_2_7" name="健康档案维护率" script="gp.application.report.script.qyqk.Qymx7ReportList">
				<properties> 
					<p name="entryName">gp.application.report.schemas.HealthVindicateCase</p>
				</properties>
			</module>
			
			
			
			
			
			<module id="CENTER2" name="特殊人群" script="gp.application.index.script.center.TsrqModule">
			</module>
			<module id="CENTER3" name="慢病" script="gp.application.index.script.center.MbModule">
			</module>
			<module id="CENTER4" name="基本医疗" script="gp.application.index.script.center.JbylModule">
			</module>
		</catagory>
		
	

		
</application>

