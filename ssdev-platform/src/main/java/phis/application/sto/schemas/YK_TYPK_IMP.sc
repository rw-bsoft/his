<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK"  alias="药品信息导入" sort="a.YPXH desc">
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="string" length="18" not-null="1"
		generator="assigned" pkey="true" display="0" layout="JBXX">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="4" layout="JBXX"/>
	<item id="TYPE" alias="类别" display="2"  not-null="1"
		type="string" length="2" layout="JBXX">
		<dic id="phis.dictionary.storeroomType"/>
	</item>
	<item id="YPSX" alias="剂型"   not-null="1" 
		length="18" layout="JBXX">
		<dic id="phis.dictionary.dosageForm"/>
	</item>
	
	<item id="PYDM" alias="拼音码" type="string" length="160" selected="true"
		queryable="true" layout="JBXX">
		<set type="exp" run="server">['py',['$','r.YPMC']]
		</set>
	</item>
	<item id="CFLX" alias="类别" display="0" defaultValue="1"/>
</entry>
