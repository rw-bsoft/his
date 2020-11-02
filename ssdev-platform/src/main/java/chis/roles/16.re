<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.16" name="区域灭螺组" parent="base" version="1386315772653" type="T16">
	<accredit>
		<apps>
			<app id="phis.application.cic.CIC">
				<others />
			</app>
			<app id="chis.application.index.INDEX">
				<others/>
			</app>
			<app id="chis.application.diseasecontrol.DISEASECONTROL">
				<catagory id="SCH">
					<others/>
				</catagory>
			</app>
			<app id="chis.application.healthmanage.HEALTHMANAGE">
				<catagory id="HR">
					<module id="B01">
						<others/>
					</module>
					<module id="B01_">
			            <action id="modify"/>
					</module>
					<module id="B011">
						<others/>
					</module>
					<module id="B011_1">
						
					</module>
					<module id="B011_2">
						<action id="readMember"/>
					</module>
					<module id="B011_2_1">
						<others/>
					</module>
					<module id="B011_3">
						<actions id="update"/>
					</module>
					<module id="B011_3_1">
						<others/>
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
						<action id="modify"/>
					</module>
					
					<module id="B0110">
						<others />
					</module>
						<module id="B0110_1">
						<others />
					</module>
						<module id="B0110_2">
						<action id="readMember"/>
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
						<others/>
					</module>
					<module id="B081">
						
						<action id="showModule"/>
						
					</module>
					<module id="B09"/>
					<module id="B11">
						<action id="update"/>
						<action id="print"/>
					</module>
					<module id="B1101">
						<action id="cancel"/>
					</module>
					<module id="B12">
						<others/>
					</module>
					   <module id="B34">
						<others />
					</module>
					<module id="B341">
						<action id="new"/>
						
					</module>
					<module id="B34101">
						<others />
					</module>
					<module id="B3410101">
					</module>
					<module id="B07">
						<others/>
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
		</apps>
		<storage acType="whitelist"> 
			<store id="chis.application.sch.schemas.SCH_SchistospmaRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.sch.schemas.SCH_SchistospmaVisit" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.manaUnitId'],['concat',['substring',['$','%user.manageUnit.id'], 0,9],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.sch.schemas.SCH_SchistospmaManage" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.inputUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.sch.schemas.SCH_SnailBaseInfomation" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like',['$','a.inputUnit'],['concat',['$','%user.manageUnit.id'],['s','%']]]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.hr.schemas.EHR_HealthRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
				</conditions>  
				<items> 
					<others acValue="1111"/> 
				</items> 
			</store>  
			<store id="chis.application.fhr.schemas.EHR_FamilyRecord" acValue="1111"> 
				<conditions> 
					<condition type="filter">['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']] ]</condition> 
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
			<others acValue="1111"/> 
		</storage>
	</accredit>
</role>
