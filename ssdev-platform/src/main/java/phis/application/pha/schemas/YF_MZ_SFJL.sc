<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YF_MZ_SFJL" tableName="YF_MZ_SFJL" alias="药房审方记录">
	<item id="JLXH" alias="记录序号" display="0" length="18" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" display="0" alias="机构ID" type="string" length="20" not-null="1"  defaultValue="%user.manageUnit.id"/>
	<item id="BRID" alias="病人ID" type="long" display="0" length="18" not-null="1"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="SBXH" alias="识别序号" length="18" not-null="1" type="long"/>
	<item id="YFSB" alias="药房识别" length="18" not-null="1" type="long"/>
	<item id="CFZH" alias="处方组号" display="0" type="long" length="18"/>
	<item id="SFBZ" alias="审核结果" type="int" width="170" defaultValue="0"/>
	<item id="SFGH" alias="审核工号" length="10" />
	<item id="SFSJ" alias="审方式见" type="timestamp" width="120" />
	<item id="SFYJ" alias="审核意见" type="string" width="200" height="80" length="255" /> 
</entry>