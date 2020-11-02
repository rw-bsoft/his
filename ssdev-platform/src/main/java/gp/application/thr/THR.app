<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.thr.THR" name="肿瘤"  type="1">
	<catagory id="THR" name="肿瘤">
		<module id="THR01" name="高危管理数" type="1" script="gp.application.tumour.script.TumourHighRiskManagerList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR02" name="新建卡数" type="1" script="gp.application.tumour.script.TumourNewCreateCardList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR03" name="高危随访人次" type="1" script="gp.application.tumour.script.TumourHighRiskVisitList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisitPlan</p>
			</properties>
		</module>
		<module id="THR04" name="肿瘤确诊" type="1" script="gp.application.tumour.script.TumourConfirmedList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourConfirmed</p>
				<p name="refConfirmedModule">chis.application.diseasemanage.DISEASEMANAGE/TR/TR0701_01</p>
			</properties>
		</module>
		<module id="THR05" name="早发现" type="1" script="gp.application.tumour.script.TumourScreeningList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourScreening</p>
				<p name="refModule">chis.application.diseasemanage.DISEASEMANAGE/TR/TR05_01</p>
			</properties>
		</module>
		<module id="THR06" name="肿瘤确诊" type="1" script="gp.application.tumour.script.TumourPrecancerList">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourConfirmed</p>
				<p name="refConfirmedModule">chis.application.diseasemanage.DISEASEMANAGE/TR/TR0701_01</p>
			</properties>
		</module>
		<!-- =========上为家医模块-下为助理模块===================-->
		<module id="THR07" name="助理肿瘤高危总建档数" type="1" script="gp.application.tumour.script.fda.AllFDCreateTHRNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR08" name="GP医生肿瘤高危总建档数" type="1" script="gp.application.tumour.script.fda.FDCreateTHRNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR09" name="助理当月新建肿瘤高危档案数" type="1" script="gp.application.tumour.script.fda.CurrentMonthAllFDCreateTHRNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR10" name="GP医生当月新建肿瘤高危档案数" type="1" script="gp.application.tumour.script.fda.CurrentMonthFDCreateTHRNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRisk</p>
			</properties>
		</module>
		<module id="THR11" name="助理当月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.CurrentMonthAllFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR12" name="GP医生当月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.CurrentMonthFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR13" name="GP医生责任辖区内当月应访肿瘤高危数" type="1" script="gp.application.tumour.script.fda.CurrentMonthFDShouldTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisitPlan</p>
			</properties>
		</module>
		<module id="THR14" name="助理上月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.PrecedingMonthAllFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR15" name="GP医生上月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.PrecedingMonthFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR16" name="GP医生责任辖区内上月应访肿瘤高危数" type="1" script="gp.application.tumour.script.fda.PrecedingMonthFDShouldTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisitPlan</p>
			</properties>
		</module>
		<module id="THR17" name="助理上月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.CurrentYearAllFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR18" name="GP医生上月肿瘤高危随访数" type="1" script="gp.application.tumour.script.fda.CurrentYearFDTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisit</p>
			</properties>
		</module>
		<module id="THR19" name="GP医生责任辖区内上月应访肿瘤高危数" type="1" script="gp.application.tumour.script.fda.CurrentYearFDShouldTHRVisitNum">
			<properties>
				<p name="entryName">gp.application.thr.schemas.MDC_TumourHighRiskVisitPlan</p>
			</properties>
		</module>
	</catagory>
</application>