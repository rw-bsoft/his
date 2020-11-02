<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_FamilyMemberRecord" alias="家庭成员" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="relaCode" alias="与户主关系" type="string"	>
		<dic id="chis.dictionary.relaCode" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="personName" alias="姓名" type="string"/>
	<item id="sexCode" alias="性别" type="string">
		<dic>
			<item key="1" text="男"/>
			<item key="2" text="女"/>
		</dic>
	</item>
	<item id="birthday" alias="出生年月" type="string"/>
	
	<item id="relation" alias="居住状况" type="string">
		<dic>
			<item key="1" text="长住"/>
			<item key="2" text="空关户"/>
			<item key="3" text="长外出"/>
			<item key="4" text="蓝印"/>
			<item key="5" text="长临"/>
			<item key="6" text="临时"/>
			<item key="7" text="1个月内"/>
			<item key="8" text="不详"/>
		</dic>
	</item>
	<item id="idNo" alias="身份证号码" type="string" width="150"/>
	<item id="phoneNo" alias="联系电话(手机)" type="string" width="150"/>
	<item id="telNo" alias="家庭电话(固定)" type="string" width="150"/>
	<item id="isLogout" alias="是否注销" type="string">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item id="isNewCard" alias="新健康卡" type="string">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
</entry>
