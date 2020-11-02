<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02_PS" tableName="MS_CF02" alias="药品信息"
	version="1331024845687" sort="YPZH,SBXH">
	<!-- 药品基本信息 -->
	<item id="SBXH" alias="处方明细序号" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1000" />
		</key>
	</item>
	<item id="YPXH" alias="药品序号" type="long" length="18" hidden="true" />
	<item id="CFSB" alias="处方识别" type="long" length="18" hidden="true" />
	<item id="uniqueId" alias="附加项目关联字段" type="long" fixed="true"
		virtual="true" display="0" />
	<item id="YPZH_SHOW" alias=" " type="long" length="1" fixed="true"
		width="25" renderer="showColor" virtual="true" />
	<item id="YPZH" alias="医嘱组号" type="long" length="1" isGenerate="false"
		display="0" width="10">
		<key>
			<rule name="increaseYzzh" type="increase" length="18" startPos="1000" />
		</key>
	</item>
	<item ref="b.YPMC" alias="药品名称" mode="remote" type="string" width="130"
		anchor="100%" length="80" colspan="2" not-null="true" />
	<item id="YFGG" alias="规格" type="string" length="20" fixed="true"
		width="60" />
	<item id="YFBZ" alias="药房包装" type="int" display="0" />
	<item id="CFTS" alias="处方帖数" type="int" display="0" />
	<item id="YCJL" alias="剂量" width="60" type="double" precision="3"
		max="9999999.999" not-null="true" />
	<item ref="b.JLDW" alias=" " type="string" length="8" fixed="true"
		width="30" />
	<item ref="b.YPJL" alias="原始剂量" type="double" precision="3"
		display="0" />
	<item id="YPYF" alias="频次" type="string" not-null="true" length="18"
		width="60">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS"
			autoLoad="true" />
	</item>
	<item id="MRCS" alias="每日次数" type="int" length="2" display="0" />
	<item id="YYTS" alias="天数" not-null="true" type="int" width="50"
		max="99999999" />
	<item id="YPSL" alias="总量" width="80" type="int" defaultValue="1"
		max="99999999" />
	<item id="YFDW" alias="单位" type="string" length="4" fixed="true"
		width="40" />
	<item id="GYTJ" alias="药品用法" not-null="true" type="int" length="4"
		width="80">
		<dic id="phis.dictionary.drugMode" autoLoad="true" searchField="PYDM"
			fields="key,text,PYDM,FYXH" />
	</item>
	<item id="YPZS" alias="服法" width="60" type="int" hidden="true">
		<dic id="phis.dictionary.suggested" autoLoad="true" editable="false" />
	</item>
	<item id="YPCD" alias="药品产地" width="80" fixed="true" type="string">
		<dic id="phis.dictionary.medicinePlace" />
	</item>
	<item id="YPDJ" width="70" type="double" alias="单价" precision="4"
		fixed="true" />
	<item id="ZFBL" width="80" type="double" alias="自负比例" precision="3"
		fixed="true" />
	<item id="HJJE" alias="合计金额" type="double" display="0" />
	<item id="PSPB" alias="皮试判别" type="int" length="1" display="0" />
	<item id="PSJG" alias="皮试结果" display="0" type="string" />
	<item id="FYGB" alias="费用归并" type="long" display="0" />
	<item ref="b.YCYL" alias="一次用量" type="int" length="12" display="2" />
	<item ref="b.TYPE" alias="药品类型" type="int" length="2" display="2" />
	<item ref="b.TSYP" alias="特殊药品" type="int" length="1" display="2" />
	<item ref="b.JYLX" alias="基本药物标志" type="int" length="1" display="2"
		defaultValue="1" />
	<item id="msg" virtual="true" alias="提示信息" type="string" display="0" />
	<item id="BZMC" alias="备注名称" display="0" type="string" />
	<item id="SFJG" alias="审方结果" type="int" display="0" />
	<item id="SFGH" alias="审方工号" display="0" type="string" />
	<item id="SFYJ" alias="审核意见" display="0" type="string" />
	<item id="ZFPB" alias="自负判别" type="int" length="1" display="0" />
	<item ref="c.KPDY" display="0" />
	<item ref="d.YFSB" display="0" />
	<item ref="d.CFLX" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_MS">
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.ZY_YPYF">
			<join parent="YPYF" child="GYTJ"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF01">
			<join parent="CFSB" child="CFSB"></join>
		</relation>
	</relations>
</entry>
