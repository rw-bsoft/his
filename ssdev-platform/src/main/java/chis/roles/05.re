<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.05" name="团队长" parent="base" type="T05" version="1386315525826">
	<accredit>
		<apps>
			<app id="chis.application.healthmanage.HEALTHMANAGE" />
			<app id="chis.application.index.INDEX">
				<others />
			</app>
			<app id="chis.application.common.COMMON">
				<others />
			</app>
			<app id="phis.application.cic.CIC">
				<others />
			</app>
			<app id="chis.application.diseasecontrol.DISEASECONTROL">
				<catagory id="PIV">
					<others />
				</catagory>
				<catagory id="GDR">
					<others />
				</catagory>
			</app>
			<app id="chis.application.diseasemanage.DISEASEMANAGE">
				<catagory id="OHR" >
					<others />
				</catagory>
				<catagory id="RVC"  >
					<others />
				</catagory>
				<catagory id="HY"  >
					<others />
				</catagory>
				<catagory id="DBS" >
					<others />
				</catagory>
				<catagory id="PSY">
					<others />
				</catagory>
				<catagory id="TR">
					<others />
				</catagory>
				<catagory id="DEF">
					<others />
				</catagory>
				<catagory id="CVD" acType="blacklist">
					<module id="M09"/>
					<module id="M10"/>
					<module id="M11"/>
				</catagory>
				<catagory id="INC">
					<others />
				</catagory>
				<catagory id="CONS">
					<others />
				</catagory>
			</app>
			<app id="chis.application.his.HIS">
				<others />
			</app>
			<app id="chis.application.gynecology.GYNECOLOGY">
				<catagory id="CDH">
					<module id="H03">
						<others />
					</module>
					<module id="H0111_3_1">
						<others />
					</module>
					<module id="H0901-2">
						<others />
					</module>
					<module id="H12">
						<others />
					</module>
					<module id="H13">
						<others />
					</module>
					<module id="H14">
						<others />
					</module>
					<module id="H15">
						<others />
					</module>
					<module id="H0111_1" />
					<module id="H0111_2">
						<others />
					</module>
					<module id="H0111_2_1" />
					<module id="H0111_3">
						<others />
					</module>
					<module id="H0111_3_2">
						<action id="print" />
					</module>
					<module id="H0111_6" />
					<module id="H04">
						<others />
					</module>
					<module id="H0401">
						<action id="modify" />
						<action id="print" />
					</module>
					<module id="H0401_1" />
					<module id="H05">
						<others />
					</module>
					<module id="H0501">
						<action id="modify" />
						<action id="print" />
					</module>
					<module id="H0501_01">
						<action id="print" />
						<action id="cancel" />
					</module>
					<module id="H0501_02">
						<action id="cancel" />
					</module>
					<module id="H97">
						<others />
					</module>
					<module id="H98">
						<others />
					</module>
					<module id="H99">
						<others />
					</module>
					<module id="H97-1" />
					<module id="H98-1" />
					<module id="H99-1" />
					<module id="H13-1" />
					<module id="H97-2" />
					<module id="H97-3" />
					<module id="H97-1-1" />
					<module id="H98-1-1" />
					<module id="H99-1-1" />
					<module id="H09">
						<others />
					</module>
					<module id="H0901">
						<action id="modify" />
						<action id="print" />
					</module>
					<module id="H0902">
						<action id="cancel" />
					</module>
					<module id="H0901-1">
						<action id="print" />
					</module>
					<module id="H09-1">
						<others />
					</module>
					<module id="H09-2">
						<others />
					</module>
					<module id="H09-2-1" />
					<module id="H09-2-2-1">
						<action id="print" />
					</module>
					<module id="H09-2-2-2" />
					<module id="H01">
						<others />
					</module>
					<module id="H0101">
						<action id="modify" />
						<action id="print" />
					</module>
					<module id="H0102">
						<action id="cancel" />
					</module>
					<module id="H02">
						<others />
					</module>
					<module id="H02_1">
						<action id="modify" />
					</module>
					<module id="H02_2" />
					<module id="H0111_7">
						<action id="print" />
					</module>
				</catagory>
				<catagory id="MHC">
					<module id="G0201_1_0">
						<others />
					</module>
					<module id="G04-2-1">
						<others />
					</module>
					<module id="G0501">
						<others />
					</module>
					<module id="G06_1">
						<others />
					</module>
					<module id="G12">
						<others />
					</module>
					<module id="G20_05">
						<others />
					</module>
					<module id="G01">
						<others />
					</module>
					<module id="G0101">
						<action id="modify" />
						<action id="print" />
					</module>
					<module id="G0101_2" />
					<module id="G0101_1">
						<others />
					</module>
					<module id="G0101_1_1" />
					<module id="G0101_1_2" />
					<module id="G0101_1_2_html"/>
					<module id="G0101_1_3">
						<others />
					</module>
					<module id="G0101_1_3_1" />
					<module id="G0101_3">
						<action id="cancel" />
					</module>
					<module id="G0101_4">
						<action id="cancel" />
					</module>
					<module id="G02">
						<others />
					</module>
					<module id="G0201">
						<others />
					</module>
					<module id="G0201_1">
						<others />
					</module>
					<module id="G0201_1_1">
						<action id="checkUp" />
						<action id="highRisk" />
						<action id="description" />
						<action id="fetals" />
						<action id="printVisit" />
					</module>
					<module id="G0201_1_1_html"/>
					<module id="G0201_1_1_1" />
					<module id="G0201_1_1_2" />
					<module id="G04">
						<others />
					</module>
					<module id="G0401">
						<others />
					</module>
					<module id="G04-1" />
					<module id="G04-2">
						<others />
					</module>
					<module id="G04-2-2">
						<action id="create" />
					</module>
					<module id="G10-1">
						<others />
					</module>
					<module id="G10-1-1">
						<others />
					</module>
					<module id="G10-1-2" />
					<module id="G10-2">
						<others />
					</module>
					<module id="G10-2-1">
						<others />
					</module>
					<module id="G10-2-2">
						<action id="cancel" />
					</module>
					<module id="G06">
						<others />
					</module>
					<module id="G06_2">
						<action id="create" />
					</module>
					<module id="GHR">
						<others />
					</module>
					<module id="GHR01">
						<action id="remove" />
					</module>
					<module id="G11">
						<others />
					</module>
					<module id="G11-1">
						<others />
					</module>
					<module id="G14">
						<others />
					</module>
					<module id="G1401">
						<others />
					</module>
				</catagory>
			</app>
			<app id="chis.application.healthcheck.HEALTHCHECK">
				<catagory id="PER">
					<module id="J0102">
						<others />
					</module>
					<module id="J01010102_2">
						<others />
					</module>
					<module id="J01">
						<others />
					</module>
					<module id="J0101">
						<others />
					</module>
					<module id="J010101">
						<others />
					</module>
					<module id="J01010101">
						<others />
					</module>
					<module id="J01010101_1">
						<others />
					</module>
					<module id="J01010101_1_1">
						<others />
					</module>
					<module id="J01010101_2">
						<others />
					</module>
					<module id="J01010102">
						<others />
					</module>
					<module id="J01010102_1">
						<others />
					</module>
				</catagory>
			</app>
			<app id="chis.application.healthmanage.HEALTHMANAGE">
				<catagory id="WL" />
				<catagory id="WL">
					<module id="A01">
						<others />
					</module>
					<module id="A01_1">
						<others />
					</module>
				</catagory>
				<catagory id="HR">
					<module id="B12">
						<others />
					</module>
					<module id="D20_2_1">
						<others />
					</module>
					<module id="B17-2-1">
						<others />
					</module>
					<module id="B18-2-1">
						<others />
					</module>
					<module id="B01">
						<others />
					</module>
					<module id="B01_">
						<others />
					</module>
					<module id="B011">
						<others />
					</module>
					<module id="B011_1">
						<others />
					</module>
					<module id="B011_2">
						<others />
					</module>
					<module id="B011_2_1">
						<others />
					</module>
					<module id="B011_3">
						<others />
					</module>
					<module id="B011_3_1">
						<others />
					</module>
					<module id="B011_5">
						<others />
					</module>
					<module id="B011_51">
						<others />
					</module>
					<module id="B011_5_1_1">
						<others />
					</module>
					<module id="B011_6">
						<others />
					</module>
					<module id="B011_6_1">
						<others />
					</module>
					<module id="B011_6_2">
						<others />
					</module>
					
					   
				    <module id="B0110">
						<others />
					</module>
						<module id="B0110_1">
						<others />
					</module>
						<module id="B0110_2">
						<others />
					</module>
					<module id="B0110_7">
						<others />
					</module>
					<module id="B0110_8">
						<others />
					</module>
					<module id="B0110">
						<others />
					</module>
					
					
					<module id="B08">
						<others />
					</module>
					 <module id="B34">
						<others />
					</module>
					<module id="B341">
						<others />
					</module>
					<module id="B34101">
						<others />
					</module>
						<module id="B3410101">
						<others />
					</module>
					
					
					
					<module id="B081">
						<others />
					</module>
					<module id="B07">
						<others />
					</module>
					<module id="B09">
						<others />
					</module>
					<module id="B10">
						<others />
					</module>
					<module id="B1001">
						<others />
					</module>
					<module id="B11">
						<others />
					</module>
					<module id="B1101">
						<others />
					</module>
					<module id="B16">
						<others />
					</module>
					<module id="B16-1">
						<others />
					</module>
					<module id="HC01">
						<others />
					</module>
					<module id="HC0101">
						<others />
					</module>
					<module id="D20">
						<others />
					</module>
					<module id="D20_1">
						<others />
					</module>
					<module id="D20_2">
						<others />
					</module>
					<module id="D20_2_2">
						<others />
					</module>
					<module id="D20_2_3">
						<others />
					</module>
					<module id="D20_2_4">
						<others />
					</module>
					<module id="D20_2_5">
						<others />
					</module>
					<module id="D20_2_6">
						<others />
					</module>
					<module id="D20_2_6_1">
						<others />
					</module>
					<module id="D20_2_6_1_1">
						<others />
					</module>
					<module id="D20_2_6_2">
						<others />
					</module>
					<module id="D20_2_6_2_1">
						<others />
					</module>
					<module id="D20_2_7">
						<others />
					</module>
					<module id="D20_2_8">
						<others />
					</module>
					<module id="D20_2_8_1">
						<others />
					</module>
					<module id="B17">
						<others />
					</module>
					<module id="B17-1">
						<others />
					</module>
					<module id="B17-2">
						<others />
					</module>
					<module id="B17-2-2">
						<others />
					</module>
					<module id="B18">
						<others />
					</module>
					<module id="B18-1">
						<others />
					</module>
					<module id="B18-2">
						<others />
					</module>
					<module id="B18-2-2">
						<others />
					</module>
				</catagory>
				<catagory id="HER">
					<module id="HE02">
						<others />
					</module>
					<module id="HE02_01">
						<others />
					</module>
					<module id="HE02_01_01">
						<others />
					</module>
					<module id="HE02_01_02">
						<others />
					</module>
					<module id="HE02_01_0201">
						<others />
					</module>
					<module id="HE02_01_0201_01">
						<others />
					</module>
					<module id="HE02_01_0201_0101">
						<others />
					</module>
					<module id="HE02_01_0201_02">
						<others />
					</module>
					<module id="HE03">
						<others />
					</module>
					<module id="HE03_01">
						<others />
					</module>
					<module id="HE04">
						<others />
					</module>
					<module id="HE04_01">
						<others />
					</module>
					<module id="HE04_02">
						<others />
					</module>
					<module id="HE04_03">
						<others />
					</module>
				</catagory>
				<catagory id="MOV">
					<module id="R01">
						<action id="request" />
						<action id="modify" />
						<action id="remove" />
					</module>
					<module id="R01_1">
						<others />
					</module>
					<module id="R01_2">
						<others />
					</module>
					<module id="R04">
						<action id="EHRRequest" />
						<action id="modify" />
						<action id="remove" />
					</module>
					<module id="R04_1">
						<others />
					</module>
					<module id="R04_2">
						<others />
					</module>
					<module id="R04_3">
						<others />
					</module>
					<module id="R05">
						<action id="request" />
						<action id="modify" />
						<action id="remove" />
					</module>
					<module id="R05_1">
						<others />
					</module>
					<module id="R05_2">
						<others />
					</module>
					<module id="R05_3">
						<others />
					</module>
				</catagory>
			</app>
			<app id="chis.application.systemmanage.SYSTEMMANAGE">
				<catagory id="PC">
					<module id="AB1">
						<action id="createInfo" />
						<action id="modify" />
						<action id="remove" />
					</module>
					<module id="AB1_1">
					  <action id="save" />
					  <action id="cancel" />
				    </module>
				</catagory>
				<catagory id="DATA">
					<module id="DA15">
						<others/>
					</module>
					<module id="DA16">
						<others/>
					</module>
				</catagory>
			</app>
		</apps>
		<storage acType="whitelist">
			<store id="chis.cdwl.schemas.EHR_CommonTaskList" acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.taskDoctorId'],["$",'%user.userId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hr.schemas.EHR_PastHistorySearch"
				acValue="1111">
				<conditions>
					<condition type="filter">['like', ['$','b.manaUnitId'],
						['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hr.schemas.EHR_HealthRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.pc.schemas.ADMIN_ProblemCollect" acValue="1111">
				<conditions>
					<condition type="filter">['eq', ['$','a.createUser'],
						['$','%user.userId']]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hc.schemas.HC_HealthCheck" acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','c.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.ppvr.schemas.EHR_PoorPeopleVisit" acValue="1111">
				<conditions>
					<condition type="filter">['like',
						['$','c.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.npvr.schemas.EHR_NormalPopulationVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',
						['$','a.createUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.gdr.schemas.GDR_GroupDinnerRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.manaUnitId'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.ohr.schemas.MDC_OldPeopleRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.dbs.schemas.MDC_DiabetesRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionFixGroup"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionRisk"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionRiskAssessment"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionRiskVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionSimilarity"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.ohr.schemas.MDC_OldPeopleVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.piv.schemas.PIV_VaccinateRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cdh.schemas.CDH_HealthCard"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cdh.schemas.CDH_BirthCertificate"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','d.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cdh.schemas.CDH_DefectRegister"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cdh.schemas.CDH_DeadRegister"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cdh.schemas.CDH_DebilityChildren"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','d.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.fhr.schemas.EHR_FamilyRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mov.schemas.MOV_EHRConfirm"
				acValue="1111">
				<conditions>
					<condition type="filter">['or',
						['like',['$','a.applyUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]],
						['like',['$','a.targetUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
						]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mov.schemas.MOV_ManaInfoBatchChange"
				acValue="1111">
				<conditions>
					<condition type="filter">['and',['eq',['$','a.archiveType'],['s','1']],['or',['like',['$','a.applyUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]],['like',['$','a.targetUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mov.schemas.MOV_HealthRecordQuery"
				acValue="1111">
				<items>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mov.schemas.MOV_ManaInfoChange"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.applyUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.ag.schemas.EHR_AreaGridChild"
				acValue="1111">
				<items>
					<item id="cdhDoctor">
						<condition type="override">
							<o target="dic.parentKey" value="['substring',['$','%user.manageUnit.id'],0,9]" />
						</condition>
					</item>
					<item id="mhcDoctor">
						<condition type="override">
							<o target="dic.parentKey" value="['substring',['$','%user.manageUnit.id'],0,9]" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_HypertensionFirst"
				acValue="1111">
				<conditions>
					<condition type="filter">['and',['like',
						['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]],['ne',['$','diagnosisType'],["$",'03']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.dbs.schemas.MDC_DiabetesVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','d.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.dbs.schemas.MDC_DiabetesSimilarity"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.inputUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			
			<store id="chis.application.hy.schemas.MDC_DiabetesRisk"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_DiabetesRiskAssessment"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_DiabetesRiskVisit"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.inputUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.her.schemas.HER_HealthRecipeRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','c.manaUnitId'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mhc.schemas.MHC_PregnantRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mhc.schemas.MHC_VisitRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','d.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mhc.schemas.MHC_Postnatal42dayRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.checkManaUnit'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mhc.schemas.MHC_PregnantStopManage"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','c.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.mhc.schemas.MHC_HighRiskVisitReasonList"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','c.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.per.schemas.PER_CheckupRegister"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.checkupOrganization'],['concat',['substring',['$','%user.manageUnit.id'],0,9],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.psy.schemas.PSY_PsychosisRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
							<o target="fixed" value="true" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="MDC_TumourRecord" acValue="0000" />
			<store id="MDC_TumourVisit" acValue="0000" />
			<store id="chis.application.sch.schemas.SCH_SchistospmaManage"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.inputUnit'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
			</store>
			<store id="chis.application.sch.schemas.SCH_SnailBaseInfomation"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.inputUnit'],['concat',['substring',['$','%user.manageUnit.id'],
						0,9],['s','%']]]</condition>
				</conditions>
			</store>
			<store id="chis.application.her.schemas.HER_EducationPlanExe"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.executeUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.her.schemas.HER_EducationRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','b.executeUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.her.schemas.HER_RecipelRecordList"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.createUnit'],["$",'%user.manageUnit.id']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_ClinicRecord"
				acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_ClinicDiag"
				acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_ClnicLab" acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_Recipe" acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_ClnicLabDetail"
				acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.his.schemas.HIS_RecipeDetailOther"
				acValue="1111">
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.def.schemas.DEF_LimbDeformityRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.def.schemas.DEF_BrainDeformityRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.def.schemas.DEF_IntellectDeformityRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.cvd.schemas.CVD_AssessRegister"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','c.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.rvc.schemas.RVC_RetiredVeteranCadresRecord"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','a.manaDoctorId'],["$",'%user.userId']]
					</condition>
				</conditions>
				<items>
					<item id="manaDoctorId">
						<condition type="override">
							<o target="defaultValue" value="%user.userId" />
						</condition>
					</item>
					<item id="manaUnitId">
						<condition type="override">
							<o target="defaultValue" value="%user.manageUnit.id" />
						</condition>
					</item>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.rvc.schemas.RVC_RetiredVeteranCadresVisit_list"
				acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','e.manaDoctorId'],["$",'%user.userId']]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="chis.application.hy.schemas.MDC_Hypertension_FCBP"
				acValue="1111">
				<conditions>
					<condition type="filter">['like',['$','a.measureUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]
					</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<others acValue="1111" />
		</storage>
		<workList acType="whitelist">
			<work id="01" />
			<work id="02" />
			<work id="16" />
			<work id="18" />
			<work id="19" />
			<work id="20" />
			<work id="21" />
			<work id="22" />
			<work id="23" />
			<work id="24" />
		</workList>
		<reminderList acType="whitelist">
			<reminder id="1" />
			<reminder id="2" />
			<reminder id="4" />
			<reminder id="10" />
		</reminderList>
	</accredit>
</role>
