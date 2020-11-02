<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_KCDJ" alias="库存冻结">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="SBXH" alias="识别序号" length="18" type="long" not-null="1" generator="assigned" pkey="true" display="0" >
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18"  not-null="1" type="long"/>
	<item id="CFSB" alias="处方识别" length="18"  not-null="1" type="long"/>
	<item id="JLXH" alias="CF02主键" length="18"  not-null="1" type="long"/>
	<item id="YPXH" alias="药品序号" length="18"  not-null="1" type="long"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" />
	<item id="YPSL" alias="冻结数量" length="10" type="double" precision="2" not-null="1"  max="999999.99"/>
	<item id="YFBZ" alias="药房包装" length="4"  type="int" not-null="1" />
	<item id="DJSJ" alias="冻结时间" type="datetime" not-null="1"/>
</entry>
