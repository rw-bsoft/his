<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_AreaGrid_LIST" alias="网格地址" tableName="EHR_AreaGrid" sort="orderNo asc , regionCode asc">
		<item id="regionName"  alias="网格地址名称"  type="string" length="50"  width="200" not-null="1"/>
	<item id="regionCode"  alias="网格地址编号"  type="string" length="25" not-null="1" width="180"  generator="assigned" pkey="true"/>
	<item id="parentCode"  alias="父节点编码"   type="string" length="25" display="0"/>
	<item id="pyCode" alias="拼音检索码" type="string" length="50" queryable="true" display="1" >
		<set type="exp" run="server">['py',['$','r.regionName']]</set>
	</item>
	<item id="isFamily"  alias="层次属性"   type="string" length="25" width="88" display="1">
	<dic id="chis.dictionary.isFamily" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true" />
</item>
	<item id="isBottom"  alias="是否最底层" type="string" length="1"  update="false" display="0">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
</entry>
