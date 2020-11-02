<?xml version="1.0" encoding="UTF-8"?>
<entry alias="健康卡发放">
	<item id="cardNum" alias="全国卡号" not-null="1"></item>
	<item id="personName" alias="姓名" type="string" length="20"
		not-null="1" />
	<item id="fullPersonName" alias="全名" type="string" length="20" />
	<item id="birthday" alias="出生日期" type="date" not-null="1"
		maxValue="%server.date.today" />
	<item id="sexCode" alias="性别" type="string" length="1" width="40"
		not-null="1" defalutValue="9">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="cardtype" alias="身份证件类型" type="string" width="150"
		not-null="1" length="25" defaultValue="01" group="基本信息">
		<dic id="platform.reg.dictionary.cardtype" />
	</item>
	<item id="cardnum" alias="身份证件号码" type="string" width="150"
		not-null="1" length="25"/>
	<item id="brxz" alias="病人性质" defaultValue="4">
		<dic>
			<item key="1" text="流动人口"></item>
			<item key="2" text="计划生育"></item>
			<item key="3" text="伤残"></item>
			<item key="4" text="普通自费"></item>
			<item key="5" text="帮困"></item>
			<item key="6" text="离休"></item>
		</dic>
	</item>
	<item id="yx" alias="RH 阴性" type="string">
		<dic>
			<item key="1" text="是" />
			<item key="2" text="否" />
			<item key="3" text="不详" />
		</dic>
	</item>
	<item id="address" alias="地址" colspan="2" not-null="1"></item>
	<item id="address2" alias="户籍地址"></item>
	<item id="center" alias="服务中心"></item>
	<item id="team" alias="服务团队" not-null="1"></item>
	<item id="juwei" alias="居委" not-null="1"></item>
	<item id="daly" alias="档案来源" not-null="1" defaultValue="签约发卡" fixed="true"></item>
	<item id="hklx" alias="户口类型" defaultValue="1">
		<dic>
			<item key="1" text="本区本镇" />
			<item key="2" text="本区外镇" />
			<item key="3" text="外区" />
			<item key="4" text="外地" />
		</dic>
	</item>
	<item id="lhrq" alias="来沪日期" type="date" fixed="true"></item>
	<item id="mobileNumber" alias="手机号码" type="string" length="20"
		not-null="1" />
	<item id="bankAccount" alias="银行卡号" type="string" length="20"
		/>
	<item id="operator" alias="建档人" type="string" length="20" />
	<item id="doctor" alias="责任医生" type="string" length="20" />
</entry>





