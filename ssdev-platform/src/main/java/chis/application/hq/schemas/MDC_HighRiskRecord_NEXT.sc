<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hq.schemas.MDC_HighRiskRecord_NEXT" alias="高危档案" >
	<item id="PHRID" pkey="true" alias="档案编号" type="string" length="30" width="165" queryable="true" fixed="true" update="false" />
	<item id="DEFINEPHRID" alias="自定义档案编号" type="string" length="60" display="1" queryable="true"/>
	<item id="PERSONNAME" alias="姓名" type="string" length="20" display="1" queryable="true"/>
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40"  display="1" queryable="true" not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="BIRTHDAY"  alias="出生日期" type="date" width="75" display="1" queryable="true"/>
	<item id="IDCARD" alias="身份证号" type="string" length="20" width="160" display="1" queryable="true"/>
	<item id="ADDRESS" alias="现住址" type="string" length="100" width="200" display="1" />
	<item id="MQZT" alias="目前状态" type="string" length="1" queryable="true" defaultValue="0">
		<dic id="chis.dictionary.mqzt"/>
	</item>
	<item id="MOBILENUMBER" alias="本人电话" type="string" length="20" display="1" queryable="true"/>
	<item id="REGISTEREDPERMANENT" alias="常住类型" type="string" length="1" not-null="1" display="0" queryable="true" defaultValue="1">
		<dic id="chis.dictionary.registeredPermanent"/>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" not-null="1" width="300" colspan="2" anchor="100%" display="1" queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="FAMILYDOCTORID" alias="家庭医生" type="string" length="20" display="1" queryable="true" update="false" fixed="true">
		<dic id="chis.dictionary.familydoctor" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="EMPIID" alias="empiid" type="string" length="32" fixed="true" colspan="3" hidden="true"/>
	<item id="MANADOCTORID" alias="责任医生" type="string" length="20" not-null="1" queryable="true" >
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="['substring',['$','%user.manageUnit.id'],0,9]"/>
	</item>
	<item id="MANAUNITID" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="HIGHRISKTYPE" alias="高危类型" type="string" length="1" >
		<dic id="chis.dictionary.HIGHRISK" render="LovCombo" />
	</item>
	<item id="FINDWAY" alias="发现途径" type="string" length="1" defaultValue="1">
		<dic id="chis.dictionary.findway" />
	</item>
	<item id="CONSTRICTION" alias="收缩压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="DIASTOLIC" alias="舒张压(mmHg)" not-null="1" type="int" minValue="50" maxValue="500" enableKeyEvents="true"
		validationEvent="false" />
	<item id="ISSMOKE" alias="是否吸烟" type="string" length="1" defaultValue="0">
		<dic id="chis.dictionary.oneyeszerono" />
	</item>
	<item id="SMOKECOUNT" alias="日吸烟量" type="int" />
	<item id="FBS" alias="空腹血糖(mmol/L)" type="double" length="4" minValue="1" maxValue="20" />
	<item id="TC" alias="总胆固醇(mmol/L)" type="double" precision="2" length="4" minValue="1" maxValue="20" />
	<item id="WAISTLINE" alias="腰围(cm)" type="double" length="8" minValue="40" maxValue="200" />
	<item id="HJX" alias="合计项" width="100" type="int" queryable="true" />
	<item id="CREATEUNIT" alias="建档机构" type="string" length="20" update="false" queryable="true" fixed="true" width="165" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="CREATEUSER" alias="建档人员" type="string" length="20" update="false" fixed="true" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="CREATEDATE" alias="建档日期" type="date" update="false" fixed="true" defaultValue="%server.date.today" queryable="true">
		<set type="exp">['$','%server.date.today']</set>
	</item>
	<item id="STATUS" alias="档案状态" type="string" length="1" defaultValue="0" display="0" fixed="true">
		<dic>
			<item key="0" text="正常" />
			<item key="1" text="已注销" />
		</dic>
	</item>
	<item id="CANCELLATIONDATE" alias="档案注销日期" type="date" display="2" fixed="true"/>
	<item id="CANCELLATIONREASON" alias="档案注销原因" type="string" length="1" display="2" fixed="true">
		<dic>
			<item key="1" text="死亡"/>
			<item key="2" text="迁出"/>
			<item key="3" text="失访"/>
			<item key="4" text="拒绝"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="CANCELLATIONUSER" alias="注销人" type="string" length="20" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="LASTMODIFYDATE" alias="最后修改日期" type="datetime"  xtype="datefield"  defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="LASTMODIFYUNIT" alias="修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="CANCELLATIONUNIT" alias="注销单位" type="string" length="20" width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
	</item>
	<item id="REGIONCODE_TEXT" alias="网格地址" type="string" length="200" display="0"/>
</entry>
