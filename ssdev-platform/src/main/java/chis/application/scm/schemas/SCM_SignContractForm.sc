<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_SignContractForm" tableName="chis.application.scm.schemas.SCM_SignContractRecord" alias="签约表">
	<item id="SCID" alias="签约记录编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
   <item id="favoreeEmpiId" alias="签约人主索引" not-null="1" type="string" length="32" display="0"/>
   <!-- enableKeyEvents="true" -->
   <item id="peopleFlag" alias="人群分类" type="string" not-null="1" length="2"  group="签约信息"  >
		<dic render="LovCombo">
			<item key="10" text="一般人群"/>
			<item key="03" text="老年人" ehrviewnav_key="B_07"/>
			<item key="02" text="0-6岁儿童" ehrviewnav_key="H_01"/>
			<item key="01" text="孕产妇"  ehrviewnav_key="G_01"/>
			<item key="04" text="高血压"  ehrviewnav_key="C_01"/>
			<item key="05" text="糖尿病"  ehrviewnav_key="D_01"/>
			<item key="07" text="结核病" />
			<item key="06" text="精障" ehrviewnav_key="P_01"/>
			<item key="08" text="残疾人" />
			<item key="13" text="建档立卡" />
			<item key="09" text="计生特殊" />
			<item key="18" text="计生特别扶助人员" />
			<item key="11" text="城乡低保" />
			<item key="19" text="城乡特困" />
			<item key="12" text="优抚对象" />
			<item key="15" text="白血病患者"/>
			<item key="16" text="麻风病"/>
			<item key="17" text="离休干部"/>
			<item key="20" text="肿瘤患者"/>
			<item key="21" text="创新创业人才"/>
			<item key="22" text="慢阻肺患者"/>
			<item key="14" text="其他"/>
		</dic>
	</item>
    <item id="SecondPartyEmpiId" alias="签约代表主索引" type="string" length="32"  display="0"/>
    <item id="SecondPartyName" alias="签约代表" type="string"  length="60" display="0"/>
    <!--<item id="intendedPopulation" alias="签约人所属人群" type="string" length="20" not-null="1" queryable="true" width="300">
		<dic id="chis.dictionary.intendedPopulation" render="LovCombo"/>
	</item>-->
	<item id="scDate" alias="签约日期" type="date" not-null="1" defaultValue="%server.date.today" group="签约信息" display="2"/>
	<item id="year" alias="签约周期" type="string" length="20" not-null="1" queryable="true"  defaultValue="1" width="300">
		<dic id="chis.dictionary.signYear"/>
	</item>
	<item id="signFlag" alias="签约状态" type="string" length="1" defaultValue="1" not-null="1" fixed="true"  group="签约信息">
		<dic>
			<item key="1" text="签约" />
			<item key="2" text="解约" />
			<!-- ** -->
			<item key="3" text="续约" />
			<!-- *zhj* -->
			<item key="4" text="待审核" />
		</dic>
	</item>
	<item id="app" alias="签约来源" type="string" length="1" display="0" queryable="true" defaultValue="0" fixed="true" group="签约信息" >
		<dic>
			<item key="0" text="电脑" />
			<item key="1" text="健康溧水APP（居民）" />
			<item key="2" text="健康溧水APP（医生）" />
		</dic>
	</item>
   	<item id="stopDate" alias="解约日期" type="date" group="签约信息" display="2" fixed="true"/>
   	<item id="stopReason" alias="解约原因" type="string" length="2" group="签约信息"  fixed="true">
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
    <item id="createUser" alias="签约医生" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
        <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
        <set type="exp">['$','%user.userId']</set>
    </item>
    <item id="createUnit" alias="签约团队" type="string" length="20" not-null="1"  width="180"  defaultValue="%user_scm.manageUnit.id">
        <dic id="chis.dictionary.user_scm" render="Tree"  parentKey="%user.userId"/>
        <!-- <set type="exp">['$','%user_scm.manageUnit']</set> -->
    </item>
     <item id="operator" alias="录入人" type="string" length="20" update="false" display="1" fixed="true" defaultValue="%user.userId" queryable="true">
        <dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"  parentKey="%user.manageUnit.id"/>
        <set type="exp">['$','%user.userId']</set>
    </item>
    <item id="operatorUnit" alias="录入单位" type="string" length="20" update="false" width="180" display="1" fixed="true" defaultValue="%user.manageUnit.id">
       <dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
    </item>
	<item id="favoreeName" alias="签约人" type="string"   length="50" fixed="true" />
	<item id="payOrNot" alias="是否收费" type="string" length="20">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />			
		</dic>
	</item>
	<item id="payOrNot" alias="是否收费" type="string" length="20">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />			
		</dic>
	</item>
	<item id="fphm" alias="发票号码" type="string" display="0" length="20" fixed="true"/>
	<item id="agreement" alias="协议书已阅" type="string" display="0" defaultValue="true" xtype="checkbox" length="10" mustchecked = "true"/>
<!--	<item id="agreement" alias="协议书已阅" type="string" not-null="1" xtype="checkbox" length="10" mustchecked = "true"/>-->
	<item id="remark" alias="备注" type="string" length="200"  colspan="2"/>
	<item id="signServicePackages" alias="已签约服务包" type="string" length="150"  colspan="1"/>
</entry>
