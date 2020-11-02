<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02" alias="门诊处方_门诊收费">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" />
	<item id="YPXH" alias="药品序号" length="18" not-null="1" display="0"/>
	<item id="CF_NEW" alias=" " width="25" type="string" fixed="true"/>
	<item id="CFSB" alias="处方识别" type="long" display="0" length="18" />
	<item id="YPZH_show" alias="组" type="long" width="35" renderer="showColor" fixed="true"/>
	<item id="YPZH" alias="组" type="long" width="20" display="0" fixed="true"/>
	<item ref="b.YPMC" alias="名称" mode="remote" width="160"/>
	<item ref="d.CFHM" display="0"/>
	<item ref="d.KFRQ" display="0"/>
	<item ref="d.CFLX" display="0"/>
	<item ref="d.KSDM" display="0"/>
	<item ref="d.YSDM" display="0"/>
	<item ref="d.DJLY" display="0"/>
	<item ref="d.FPHM" display="0"/>
	<item ref="d.DJYBZ" display="0"/>
	<item ref="d.YFSB" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" display="0" length="18" not-null="1">
  		<dic id="phis.dictionary.medicinePlace" />
 	</item>
	<item id="YFDW" alias="单位" type="string" width="50" length="4" fixed="true"/>
	
	<item id="YFGG" alias="规格" type="string" length="20" fixed="true"/>
	<item id="YPDJ" alias="单价" type="double" length="12" width="70" min="0" max="99999999.9999" precision="4" not-null="1"/>
	<item id="YPDJ_Y" alias="单价" type="double" length="12" width="70" display="0" precision="4" not-null="1"/>
	<item id="YPSL" alias="数量" type="double" length="10" width="50" min="0" max="9999.99" precision="2" not-null="1"/>
	<!--做到底层的时候金额加载加上事件,计算,现在界面设计暂时没做-->
	<item id="HJJE" alias="金额" type="double" length="10" width="70" precision="2" fixed="true"/>
	<item id="ZFBL" alias="自负比例" type="double" length="6" display="0"  precision="3" not-null="1" fixed="true"/>
	<item id="YPYF" alias="频次" type="string" width="50" length="18">
  		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS" autoLoad="true"/>
  	</item>
	<item id="YYZBM" width="90" alias="医保报销" type="string" length="20" renderer="YYZBMRenderer"/>
  	<item id="ZXKS" alias="执行科室" type="long" width="150" display="0"  length="18" not-null="1" >
		<dic id="phis.dictionary.department_mzyj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYCODE" />
	</item>
	<item id="FYGB" alias="费用归并" type="long" length="18" display="0"/>
	<item id="GBMC" alias="费用归并" type="long" length="18" display="0"/>
	<item id="CFTS" alias="处方贴数" type="int" length="2" display="0" not-null="1"/>
	<item id="SFJG" alias="审方结果" type="int" display="0" />
	<item id="YCSL" alias="一次数量" type="string" length="20" display="0"/>
	<item id="ZFPB" alias="自负判别" type="int" length="1" display="0"/>
	<item id="XMSPBH" alias="项目审批编号" type="string" length="15" display="0"/>
	<item id="uniqueId" alias="附加项目关联字段" type="long" fixed="true" virtual="true" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" display="0"/>
	<item id="YCJL" alias="剂量" width="60" type="double" precision="3" display="0" max="9999999.999"
		not-null="true" />
	<item ref="b.JLDW" alias=" " type="string" length="8" fixed="true" display="0" width="30"/>
	<item ref="b.YPJL" alias="原始剂量" type="double" precision="3" display="0" />
	<item id="MRCS" alias="每日次数" type="int" length="2" display="0" />
	<item id="YYTS"  alias="天数" not-null="true" type="int" display="0" width="50" max="99999999" />
	<item id="GYTJ" alias="药品用法" not-null="true" type="int" display="0" length="4" width="80">
		<dic id="phis.dictionary.drugMode" autoLoad="true" searchField="PYDM" fields="key,text,PYDM,FYXH"/>
	</item>
	<item id="YPZS"  alias="服法" width="60" type="int" display="0" >
		<dic id="phis.dictionary.suggested" autoLoad="true" editable="false"/>
	</item>
	<item id="BZXX" alias="备注" width="80" type="string" display="0"/>
	<item id="PSPB" alias="皮试判别" type="int" length="1" display="0" />
	<item id="PSJG" alias="皮试结果" display="0" type="string"/>
	<item ref="b.KSBZ" defaultValue="0"  alias="是否抗生素" type="string"
		length="1" hidden="true">
	</item>
	<item ref="b.YCYL" alias="一次用量" type="int" length="12" display="0" />
	<item ref="b.TYPE" alias="药品类型" type="int" length="2" display="0" />
	<item ref="b.TSYP" alias="特殊药品" type="int" length="1" display="0" />
	<item ref="b.JYLX" alias="基本药物标志" type="int" length="1" display="0" defaultValue="1"/>
	<item id="msg" virtual="true" alias="提示信息" type="string"  display="0" />
	<item id="BZMC" alias="备注名称"  display="0" type="string"/>
	<item id="SFGH" alias="审方工号" display="0" type="string"/>
	<item id="SFYJ" alias="审核意见" display="0" type="string"/>
	<item ref="c.KPDY" display="0"/>
	<item ref="b.NHBM_BSOFT" length="20" type="string" display="0" />
	<item ref="b.YPSX" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.YK_TYPK_MS" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.ZY_YPYF" >
			<join parent="YPYF" child="GYTJ"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF01" >
			<join parent="CFSB" child="CFSB"></join>
		</relation>
	</relations>
</entry>
