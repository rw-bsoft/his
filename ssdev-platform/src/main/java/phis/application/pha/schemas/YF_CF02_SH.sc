<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MS_CF02"   alias="处方审核明细列表" sort="a.YPZH asc">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" type="long" generator="assigned" pkey="true">
	  	<key>
			<rule name="increaseId" type="increase" length="16" startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" />
	<item id="BRID" alias="病人ID" display="0"  type="long" length="18" />
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1" />
	<item id="SFJG" alias="审方结果" type="int" defaultValue="0" renderer="auditRender" />
	<item id="SFGH" alias="审方工号" length="10" display="0" fixed="true" />
	<item id="SFRY" alias="审方人员" length="100" fixed="true" />
	<item id="SFYJ" alias="审核意见" type="string" width="200" height="80" length="255" />
	<item id="YPXH" alias="药品序号" type="long" display="0" length="18" not-null="1" />
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="YFGG" alias="规格" type="string" length="20"  layout="JBXX" />
	<item id="YCJL" alias="剂量" length="10" width="70" type="double" min="0" precision="3" max="9999999.999" defaultValue="1"/>
	<item id="YPYF" type="string" display="0" length="18"/>
	<item id="YPYF_STR" alias="频次" type="string" length="18" width="40" />
	<item id="YYTS" alias="天数" type="int"/>
	<item id="YPSL" alias="总量" type="double" length="10" precision="2" not-null="1" />
	<item id="YFDW" alias="单位" type="string" length="4" layout="JBXX" not-null="1" />
	<item id="GYTJ" type="int" display="0" />
	<item id="GYTJ_STR" alias="药品用法" length="9" width="70" type="String" not-null="1" />
	<item id="YPZS" type="int" display="0" />
	<item id="YPZS_STR" alias="服法" width="60" type="string" />
  	<item id="CDMC" alias="药品产地" type="string"  width="100" anchor="100%" length="7" not-null="true" colspan="2" />
  	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" not-null="1"/>
	<item id="HJJE" alias="金额" type="double" length="10" width="100" precision="2"/>
	<item id="YFKC" alias="库存数量" width="100" type="double" length="18" precision="2" />
	<item id="YPZH" alias="药品组号" display="0" type="long" length="18" />
</entry>