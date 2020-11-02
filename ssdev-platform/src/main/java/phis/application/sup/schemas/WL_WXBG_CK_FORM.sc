<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WXBG_CK_FORM" tableName="WL_WXBG" alias="维修报告(WL_WXBG)">
	<item id="WXXH" alias="维修序号" type="long" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase"  startPos="1" />
		</key>
	</item>
	
	<item id="GZMS" alias="故障描述" length="200" colspan="3" xtype="textarea"/>
	<item id="SYKS" alias="申请科室"  type="long" length="18">
		<dic id="phis.dictionary.wl_hsqx" filter="['and',['eq',['$','item.properties.YGID'],['$','%user.userId']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" /> 
	</item>
	<item id="JJCD" alias="紧急程度" type="int" defaultValue="1" length="1">
		<dic>
			<item key="1" text="一般"/>
			<item key="2" text="紧急"/>
		</dic>
	</item>
	<item id="KFXH" alias="维修库房" fixed="true" type="int" length="8">
		<dic id="phis.dictionary.treasury" filter="['and',['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']],['eq',['$','item.properties.WXKF'],['i',1]]]" autoLoad="true" />
	</item>
	<item id="WXDW" alias="维修单位" type="long" length="18" display="0"/>
	<item id="SQGH" alias="申请人员"  length="10">
		<dic id="phis.dictionary.doctor" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="LXDH" alias="联系电话" length="20"/>
	<item id="SXRQ" alias="送修时间"  type="date"/>
	<item id="BZXX" alias="备注信息" length="200" colspan="3" xtype="textarea"/>
	
	
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="ZBXH" alias="帐薄序号" length="18" display="0"/>
	<item id="WZXH" alias="物资序号" length="12" display="0"/>
  
	<item id="JSCD" alias="及时程度" fixed="true" length="1">
		<dic>
			<item key="1" text="非常不及时"/>
			<item key="2" text="不及时"/>
			<item key="3" text="一般"/>
			<item key="4" text="及时"/>
			<item key="5" text="非常及时"/>
		</dic>
	</item>
	<item id="MYCD" alias="满意程度" fixed="true" length="1">
		<dic>
			<item key="1" text="非常不满意"/>
			<item key="2" text="不满意"/>
			<item key="3" text="一般"/>
			<item key="4" text="满意"/>
			<item key="5" text="非常满意"/>
		</dic>
	</item>
</entry>
