<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_WXBG_SQ" tableName="WL_WXBG" alias="维修报告(WL_WXBG)">
	<item id="WXXH" alias="维修序号" type="long" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase"  startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" length="8" type="int"  display="0">
		<dic id="phis.dictionary.treasury" filter="['and',['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']],['eq',['$','item.properties.WXKF'],['i',1]]]" autoLoad="true" />
	</item>
	<item id="ZBXH" alias="帐薄序号" type="long" length="18" display="0"/>
	<item id="WZXH" alias="物资序号" type="long" length="12" display="0"/>
	<item id="CJXH" alias="厂家序号" type="long" length="18" display="0"/>
  
  
	<item id="WXZT" alias="维修状态" type="int" length="1">
		<dic>
			<item key="0" text="提交"/>
			<item key="1" text="待修/在修"/>
			<item key="2" text="完修"/>
		</dic>
	</item>
	<item id="SYKS" alias="申请科室" type="long" length="18">
		<dic id="phis.dictionary.department" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic> 
	</item>
	<item id="SQGH" alias="申请人员" length="10">
		<dic id="phis.dictionary.wzdoctor_yjqx" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="LXDH" alias="联系电话" length="20"/>
	<item id="SXRQ" alias="送修时间" type="date"/>
	<item id="GZMS" alias="故障描述" length="200"/>
	<item id="FZGH" alias="维修负责人" defaultValue="%user.userId" length="10">
		<dic id="phis.dictionary.wzdoctor_yjqx" filter = "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYDM">
		</dic>
	</item>
	<item id="KSRQ" alias="维修开始日期" type="date"/>
	<item id="JSRQ" alias="维修结束日期" type="date"/>
	<item id="WXSJ" alias="维修工时" length="8" type="double" precision="2"/>
	<item id="ZJFY" alias="总计费用"  type="double"  length="18" precision="4"/>
	<item id="WXBH" alias="维修编号" length="20" display="0"/>
   
   
   
	<item id="QYRQ" alias="启用日期" type="date"  display="0"/>
	<item id="JJCD" alias="紧急程度" type="int" length="1"  display="0">
		<dic>
			<item key="1" text="一般"/>
			<item key="2" text="紧急"/>
		</dic>
	</item>
	<item id="GZXZ" alias="工作性质" length="200"  display="0"/>
	<item id="GZXX" alias="故障现象" length="200"  display="0"/>
	<item id="GZYY" alias="故障原因" length="200"  display="0"/>
	<item id="GZNR" alias="工作内容" length="200"  display="0"/>
	<item id="SBXZ" alias="设备现状" type="int"  length="2"  display="0"/>
	<item id="WXLB" alias="维修类别"  type="int"  length="1"  display="0"/>
	<item id="WXDW" alias="维修单位" type="long"  length="18"  display="0">
		<dic id="phis.dictionary.supplyUnit"  filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>
	<item id="WXRC" alias="维修人次" type="int"  length="4"  display="0"/>
  
 
	<item id="WXFY" alias="维修费用"  type="double"  length="18" precision="4" display="0"/>
	<item id="CLFY" alias="材料费用"  type="double" length="18" precision="4" display="0"/>
 
	<item id="FPHM" alias="发票号码" length="30" display="0"/>
	<item id="TJRQ" alias="提交日期" type="date" display="0"/>
	<item id="SHGH" alias="审核人员" length="10" display="0"/>
	<item id="SHRQ" alias="审核日期" type="date" display="0"/>
	<item id="YSGH" alias="验收人员" length="10" display="0"/>
	<item id="YSRQ" alias="验收日期" type="date" display="0"/>
	<item id="ZFGH" alias="作废人员" length="10" display="0"/>
	<item id="ZFRQ" alias="作废日期" type="date" display="0"/>
  
  
  
	<item id="GLXH" alias="关联序号" type="long" length="18"  display="0"/>
	<item id="GLLB" alias="关联类别" type="int" length="1"  display="0"/>
	<item id="BZXX" alias="备注信息" length="200" display="0"/>
	<item id="JSCD" alias="及时程度" type="int" length="1" display="0">
		<dic>
			<item key="1" text="非常不及时"/>
			<item key="2" text="不及时"/>
			<item key="3" text="一般"/>
			<item key="4" text="及时"/>
			<item key="5" text="非常及时"/>
		</dic>
	</item>
	<item id="MYCD" alias="满意程度" type="int" length="1" display="0">
		<dic>
			<item key="1" text="非常不满意"/>
			<item key="2" text="不满意"/>
			<item key="3" text="一般"/>
			<item key="4" text="满意"/>
			<item key="5" text="非常满意"/>
		</dic>
	</item>
</entry>
