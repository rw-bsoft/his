<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.11" name="市CDC" parent="base" version="1386317728238" type="T11">
	<accredit>
		<apps>
			<app id="chis.application.index.INDEX">
				<others/>
			</app>
			<app id="phis.application.cic.CIC">
				<others />
			</app>
			<app id="chis.application.diseasecontrol.DISEASECONTROL">
				<catagory id="PIV">
					<module id="ET02">
						<others/>
					</module>
					<module id="E01">
						<action id="modify"/>
						<action id="print"/>
					</module>
					<module id="ET01">
						<others/>
					</module>
				</catagory>
			</app>
			<app id="chis.application.diseasemanage.DISEASEMANAGE">
				<catagory id="HY">
					<module id="C30_list">
						<others/>
					</module>
					<module id="C30_form">
						<others/>
					</module>
					<module id="D0-1-2-1">
						<others/>
					</module>
					<module id="D0-2-0">
						<others/>
					</module>
					<module id="DHI_01_01">
						<others/>
					</module>
					<module id="D0-1-4">
						<others/>
					</module>
					<module id="C18-1-1">
						<others/>
					</module>
					<module id="C19-1-1">
						<others/>
					</module>
					<module id="C20-2-2">
						<others/>
					</module>
					<module id="D01">
						<others/>
					</module>
					<module id="D01-hr">
						<action id="modify"/>
						<action id="visit"/>
						<action id="print"/>
					</module>
					<module id="D0-1">
						<others/>
					</module>
					<module id="D0-1-1-2-0">
						<action id="cancel"/>
					</module>
					<module id="D0-1-2">
						<others/>
					</module>
					<module id="D0-1-2-2"/>
					<module id="D0-1-3">
						<others/>
					</module>
					<module id="D0-2-1"/>
					<module id="HY01_01">
						<others/>
					</module>
					<module id="D0-2-2">
						<action id="add"/>
						<action id="modify"/>
					</module>
					<module id="D0-2-2-1">
						<action id="cancel"/>
					</module>
					<module id="D0-1-1">
						<others/>
					</module>
					<module id="D0-1-1-1"/>
					<module id="D0-1-1-2">
						<action id="add"/>
						<action id="modify"/>
					</module>
					<module id="D0-2-3"/>
					<module id="D0-2-4"/>
					<module id="DHI_01">
						<others/>
					</module>
					<module id="DHI_01_02"/>
					<module id="D02">
						<others/>
					</module>
					<module id="D02-list">
						<others/>
					</module>
					<module id="D03">
						<others/>
					</module>
					<module id="D03-list">
						<others/>
					</module>
					<module id="C17">
						<action id="visit"/>
						<action id="print"/>
					</module>
					<module id="C17-1">
						<action id="cancel"/>
					</module>
					<module id="C18">
						<action id="estimate"/>
						<action id="print"/>
					</module>
					<module id="C18-1">
						<others/>
					</module>
					<module id="C18-1-2"/>
					<module id="C18-1-3"/>
					<module id="C18-2"/>
					<module id="C19">
						<others/>
					</module>
					<module id="C19-1">
						<others/>
					</module>
					<module id="C19-1-2"/>
					<module id="C20">
						<action id="print"/>
					</module>
					<module id="C20-1">
						<action id="save"/>
					</module>
					<module id="C20-2">
						<others/>
					</module>
					<module id="C20-2-1"/>
				</catagory>
				<catagory id="DBS">
				   <others />
			    </catagory>
<!--			<catagory id="DBS">
				   <module id="D11-4">
			        	<others/>
		        	</module>
					<module id="D11-2-1">
						<others/>
					</module>
					<module id="D11-2-2">
						<others/>
					</module>
					<module id="D11-3">
						<others/>
					</module>
					<module id="D11-3-0">
						<others/>
					</module>
					<module id="D11-5">
						<others/>
					</module>
					<module id="D18-1-1">
						<others/>
					</module>
					<module id="D19-1-1">
						<others/>
					</module>
					<module id="D04">
						<others/>
					</module>
					<module id="D04-1">
						<action id="modify"/>
						<action id="print"/>
					</module>
					<module id="D11">
						<others/>
					</module>
					<module id="D11-1">
						<others/>
					</module>
					<module id="D11-1-1">
						<action id="turn"/>
					</module>
					<module id="D11-1-2">
						<action id="add"/>
						<action id="modify"/>
					</module>
					<module id="D11-2">
						<others/>
					</module>
					<module id="D11-3-1">
						<action id="turn"/>
					</module>
					<module id="D11-3-1-1"/>
					<module id="D11-3-2">
						<action id="add"/>
						<action id="modify"/>
					</module>
					<module id="D11-3-5"/>
					<module id="D11-3-3"/>
					<module id="D11-3-4"/>
					<module id="D11-5-1"/>
					<module id="D05">
						<others/>
					</module>
					<module id="D05-list">
						<others/>
					</module>
					<module id="D17">
						<action id="print"/>
					</module>
					<module id="D17-1">
						<action id="cancel"/>
					</module>
					<module id="D18">
						<action id="estimate"/>
						<action id="print"/>
					</module>
					<module id="D18-1">
						<others/>
					</module>
					<module id="D18-1-2"/>
					<module id="D18-1-3"/>
					<module id="D18-2"/>
					<module id="D19">
						<others/>
					</module>
					<module id="D19-1">
						<others/>
					</module>
					<module id="D19-1-2"/>
					<module id="D20">
						<action id="print"/>
					</module>
					<module id="D20-1">
						<action id="cancel"/>
					</module>
					<module id="D20-2">
						<action id="cancel"/>
					</module>
				</catagory>
-->
				<catagory id="TR">
					<module id="TR01_02">
						<action id="modify"/>
						<action id="listenerRegister"/>
					</module>
					<module id="TR01_0201"/>
					<module id="TR01_0202">
						<others/>
					</module>
					<module id="TR01_0202_01"/>
					<module id="TR01_0202_02">
						<action id="modify" name="查看"/>
					</module>
					<module id="TR01_0202_03">
						<action id="modify"/>
					</module>
					<module id="TR01_03">
						<action id="modify"/>
					</module>
					<module id="TR04">
						<action id="modify"/>
					</module>
					<module id="TR04_01">
						<action id="cancel" />
					</module>
					<module id="TR05">
						<action id="modify"/>
						<action id="viewPMH"/>
					</module>
					<module id="TR05_01">
						<others/>
					</module>
					<module id="TR05_0101"/>
					<module id="TR05_0102"/>
					<module id="TR05_0103"/>
					<module id="TR06">
						<others/>
					</module>
					<module id="TR0601">
						<action id="modify" />
						<action id="viewPMH"/>
					</module>
					<module id="TR0601_01">
						<others/>
					</module>
					<module id="TR0601_0101">
						<others/>
					</module>
					<module id="TR0601_0101_01"/>
					<module id="TR0601_0101_02"/>
					<module id="TR0601_0102"/>
					<module id="TR0601_0103">
						<others/>
					</module>
					<module id="TR0601_0103_01"/>
					<module id="TR0601_0103_02">
						<action id="printRecipe" />
					</module>
					<module id="TR0602">
						<others/>
					</module>
					<module id="TR0602_01">
						<others/>
					</module>
					<module id="TR07">
						<others/>
					</module>
					<module id="TR0701">
						<action id="viewPMH"/>
					</module>
					<module id="TR0701_M">
						<others/>
					</module>
					<module id="TR0701_01">
						<action id="cancel"/>
					</module>
					<module id="TR0701_03"/>
					<module id="TR08">
						<others/>
					</module>
					<module id="TR0801">
						<action id="modify"/>
					</module>
					<module id="TR0801_01">
						<others/>
					</module>
					<module id="TR0801_0101"/>
					<module id="TR0801_0101_2"/>
					<module id="TR0801_0102"/>
					<module id="TR0801_0103"/>
					<module id="TR0801_02">
						<others/>
					</module>
					<module id="TR0801_0201"/>
					<module id="TR0801_0202"/>
					<module id="TR_PMHView">
						<others/>
					</module>
					<module id="TR_THQM">
						<others/>
					</module>
					<module id="TR_TCRList"/>
					<module id="TR_PMH">
						<others/>
					</module>
					<module id="TR_PMH_List"/>
					<module id="TR_PMH_Form"/>
					<module id="THQList"/>
					<module id="PMH_THQ">
						<action id="HQGCForm"/>
						<action id="HQForm"/>
					</module>
					<module id="THQ">
						<action id="HQGCForm"/>
						<action id="HQForm"/>
					</module>
					<module id="HQGCForm"/>
					<module id="HQForm"/>
				</catagory>
				<catagory id="CVD">
					<module id="M01-1"/>
					<module id="M01-5"/>
					<module id="D00">
						<others/>
					</module>
					<module id="M00_1">
						<action id="modify"/>
						<action id="print"/>
					</module>
					<module id="M01">
						<others/>
					</module>
					<module id="M01-2"/>
					<module id="M01-3">
						<action id="print"/>
					</module>
					<module id="M01-4"/>
					<module id="M12">
						<action id="cancel"/>
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
			</app>
			<app id="chis.application.common.COMMON">
				<catagory id="COMMON">
					<module id="ImportDrugInfo">
						<others/>
					</module>
				</catagory>
			</app>
		</apps>
		<storage acType="whitelist"> 
			<store id="chis.application.hr.schemas.EHR_PastHistorySearch" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like', ['$','b.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.hr.schemas.EHR_HealthRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like', ['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.pc.schemas.ADMIN_ProblemCollect" acValue="1111"> 
				<conditions> 
					<condition type="filter">['eq', ['$','a.createUser'], ['$','%user.userId']]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.dbs.schemas.MDC_DiabetesRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.userId"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.manageUnit.id"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.ohr.schemas.MDC_OldPeopleRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.hy.schemas.MDC_HypertensionRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.piv.schemas.PIV_VaccinateRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.fhr.schemas.EHR_FamilyRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.hy.schemas.MDC_HypertensionVisit" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.userId"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.manageUnit.id"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.hy.schemas.MDC_HypertensionFirst" acValue="1111"> 
				<conditions> 
					<condition type="filter">['and',['like', ['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]],['ne',['$','diagnosisType'],["$",'03']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.userId"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.manageUnit.id"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.dbs.schemas.MDC_DiabetesVisit" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','d.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.userId"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.manageUnit.id"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.dbs.schemas.MDC_DiabetesSimilarity"
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
			<store id="chis.application.psy.schemas.PSY_PsychosisRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<item id="manaDoctorId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.userId"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<item id="manaUnitId"> 
						<condition type="override"> 
							<o target="defaultValue" value="%user.manageUnit.id"/>  
							<o target="fixed" value="true"/> 
						</condition> 
					</item>  
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="MDC_TumourRecord" acValue="0000"/>  
			<store id="MDC_TumourVisit" acValue="0000"/>  
			<store id="chis.application.his.schemas.HIS_ClinicRecord" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.his.schemas.HIS_ClinicDiag" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.his.schemas.HIS_ClnicLab" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.his.schemas.HIS_Recipe" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.his.schemas.HIS_ClnicLabDetail" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.his.schemas.HIS_RecipeDetailOther" acValue="1111"> 
				<items> 
					<others acValue="1111"/> 
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
			<others acValue="1111"/> 
		</storage>
	</accredit>
</role>
