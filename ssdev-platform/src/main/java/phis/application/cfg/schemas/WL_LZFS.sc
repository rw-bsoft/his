<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_LZFS" alias="流转方式" sort="FSPX">
	<item id="FSXH" alias="方式序号" type="long" length="12" not-null="1" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" type="increase" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="BLSX" alias="保留属性" type="int" length="1" display="0">
		<dic>
			<item key="0" text="自定义"/>
			<item key="1" text="系统保留"/>
		</dic>
	</item>	
  
	<item id="DJNF" alias="单据年份" type="int" length="1" display="0">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>	
	<item id="DJYF" alias="单据月份" type="int" length="1" display="0">
		<dic>
			<item key="0" text="无"/>
			<item key="1" text="有"/>
		</dic>
	</item>	
	<item id="XHCD" alias="序号长度" type="int" length="2" display="0"/>
	<item id="DJQZ" alias="单据前缀" length="10" display="0"/>  
    
	<item id="FSMC" alias="方式名称" length="30" width="120" not-null="1"/>
	<item id="KFXH" alias="库房名称" type="int"  fixed="true" autoLoad="true" length="8"  display="2">
		<dic id="phis.dictionary.WL_KFXX_HSLB" autoLoad="true"/>
	</item>
	<item id="DJLX" alias="单据类型" length="6" not-null="1" fixed="true">
		<dic id="phis.dictionary.circulationShowModules" autoLoad="true">
		</dic> 
	</item>
	<item id="YWLB" alias="业务类别" type="int" length="1" not-null="1">
		<dic autoLoad="true">
			<item key="1" text="增"/>
			<item key="-1" text="减"/>
			<item key="0" text="其他"/>
		</dic>
	</item>
	<item id="SYKF" alias="使用库房" type="int" length="1" display="1">
		<dic>
			<item key="1" text="一级库房"/>
			<item key="2" text="二级库房"/>
		</dic>
	</item>	
	<item id="JZBZ" alias="记帐标志"  fixed="true" type="int" length="1">
		<dic autoLoad="true">
			<item key="0" text="不建账"/>
			<item key="1" text="科室建账"/>
		</dic>
	</item>	
	<item id="ZHZT" alias="资产转后状态" width="100" fixed="true" type="int" length="2">
		<dic autoLoad="true">
			<item key="0" text="在库"/>
			<item key="1" text="在用"/>
			<item key="-1" text="封存"/>
			<item key="-2" text="报损"/>
			<item key="-3" text="其他减少"/>
		</dic>
	</item>  
	<item id="FSPX" alias="方式排序" type="int" length="4"/>
	<item id="FSZT" alias="方式状态" type="int" defaultValue="1" fixed="true" length="1" not-null="1">
		<dic autoLoad="true">
			<item key="-1" text="注销"/>
			<item key="1" text="在用"/>
		</dic>
	</item>  
	<item id="TSBZ" alias="特殊标志" type="int" defaultValue="0" length="1" display="2">
		<dic autoLoad="true">
			<item key="0" text="正常方式"/>
			<item key="1" text="特殊方式"/>
		</dic>
	</item>
	<item id="FKBZ" alias="付款标志" type="int" defaultValue="0" length="1" display="2">
		<dic autoLoad="true">
			<item key="0" text="不付款"/>
			<item key="1" text="付款"/>
		</dic>
	</item>
    
  
	<item id="DYHS" alias="打印行数" type="int" length="2" display="0"/>
</entry>
