<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_KCDJ" alias="库存冻结">
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1" defaultValue="%user.manageUnit.id" display="0" />
	<item id="SBXH" alias="识别序号" length="18" type="long" not-null="1" generator="assigned" pkey="true" display="0" >
		<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
	</item>
	<item id="YFSB" alias="药房识别" length="18"  not-null="1" type="long" display="0"/>
	<item id="CFSB" alias="处方识别" length="18"  not-null="1" type="long" display="0"/>
	<item id="JLXH" alias="CF02主键" length="18"  not-null="1" type="long" display="0"/>
	<item id="YPXH" alias="药品序号" length="18"  not-null="1" type="long" display="0"/>
	<item id="YPCD" alias="产地" type="long" length="18" not-null="1" display="0"/>
	<item id="YFBZ" alias="药房包装" length="4"  type="int" not-null="1" display="0"/>
	<item ref="b.YPMC" />
	<item ref="e.CDMC" alias="产地"/>
	<item ref="f.YPDJ" />
	<item ref="b.PYDM" />
	<item ref="d.YFGG" />
	<item ref="d.YFDW" />
	<item ref="c.JZXH" />
	<item id="YPSL" alias="冻结数量" length="10" type="double" precision="2" not-null="1"  max="999999.99"/>
	
	<item id="DJSJ" alias="冻结时间" type="datetime" not-null="1" width="180"/>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
		           <join parent="YPXH" child="YPXH" />
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF01" >
		           <join parent="CFSB" child="CFSB" />
		</relation>
		<relation type="parent" entryName="phis.application.pha.schemas.YF_YPXX" >
		           <join parent="YPXH" child="YPXH" />
		           <join parent="YFSB" child="YFSB" />
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
		           <join parent="YPCD" child="YPCD" />
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF02" >
		           <join parent="SBXH" child="JLXH" />
		</relation>
	</relations>
</entry>
