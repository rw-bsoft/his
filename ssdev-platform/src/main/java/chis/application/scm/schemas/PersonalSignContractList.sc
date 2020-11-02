<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.PersonalSignContractList" tableName="chis.application.scm.schemas.SCM_SignContractRecord" sort="endDate desc,createDate desc" alias="签约表">
	<item id="SCID" alias="签约记录编号" type="string" length="20" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="signFlag" alias="状态" type="string" length="1" defaultValue="1" not-null="1" fixed="true" group="签约信息" width="35" renderer="showColor">
		<dic>
			<item key="1" text="签约" />
			<item key="2" text="解约" />
			<!-- ** -->
			<item key="3" text="续约" />
			<!-- *zhj* -->
			<item key="4" text="待审核" />
		</dic>
	</item>
	<item id="app" alias="签约来源" type="string" length="1" defaultValue="0" not-null="1" fixed="true" group="签约信息" >
		<dic>
			<item key="0" text="电脑" />
			<item key="1" text="健康溧水APP（居民）" />
			<item key="2" text="健康溧水APP（医生）" />
		</dic>
	</item> 
		<item id="peopleFlag" alias="人群分类" type="string" not-null="1" length="2"  group="签约信息" width="60">
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
	<item id="familyId" alias="所属家庭" type="string" length="20" display="0"/>
	<item id="beginDate" alias="开始日期" type="date" not-null="1" minValue="%server.date.today" group="签约信息"/>
	<item id="endDate" alias="结束日期" type="date" not-null="1" minValue="%server.date.today" group="签约信息"/>
	<item id="scDate" alias="签约日期" type="date" not-null="1" defaultValue="%server.date.today" group="签约信息" display="1"/>
	<item id="createDate" alias="录入日期" update="false" type="datetime" display="1" width="140"/>
	<item id="stopDate" alias="解约日期" type="date" minValue="%server.date.today" group="签约信息" display="1"/>
	<item id="stopReason" alias="解约原因" type="string" length="2" group="签约信息" display="0">
		<dic>
			<item key="01" text="失访" />
			<item key="02" text="迁出" />
			<item key="03" text="死亡  " />
			<item key="04" text="本人要求  " />
			<item key="05" text="改签" />
			<item key="06" text="发票作废" />
			<item key="99" text="其他" />
		</dic>
	</item>
	<item id="favoreeEmpiId" alias="签约人主索引" not-null="1" type="string" length="32" display="0"/>
	<item id="favoreeName" alias="签约人" type="string" not-null="1" xtype="lookupfieldex" length="50"  group="签约确认" display="0"/>
	<item id="favoreePhone" alias="签约人电话" type="string" length="20" group="签约确认" display="0"/>
	<item id="favoreeMobile" alias="签约人手机" type="string" length="20" group="签约确认" display="0"/>
	<item id="intendedPopulation" alias="签约人所属人群" type="string" length="20" not-null="1" width="300" colspan="3" group="签约确认" display="0">
		<dic id="chis.dictionary.intendedPopulation" render="LovCombo"/>
	</item>
	<item id="year" alias="签约周期" type="string" length="20" not-null="1"  width="300" colspan="2" group="签约确认" display="0">
		<dic id="chis.dictionary.signYear"/>
	</item>
	<item id="firstPartyName" alias="甲方确认人" type="string" length="60" not-null="1" group="签约确认" display="0"/>
	<item id="SecondPartyEmpiId" alias="乙方确认人主索引" type="string" length="32" not-null="1" display="0"/>
	<item id="SecondPartyName" alias="乙方确认人" type="string" xtype="lookupfieldex" length="60" not-null="1" colspan="2" group="签约确认" display="0"/>
	<item id="createUser" alias="签约医生" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" group="签约确认" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createUnit" alias="签约单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id" colspan="2" group="签约确认" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="payOrNot" alias="是否收费" type="string" length="20">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />			
		</dic>
	</item>
	<item id="fphm" alias="发票号码" type="string" display="0" length="20" fixed="true"/>
	<item id="remark" alias="备注" type="string" length="200" not-null="1" fixed="true"/>
</entry>