<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WXBG_WX_FORM" tableName="WL_WXBG" alias="维修报告(WL_WXBG)">
	<item id="WXXH" alias="维修序号" type="long" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase"  startPos="1" />
		</key>
	</item>
 
	<item id="SYKS" alias="申请科室"  not-null="1" fixed="true" type="long" length="18">
		<dic id="phis.dictionary.department" defaultIndex="0" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic> 
	</item>
	<item id="SQGH" alias="申请人员" not-null="1" fixed="true" length="10">
		<dic id="phis.dictionary.wzdoctor_yjqx" defaultIndex="0" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="LXDH" alias="联系电话" length="20" fixed="true"/>
	<item id="SXRQ" alias="申请日期" type="date" fixed="true"/>
	<item id="GZMS" alias="故障描述" not-null="1" length="200" colspan="4" fixed="true" xtype="textarea"/>
	<item id="GZNR" alias="工作内容" length="200" colspan="4" not-null="1" xtype="textarea"/>
	<item id="WXLB" alias="维修类别"  type="int" defaultValue="1"  length="1">
		<dic>
			<item key="1" text="内维修"/>
			<item key="2" text="外维修"/>
		</dic>
	</item>
	<item id="KSRQ" alias="维修开始日期" type="date" defaultValue="%server.date.date"/>
	<item id="JSRQ" alias="维修结束日期" type="date" defaultValue="%server.date.date"/>
	<item id="WXSJ" alias="维修时间"  length="8" type="double" not-null="1" precision="2" defaultValue="1"/>
	<item id="WXFY" alias="维修费用"  type="double" defaultValue="0" length="18" precision="4"/>
	<item id="CLFY" alias="材料费用"  type="double" fixed="true" defaultValue="0" length="18" precision="4"/>
	<item id="ZJFY" alias="总计费用"  type="double" defaultValue="0" display="0"  length="18" precision="4"/>
	<item id="FPHM" alias="发票号码"  length="30"/>
  
	<item id="FZGH" alias="维修负责人" not-null="1" length="10">
		<dic id="phis.dictionary.wzdoctor_yjqx"  filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="WXRC" alias="维修人次"   type="int"  length="4"/>
	<item id="WXZT" alias="维修状态" type="int" length="1" readOnly="readOnly">
		<dic id="phis.dictionary.repairStatus" />
	</item>
	<item id="SBWX" alias="设备维修" type="int" length="1" xtype="checkbox" virtual="true"/>
	<item id="SHGH" alias="审核人员" length="10"  display="0" >
		<dic id="phis.dictionary.wzdoctor_yjqx" defaultIndex="0" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="SHRQ" alias="审核日期" type="date"  display="0"/>
	<item id="YSGH" alias="验收人员" length="10"  display="0">
		<dic id="phis.dictionary.wzdoctor_yjqx" defaultIndex="0" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="YSRQ" alias="验收日期" type="date"  display="0"/>
 
</entry>
