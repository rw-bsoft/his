<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="GP_YYGL_FORM" tableName="GP_YYGL" alias="预约管理" sort="YYXH desc">
	<item id="YYXH" alias="预约序号" type="String" length="16"
		generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	
	<item id="SFZH" alias="身份证号" type="string" length="18" />
	<item id="JKKH" alias="健康卡号" type="string" length="32" />
	<item id="BRXM" alias="姓名" type="string" fixed="true"/>
	<item id="BRXB" alias="性别" type="int" length="1"  defaultValue="9" fixed="true">
		<dic autoLoad="true" id="gp.dictionary.gender"/>
	</item>
	<item id="CSRQ" alias="出生日期" type="date" autoUpdate="true" fixed="true"/>
	<item id="LXDH" alias="联系电话" type="string" length="16" autoUpdate="true" fixed="true"/>
	<item id="JTDZ" alias="家庭地址" type="string" length="100" fixed="true"/>
	<item id="WGDZ" alias="网格地址" type="string" length="25"  width="200" fixed="true">
		<dic id="gp.dictionary.areaGrid" autoLoad="true"/>
	</item>
	<item id="GXJG" alias="管辖机构" type="string" length="20" width="180"  fixed="true">
		<dic id="gp.dictionary.manageUnit" autoLoad="true"/>
	</item>
	<item id="XXLY" alias="信息来源" type="string" width="300" length="1" fixed="true">
		<dic>
			<item key="1" text="来电来访" />
			<item key="2" text="预约" />
		</dic>
	</item>
	<item id="YYSJ" alias="预约时间" type="date"  queryable="true" fixed="true"  autoUpdate="true"/>
	<item id="ZBLB" alias="值班类别" type="int" fixed="true" length="1">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="YYYS" alias="预约医生" type="string" length="18" queryable="true" defaultValue="%user.userId">
		<dic id="gp.dictionary.relevanceDoctor" filter ="['eq',['$','item.properties.fda'],['s','%user.userId']]" > 
		</dic>
	</item>
	<item id="LRRY" alias="录入人员" type="string" length="18" defaultValue="%user.userId" fixed="true">
		<dic id="gp.dictionary.doctor"> 
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
	<item id="MS" alias="描述" type="string" width="300" length="1000" colspan="3"/>
	
	<item id="ZRYS" alias="责任医生" length="18" type="string" fixed="true"  display="0">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="YYKS" alias="预约科室" type="long" display="0"/>
	<item id="YYDZT" alias="预约单状态" type="int" length="1"
		defaultValue="0"  queryable="true" display="0">
		<dic>
			<item key="0" text="未执行" />
			<item key="1" text="已执行" />
			<item key="2" text="取消" />
		</dic>
	</item>	
	<item id="ZXRY" alias="执行人员" length="18" type="string" fixed="true" display="0">
		<dic id="gp.dictionary.doctor"> 
		</dic>
	</item>
	<item id="ZXSJ" alias="执行时间" type="date" display="0"/>
	
</entry>
