<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_FLZD" alias="分类字典">
  <item id="ZDXH" alias="字典序号" type="long" length="12" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="12"
				startPos="1" />
		</key>
  </item>
  
  <item id="LBXH" alias="类别序号" type="int"   length="8" width="20"  display="0" />
  <item id="SJFL" alias="上级分类" type="int" length="8" readOnly="true" />
  <item id="FLBM" alias="分类编码" length="30" not-null="1" />
  <item id="FLMC" alias="分类名称" length="30" not-null="1" width="100" colspan="2"/>
  <item id="GZXH" alias="规则序号" type="int" length="8" colspan="2" display="0"/>
  <item id="JGID" alias="机构ID" type="string" length="20" defaultValue="%user.manageUnit.id" display="0"/>
</entry>
