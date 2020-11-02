<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_SignContractForm" tableName="chis.application.scm.schemas.SCM_StopSignContractForm" alias="解约信息">
	<item id="signFlag" alias="签约状态" type="string" length="1" defaultValue="2" not-null="1" fixed="true" colspan="2">
		<dic>
			<item key="1" text="签约" />
			<item key="2" text="解约" />
			<!-- ** -->
			<item key="3" text="续约" />
			<!-- *zhj* -->
		</dic>
	</item>
	<item id="peopleFlag" alias="人群分类" type="string" not-null="1" length="2"  group="签约信息"  >
		<dic render="LovCombo">
			<item key="10" text="一般人群" />
			<item key="03" text="60岁及以上老年人"/>
			<item key="02" text="0-6岁儿童"/>
			<item key="01" text="孕产妇" />
			<item key="04" text="高血压患者" />
			<item key="05" text="2型糖尿病患者" />
			<item key="07" text="肺结核患者" />
			<item key="06" text="严重精神障碍患者" />
			<item key="08" text="残疾人" />
			<item key="13" text="农村建档立卡低收入人口" />
			<item key="09" text="计划生育特殊家庭人员" />
			<item key="18" text="计生特别扶助人员" />
			<item key="11" text="城乡低保人员" />
			<item key="19" text="城乡特困人员" />
			<item key="12" text="重点优抚对象" />
            <item key="15" text="白血病患儿"/>
            <item key="16" text="麻风病患者"/>
            <item key="17" text="离休干部"/>
            <item key="20" text="肿瘤患者"/>
            <item key="21" text="创新创业人才"/>
            <item key="22" text="慢阻肺"/>
            <item key="14" text="其他"/>
		</dic>
	</item>
   	<item id="stopDate" alias="解约日期" type="date"  display="2" colspan="2"/>
   	<item id="stopReason" alias="解约原因" type="string" length="2" display="2" colspan="2">
		<dic>
			<item key="01" text="失访" />
			<item key="02" text="迁出" />
			<item key="03" text="死亡" />
			<item key="04" text="本人要求" />
			<item key="05" text="改签" />
			<item key="06" text="发票作废" />
			<item key="99" text="其他" />
		</dic>
	</item>
</entry>
