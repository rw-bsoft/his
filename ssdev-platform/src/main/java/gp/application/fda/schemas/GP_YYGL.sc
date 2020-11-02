<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="GP_YYGL" alias="预约管理" sort="YYXH desc">
	<item id="YYXH" alias="预约序号" type="String" length="16" width="140"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="YYYS" alias="预约医生" type="string" length="18" defaultValue="%user.userId" queryable="true">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="LRSJ" alias="录入时间" type="date" display="0" defaultValue="%server.date.date"/>
	<item id="JKKH" alias="健康卡号" type="string" length="32" />
	<item id="BRXM" alias="患者姓名" type="string"  />
	<item id="YYSJ" alias="预约时间" type="date"  queryable="true"/>
	<item id="ZBLB" alias="值班类别" type="int" fixed="true" length="1">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="YYDZT" alias="预约单状态" type="int" length="1"
		defaultValue="0"  queryable="true">
		<dic>
			<item key="0" text="未执行" />
			<item key="1" text="已执行" />
		</dic>
	</item>	
	<item id="YYLX" alias="预约类型" type="int" length="1"
		defaultValue="0"  queryable="true">
		<dic>
			<item key="0" text="门诊预约" />
			<item key="1" text="门诊出诊" />
			<item key="2" text="家床预约" />
			<item key="3" text="随访预约" />
			<item key="4" text="转诊预约" />
		</dic>
	</item>
	<item id="LRRY" alias="录入人员" length="18"  type="string"  defaultValue="%user.userId">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	
	<item id="BRXB" alias="性别" type="int" length="1"  display="0">
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="CSRQ" alias="出生日期" type="date" display="0"/>
	<item id="LXDH" alias="联系电话" type="string" length="16" autoUpdate="true" display="0"/>
	<item id="JTDZ" alias="家庭地址" type="string" length="100" display="0"/>
	<item id="TD" alias="团队" type="string" length="50" fixed="true" display="0"/>
	<item id="JW" alias="居委" type="string" length="50" fixed="true" display="0"/>
	<item id="ZXRY" alias="执行人员" length="18" type="string" fixed="true" display="0">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="ZXSJ" alias="执行时间" type="date"/>
	<item id="SFZH" alias="身份证号" type="string" length="18" display="0"/>
	<item id="YBKH" alias="医保卡号" type="string" length="32" display="0"/>
	<item id="ZRYS" alias="责任医生" length="18"  type="string" fixed="true" display="0">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="MS" alias="描述" type="string" width="300" length="1000" display="0"/>
	<item id="XXLY" alias="信息来源" type="string" width="300" length="1" display="0">
		<dic>
			<item key="1" text="来电来访" />
			<item key="2" text="预约" />
		</dic>
	</item>
</entry>
