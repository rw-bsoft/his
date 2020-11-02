<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPXX" alias="药库药品信息" sort="b.YPXH desc">

	<item ref="b.YPXH" />
	<item ref="b.YPMC" />
	<item ref="b.YPGG" />
	<item ref="b.YPDW" />
	<item ref="b.TYPE" />
	<item ref="b.YPSX" />
	<item ref="b.YPJL" />
	<item ref="b.JLDW" />
	<item ref="b.YPDC" />
	<item ref="b.YPXQ" />
	<item ref="b.ABC" />
	<item ref="b.PYDM" />
	<item ref="b.JXDM" />
	<item ref="b.QTDM" />
	<item ref="b.PSPB" />
	<item ref="b.JBYWBZ" />
	<item ref="b.JYLX" />
	<item ref="b.ZXDW" />
	<item ref="b.YFGG" />
	<item ref="b.YFDW" />
	<item ref="b.ZXBZ" />
	<item ref="b.BFGG" />
	<item ref="b.BFDW" />
	<item ref="b.YFBZ" />
	<item ref="b.BFBZ" />
	<item ref="b.FYFS" />
	<item ref="b.GYFF" />
	<item ref="b.TSYP" />
	<item ref="b.YPZC" />
	<item ref="b.YBFL" />
	<item ref="b.CFYP" />
	<item ref="b.KSBZ" />
	<item ref="b.YCYL" />
	<item id="JGID" alias="机构ID" length="20"  type="string" not-null="1" display="0" defaultValue="%user.manageUnit.id" />
	<item id="YPXH" alias="药品序号" length="18" type="string" display="0"
		not-null="1" generator="assigned" pkey="true" />
	<item id="YKSB" alias="药库识别" length="18" defaultValue="1" display="0" not-null="1" />
	<item id="GCSL" alias="高储数量" length="10" display="2" precision="4"
		not-null="1" />
	<item id="DCSL" alias="低储数量" length="10" display="2" precision="4"
		not-null="1" />
	<item id="KWBM" alias="库位编码" type="string" display="0" length="16" />
	<item id="YKZF" alias="药库作废" length="1" display="2" not-null="1">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="CFLX" alias="处方类型" length="2" display="2" not-null="1">
		<dic id="phis.dictionary.storeroomType" />
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"/>
		</relation>
	</relations>
</entry>
