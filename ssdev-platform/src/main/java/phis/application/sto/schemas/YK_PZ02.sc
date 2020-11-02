<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_PZ02" alias="平账02">
  <item id="JGID" alias="机构ID" length="20" not-null="1" type="string"/>
  <item id="JLXH" alias="记录序号" length="18" not-null="1" generator="assigned" pkey="true" type="long">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="PZID" alias="平账ID号" length="18" not-null="1" type="long"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" type="long"/>
  <item id="SBXH" alias="识别序号" length="18" not-null="1" type="long"/>
  <item id="YPXH" alias="药品序号" length="18" not-null="1" type="long"/>
  <item id="YPCD" alias="药品产地" length="18" not-null="1" type="long"/>
  <item id="YPPH" alias="药品批号" type="string" length="20"/>
  <item id="YPXQ" alias="药品效期" type="datetime"/>
  <item id="PZSL" alias="平账数量" length="10" precision="4" not-null="1" type="double"/>
  <item id="YJHJ" alias="原进货价格" length="12" precision="6" not-null="1" type="double"/>
  <item id="YPFJ" alias="原批发价格" length="12" precision="6"  type="double"/>
  <item id="YLSJ" alias="原零售价格" length="12" precision="6" not-null="1" type="double"/>
  <item id="YJHE" alias="原进货金额" length="12" precision="4" not-null="1" type="double"/>
  <item id="YPFE" alias="原批发金额" length="12" precision="4"  type="double"/>
  <item id="YLSE" alias="原零售金额" length="12" precision="4" not-null="1" type="double"/>
  <item id="XJHJ" alias="新进货价格" length="12" precision="6" not-null="1" type="double"/>
  <item id="XPFJ" alias="新批发价格" length="12" precision="6"  type="double"/>
  <item id="XLSJ" alias="新零售价格" length="12" precision="6" not-null="1" type="double"/>
  <item id="XJHE" alias="新进货金额" length="12" precision="4" not-null="1" type="double"/>
  <item id="XPFE" alias="新批发金额" length="12" precision="4"  type="double"/>
  <item id="XLSE" alias="新零售金额" length="12" precision="4" not-null="1" type="double"/>
</entry>
