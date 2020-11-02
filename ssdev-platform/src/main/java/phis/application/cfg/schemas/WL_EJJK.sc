<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_EJJK" alias="二级建库">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="WZMC" alias="物资名称" virtual="true" fixed="true" width="200" length="60" queryable="true"/>
	<item id="WZGG" alias="物资规格" virtual="true" fixed="true" length="40"/>
	<item id="WZDW" alias="物资单位" virtual="true" fixed="true" length="10" />
	<item id="PYDM" alias="拼音代码"  fixed="true" virtual="true" length="10" />
	<item id="GCSL" alias="高储数量" virtual="true" type="double" length="12" precision="2"/>
	<item id="DCSL" alias="低储数量" virtual="true" type="double" length="12" precision="2"/>
	<item id="WZZT" alias="物资状态" type="int" fixed="true" length="1">
		<dic id="phis.dictionary.wzzt"  autoLoad="true"/>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20"/>
	<item id="KFXH" alias="所属库房" type="int" length="8" fixed="true" not-null="true">
		<dic id="phis.dictionary.treasury"  filter="['eq',['$map',['s','JGID']],['$','%user.manageUnit.id']]" autoLoad="true" />
	</item>	
	<item id="KSDM" alias="科室代码" display="0" type="long" length="18"/>
	<item id="WZXH" alias="物资序号" display="0" type="long" length="18"/>
	<item id="YRBZ" alias="引入标志" display="0" type="int" length="1"/>
</entry>
