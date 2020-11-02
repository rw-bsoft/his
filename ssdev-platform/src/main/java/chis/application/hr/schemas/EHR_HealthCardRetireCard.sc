<?xml version="1.0" encoding="UTF-8"?>
<entry alias="医保卡">
	<item id="cardNum" alias="离休卡号" not-null="1"></item>
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
	<item id="phoneNumber" alias="家庭电话" type="string" length="20"
		not-null="1" />
	<item id="born" alias="出生地" type="string" length="20" />
	<item id="zz" alias="种族" defaultValue="1">
		<dic>
			<item key="1" text="黄色人种" />
			<item key="2" text="白色人种" />
			<item key="3" text="黑色人种" />
		</dic>
	</item>
	<item id="nationalityCode" alias="国籍" type="string" length="3"
		defaultValue="CN">
		<dic id="chis.dictionary.nationality" />
	</item>
	<item id="nationCode" alias="民族" type="string" length="2"
		not-null="1" defaultValue="01">
		<dic id="chis.dictionary.ethnic" />
	</item>
	<item id="bloodTypeCode" alias="血型" type="string" length="1"
		not-null="1" defaultValue="5">
		<dic id="chis.dictionary.blood"/>
	</item>
	<item id="company" alias="工作单位" type="string" length="20" colspan="2" />
	<item id="dwdz" alias="单位地址" type="string" length="20" />
	<item id="gzmc" alias="工作名称" type="string" length="20"
		defaultValue="4">
		<dic>
			<item key="1" text="科学研究人员" />
			<item key="2" text="工农技术人员" />
			<item key="3" text="技术管理和辅助" />
			<item key="4" text="不详" />
		</dic>
	</item>
	<item id="gzdm" alias="工作代码" type="string" length="20" />
	<item id="bgdh" alias="办公电话" type="string" length="20" />
	<item id="czhm" alias="传真号码" type="string" length="20" />
	<item id="email" alias="电子邮件" type="string" />
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="2"
		defaultValue="9" width="50">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1"
			onlySelectLeaf="true" />
	</item>

	<item id="zjxy" alias="宗教信仰" type="string" length="20"
		defaultValue="4">
		<dic>
			<item key="1" text="佛教" />
			<item key="2" text="道教" />
			<item key="3" text="伊斯兰教" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="lxzh" alias="离休证号" type="string" length="20" />
	<item id="lxgb" alias="离休干部" type="string" length="20" xtype="checkbox" />
	<item id="school" alias="学校" type="string" length="20" />
	<item id="grade" alias="年级" type="string" length="20" />
	<item id="class" alias="班级" type="string" length="20" />
	<item id="stuNo" alias="统一学籍号" type="string" length="20" />
	<item id="startdate" alias="开始日期" type="date" width="75"
		maxValue="%server.date.today"/>
	<item id="enddate" alias="结束日期" type="date" width="75"
		maxValue="%server.date.today"/>
</entry>





