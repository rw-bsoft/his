<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPXX"  alias="药库药品信息" sort="b.TYPE,a.YPXH">
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="JGID" alias="机构ID" length="20" not-null="1" display="0"   defaultValue="%user.manageUnit.id"  layout="GDC" update="false" pkey="true" />
	<item ref="b.YPMC" />
	<item ref="b.YPGG" />
	<item ref="b.YPDW" />
	<item ref="b.ZXDW" />
	<item ref="b.ZXBZ" />
	<item ref="b.YFDW" />
	<item ref="b.YFBZ" />
	<item ref="b.BFDW" />
	<item ref="b.BFBZ" />
	<item ref="b.PYDM" />
	<item ref="b.JXDM" />
	<item ref="b.QTDM" />
	<item id="YPXH" alias="药品序号" length="18" type="long" display="0" not-null="1" pkey="true" layout="GDC" >	
	<item id="YKSB" alias="药库识别" length="18" display="0" type="long" not-null="1" layout="GDC"/>
	</item>
	<item id="YKZF" alias="药库作废" length="1" display="0" not-null="1" layout="GDC" defaultValue="0" update="false"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK_BZ" />
		<join parent="YPXH" child="YPXH" />
	</relations>

</entry>
