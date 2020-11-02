<?xml version="1.0" encoding="UTF-8"?>

<entry ientityName="YF_YFLB" tableName="YF_YFLB" alias="药房列表" sort="a.YFSB">
	<item id="JGID" alias="机构ID" display="0"  type="string" update="false" defaultValue="%user.manageUnit.id"  length="20" not-null="1" />
	<item id="YFSB" alias="药房识别" type="long" display="0" update="false" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onTpRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="YFMC" alias="药房名称" layout="JBXX" disableKeyFilter ="true" not-null="1"  length="15">
	</item>
	
	<item id="BZLB" alias="包装类别" type="int" length="1" defaultValue="1" layout="JBXX" >
		<dic id="phis.dictionary.packagingCategories"/>	
	</item>
	<item id="SJGLBZ" alias="三级管理标志" type="int" display="0" length="1" defaultValue="0" >
		<dic id="phis.dictionary.confirm"/> 	
	</item>
	<item id="SJKF" alias="三级库房"  type="int" display="0" length="18"  defaultValue="0">
	</item>
	<item id="SJJG" alias="上级发药机构"   length="20" layout="JBXX">
		<dic id="phis.@manageUnit" slice="0"/> 	
	</item>
	<item id="SJYF" alias="上级发药药房"   length="18" type="long" layout="JBXX">
		<dic id="phis.dictionary.pharmacy_db"/> 	
	</item>
	<item id="XYQX" alias="西药权限"  renderer="onRenderer"  length="1"  layout="YPQX" xtype="checkbox" >
	</item>
 
	<item id="ZYQX" alias="中药权限" renderer="onRenderer"  length="1"  layout="YPQX" xtype="checkbox">
	</item>
	<item id="CYQX" alias="草药权限" renderer="onRenderer"  length="1" layout="YPQX" xtype="checkbox">
	</item>
	<item id="LYKS" alias="领药科室" type="string" defaultValue="0" display="2" layout="FYKS"  width="500" length="200">
		<dic id="phis.dictionary.publicDepartment" render="Checkbox" autoLoad="true" searchField="PYDM"/>
	</item>
	<item id="ZXBZ" alias="注销标志"  type="int" length="1"  defaultValue="0" display="0">
		<dic id="phis.dictionary.status"/>
	</item>
	
</entry>
