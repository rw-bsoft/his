<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="GP_YYGL" alias="预约管理" sort="YYXH desc">
	<item id="YYXH" alias="预约序号" type="String" length="16" width="140"
		generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>	
	</item>
	<item id="JKKH" alias="健康卡号" type="string" length="32" queryable="true" selected="true"/>
	<item id="BRXM" alias="姓名" type="string" queryable="true"/>
	<item id="BRXB" alias="性别" type="int" length="1" width="40" >
		<dic id="gp.dictionary.gender"/>
	</item>
	<item id="CSRQ" alias="出生日期" type="date" />
	<item id="LXDH" alias="联系电话" type="string" length="16" autoUpdate="true" width="120"/>
	<item id="JTDZ" alias="家庭地址" type="string" length="100" width="200"/>
	<item id="WGDZ" alias="网格地址" type="string" length="25"  width="200"  update="false">
		<dic id="gp.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="GXJG" alias="管辖机构" type="string" length="20" width="180"  fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="gp.dictionary.manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="YYLX" alias="预约类型" type="int" length="1"
		defaultValue="0"  >
		<dic>
			<item key="0" text="门诊预约" />
			<item key="1" text="门诊出诊" />
			<item key="2" text="家床预约" />
			<item key="3" text="随访预约" />
			<item key="4" text="转诊预约" />
		</dic>
	</item>
	<item id="YYSJ" alias="预约时间" type="date" />
	<item id="ZBLB" alias="值班类别" type="int" fixed="true" length="1">
		<dic autoLoad="true">
			<item key="1" text="上午"/>
			<item key="2" text="下午"/>
		</dic>
	</item>
	<item id="YYYS" alias="预约医生" type="string" length="10" defaultValue="%user.userId" >
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="YYDZT" alias="预约单状态" type="int" length="1"
		defaultValue="0"  >
		<dic>
			<item key="0" text="未执行" />
			<item key="1" text="已执行" />
			<item key="2" text="取消" />
		</dic>
	</item>	
	<item id="YYKS" alias="预约科室" type="long" width="300"  display="0">
		<dic id="platform.reg.dictionary.officeDic" autoLoad="true"/>
	</item>
	<item id="ZXRY" alias="执行人员" length="18" type="string" fixed="true">
		<dic id="gp.dictionary.doctor" > 
		</dic>
	</item>
	<item id="ZXSJ" alias="执行时间" type="date"/>
	
	
	
	<item id="SFZH" alias="身份证号" type="string" length="18" display="0"/>
	<item id="YBKH" alias="医保卡号" type="string" length="32" display="0"/>
	<item id="LRRY" alias="录入人员" length="18"  type="string"  defaultValue="%user.userId" display="0">
		<dic id="gp.dictionary.doctor" filter ="['and',['eq',['$','item.properties.organizCode'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.logoff'],['s','1']]]"> 
		</dic>
	</item>
	<item id="LRSJ" alias="录入时间" type="date" display="0" defaultValue="%server.date.date"/>
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
