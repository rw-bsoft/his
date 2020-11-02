<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_ZYJB" alias="中医疾病">
	<item id="JBBS" alias="疾病标识" type="long" length="9" display="0" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	
	 <item id="ZYFL" alias="中医分类" type="long" length="9" not-null="1">
        <dic id="phis.dictionary.ZYJBFL"  autoLoad="true">
        </dic>
    </item>
    <item id="JBDM" alias="疾病代码" type="string" length="20" not-null="1" />
    <item id="JBMC" alias="疾病名称" type="string" length="60" not-null="1" width="130"/>
    <item id="PYDM" alias="拼音码" type="string" length="10" target="JBMC" codeType="py" queryable="true"/>
    <item id="WBDM" alias="五笔码" type="string" length="10" target="JBMC" display="0" codeType="wb"/>
    <item id="QTDM" alias="其它码" type="string" length="10" display="0"/>
    <item id="XBXZ" alias="性别限制" type="int" length="1" display="0" defaultValue="0"/>
    <item id="YXZY" alias="允许治愈" type="int" length="1" display="0" defaultValue="0"/>
    <item id="YXHZ" alias="允许好转" type="int" length="1" display="0" defaultValue="0"/>
    <item id="YXWY" alias="允许未愈" type="int" length="1" display="0" defaultValue="0"/>
    <item id="YXSW" alias="允许死亡" type="int" length="1" display="0" defaultValue="0"/>
    <item id="YXQT" alias="允许其它" type="int" length="1" display="0" defaultValue="0"/>
    <item id="KZFS" alias="控制方式" type="int" length="1" not-null="1" display="0" defaultValue="0"/>
    <item id="ZXBZ" alias="注销标志" type="int" length="1" not-null="1" display="0" defaultValue="0"/>
    <item id="BZXX" alias="备注信息" type="string" display="0" length="255" width="200"/>
    <item ref="b.FLBM" alias="分类编码" type="string" display="0" length="30"/>
    <relations>
		<relation type="parent" entryName="phis.application.emr.schemas.EMR_ZYFL" >
			<join parent="ZYFL" child="ZYFL"></join>
		</relation>
  </relations>
</entry>
