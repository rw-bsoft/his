<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hq.schemas.HC_HealthCheck" alias="基本情况">
	<item id="HEALTHCHECK" alias="检查单号" length="16" width="130"
		type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="CREATEUNIT" alias="体检单位" length="20" queryable="true" type="string" display="0" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="EMPIID" alias="EMPIID" length="32" type="string" hidden="true" />
	<item id="PHRID" alias="健康档案号" length="30" type="string"  hidden="true" />
	<item id="PERSONNAME"  alias="姓名" type="string" length="20" queryable="true" />
	<item id="SEXCODE" alias="性别" type="string" length="1" width="40" queryable="true" >
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="BIRTHDAY" alias="出生日期" type="date" width="75" />
	<item id="IDCARD" alias="身份证号" type="string" length="20" width="160" queryable="true" />
	<item id="MOBILENUMBER" alias="本人电话" type="string" length="20" />
	<item id="CONTACTPHONE" alias="联系人电话" type="string" length="20" />
	<item id="ADDRESS" alias="现住址" type="string" length="30" queryable="true" />
	<item id="MQZT" alias="目前状态" type="string" length="1" queryable="true" defaultValue="0">
		<dic id="chis.dictionary.mqzt"/>
	</item>
	<item id="REGIONCODE" alias="网格地址" type="string" length="25" not-null="1" width="300" colspan="2" anchor="100%" display="1" queryable="true">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="CHECKDATE" alias="体检日期" type="date" queryable="true" />
	<item id="BREATHE" alias="呼吸(次/分)" type="int" maxValue="100" />
	<item id="PULSE" alias="脉率(次/分)" type="int" maxValue="200" />
	<item id="WAISTLINE" alias="腰围(cm)" length="8" type="double" />
	<item id="CONSTRICTION_L" alias="收缩压L(mmHg)" width="110" type="int" />
	<item id="DIASTOLIC_L" alias="舒张压L(mmHg)" width="110" type="int" />
	<item id="CONSTRICTION" alias="收缩压R(mmHg)" width="110" type="int" />
	<item id="DIASTOLIC" alias="舒张压R(mmHg)" width="110" type="int" />
	<item id="FBS" alias="空腹血糖"  type="double"/>
	<item id="TC" alias="血脂" type="double" />
	<item id="SFXY" alias="是否吸烟" length="1" type="string" >
		<dic>
			<item key="1" text="从不吸烟"/>
			<item key="2" text="已戒烟"/>
			<item key="3" text="吸烟"/>
		</dic>
	</item>
	<item id="XYGZ" alias="血压高值" width="100" type="string" queryable="true" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="XZXY" alias="现在吸烟" width="100" type="string" queryable="true" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="XTGZ" alias="血糖高值" width="100" type="string" queryable="true" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="XZGZ" alias="血脂高值" width="100" type="string" queryable="true" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="YWCB" alias="腰围超标" width="100" type="string" queryable="true" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="HJX" alias="合计项" width="100" type="int" queryable="true" />
	<item id="FXTJ" alias="发现途径" width="100" type="string" />
	<item id="MANAUNITID" alias="管辖机构" type="string" length="20" width="165" fixed="true" queryable="true" >
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="SFJD" alias="是否建档" width="100" type="string" >
		<dic id="chis.dictionary.oneyeszerono"/>
	</item>
	<item id="JDSJ" alias="建档时间" type="date" width="75" />
</entry>
