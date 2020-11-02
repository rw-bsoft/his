<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YJ02_FJXM" tableName="MS_YJ02" alias="门诊医技单02表" sort="YJZH ,SBXH asc">
	<item id="SBXH" alias="记录编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="YJZH" type="int" length="1" fixed="true" width="30" display="0" />
	<item id="JGID" alias="机构ID" length="8" display="0" not-null="1" defaultValue="%user.manageUnit.id" type="string"/>
	<item id="YJXH" alias="医技序号" type="long" display="0" length="18" not-null="1" defaultValue="0"/>
	<item id="YLXH" alias="医疗序号" type="int" display="0"  length="18" not-null="1"/>
	<item id="XMLX" alias="项目类型" type="int" display="0" length="2" defaultValue="0"/>
	<item id="YJZX" alias="医技主项" type="int" display="0" length="1" defaultValue="0"/>
	<item id="FYMC" alias="附加项目"  width="200"  mode="remote" type="string" />
	<item id="FYDW" alias="单位" fixed="true" type="string"/>
	<item id="YLSL" alias="数量" type="double" length="8" precision="2" max="999999.99" min="1" defaultValue="1" />
	<item id="YLSL_YS" alias="数量_原始" type="double"  display="0"/>
	<item id="YLDJ" alias="单价" type="double" length="10" precision="2" defaultValue="0" nullToValue="0" />
	<item id="HJJE" alias="金额" type="double" fixed="true" length="12" precision="2" not-null="1" />
	<item id="ZFBL" alias="自负比例" type="double" length="6" precision="3" nullToValue="0" defaultValue="1" fixed="true" />
	<item id="FYGB" alias="费用归并" type="int"  display="0" length="18" not-null="1" defaultValue="0"/>
	<item id="BZXX" alias="备注信息" type="string" display="0" length="255"/>
	<item id="uniqueId" alias="附加项目关联字段" type="long" fixed="true" virtual="true" display="0"/>
	<item id="YPZH" alias="药品组号" type="long" fixed="true" virtual="true" display="0"/>
</entry>
